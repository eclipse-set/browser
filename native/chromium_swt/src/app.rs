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
use crate::cef;
extern crate winapi;
use std::mem::size_of;
use std::os::raw::{c_int, c_void};
use std::ptr::null_mut;

pub fn create_browser(
    canvas_hwnd: *mut c_void,
    url: &str,
    jclient: &mut cef::_cef_client_t,
    w: c_int,
    h: c_int,
    js: c_int,
    bg: cef::cef_color_t,
) -> *mut cef::cef_browser_t {
    let window_info = cef_window_info(canvas_hwnd, w, h);
    // Browser settings.
    let browser_settings = cef::_cef_browser_settings_t {
        size: size_of::<cef::_cef_browser_settings_t>(),
        windowless_frame_rate: 0,
        standard_font_family: chromium::utils::cef_string_empty(),
        fixed_font_family: chromium::utils::cef_string_empty(),
        serif_font_family: chromium::utils::cef_string_empty(),
        sans_serif_font_family: chromium::utils::cef_string_empty(),
        cursive_font_family: chromium::utils::cef_string_empty(),
        fantasy_font_family: chromium::utils::cef_string_empty(),
        default_font_size: 0,
        default_fixed_font_size: 0,
        minimum_font_size: 0,
        minimum_logical_font_size: 0,
        default_encoding: chromium::utils::cef_string_empty(),
        remote_fonts: cef::cef_state_t::STATE_DEFAULT,
        javascript: if js == 0 {
            cef::cef_state_t::STATE_DISABLED
        } else {
            cef::cef_state_t::STATE_DEFAULT
        },
        javascript_close_windows: cef::cef_state_t::STATE_DEFAULT,
        javascript_access_clipboard: cef::cef_state_t::STATE_DEFAULT,
        javascript_dom_paste: cef::cef_state_t::STATE_DEFAULT,
        image_loading: cef::cef_state_t::STATE_DEFAULT,
        image_shrink_standalone_to_fit: cef::cef_state_t::STATE_DEFAULT,
        text_area_resize: cef::cef_state_t::STATE_DEFAULT,
        tab_to_links: cef::cef_state_t::STATE_DEFAULT,
        local_storage: cef::cef_state_t::STATE_DEFAULT,
        databases: cef::cef_state_t::STATE_DEFAULT,
        webgl: cef::cef_state_t::STATE_DEFAULT,
        background_color: bg,
        accept_language_list: chromium::utils::cef_string_empty(),
        chrome_status_bubble: cef::cef_state_t::STATE_DISABLED,
    };

    let url_cef = chromium::utils::cef_string(url);

    // Create browser.
    let browser: *mut cef::cef_browser_t = unsafe {
        cef::cef_browser_host_create_browser_sync(
            &window_info,
            jclient,
            &url_cef,
            &browser_settings,
            null_mut(),
            null_mut(),
        )
    };
    assert_eq!(
        unsafe { (*browser).base.size },
        size_of::<cef::_cef_browser_t>()
    );
    browser
}

fn cef_window_info(hwnd: *mut c_void, w: c_int, h: c_int) -> cef::_cef_window_info_t {
    cef::_cef_window_info_t {
        bounds: cef::win::_cef_rect_t {
            x: 0,
            y: 0,
            width: w,
            height: h,
        },
        parent_window: hwnd as cef::win::HWND,
        windowless_rendering_enabled: 0,
        window: 0 as cef::win::HWND,
        ex_style: 0,
        window_name: cef::cef_string_t {
            str_: null_mut(),
            length: 0,
            dtor: Option::None,
        },
        style: winapi::um::winuser::WS_CHILDWINDOW
            | winapi::um::winuser::WS_CLIPCHILDREN
            | winapi::um::winuser::WS_CLIPSIBLINGS
            | winapi::um::winuser::WS_VISIBLE
            | winapi::um::winuser::WS_TABSTOP,
        menu: 0 as cef::win::HMENU,
        shared_texture_enabled: 0,
        external_begin_frame_enabled: 0,
    }
}

pub fn set_window_parent(
    window_info: *mut cef::_cef_window_info_t,
    hwnd: *mut c_void,
    x: c_int,
    y: c_int,
    w: c_int,
    h: c_int,
) {
    unsafe {
        if x != 0 {
            (*window_info).bounds.x = x;
        }
        if y != 0 {
            (*window_info).bounds.y = y;
        }
        if w != 0 {
            (*window_info).bounds.width = w;
        }
        if h != 0 {
            (*window_info).bounds.height = h;
        }
        (*window_info).parent_window = hwnd as cef::win::HWND;
        (*window_info).windowless_rendering_enabled = 0;
        (*window_info).window = 0 as cef::win::HWND;
        (*window_info).ex_style = 0;
        (*window_info).window_name = cef::cef_string_t {
            str_: null_mut(),
            length: 0,
            dtor: Option::None,
        };
        if !hwnd.is_null() {
            (*window_info).style = winapi::um::winuser::WS_CHILDWINDOW
                | winapi::um::winuser::WS_CLIPCHILDREN
                | winapi::um::winuser::WS_CLIPSIBLINGS
                | winapi::um::winuser::WS_VISIBLE
                | winapi::um::winuser::WS_TABSTOP;
        }
        (*window_info).menu = 0 as cef::win::HMENU;
    };
}
