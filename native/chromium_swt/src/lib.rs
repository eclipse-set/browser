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
extern crate chromium;
use chromium::cef;

mod app;

use std::os::raw::{c_char, c_int, c_void};

pub fn cefswt_init(
    japp: *mut cef::cef_app_t,
    cefrust_path: *const c_char,
    cef_path: *const c_char,
    version: *const c_char,
    debug_port: c_int,
) {
    assert_eq!(
        unsafe { (*japp).base.size },
        std::mem::size_of::<cef::_cef_app_t>()
    );

    let cefrust_path = chromium_subp::utils::str_from_c(cefrust_path);
    let cef_path = chromium_subp::utils::str_from_c(cef_path);
    let version = chromium_subp::utils::str_from_c(version);

    let main_args = chromium_subp::utils::prepare_args();

    let cefrust_dir = std::path::Path::new(&cefrust_path);
    let cef_dir = std::path::Path::new(&cef_path);

    let subp = chromium_subp::utils::subp_path(cefrust_dir, version);
    let subp_cef = chromium_subp::utils::cef_string(&subp);

    if cef_path != cefrust_path {
        set_env_var(cef_path, "PATH", ";");
    }

    let resources_cef = chromium_subp::utils::cef_string(cef_dir.to_str().unwrap());
    let locales_cef = chromium_subp::utils::cef_string(cef_dir.join("locales").to_str().unwrap());
    let framework_dir_cef = chromium_subp::utils::cef_string_empty();
    let cache_dir_cef = chromium_subp::utils::cef_string(
        cef_dir
            .parent()
            .unwrap()
            .parent()
            .unwrap()
            .join("cef_cache")
            .to_str()
            .unwrap(),
    );
    let logfile_cef = chromium_subp::utils::cef_string(cef_dir.join("lib.log").to_str().unwrap());

    let settings = cef::_cef_settings_t {
        size: std::mem::size_of::<cef::_cef_settings_t>(),
        no_sandbox: 1,
        browser_subprocess_path: subp_cef,
        framework_dir_path: framework_dir_cef,
        multi_threaded_message_loop: 0,
        external_message_pump: 1,
        windowless_rendering_enabled: 0,
        command_line_args_disabled: 0,
        cache_path: cache_dir_cef,
        root_cache_path: cache_dir_cef,
        user_data_path: chromium_subp::utils::cef_string_empty(),
        persist_session_cookies: 1,
        persist_user_preferences: 1,
        user_agent: chromium_subp::utils::cef_string_empty(),
        locale: chromium_subp::utils::cef_string_empty(),
        log_file: logfile_cef,
        log_severity: cef::cef_log_severity_t::LOGSEVERITY_INFO,
        javascript_flags: chromium_subp::utils::cef_string_empty(),
        resources_dir_path: resources_cef,
        locales_dir_path: locales_cef,
        pack_loading_disabled: 0,
        remote_debugging_port: debug_port,
        uncaught_exception_stack_size: 0,
        background_color: 0,
        accept_language_list: chromium_subp::utils::cef_string_empty(),
        main_bundle_path: chromium_subp::utils::cef_string_empty(),
        chrome_runtime: 0,
        user_agent_product: chromium_subp::utils::cef_string_empty(),
        cookieable_schemes_list: chromium_subp::utils::cef_string_empty(),
        cookieable_schemes_exclude_defaults: 0,

    };

    do_initialize(main_args, settings, japp);
}

fn set_env_var(cef_path: &str, var: &str, sep: &str) {
    match std::env::var(var) {
        Ok(paths) => {
            let paths = format!("{}{}{}", cef_path, sep, paths);
            println!("{}: {}", var, paths);
            std::env::set_var(var, paths);
        }
        Err(e) => {
            println!("Couldn't read {} ({})", var, e);
            std::env::set_var(var, cef_path);
        }
    };
}

fn do_initialize(
    main_args: cef::_cef_main_args_t,
    settings: cef::_cef_settings_t,
    app_raw: *mut cef::_cef_app_t,
) {
    unsafe { cef::cef_enable_highdpi_support() };
    unsafe { cef::cef_initialize(&main_args, &settings, &mut (*app_raw), std::ptr::null_mut()) };
}

