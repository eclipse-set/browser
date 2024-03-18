/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use chromium_jni_macro::jni_name;
use chromium_jni_utils::JNICEFCallback;
use chromium_jni_utils::JNIWrapperType;
use jni::objects::JClass;
use jni::objects::JObject;
use jni::sys::jlong;
use jni::JNIEnv;

/// Implements a JNI function allocate_[typename] which allocates a type on the heap and returns its
/// address. Must be freed manually (via jni_deallocate)!
macro_rules! jni_allocate {
    ($name:tt, $type:ty) => {
        #[jni_name($name, $type)]
        pub extern "C" fn allocate(env: JNIEnv, _class: JClass, object: JObject) -> jlong {
            let object: JNIWrapperType<$type> =
                JNICEFCallback::jni_allocate(env, env.new_global_ref(object).unwrap());
            return Box::into_raw(Box::new(object)) as jlong;
        }
    };
}

/// Implements a JNI function deallocate_[typename] which deallocates a type on the heap
macro_rules! jni_deallocate {
    ($name:tt, $type:ty) => {
        #[jni_name($name, $type)]
        pub extern "C" fn deallocate(
            _env: JNIEnv,
            _class: JClass,
            object: *mut chromium::cef::_cef_display_handler_t,
        ) {
            unsafe { drop(Box::from_raw(object)) };
        }
    };
}

/// Implements both jni_allocate and jni_deallocate for a given type
macro_rules! jni_structure {
    ($type:ty) => {
        jni_allocate!("org.eclipse.set.browser.lib.ChromiumLib", $type);
        jni_deallocate!("org.eclipse.set.browser.lib.ChromiumLib", $type);
    };
}

jni_structure!(chromium::cef::_cef_client_t);
jni_structure!(chromium::cef::_cef_app_t);
jni_structure!(chromium::cef::_cef_context_menu_handler_t);
jni_structure!(chromium::cef::_cef_browser_process_handler_t);
jni_structure!(chromium::cef::_cef_focus_handler_t);
jni_structure!(chromium::cef::_cef_jsdialog_handler_t);
jni_structure!(chromium::cef::_cef_life_span_handler_t);
jni_structure!(chromium::cef::_cef_load_handler_t);
jni_structure!(chromium::cef::_cef_request_handler_t);
jni_structure!(chromium::cef::_cef_display_handler_t);
jni_structure!(chromium::cef::_cef_download_handler_t);
jni_structure!(chromium::cef::_cef_resource_handler_t);
jni_structure!(chromium::cef::_cef_scheme_handler_factory_t);
jni_structure!(chromium::cef::_cef_cookie_visitor_t);
jni_structure!(chromium::cef::_cef_string_visitor_t);
