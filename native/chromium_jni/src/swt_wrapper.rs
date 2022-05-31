/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use chromium_jni_macro::jni_wrap;
use chromium_swt::{
    cefswt_auth_callback, cefswt_close_browser, cefswt_context_menu_cancel, cefswt_create_browser,
    cefswt_delete_cookies, cefswt_dialog_close, cefswt_do_message_loop_work, cefswt_eval,
    cefswt_execute, cefswt_free, cefswt_function, cefswt_function_arg, cefswt_function_return,
    cefswt_get_cookie, cefswt_get_id, cefswt_get_text, cefswt_get_url, cefswt_go_back,
    cefswt_go_forward, cefswt_init, cefswt_is_main_frame, cefswt_is_same, cefswt_load_url,
    cefswt_reload, cefswt_resized, cefswt_set_cookie, cefswt_set_focus,
    cefswt_set_window_info_parent, cefswt_shutdown, cefswt_stop,
};
use jni::objects::{JClass, JObject, JString, JValue, ReleaseMode};
use jni::sys::jboolean;
use jni::sys::jbyteArray;
use jni::sys::jdouble;
use jni::sys::jint;
use jni::sys::jlong;
use jni::sys::jstring;
use jni::JNIEnv;
use std::ffi::CStr;
use std::os::raw::c_char;
use std::os::raw::c_int;
use std::os::raw::c_void;

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1cstring_1to_1java(
    env: JNIEnv,
    _class: JClass,
    string: *const c_char,
) -> jstring {
    if std::ptr::null() == string {
        return std::ptr::null_mut();
    }
    let value = unsafe { CStr::from_ptr(string) };
    return env
        .new_string(value.to_str().unwrap())
        .unwrap()
        .into_inner();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1cefstring_1to_1java(
    env: JNIEnv,
    _class: JClass,
    string: *mut chromium::cef::_cef_string_utf16_t,
) -> jstring {
    if std::ptr::null() == string {
        return std::ptr::null_mut();
    }
    let value = unsafe { CStr::from_ptr(chromium_swt::cefswt_cefstring_to_java(string)) };
    return env
        .new_string(value.to_str().unwrap())
        .unwrap()
        .into_inner();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1request_1to_1java(
    env: JNIEnv,
    _class: JClass,
    request: *mut chromium::cef::_cef_request_t,
) -> jstring {
    let value = unsafe { CStr::from_ptr(chromium_swt::cefswt_request_to_java(request)) };
    return env
        .new_string(value.to_str().unwrap())
        .unwrap()
        .into_inner();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1cookie_1value(
    env: JNIEnv,
    _class: JClass,
    cookie: *mut chromium::cef::_cef_cookie_t,
) -> jstring {
    let value = unsafe { CStr::from_ptr(chromium_swt::cefswt_cookie_value(cookie)) };
    return env
        .new_string(value.to_str().unwrap())
        .unwrap()
        .into_inner();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1cookie_1to_1java(
    env: JNIEnv,
    _class: JClass,
    cookie: *mut chromium::cef::_cef_cookie_t,
) -> jstring {
    let value = unsafe { CStr::from_ptr(chromium_swt::cefswt_cookie_to_java(cookie)) };
    return env
        .new_string(value.to_str().unwrap())
        .unwrap()
        .into_inner();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1function_1id(
    _env: JNIEnv,
    _class: JClass,
    arg0: jlong,
    arg1: JObject,
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

// Auto generated wrappers for various chormium_swt functions
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_function_arg,
    *mut chromium::cef::_cef_process_message_t,
    jint,
    unsafe extern "system" fn(work: c_int, kind: c_int, value: *const c_char)
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_function_return,
    *mut chromium::cef::_cef_browser_t,
    jint,
    jint,
    jint,
    JString
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_load_url,
    *mut chromium::cef::_cef_browser_t,
    JString,
    jbyteArray,
    jint,
    JString,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_auth_callback,
    *mut chromium::cef::_cef_auth_callback_t,
    JString,
    JString,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_create_browser,
    return jlong,
    *mut c_void,
    JString,
    &mut chromium::cef::_cef_client_t,
    jint,
    jint,
    jint,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_eval,
    *mut chromium::cef::_cef_browser_t,
    JString,
    jint,
    unsafe extern "system" fn(work: c_int, kind: c_int, value: *const c_char)
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_function,
    *mut chromium::cef::_cef_browser_t,
    JString,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_get_cookie,
    return jboolean,
    JString,
    *mut chromium::cef::_cef_cookie_visitor_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_init,
    *mut chromium::cef::_cef_app_t,
    JString,
    JString,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_set_cookie,
    return jboolean,
    JString,
    JString,
    JString,
    JString,
    JString,
    jint,
    jint,
    jdouble
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_execute,
    *mut chromium::cef::_cef_browser_t,
    JString
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_set_window_info_parent,
    *mut chromium::cef::_cef_window_info_t,
    *mut *mut chromium::cef::_cef_client_t,
    *mut chromium::cef::_cef_client_t,
    *mut c_void,
    jint,
    jint,
    jint,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_is_main_frame,
    return jboolean,
    *mut chromium::cef::_cef_frame_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_get_id,
    return jint,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_get_text,
    *mut chromium::cef::_cef_browser_t,
    *mut chromium::cef::_cef_string_visitor_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_get_url,
    return jlong,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_go_back,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_go_forward,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_free,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_do_message_loop_work,
    return jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_close_browser,
    *mut chromium::cef::_cef_browser_t,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_context_menu_cancel,
    *mut chromium::cef::_cef_run_context_menu_callback_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_delete_cookies
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_dialog_close,
    *mut chromium::cef::_cef_jsdialog_callback_t,
    jint,
    *mut chromium::cef::_cef_string_utf16_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_is_same,
    return jboolean,
    *mut chromium::cef::_cef_browser_t,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_reload,
    *mut chromium::cef::_cef_browser_t
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_resized,
    *mut chromium::cef::_cef_browser_t,
    jint,
    jint
);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_set_focus,
    *mut chromium::cef::_cef_browser_t,
    jboolean,
    *mut c_void
);
jni_wrap!("org.eclipse.set.browser.lib.ChromiumLib", cefswt_shutdown);
jni_wrap!(
    "org.eclipse.set.browser.lib.ChromiumLib",
    cefswt_stop,
    *mut chromium::cef::_cef_browser_t
);
