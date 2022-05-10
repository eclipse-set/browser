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
use chromium_subp::utils;
use winapi::um::handleapi::CloseHandle;
use winapi::um::processthreadsapi::OpenProcess;
use winapi::um::winnt::HANDLE;

use std::os::raw::c_int;
use std::ptr::null_mut;
use std::thread;
use winapi::um::tlhelp32;

unsafe fn get_parent_handle() -> Option<HANDLE> {
    let snapshot =
        winapi::um::tlhelp32::CreateToolhelp32Snapshot(winapi::um::tlhelp32::TH32CS_SNAPPROCESS, 0);
    let handle = get_parent_handle_snapshot(snapshot);
    CloseHandle(snapshot);
    handle
}

unsafe fn get_parent_handle_snapshot(snapshot: HANDLE) -> Option<winapi::um::winnt::HANDLE> {
    let pid = winapi::um::processthreadsapi::GetCurrentProcessId();
    let process: *mut tlhelp32::PROCESSENTRY32 = std::ptr::null_mut();
    if tlhelp32::Process32First(snapshot, process) != 0 {
        loop {
            if (*process).th32ProcessID == pid {
                return Some(OpenProcess(
                    winapi::um::winnt::SYNCHRONIZE,
                    0,
                    (*process).th32ParentProcessID,
                ));
            }

            if tlhelp32::Process32Next(snapshot, process) == 0 {
                break;
            }
        }
    }
    None
}

fn main() {
    thread::spawn(|| unsafe {
        get_parent_handle().map(|handle| {
            winapi::um::synchapi::WaitForSingleObject(handle, 0xFFFFFFFF);
            std::process::exit(0);
        })
    });

    let main_args = utils::prepare_args();
    let mut app = app::App::new();
    unsafe { cef::cef_enable_highdpi_support() };
    let exit_code: c_int =
        unsafe { cef::cef_execute_process(&main_args, app.as_ptr(), null_mut()) };
    std::process::exit(exit_code);
}
