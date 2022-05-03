/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use jni::sys::jint;
use jni::objects::JClass;
use jni::JNIEnv;

/// Implementation of sizeof(T) for various CEF types
/// returns the size of the type in bytes
macro_rules! jni_sizeof {
    ($name:ident, $type:ty) => {
        #[no_mangle]
        pub extern "C" fn $name(_env: JNIEnv, _class: JClass) -> jint {
            return std::mem::size_of::<$type>() as i32;
        }
    }
}

jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1app_1t_1sizeof, chromium::cef::_cef_app_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1browser_1process_1handler_1t_1sizeof, chromium::cef::_cef_browser_process_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1client_1t_1sizeof, chromium::cef::_cef_client_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1context_1menu_1handler_1t_1sizeof, chromium::cef::_cef_context_menu_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1cookie_1visitor_1t_1sizeof, chromium::cef::_cef_context_menu_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1display_1handler_1t_1sizeof, chromium::cef::_cef_display_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1focus_1handler_1t_1sizeof, chromium::cef::_cef_focus_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1jsdialog_1handler_1t_1sizeof, chromium::cef::_cef_jsdialog_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1life_1span_1handler_1t_1sizeof, chromium::cef::_cef_life_span_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1load_1handler_1t_1sizeof, chromium::cef::_cef_load_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1popup_1features_1t_1sizeof, chromium::cef::_cef_popup_features_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1request_1handler_1t_1sizeof, chromium::cef::_cef_request_handler_t);
jni_sizeof!(Java_org_eclipse_set_browser_lib_ChromiumLib_cef_1string_1visitor_1t_1sizeof, chromium::cef::_cef_string_visitor_t);