pub fn cefswt_create_browser(
    hwnd: *mut c_void,
    url: *const c_char,
    client: &mut cef::_cef_client_t,
    w: c_int,
    h: c_int,
    js: c_int,
    cbg: c_int,
) -> *const cef::cef_browser_t {
    let bg: cef::cef_color_t = cbg as cef::cef_color_t;
    assert_eq!(
        (*client).base.size,
        std::mem::size_of::<cef::_cef_client_t>()
    );
    let url = chromium_subp::utils::str_from_c(url);
    let browser = app::create_browser(hwnd, url, client, w, h, js, bg);
    browser
}

pub fn cefswt_set_window_info_parent(
    window_info: *mut cef::_cef_window_info_t,
    client: *mut *mut cef::_cef_client_t,
    jclient: *mut cef::_cef_client_t,
    hwnd: *mut c_void,
    x: c_int,
    y: c_int,
    w: c_int,
    h: c_int,
) {
    unsafe {
        (*client) = jclient;
        app::set_window_parent(window_info, hwnd, x, y, w, h);
    };
}

pub fn cefswt_do_message_loop_work() -> c_int {
    let result = std::panic::catch_unwind(|| {
        unsafe { cef::cef_do_message_loop_work() };
    });
    match result {
        Ok(_) => 1,
        Err(_) => 0,
    }
}

pub fn cefswt_free(obj: *mut cef::cef_browser_t) {
    unsafe {
        assert_eq!((*obj).base.size, std::mem::size_of::<cef::_cef_browser_t>());
    }
}

pub fn cefswt_get_id(browser: *mut cef::cef_browser_t) -> c_int {
    unsafe {
        let get_id = (*browser).get_identifier.unwrap();
        get_id(browser)
    }
}

pub fn cefswt_resized(browser: *mut cef::cef_browser_t, width: i32, height: i32) {
    let browser_host = get_browser_host(browser);
    let get_window_handle_fn = unsafe {
        (*browser_host)
            .get_window_handle
            .expect("no get_window_handle")
    };
    let win_handle = unsafe { get_window_handle_fn(browser_host) };
    do_resize(win_handle, width, height);
}

fn do_resize(win_handle: *mut c_void, width: i32, height: i32) {
    let x = 0;
    let y = 0;
    unsafe {
        winapi::um::winuser::SetWindowPos(
            win_handle as winapi::shared::windef::HWND,
            std::ptr::null_mut(),
            x,
            y,
            width,
            height,
            winapi::um::winuser::SWP_NOZORDER,
        )
    };
}

pub fn cefswt_close_browser(browser: *mut cef::cef_browser_t, force: c_int) {
    let browser_host = get_browser_host(browser);
    let close_fn = unsafe { (*browser_host).close_browser.expect("null close_browser") };
    unsafe { close_fn(browser_host, force) };
}

pub fn cefswt_load_url(
    browser: *mut cef::cef_browser_t,
    url: *const c_char,
    post_bytes: *const c_void,
    post_size: usize,
    headers: *const c_char,
    headers_size: usize,
) {
    let url = chromium_subp::utils::str_from_c(url);
    let url_cef = chromium_subp::utils::cef_string(url);
    unsafe {
        let get_frame = (*browser).get_main_frame.expect("null get_main_frame");
        let main_frame = get_frame(browser);
        if post_bytes.is_null() && headers.is_null() {
            (*main_frame).load_url.unwrap()(main_frame, &url_cef);
        } else {
            let request = cef::cef_request_create();
            (*request).set_url.unwrap()(request, &url_cef);
            if !post_bytes.is_null() {
                let post_data = cef::cef_post_data_create();
                let post_element = cef::cef_post_data_element_create();
                (*post_element).set_to_bytes.unwrap()(post_element, post_size, post_bytes);
                (*post_data).add_element.unwrap()(post_data, post_element);

                (*request).set_post_data.unwrap()(request, post_data);
            }

            if !headers.is_null() {
                let map = cef::cef_string_multimap_alloc();

                let headers = chromium_subp::utils::str_from_c(headers);
                let headers: Vec<&str> = headers.splitn(headers_size, "::").collect();
                for i in 0..headers_size {
                    let header_str = headers[i];
                    let header: Vec<&str> = header_str.splitn(2, ':').collect();
                    let key = header[0].trim();
                    let value = header[1].trim();
                    let key = chromium_subp::utils::cef_string(key);
                    let value = chromium_subp::utils::cef_string(value);

                    cef::cef_string_multimap_append(map, &key, &value);
                }
                (*request).set_header_map.unwrap()(request, map);
            }

            (*main_frame).load_request.unwrap()(main_frame, request);
        }
    }
}

