/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
extern crate proc_macro;
use crate::jni_name::{create_jni_fn_name, JNIName};
use crate::utils::unzip4::Unzip4;
use proc_macro::TokenStream;
use proc_macro2::{Ident, Span};
use quote::quote;
use syn::{parse_macro_input, FnArg, Type};

pub fn jni_wrap_attr_impl(attr: TokenStream, item: TokenStream) -> TokenStream {
    let JNIName { namespace, suffix } = parse_macro_input!(attr as JNIName);
    let item2 = item.clone().into();
    let function: syn::ItemFn =
        syn::parse2(item2).expect("jni_wrap can only be applied to a function");

    let (func_args, param_prepare, call_args, param_cleanup): (
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
        Vec<proc_macro2::TokenStream>,
    ) = function
        .sig
        .inputs
        .iter()
        .enumerate()
        .map(|(i, ty)| setup_argument(i, ty))
        .unzip4();

    let func_name = &function.sig.ident;
    // Return type
    let return_type = match &function.sig.output {
        syn::ReturnType::Type(_, ty) => Option::Some(quote!(#ty).to_string()),
        _ => Option::None,
    };

    // Call expression: either a simple function call, or one which collects the result
    let call_expr = match &return_type {
        None => quote! { #func_name(#(#call_args, )*); },
        Some(_) => quote! { let result = #func_name(#(#call_args, )*); },
    };

    // Return expression: Return the result if present
    let (ret_expr, return_expr) = handle_return(&return_type);

    let original_function: proc_macro2::TokenStream = item.into();
    let fname = syn::Ident::new(
        &create_jni_fn_name(&namespace, &function.sig.ident.to_string(), &suffix),
        function.sig.ident.span(),
    );
    let q = quote! {
        #[no_mangle]
        pub extern fn #fname(mut _env: jni::EnvUnowned, _class: jni::objects::JClass, #(#func_args, )*) #ret_expr {
            _env.with_env(|_env| -> jni::errors::Result<_> {
                #(#param_prepare; )*
                #call_expr
                #(#param_cleanup; )*
                #return_expr
            }).resolve::<jni::errors::ThrowRuntimeExAndDefault>()
        }

        #original_function
    };
    TokenStream::from(q)
}

/// Create per-argument statements as a 4-tuple
/// 1: Function argument
/// 2: Mapping statement (Java type -> Rust type)
/// 3: Call expression
/// 4: Cleanup statement (for manually cleared types created during 2)
fn setup_argument(
    index: usize,
    ty: &FnArg,
) -> (
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
) {
    let ty = match ty {
        FnArg::Typed(pattern) => &pattern.ty,
        FnArg::Receiver(_) => todo!(),
    };
    let arg = Ident::new(&format!("arg{index}"), Span::call_site());
    let param = Ident::new(&format!("param{index}"), Span::call_site());
    let (arg_type, prepare, cleanup) = handle_arg(&arg, ty);
    let call_arg = if quote! { #ty }.to_string() == "* const c_char" {
        quote! { 
            #param
                .as_ref()
                .map_or(std::ptr::null(), |value| value.as_ptr())
         }
    } else {
        quote! { #param }
    };

    (
        // JNI Function argument: arg[index]: type
        quote! { #arg : #arg_type },
        // Preparation statement: let param[index] = arg[index] + conversion
        quote! { let #param = #prepare },
        // Call expression
        call_arg,
        // Cleanup statement as required by the preparation statement
        cleanup,
    )
}

/// Determines result type and mapping to the Java equivalent
/// result is (resultType, mapping)
fn handle_return(retn: &Option<String>) -> (proc_macro2::TokenStream, proc_macro2::TokenStream) {
    match retn {
        None => (quote! {}, quote! { Ok(()) }),
        Some(t) => match t.as_str() {
            "String" => (
                quote! { -> jni::sys::jstring },
                quote! { Ok(_env.new_string(result)?.into_raw()) },
            ),
            "CStr" => (
                quote! { -> jni::sys::jstring },
                quote! { Ok(_env.new_string(result.to_str().unwrap())?.into_raw()) },
            ),
            "* mut c_char" | "* const c_char" => (
                quote! { -> jni::sys::jstring },
                quote! {
                    if result.is_null() {
                        return Ok(std::ptr::null_mut());
                    }
                    let result = unsafe { CStr::from_ptr(result) };
                    Ok(_env.new_string(result.to_str().unwrap())?.into_raw())
                },
            ),
            "cef :: cef_string_userfree_t" => (
                quote! { -> jni::sys::jstring },
                quote! {
                    let str: String = chromium::utils::str_from_cef(result);
                    unsafe { cef::cef_string_userfree_utf16_free(result) };
                    Ok(_env.new_string(str)?.into_raw())
                },
            ),
            "c_int" | "i32" => (
                quote! { -> jni::sys::jint },
                quote! { Ok(result as jni::sys::jint) },
            ),
            _ => {
                // Catch all for pointers
                if t.starts_with("* mut ") || t.starts_with("* const ") {
                    (
                        quote! { -> jni::sys::jlong },
                        quote! { Ok(result as jni::sys::jlong) },
                    )
                } else {
                    panic!("Invalid return type {retn:?}");
                }
            }
        },
    }
}

/// Determines parameter type, mapping from the Java equivalent and cleanup for temporaries
/// result is (paramType, mapping, cleanup)
fn handle_arg(
    arg: &Ident,
    ty: &Type,
) -> (
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
    proc_macro2::TokenStream,
) {
    let tystr = quote! {#ty}.to_string();
    match tystr.as_str() {
        "i32" | "c_int" => (
            quote! { jni::sys::jint },
            quote! { #arg.try_into().unwrap() },
            quote! {},
        ),
        "i64" | "usize" => (
            quote! { jni::sys::jlong },
            quote! { #arg.try_into().unwrap() },
            quote! {},
        ),
        "f64" => (
            quote! { jni::sys::jdouble },
            quote! { #arg.try_into().unwrap() },
            quote! {},
        ),
        "bool" => (
            quote! { jni::sys::jboolean },
            quote! { #arg },
            quote! {},
        ),
        "* const c_char" => (
            quote! { jni::objects::JString },
            quote! { 
                if jni::refs::Reference::is_null(&#arg) {
                    None
                } else {
                    Some(std::ffi::CString::new(#arg.try_to_string(_env)?).unwrap())
                }
             },
            quote! {},
        ),
        "Option < Vec < u8 > >" => (
            quote! { jni::sys::jbyteArray },
            quote! {
                (|| -> jni::errors::Result<Vec<u8>> {
                    let array = unsafe { jni::objects::JByteArray::from_raw(_env, #arg) };
                    let length = array.len(_env)?;
                    let mut bytes = vec![0i8; length];
                    array.get_region(_env, 0, &mut bytes)?;
                    Ok(bytes.into_iter().map(|byte| byte as u8).collect())
                })()
                .ok()
            },
            quote! {},
        ),
        "Vec < u8 >" => (
            quote! { jni::sys::jbyteArray },
            quote! {
                {
                    let array = unsafe { jni::objects::JByteArray::from_raw(_env, #arg) };
                    let length = array.len(_env)?;
                    let mut bytes = vec![0i8; length];
                    array.get_region(_env, 0, &mut bytes)?;
                    bytes.into_iter().map(|byte| byte as u8).collect::<Vec<_>>()
                }
            },
            quote! {},
        ),
        _ => {
            // Catch all for other pointers and refs
            if tystr.starts_with("* mut ")
                || tystr.starts_with("& mut ")
                || tystr.starts_with("unsafe extern \"system\" fn")
            {
                (quote! { #ty }, quote! { #arg }, quote! {})
            } else {
                panic!("Invalid argument type {tystr:?}");
            }
        }
    }
}
