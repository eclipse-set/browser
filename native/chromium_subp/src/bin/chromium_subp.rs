extern crate chromium;
extern crate chromium_subp;

use chromium::cef;
use chromium_subp::app;
use chromium_subp::utils;

use std::os::raw::c_int;
use std::ptr::null_mut;

fn main() {
    let main_args = utils::prepare_args();
    let mut app = app::App::new();
    unsafe { cef::cef_enable_highdpi_support() };
    let exit_code: c_int =
        unsafe { cef::cef_execute_process(&main_args, app.as_ptr(), null_mut()) };
    std::process::exit(exit_code);
}
