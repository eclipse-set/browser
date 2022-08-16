/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
pub mod cef;
pub mod utils;

use chromium_jni_utils::FromJava;
use chromium_jni_utils::FromJavaMember;
use jni::objects::JObject;
use jni::objects::JValue;
use jni::JNIEnv;

impl FromJavaMember for cef::cef_base_ref_counted_t {
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> cef::cef_base_ref_counted_t {
        let obj = env
            .get_field(
                object,
                name,
                "Lorg/eclipse/set/browser/lib/cef_base_ref_counted_t;",
            )
            .unwrap()
            .l()
            .unwrap();
        FromJava::from_java(env, obj)
    }
}

/// Allows constructing an object from a Java object
pub trait ToJava<'a> {
    /// Constructs `Self` from the JNI object `object`
    fn to_java(_env: JNIEnv<'a>, value: Self) -> JValue<'a>;
}

impl<'a, T> ToJava<'a> for *mut T {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Long(unsafe { std::mem::transmute(value) })
    }
}
impl<'a, T> ToJava<'a> for *const T {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Long(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for i32 {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(value)
    }
}

impl<'a> ToJava<'a> for usize {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Long(value.try_into().unwrap())
    }
}
impl<'a> ToJava<'a> for i64 {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Long(value)
    }
}
impl<'a> ToJava<'a> for f64 {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Double(value)
    }
}

impl<'a> ToJava<'a> for cef::cef_process_id_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}
impl<'a> ToJava<'a> for cef::cef_cursor_type_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}
impl<'a> ToJava<'a> for cef::cef_errorcode_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}
impl<'a> ToJava<'a> for cef::cef_transition_type_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_window_open_disposition_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_log_severity_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_event_flags_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_termination_status_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_focus_source_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_jsdialog_type_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}

impl<'a> ToJava<'a> for cef::cef_quick_menu_edit_state_flags_t {
    fn to_java(_env: JNIEnv, value: Self) -> JValue<'a> {
        JValue::Int(unsafe { std::mem::transmute(value) })
    }
}
