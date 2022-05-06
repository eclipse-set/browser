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
            source: JObject
        ) {
            unsafe {
                std::ptr::copy(&FromJava::from_java(env, source), destination, 1);
            }
        }
    };
}

jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1app_1t_2, chromium::cef::_cef_app_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1client_1t_2, chromium::cef::_cef_client_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1context_1menu_1handler_1t_2, chromium::cef::_cef_context_menu_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1cookie_1visitor_1t_2, chromium::cef::_cef_cookie_visitor_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1browser_1process_1handler_1t_2, chromium::cef::_cef_browser_process_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1display_1handler_1t_2, chromium::cef::_cef_display_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1focus_1handler_1t_2, chromium::cef::_cef_focus_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1jsdialog_1handler_1t_2, chromium::cef::_cef_jsdialog_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1life_1span_1handler_1t_2, chromium::cef::_cef_life_span_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1load_1handler_1t_2, chromium::cef::_cef_load_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1request_1handler_1t_2, chromium::cef::_cef_request_handler_t);
jni_memmove!(Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__JLorg_eclipse_set_browser_lib_cef_1string_1visitor_1t_2, chromium::cef::_cef_string_visitor_t);

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__Lorg_eclipse_set_browser_lib_cef_1popup_1features_1t_2JI(
    env: JNIEnv,
    _class: JClass,
    destination: JObject,
    source: *mut chromium::cef::_cef_popup_features_t
) {
    let source_object = unsafe { *source };
    env.set_field(
        destination,
        "x",
        "I",
        JValue::Int(source_object.x.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "xSet",
        "I",
        JValue::Int(source_object.xSet.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "y",
        "I",
        JValue::Int(source_object.y.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "ySet",
        "I",
        JValue::Int(source_object.ySet.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "width",
        "I",
        JValue::Int(source_object.width.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "widthSet",
        "I",
        JValue::Int(source_object.widthSet.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "height",
        "I",
        JValue::Int(source_object.height.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "heightSet",
        "I",
        JValue::Int(source_object.heightSet.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "menuBarVisible",
        "I",
        JValue::Int(source_object.menuBarVisible.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "statusBarVisible",
        "I",
        JValue::Int(source_object.statusBarVisible.try_into().unwrap()),
    )
    .unwrap();
    env.set_field(
        destination,
        "toolBarVisible",
        "I",
        JValue::Int(source_object.toolBarVisible.try_into().unwrap()),
    )
    .unwrap();    env.set_field(
        destination,
        "scrollbarsVisible",
        "I",
        JValue::Int(source_object.scrollbarsVisible.try_into().unwrap()),
    )
    .unwrap();
}
