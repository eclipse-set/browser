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
use chromium::cef;
use chromium_jni_macro::jni_wrapper;
use std::os::raw::c_char;

#[jni_wrapper("org.eclipse.set.browser.lib.cef_request_t")]
pub fn cefswt_request_get_header_by_name(
    request: *mut cef::cef_request_t,
    name: *const c_char,
) -> cef::cef_string_userfree_t {
    unsafe {
        let name = chromium::utils::str_from_c(name);
        let name = chromium::utils::cef_string(name);
        (*request).get_header_by_name.unwrap()(request, &name)
    }
}

#[jni_wrapper("org.eclipse.set.browser.lib.cef_request_t")]
pub fn cefswt_request_get_url(request: *mut cef::cef_request_t) -> cef::cef_string_userfree_t {
    unsafe { (*request).get_url.unwrap()(request) }
}

#[jni_wrapper("org.eclipse.set.browser.lib.cef_request_t")]
pub fn cefswt_request_get_method(request: *mut cef::cef_request_t) -> cef::cef_string_userfree_t {
    unsafe { (*request).get_url.unwrap()(request) }
}
