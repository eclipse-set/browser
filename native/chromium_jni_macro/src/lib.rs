extern crate proc_macro;
use proc_macro::TokenStream;
mod jni_wrap_impl;
mod derive_from_java_impl;

use crate::jni_wrap_impl::jni_wrap_impl;
use crate::derive_from_java_impl::derive_from_java_impl;

#[proc_macro]
pub fn jni_wrap(tokens: TokenStream) -> TokenStream {
    return jni_wrap_impl(tokens);
}


#[proc_macro_derive(FromJava)]
pub fn from_java_derive(tokens: TokenStream) -> TokenStream {
    return derive_from_java_impl(tokens);
}