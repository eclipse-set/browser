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
mod derive_from_java_impl;
mod jni_allocate;
mod jni_name;
mod jni_wrap_impl;
mod utils;

/// See derive_from_java_impl.rs
#[proc_macro_derive(FromJava)]
pub fn from_java_derive(tokens: TokenStream) -> TokenStream {
    derive_from_java_impl::derive_from_java_impl(tokens)
}

/// See jni_allocate.rs
#[proc_macro_derive(JNICEFCallback)]
pub fn jni_allocate(tokens: TokenStream) -> TokenStream {
    jni_allocate::jni_allocate(tokens)
}

#[proc_macro_attribute]
pub fn jni_name(attr: TokenStream, item: TokenStream) -> TokenStream {
    jni_name::jni_name(attr, item)
}

#[proc_macro_attribute]
pub fn jni_wrapper(attr: TokenStream, item: TokenStream) -> TokenStream {
    jni_wrap_impl::jni_wrap_attr_impl(attr, item)
}
