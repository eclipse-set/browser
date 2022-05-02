extern crate proc_macro;

use proc_macro::TokenStream;
use proc_macro2::{Ident, Span};
use quote::quote;
use syn::parse::{Parse, ParseStream};
use syn::{
    self, parse_macro_input, Result, Token, Type, TypePath, Path, ExprLit, ExprReturn, Lit, Expr, ExprType
};

struct JNIWrapInput {
    namespace: String,
    func: Ident,
    retn: Option<Ident>,
    args: Vec<Type>,
}

impl Parse for JNIWrapInput {
    fn parse(input: ParseStream) -> Result<Self> {
        let namespace: ExprLit = input.parse()?;
        let ns_string = match namespace.lit {
            Lit::Str(c) => c.value(),
            _ => unreachable!()
        };
        input.parse::<Token![,]>()?;
        let func: Ident = input.parse()?;

        let mut retn: Option<Ident> = None;

        if input.peek(Token![,]) && input.peek2(Token![return])
        {
            input.parse::<Token![,]>()?;
            let retexpr: ExprReturn = input.parse()?;
            retn = match *(retexpr.expr.unwrap()) {
                Expr::Path(exprpath) => Some(get_path_type(&exprpath.path)),
                _ => unreachable!()
            }
        }   

        let mut args: Vec<Type> = Vec::new();
        while input.peek(Token![,]) {
            input.parse::<Token![,]>()?;
            let ty: Type = input.parse()?;
            args.push(ty);
        }
        Ok(JNIWrapInput {
            namespace: ns_string,
            func: func,
            retn: retn,
            args: args,
        })
    }
}

pub fn jni_wrap_impl(tokens: TokenStream) -> TokenStream {
    let JNIWrapInput { namespace, func, retn, args } = parse_macro_input!(tokens as JNIWrapInput);

    let func_args = args
        .iter()
        .enumerate()
        .map(|(i, ty)| {
            let arg = Ident::new(&format!("arg{}", i), Span::call_site());
            quote! {
                #arg : #ty
            }
        })
        .collect::<Vec<_>>();

    //
    let call_args = args
        .iter()
        .enumerate()
        .map(|(i, _ty)| {
            let arg = Ident::new(&format!("param{}", i), Span::call_site());
            quote! {
                #arg
            }
        })
        .collect::<Vec<_>>();

    let param_prepare = args
        .iter()
        .enumerate()
        .map(|(i, ty)| {
            let arg = Ident::new(&format!("arg{}", i), Span::call_site());
            let param = Ident::new(&format!("param{}", i), Span::call_site());
            match ty {
                Type::Path(tp) => { 
                    match get_path_type(&tp.path).to_string().as_str() {
                        "jboolean" => quote! { let #param = #arg != 0 },
                        "JString" => quote! { let #param = _env.get_string_utf_chars(#arg).unwrap_or(std::ptr::null_mut()) },
                        "jbyteArray" => quote! { let #param = _env.get_byte_array_elements(#arg, ReleaseMode::CopyBack).map(|arr| arr.as_ptr()).unwrap_or(std::ptr::null_mut()) as *mut c_void},
                        _ => quote! { let #param = #arg.try_into().unwrap() } 
                    }
                    
                }
                _ => quote! { let #param = #arg }
            }
        })
        .collect::<Vec<_>>();
    
    let param_cleanup = args
        .iter()
        .enumerate()
        .map(|(i, ty)| {
            let arg = Ident::new(&format!("arg{}", i), Span::call_site());
            let param = Ident::new(&format!("param{}", i), Span::call_site());
            if jstring_type() == ty.clone() {
                quote! { if !#param.is_null() { _env.release_string_utf_chars(#arg, #param).unwrap() } }
            } else {
                quote! { }
            }
        })
        .collect::<Vec<_>>();

    let jniname = create_jni_func_name(&namespace, &func);

    let call_expr = match retn {
        None => quote! {  #func(#(#call_args, )*); },
        Some(_) => quote! {  
            let result = #func(#(#call_args, )*); 
        }
    };

    let return_expr = match retn.clone() {
        None => quote! { },
        Some(t) => quote! {  
            return result as #t;
        }
    };

    let return_type = match retn {
        None => quote! { },
        Some(t) => quote! { -> #t }
    };

    let q = quote! {
        #[no_mangle]
        pub extern fn #jniname(_env: JNIEnv, _class: JClass, #(#func_args, )*) #return_type {
            #(#param_prepare; )*
            #call_expr
            #(#param_cleanup; )*
            #return_expr
        }
    };
    TokenStream::from(q)
}

fn jstring_type() -> Type {
    return Type::Path(TypePath {
        qself: None,
        path: Path::from(Ident::new("JString", Span::call_site())),
    });
}


fn create_jni_func_name(namespace: &str, func: &Ident) -> Ident {
    let namespace_underscored = namespace.replace('_', "_1").replace('.', "_");
    let fn_name_underscored = func.to_string().replace('_', "_1");
    return Ident::new(&format!("Java_{}_{}", namespace_underscored, fn_name_underscored), Span::call_site());
}

fn get_path_type(path: &Path) -> Ident 
{
    return path.segments.last().unwrap().ident.clone();
}