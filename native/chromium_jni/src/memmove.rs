/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use chromium_jni_utils::FromJava;
use jni::objects::{JClass, JObject, JValue};
use jni::JNIEnv;

/// Implementation of memmove(dest, source, 1) for various CEF types
macro_rules! jni_memmove {
    ($name:ident, $type:ty) => {
        #[no_mangle]
        pub extern "C" fn $name(
            env: JNIEnv,
            _class: JClass,
            destination: *mut $type,
            source: JObject,
        ) {
            unsafe {
                std::ptr::copy(&FromJava::from_java(env, source), destination, 1);
            }
        }
    };
}

jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1cookie_1visitor_1t_2, chromium::cef::_cef_cookie_visitor_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1string_1visitor_1t_2, chromium::cef::_cef_string_visitor_t);

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__Lorg_eclipse_set_browser_lib_cef_1popup_1features_1t_2J(
    env: JNIEnv,
    _class: JClass,
    destination: JObject,
    source: *mut chromium::cef::_cef_popup_features_t,
) {
    let source_object = unsafe { *source };
    env.set_field(destination, "x", "I", JValue::Int(source_object.x))
        .unwrap();
    env.set_field(destination, "xSet", "I", JValue::Int(source_object.xSet))
        .unwrap();
    env.set_field(destination, "y", "I", JValue::Int(source_object.y))
        .unwrap();
    env.set_field(destination, "ySet", "I", JValue::Int(source_object.ySet))
        .unwrap();
    env.set_field(destination, "width", "I", JValue::Int(source_object.width))
        .unwrap();
    env.set_field(
        destination,
        "widthSet",
        "I",
        JValue::Int(source_object.widthSet),
    )
    .unwrap();
    env.set_field(
        destination,
        "height",
        "I",
        JValue::Int(source_object.height),
    )
    .unwrap();
    env.set_field(
        destination,
        "heightSet",
        "I",
        JValue::Int(source_object.heightSet),
    )
    .unwrap();
    env.set_field(
        destination,
        "menuBarVisible",
        "I",
        JValue::Int(source_object.menuBarVisible),
    )
    .unwrap();
    env.set_field(
        destination,
        "statusBarVisible",
        "I",
        JValue::Int(source_object.statusBarVisible),
    )
    .unwrap();
    env.set_field(
        destination,
        "toolBarVisible",
        "I",
        JValue::Int(source_object.toolBarVisible),
    )
    .unwrap();
    env.set_field(
        destination,
        "scrollbarsVisible",
        "I",
        JValue::Int(source_object.scrollbarsVisible),
    )
    .unwrap();
}
