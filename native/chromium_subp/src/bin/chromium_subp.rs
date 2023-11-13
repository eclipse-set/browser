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
    // Disable High DPI per Monitor support.
    //   By default Chromium enables the "PerMonitorV2" strategy for high DPI awareness. However SWT does not yet support
    //   "PerMonitorV2", but only "System" DPI awareness. Mismatching DPI awareness between the render process (chromium)
    //   and the host process (swt/java) causes strange visual artifacts.
    //
    //   Thus set "System" DPI awareness here, to override Chromiums default to match SWT.
    unsafe {
        winapi::um::winuser::SetProcessDpiAwarenessContext(
            winapi::shared::windef::DPI_AWARENESS_CONTEXT_SYSTEM_AWARE,
        );
    }

    let main_args = chromium::utils::prepare_args();
    let mut app = app::App::new();
    let exit_code: c_int =
        unsafe { cef::cef_execute_process(&main_args, app.as_ptr(), null_mut()) };
    std::process::exit(exit_code);
}
