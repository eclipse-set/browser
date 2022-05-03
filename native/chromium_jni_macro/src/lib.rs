/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
extern crate proc_macro;

use proc_macro::TokenStream;
mod jni_wrap_impl;
mod derive_from_java_impl;

/// See jni_wrap_impl.rs
#[proc_macro]
pub fn jni_wrap(tokens: TokenStream) -> TokenStream {
    return jni_wrap_impl::jni_wrap_impl(tokens);
}

/// See derive_from_java_impl.rs
#[proc_macro_derive(FromJava)]
pub fn from_java_derive(tokens: TokenStream) -> TokenStream {
    return derive_from_java_impl::derive_from_java_impl(tokens);
}