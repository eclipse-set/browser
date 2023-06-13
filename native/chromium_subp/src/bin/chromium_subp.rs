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
extern crate chromium;
extern crate chromium_subp;

use chromium::cef;
use chromium_subp::app;
use std::os::raw::c_int;
use std::ptr::null_mut;

fn main() {
    let main_args = chromium::utils::prepare_args();
    let mut app = app::App::new();
    let exit_code: c_int =
        unsafe { cef::cef_execute_process(&main_args, app.as_ptr(), null_mut()) };
    std::process::exit(exit_code);
}
