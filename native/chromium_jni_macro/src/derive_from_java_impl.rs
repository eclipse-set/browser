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
use quote::quote;
use syn::{
    parse_macro_input, punctuated::Punctuated, token::Comma, Data, DataStruct, DeriveInput,
    Field, Fields,
};

/// Implementation for derive(FromJava)
///
/// Creates an implementation which constructs the type by invoking
/// `FromJavaMember::from_java_member` on each struct field
pub fn derive_from_java_impl(tokens: TokenStream) -> TokenStream {
    let input = parse_macro_input!(tokens as DeriveInput);

    let name = input.ident;

    // Collect named fields of the struct
    let fields = match input.data {
        Data::Struct(DataStruct {
            fields: Fields::Named(fields),
            ..
        }) => fields.named,
        _ => panic!("only named fields are allowed"),
    };

    // For each field, build the initialization call
    let query_parts = build_initialization(&fields);

    let modified = quote! {
        impl FromJava for #name {
            fn from_java(env: JNIEnv, object: JObject) -> #name {
                return #name {
                    #(#query_parts, )*
                };
            }
        }
    };
    TokenStream::from(modified)
}

/// For a field `x` construct `x: FromJavaMember::from_java_member(env, object, "x")`
fn build_initialization(
    fields: &Punctuated<Field, Comma>,
) -> impl Iterator<Item = proc_macro2::TokenStream> + '_ {
    let fields = fields.iter().enumerate().map(move |(_i, field)| {
        let field_ident = field.ident.as_ref().unwrap();
        quote! { #field_ident: FromJavaMember::from_java_member(env, object, stringify!(#field_ident)) }
    });
    fields
}
