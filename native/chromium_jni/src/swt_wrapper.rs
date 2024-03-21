/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use jni::objects::{JClass, JObject, JValue};
use jni::sys::jlong;
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__Lorg_eclipse_set_browser_lib_cef_1popup_1features_1t_2J(
    mut env: JNIEnv,
    _class: JClass,
    destination: &JObject,
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
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1function_1id(
    mut _env: JNIEnv,
    _class: JClass,
    arg0: jlong,
    arg1: &JObject,
) {
    // IMPROVE: Avoid creating this object here
    let mut func_st = chromium_swt::FunctionSt {
        args: 0,
        id: 0,
        port: 0,
    };
    unsafe {
        chromium_swt::cefswt_function_id(
            arg0 as *mut chromium::cef::_cef_process_message_t,
            &mut func_st,
        );
    }
    _env.set_field(
        arg1,
        "args",
        "I",
        JValue::Int(func_st.args.try_into().unwrap()),
    )
    .unwrap();
    _env.set_field(arg1, "id", "I", JValue::Int(func_st.id))
        .unwrap();
    _env.set_field(arg1, "port", "I", JValue::Int(func_st.port))
        .unwrap();
}
