/********************************************************************************
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
 ********************************************************************************/
use crate::cef;
use std::ffi::CStr;
use std::os::raw::c_char;
extern crate winapi;

pub fn prepare_args() -> cef::_cef_main_args_t {
    let h_instance =
        unsafe { winapi::um::libloaderapi::GetModuleHandleA(0 as winapi::um::winnt::LPCSTR) };
    cef::_cef_main_args_t {
        instance: unsafe { ::std::mem::transmute(h_instance) },
    }
}

pub fn cef_string(value: &str) -> cef::cef_string_t {
    let mut str_cef = cef::cef_string_t {
        str_: ::std::ptr::null_mut(),
        length: 0,
        dtor: Option::Some(dtr),
    };
    unsafe {
        cef::cef_string_utf8_to_utf16(value.as_ptr() as *mut c_char, value.len(), &mut str_cef);
    }
    str_cef
}

pub fn cef_string_from_c(cstr: *const c_char) -> cef::cef_string_t {
    if cstr.is_null() {
        cef_string_empty()
    } else {
        cef_string(str_from_c(cstr))
    }
}

pub fn cef_string_empty() -> cef::cef_string_t {
    let mut empty_str = cef::cef_string_t {
        str_: ::std::ptr::null_mut(),
        length: 0,
        dtor: Option::Some(dtr),
    };

    let emp = "";
    unsafe {
        cef::cef_string_utf8_to_utf16(emp.as_ptr() as *mut c_char, 0, &mut empty_str);
    }

    empty_str
}

unsafe extern "C" fn dtr(_: *mut cef::char16) {}

pub fn str_from_c(cstr: *const c_char) -> &'static str {
    if cstr.is_null() {
        ""
    } else {
        let slice = unsafe { CStr::from_ptr(cstr) };
        ::std::str::from_utf8(slice.to_bytes()).expect("failed from_utf8")
    }
}

pub fn cstr_from_cef(cefstring: *const cef::cef_string_t) -> *mut c_char {
    if cefstring.is_null() || unsafe { (*cefstring).str_.is_null() } {
        return ::std::ptr::null_mut();
    }
    let utf8 = unsafe { cef::cef_string_userfree_utf8_alloc() };
    unsafe { cef::cef_string_utf16_to_utf8((*cefstring).str_, (*cefstring).length, utf8) };
    unsafe { (*utf8).str_ }
}

pub fn str_from_cef(cefstring: *const cef::cef_string_t) -> String {
    if cefstring.is_null() {
        return "".to_string();
    }
    let utf8 = unsafe { cef::cef_string_userfree_utf8_alloc() };
    unsafe { cef::cef_string_utf16_to_utf8((*cefstring).str_, (*cefstring).length, utf8) };
    let str = unsafe { CStr::from_ptr((*utf8).str_).to_str().unwrap().to_owned() };
    unsafe {
        cef::cef_string_userfree_utf8_free(utf8);
    }
    return str;
}
