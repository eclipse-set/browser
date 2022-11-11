/**
 * Copyright (c) 2022 DB Netz AG and others.
 * Copyright (c) 2020 Equo
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Guillermo Zunino, Equo - initial implementation
 */
use std::os::raw::c_char;

use chromium_jni_macro::jni_wrapper;

#[jni_wrapper("org.eclipse.set.browser.lib.cef_response_t")]
pub fn cefswt_response_set_mime_type(
    response: *mut chromium::cef::cef_response_t,
    mime: *const c_char,
) {
    unsafe {
        let mime = chromium::utils::str_from_c(mime);
        let mime = chromium::utils::cef_string(mime);
        (*response).set_mime_type.unwrap()(response, &mime);
    }
}

#[jni_wrapper("org.eclipse.set.browser.lib.cef_response_t")]
pub fn cefswt_response_set_status_code(response: *mut chromium::cef::cef_response_t, status: i32) {
    unsafe {
        (*response).set_status.unwrap()(response, status);
    }
}

#[jni_wrapper("org.eclipse.set.browser.lib.cef_response_t")]
pub fn cefswt_response_set_header(
    response: *mut chromium::cef::cef_response_t,
    name: *const c_char,
    value: *const c_char,
) {
    unsafe {
        let name = chromium::utils::str_from_c(name);
        let name = chromium::utils::cef_string(name);

        let value = chromium::utils::str_from_c(value);
        let value = chromium::utils::cef_string(value);

        (*response).set_header_by_name.unwrap()(response, &name, &value, 1);
    }
}
