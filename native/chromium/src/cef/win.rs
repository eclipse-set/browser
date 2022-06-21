/* automatically generated by rust-bindgen 0.59.2 */

#![allow(dead_code)]
#![allow(non_snake_case)]
#![allow(non_camel_case_types)]
#![allow(non_upper_case_globals)]
#![allow(unused_imports)]
use chromium_jni_macro::FromJava;
use chromium_jni_utils::FromJava;
use chromium_jni_utils::FromJavaMember;
use chromium_jni_macro::JNICEFCallback;
use chromium_jni_utils::JNICEFCallback;
use chromium_jni_utils::JNIWrapperType;
use jni::JNIEnv;
use crate::ToJava;
use jni::objects::GlobalRef;
use jni::objects::JObject;
use chromium_jni_utils::jni_unwrap;
use crate::cef::cef_string_t;

pub type DWORD = ::std::os::raw::c_ulong;
#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct HINSTANCE__ {
    pub unused: ::std::os::raw::c_int,
}
pub type HINSTANCE = *mut HINSTANCE__;
#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct HWND__ {
    pub unused: ::std::os::raw::c_int,
}
pub type HWND = *mut HWND__;
#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct HMENU__ {
    pub unused: ::std::os::raw::c_int,
}
pub type HMENU = *mut HMENU__;
#[doc = ""]
#[doc = " Structure representing a rectangle."]
#[doc = ""]
#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct _cef_rect_t {
    pub x: ::std::os::raw::c_int,
    pub y: ::std::os::raw::c_int,
    pub width: ::std::os::raw::c_int,
    pub height: ::std::os::raw::c_int,
}
#[doc = ""]
#[doc = " Structure representing a rectangle."]
#[doc = ""]
pub type cef_rect_t = _cef_rect_t;
#[doc = ""]
#[doc = " Structure representing CefExecuteProcess arguments."]
#[doc = ""]
#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct _cef_main_args_t {
    pub instance: HINSTANCE,
}
#[doc = ""]
#[doc = " Structure representing window information."]
#[doc = ""]
#[repr(C)]
pub struct _cef_window_info_t {
    #[doc = " Standard parameters required by CreateWindowEx()"]
    pub ex_style: DWORD,
    pub window_name: cef_string_t,
    pub style: DWORD,
    pub bounds: cef_rect_t,
    pub parent_window: HWND,
    pub menu: HMENU,
    #[doc = ""]
    #[doc = " Set to true (1) to create the browser using windowless (off-screen)"]
    #[doc = " rendering. No window will be created for the browser and all rendering will"]
    #[doc = " occur via the CefRenderHandler interface. The |parent_window| value will be"]
    #[doc = " used to identify monitor info and to act as the parent window for dialogs,"]
    #[doc = " context menus, etc. If |parent_window| is not provided then the main screen"]
    #[doc = " monitor will be used and some functionality that requires a parent window"]
    #[doc = " may not function correctly. In order to create windowless browsers the"]
    #[doc = " CefSettings.windowless_rendering_enabled value must be set to true."]
    #[doc = " Transparent painting is enabled by default but can be disabled by setting"]
    #[doc = " CefBrowserSettings.background_color to an opaque value."]
    #[doc = ""]
    pub windowless_rendering_enabled: ::std::os::raw::c_int,
    #[doc = ""]
    #[doc = " Set to true (1) to enable shared textures for windowless rendering. Only"]
    #[doc = " valid if windowless_rendering_enabled above is also set to true. Currently"]
    #[doc = " only supported on Windows (D3D11)."]
    #[doc = ""]
    pub shared_texture_enabled: ::std::os::raw::c_int,
    #[doc = ""]
    #[doc = " Set to true (1) to enable the ability to issue BeginFrame requests from the"]
    #[doc = " client application by calling CefBrowserHost::SendExternalBeginFrame."]
    #[doc = ""]
    pub external_begin_frame_enabled: ::std::os::raw::c_int,
    #[doc = ""]
    #[doc = " Handle for the new browser window. Only used with windowed rendering."]
    #[doc = ""]
    pub window: HWND,
}
