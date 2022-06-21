/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
extern crate proc_macro;
use crate::utils::unzip4::Unzip4;
use proc_macro::TokenStream;
use proc_macro2::{Ident, Span};
use quote::quote;
use syn::parse::{Parse, ParseStream};
use syn::{self, parse_macro_input, Expr, ExprLit, ExprReturn, Lit, Path, Result, Token, Type};

/// Input for jni_wrap
struct JNIWrapInput {
    /// The (Java) namespace of the function.
    /// Used to construct the final name as required by JNI
    namespace: String,
    /// The name of the function to wrap
    func: Ident,
    /// Return type of the function, or None if void
    retn: Option<Ident>,
    /// List of arguments to the function
    /// These should either be one of the types defined in jni::sys,
    /// jni::objects or raw pointers (represented in Java as longs)
    args: Vec<Type>,
}

impl Parse for JNIWrapInput {
    /// Parse an expression such as
    /// `function, return type, argtype1, argtype2` (function returning type taking two arguments)
    /// or `function` (function returning nothing taking no arguments)
    fn parse(input: ParseStream) -> Result<Self> {
        // Read a string literal
        let namespace: ExprLit = input.parse()?;
        let ns_string = match namespace.lit {
            Lit::Str(c) => c.value(),
            _ => unreachable!(),
        };

        // Read a function name
        input.parse::<Token![,]>()?;
        let func: Ident = input.parse()?;
        // Optionally read `return [type]`
        let mut retn: Option<Ident> = None;
        if input.peek(Token![,]) && input.peek2(Token![return]) {
            input.parse::<Token![,]>()?;
            let retexpr: ExprReturn = input.parse()?;
            retn = match *(retexpr.expr.unwrap()) {
                Expr::Path(exprpath) => Some(get_path_type(&exprpath.path)),
                _ => unreachable!(),
            }
        }

        // Read a list of types separated by commas
        let mut args: Vec<Type> = Vec::new();
        while input.peek(Token![,]) {
            input.parse::<Token![,]>()?;
            let ty: Type = input.parse()?;
            args.push(ty);
        }

        Ok(JNIWrapInput {
            namespace: ns_string,
            func,
            retn,
            args,
        })
    }
}

/// Implementation for jni_wrap
pub fn jni_wrap_impl(tokens: TokenStream) -> TokenStream {
    let JNIWrapInput {
        namespace,
        func,
        retn,
        args,
    } = parse_macro_input!(tokens as JNIWrapInput);

    // Construct a set of per-argument expressions
    let (func_args, param_prepare, call_args, param_cleanup): (
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
    ) = args
        .iter()
        .enumerate()
        .map(|(i, ty)| setup_argument(i, ty))
        .unzip4();

    // Function name
    let jniname = create_jni_func_name(&namespace, &func);

    // Call expression: either a simple function call, or one which collects the result
    let call_expr = match retn {
        None => quote! {  #func(#(#call_args, )*); },
        Some(_) => quote! {
            let result = #func(#(#call_args, )*);
        },
    };

    // Return expression: Return the result if present
    let return_expr = match retn.clone() {
        None => quote! {},
        Some(t) => quote! {
            return result as #t;
        },
    };

    // Return type
    let return_type = match retn {
        None => quote! {},
        Some(t) => quote! { -> #t },
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

/// Create per-argument statements
fn setup_argument(
    index: usize,
    ty: &Type,
) -> (
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
) {
    let arg = Ident::new(&format!("arg{}", index), Span::call_site());
    let param = Ident::new(&format!("param{}", index), Span::call_site());
    (
        // JNI Function argument: arg[index]: type
        quote! { #arg : #ty },
        // Preparation statement: let param[index] = arg[index] + conversion
        param_prepare(index, ty, &arg, &param),
        // Call expression
        quote! { #param },
        // Cleanup statement as required by the preparation statement
        param_cleanup(index, ty, &arg, &param),
    )
}

/// Generates a statement to prepare a parameter by performing
/// necessary conversions
fn param_prepare(_index: usize, ty: &Type, arg: &Ident, param: &Ident) -> proc_macro2::TokenStream {
    match ty {
        Type::Path(tp) => {
            match get_path_type(&tp.path).to_string().as_str() {
                // jboolean is an integral type, convert to a proper bool
                "jboolean" => quote! { let #param = #arg != 0 },
                // Read chars from JString directly
                "JString" => {
                    quote! { let #param = _env.get_string_utf_chars(#arg).unwrap_or(std::ptr::null_mut()) }
                }
                // Treat jbyteArrays as raw byte array
                "jbyteArray" => {
                    quote! { let #param = _env.get_byte_array_elements(#arg, ReleaseMode::CopyBack).map(|arr| arr.as_ptr()).unwrap_or(std::ptr::null_mut()) as *mut c_void}
                }
                // Otherwise attempt to defer to TryInto
                _ => quote! { let #param = #arg.try_into().unwrap() },
            }
        }
        _ => quote! { let #param = #arg },
    }
}

/// Generates a statement to clean up as required by `param_prepare`
fn param_cleanup(_index: usize, ty: &Type, arg: &Ident, param: &Ident) -> proc_macro2::TokenStream {
    match ty {
        Type::Path(tp) => {
            match get_path_type(&tp.path).to_string().as_str() {
                // Release characters
                "JString" => {
                    quote! {
                        if !#param.is_null() {
                            _env.release_string_utf_chars(#arg, #param).unwrap()
                        }
                    }
                }
                // Otherwise do nothing
                _ => quote! {},
            }
        }
        _ => quote! {},
    }
}

/// Constructs the function name required for JNI
fn create_jni_func_name(namespace: &str, func: &Ident) -> Ident {
    let namespace_underscored = namespace.replace('_', "_1").replace('.', "_");
    let fn_name_underscored = func.to_string().replace('_', "_1");
    return Ident::new(
        &format!("Java_{}_{}", namespace_underscored, fn_name_underscored),
        Span::call_site(),
    );
}

/// Reads the final type ident from a type Path
fn get_path_type(path: &Path) -> Ident {
    return path.segments.last().unwrap().ident.clone();
}
