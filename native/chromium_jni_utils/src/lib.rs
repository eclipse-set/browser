use jni::objects::{JClass, JObject, JString, JValue, ReleaseMode};
use jni::sys::jboolean;
use jni::sys::jbyteArray;
use jni::sys::jdouble;
use jni::sys::jint;
use jni::sys::jlong;
use std::os::raw::c_void;
use jni::JNIEnv;

pub trait FromJavaMember {
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Self;
}

pub trait FromJava {
    fn from_java(env: JNIEnv, object: JObject) -> Self;
}


impl<T, U> FromJavaMember for Option<unsafe extern "C" fn(T) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}

impl<T, U, V> FromJavaMember for Option<unsafe extern "C" fn(T, V) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W> FromJavaMember for Option<unsafe extern "C" fn(T, V, W) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}

impl<T, U, V, W, Q> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R, S> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R, S, A> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}



impl<T, U, V, W, Q, R, S, A, B> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}

impl<T, U, V, W, Q, R, S, A, B, C> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R, S, A, B, C, D> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R, S, A, B, C, D, E> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}


impl<T, U, V, W, Q, R, S, A, B, C, D, E, F> FromJavaMember for Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F) -> U>
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> Option<unsafe extern "C" fn(T, V, W, Q, R, S, A, B, C, D, E, F) -> U> {
        let field_value = env.get_field(object, name, "J").and_then(|v| v.j()).map(|v| v as *const());
        let pointer = match field_value {
            Ok(ptr) => Some(unsafe { std::mem::transmute(ptr) }),
            Err(_) => None
        };
        return pointer;
    }
}

impl FromJavaMember for usize
{
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> usize {
        return env.get_field(object, name, "I").and_then(|v| v.i()).unwrap() as usize;
    }   
}