pub fn cefswt_get_url(browser: *mut cef::cef_browser_t) -> *mut c_char {
    let get_frame = unsafe { (*browser).get_main_frame.expect("null get_main_frame") };
    let main_frame = unsafe { get_frame(browser) };
    assert!(!main_frame.is_null());
    let get_url = unsafe { (*main_frame).get_url.expect("null get_url") };
    let url = unsafe { get_url(main_frame) };
    chromium_subp::utils::cstr_from_cef(url)
}

pub fn cefswt_cefstring_to_java(cefstring: *mut cef::cef_string_t) -> *const c_char {
    return chromium_subp::utils::cstr_from_cef(cefstring);
}

pub fn cefswt_request_to_java(request: *mut cef::cef_request_t) -> *mut c_char {
    let url = unsafe { (*request).get_url.expect("null get_url")(request) };
    let cstr = chromium_subp::utils::cstr_from_cef(url);
    unsafe { cef::cef_string_userfree_utf16_free(url) };
    cstr
}

pub fn cefswt_cookie_to_java(cookie: *mut cef::_cef_cookie_t) -> *mut c_char {
    let name = unsafe { (*cookie).name };
    return chromium_subp::utils::cstr_from_cef(&name);
}

pub fn cefswt_stop(browser: *mut cef::cef_browser_t) {
    unsafe {
        (*browser).stop_load.expect("null stop_load")(browser);
    };
}

pub fn cefswt_reload(browser: *mut cef::cef_browser_t) {
    unsafe {
        (*browser).reload.expect("null reload")(browser);
    };
}

pub fn cefswt_get_text(browser: *mut cef::cef_browser_t, visitor: *mut cef::_cef_string_visitor_t) {
    assert_eq!(
        unsafe { (*visitor).base.size },
        std::mem::size_of::<cef::_cef_string_visitor_t>()
    );
    let get_frame = unsafe { (*browser).get_main_frame.expect("null get_main_frame") };
    let main_frame = unsafe { get_frame(browser) };
    assert!(!main_frame.is_null());
    let get_text = unsafe { (*main_frame).get_source.expect("null get_text") };
    unsafe { get_text(main_frame, visitor) };
}

pub fn cefswt_execute(browser: *mut cef::cef_browser_t, text: *const c_char) {
    let text = chromium_subp::utils::str_from_c(text);
    let text_cef = chromium_subp::utils::cef_string(text);
    let url_cef = chromium_subp::utils::cef_string_empty();
    let get_frame = unsafe { (*browser).get_main_frame.expect("null get_main_frame") };
    let main_frame = unsafe { get_frame(browser) };
    let execute = unsafe {
        (*main_frame)
            .execute_java_script
            .expect("null execute_java_script")
    };
    unsafe { execute(main_frame, &text_cef, &url_cef, 0) };
}

pub fn cefswt_eval(
    browser: *mut cef::cef_browser_t,
    text: *const c_char,
    id: i32,
    callback: unsafe extern "system" fn(work: c_int, kind: c_int, value: *const c_char),
) -> c_int {
    let text_cef = chromium_subp::utils::cef_string_from_c(text);
    let name = chromium_subp::utils::cef_string("eval");
    unsafe {
        let msg = cef::cef_process_message_create(&name);
        let args = (*msg).get_argument_list.unwrap()(msg);
        let s = (*args).set_int.unwrap()(args, 1, id);
        assert_eq!(s, 1);
        let s = (*args).set_string.unwrap()(args, 2, &text_cef);
        assert_eq!(s, 1);
        match chromium_subp::socket::wait_response(
            browser,
            msg,
            args,
            cef::cef_process_id_t::PID_RENDERER,
            Some(callback),
        ) {
            Ok(r) => {
                callback(0, r.kind as i32, r.str_value.as_ptr());
                1
            }
            Err(_e) => 0,
        }
    }
}

