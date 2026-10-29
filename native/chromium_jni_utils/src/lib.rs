/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use jni::objects::Global;
use jni::objects::JObject;
use jni::objects::JValueOwned;
use jni::strings::JNIString;
use jni::Env;
use jni::JavaVM;

/// Wrapper type for a CEF ref counted struct, which includes a
/// reference to the associated Java object
///
/// IMPROVE: Can we limit T to containing a CEF ref counter (cef_base_ref_counted_t)?
#[repr(C)]
pub struct JNIWrapperType<T> {
    pub value: T,
    pub this: Global<JObject<'static>>,
    pub jvm: JavaVM,
}

pub fn jni_unwrap<T>(ptr: *mut T) -> *mut JNIWrapperType<T> {
    ptr as *mut JNIWrapperType<T>
}

pub trait JNICEFCallback {
    fn jni_allocate(
        env: &mut Env,
        object: Global<JObject<'static>>,
    ) -> JNIWrapperType<Self>
    where
        Self: Sized;
}

/// Allows extracting an object from a Java field
pub trait FromJavaMember {
    /// Constructs `Self` from the field `name` in the JNI object `object`
    fn from_java_member(env: &mut Env, object: &JObject, name: &str) -> Self;
}

/// Allows constructing an object from a Java object
pub trait FromJava {
    /// Constructs `Self` from the JNI object `object`
    fn from_java(env: &mut Env, object: JObject) -> Self;
}

/// Allows constructing an object from a Java object
pub trait FromJavaValue {
    /// Constructs `Self` from the JNI object `object`
    fn from_java_value(env: &mut Env, object: JValueOwned) -> Self;
}

impl FromJavaValue for i32 {
    fn from_java_value(_env: &mut Env, object: JValueOwned) -> Self {
        match object {
            JValueOwned::Int(i) => i,
            JValueOwned::Byte(b) => b.into(),
            JValueOwned::Short(b) => b.into(),
            JValueOwned::Bool(b) => b.into(),
            _ => panic!("Wrong type for from_java_value"),
        }
    }
}

impl FromJavaValue for i64 {
    fn from_java_value(_env: &mut Env, object: JValueOwned) -> Self {
        match object {
            JValueOwned::Long(l) => l,
            JValueOwned::Int(i) => i.into(),
            JValueOwned::Byte(b) => b.into(),
            JValueOwned::Short(b) => b.into(),
            JValueOwned::Bool(b) => b.into(),
            _ => panic!("Wrong type for from_java_value"),
        }
    }
}

impl<T> FromJavaValue for *mut T {
    fn from_java_value(_env: &mut Env, object: JValueOwned) -> Self {
        object.j().unwrap() as *mut T
    }
}

impl FromJavaMember for usize {
    fn from_java_member(env: &mut Env, object: &JObject, name: &str) -> usize {
        get_int_field(env, object, name).unwrap() as usize
    }
}

fn get_int_field(env: &mut Env, object: &JObject, name: &str) -> jni::errors::Result<i32> {
    let name = JNIString::new(name);
    env.get_field(object, &name, jni::jni_sig!("I"))
        .and_then(|value| value.i())
}

fn get_long_field(env: &mut Env, object: &JObject, name: &str) -> jni::errors::Result<i64> {
    let name = JNIString::new(name);
    env.get_field(object, &name, jni::jni_sig!("J"))
        .and_then(|value| value.j())
}

// A list of implementations for function pointers for functions of various arity follows
// IMPROVE: Is there a better way to generalize across an arbitrary number of function args
impl<T, U> FromJavaMember for Option<unsafe extern "C" fn(T) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V> FromJavaMember for Option<unsafe extern "C" fn(T, V) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W> FromJavaMember for Option<unsafe extern "C" fn(T, V, W) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S) -> U> {
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B, C> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B, C, D> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B, C, D, E> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B, C, D, E, F> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}

impl<T, U, V, W, Q, R, S, A, B, C, D, E, F, G> FromJavaMember
    for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F, G) -> U>
{
    fn from_java_member(
        env: &mut Env,
        object: &JObject,
        name: &str,
    ) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F, G) -> U> {
        let field_value = get_long_field(env, object, name)
            .map(|v| v as *const ());
        match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None,
        }
    }
}