pub fn cefswt_function(browser: *mut cef::cef_browser_t, name: *const c_char, id: i32) {
    let name_cef = chromium_subp::utils::cef_string_from_c(name);
    let msg_name = chromium_subp::utils::cef_string("function");
    unsafe {
        let msg = cef::cef_process_message_create(&msg_name);
        let args = (*msg).get_argument_list.unwrap()(msg);
        let s = (*args).set_int.unwrap()(args, 0, id);
        assert_eq!(s, 1);
        let s = (*args).set_string.unwrap()(args, 1, &name_cef);
        assert_eq!(s, 1);
        let frame = (*browser).get_main_frame.unwrap()(browser);

        (*frame).send_process_message.unwrap()(
            frame,
            cef::cef_process_id_t::PID_RENDERER,
            msg,
        );
    }
}

#[repr(C)]
#[derive(Debug)]
pub struct FunctionSt {
    pub id: i32,
    pub port: i32,
    pub args: usize,
}

pub unsafe extern "C" fn cefswt_function_id(
    message: *mut cef::cef_process_message_t,
    st: *mut FunctionSt,
) {
    let valid = (*message).is_valid.unwrap()(message);
    let name = (*message).get_name.unwrap()(message);
    (*st).id = -1;
    (*st).args = 0;
    (*st).port = 0;
    if valid == 1
        && cef::cef_string_utf16_cmp(&chromium_subp::utils::cef_string("function_call"), name) == 0
    {
        let args = (*message).get_argument_list.unwrap()(message);
        let args_len = (*args).get_size.unwrap()(args);
        let port = (*args).get_int.unwrap()(args, 0);
        (*st).id = (*args).get_int.unwrap()(args, 1);
        (*st).args = (args_len - 1) / 2;
        (*st).port = port;
    }
}

pub fn cefswt_function_arg(
    message: *mut cef::cef_process_message_t,
    index: i32,
    callback: unsafe extern "system" fn(work: c_int, kind: c_int, value: *const c_char),
) -> c_int {
    unsafe {
        let args = (*message).get_argument_list.unwrap()(message);
        let kind = (*args).get_int.unwrap()(args, (1 + index * 2 + 1) as usize);
        let arg = (*args).get_string.unwrap()(args, (1 + index * 2 + 2) as usize);
        let cstr = chromium_subp::utils::cstr_from_cef(arg);
        let kind = chromium_subp::socket::ReturnType::from(kind);
        callback(0, kind as i32, cstr);
        1
    }
}

pub fn cefswt_function_return(
    _browser: *mut cef::cef_browser_t,
    _id: i32,
    port: i32,
    kind: i32,
    ret: *const c_char,
) -> c_int {
    let cstr = unsafe { std::ffi::CStr::from_ptr(ret) };
    return chromium_subp::socket::socket_client(port as u16, cstr.to_owned(), unsafe {
        std::mem::transmute(kind)
    });
}

pub fn cefswt_set_focus(browser: *mut cef::cef_browser_t, set: bool, parent: *mut c_void) {
    let browser_host = get_browser_host(browser);
    let focus_fn = unsafe { (*browser_host).set_focus.expect("null set_focus") };
    let focus = if set { 1 } else { 0 };
    unsafe { focus_fn(browser_host, focus) };
    if !set && !parent.is_null() {
        do_set_focus(parent, focus);
    }
}

fn do_set_focus(_parent: *mut c_void, _focus: i32) {
    // TODO
}

pub fn cefswt_is_same(browser: *mut cef::cef_browser_t, that: *mut cef::cef_browser_t) -> c_int {
    unsafe { (*browser).is_same.unwrap()(browser, that) }
}

pub fn cefswt_dialog_close(
    callback: *mut cef::_cef_jsdialog_callback_t,
    success: c_int,
    prompt: *mut cef::cef_string_t,
) {
    unsafe { (*callback).cont.unwrap()(callback, success, prompt) };
}

pub fn cefswt_context_menu_cancel(callback: *mut cef::_cef_run_context_menu_callback_t) {
    unsafe { (*callback).cancel.unwrap()(callback) };
}

pub fn cefswt_auth_callback(
    callback: *mut cef::_cef_auth_callback_t,
    juser: *const c_char,
    jpass: *const c_char,
    cont: c_int,
) {
    unsafe {
        if cont == 1 {
            let user = chromium_subp::utils::cef_string_from_c(juser);
            let pass = chromium_subp::utils::cef_string_from_c(jpass);
            (*callback).cont.unwrap()(callback, &user, &pass)
        } else {
            (*callback).cancel.unwrap()(callback)
        }
    };
}

pub fn cefswt_set_cookie(
    jurl: *const c_char,
    jname: *const c_char,
    jvalue: *const c_char,
    jdomain: *const c_char,
    jpath: *const c_char,
    secure: i32,
    httponly: i32,
    max_age: f64,
) -> c_int {
    let manager = unsafe { cef::cef_cookie_manager_get_global_manager(std::ptr::null_mut()) };
    let url = chromium_subp::utils::cef_string_from_c(jurl);
    let domain = chromium_subp::utils::cef_string_from_c(jdomain);
    let path = chromium_subp::utils::cef_string_from_c(jpath);
    let name = chromium_subp::utils::cef_string_from_c(jname);
    let value = chromium_subp::utils::cef_string_from_c(jvalue);
    let has_expires = if max_age == -1.0 { 0 } else { 1 };
    let mut expires = cef::cef_time_t {
        year: 0,
        month: 0,
        day_of_week: 0,
        day_of_month: 0,
        hour: 0,
        minute: 0,
        second: 0,
        millisecond: 0,
    };

    if max_age == -1.0 {
        unsafe { cef::cef_time_from_doublet(max_age, &mut expires) };
    }

    let cookie = cef::_cef_cookie_t {
        name,
        value,
        domain,
        path,
        secure,
        httponly,
        has_expires,
        expires,
        creation: expires,
        last_access: expires,
        same_site: cef::cef_cookie_same_site_t::CEF_COOKIE_SAME_SITE_NO_RESTRICTION,
        priority: cef::cef_cookie_priority_t::CEF_COOKIE_PRIORITY_MEDIUM,
    };
    unsafe {
        (*manager).set_cookie.expect("null set_cookie")(
            manager,
            &url,
            &cookie,
            std::ptr::null_mut(),
        )
    }
}

pub fn cefswt_get_cookie(jurl: *const c_char, jvisitor: *mut cef::_cef_cookie_visitor_t) -> c_int {
    let manager = unsafe { cef::cef_cookie_manager_get_global_manager(std::ptr::null_mut()) };
    let url = chromium_subp::utils::cef_string_from_c(jurl);

    unsafe {
        (*manager)
            .visit_url_cookies
            .expect("null visit_url_cookies")(manager, &url, 1, jvisitor)
    }
}

pub fn cefswt_cookie_value(cookie: *mut cef::_cef_cookie_t) -> *mut c_char {
    unsafe { chromium_subp::utils::cstr_from_cef(&mut (*cookie).value) }
}

pub fn cefswt_delete_cookies() {
    let manager = unsafe { cef::cef_cookie_manager_get_global_manager(std::ptr::null_mut()) };
    unsafe {
        (*manager).delete_cookies.expect("null delete_cookies")(
            manager,
            std::ptr::null_mut(),
            std::ptr::null_mut(),
            std::ptr::null_mut(),
        )
    };
}

pub fn cefswt_shutdown() {
    // Shut down CEF.
    unsafe { cef::cef_shutdown() };
}

fn get_browser_host(browser: *mut cef::cef_browser_t) -> *mut cef::_cef_browser_host_t {
    let get_host_fn = unsafe { (*browser).get_host.expect("null get_host") };
    let browser_host = unsafe { get_host_fn(browser) };
    browser_host
}

pub fn cefswt_is_main_frame(frame: *mut cef::_cef_frame_t) -> i32 {
    unsafe { (*frame).is_main.expect("null is_main")(frame) }
}

pub fn cefswt_go_forward(browser: *mut cef::_cef_browser_t) {
    unsafe { (*browser).go_forward.expect("null go_forward")(browser) };
}

pub fn cefswt_go_back(browser: *mut cef::_cef_browser_t) {
    unsafe { (*browser).go_back.expect("null go_back")(browser) };
}
