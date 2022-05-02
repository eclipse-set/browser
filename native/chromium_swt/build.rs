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
#[cfg(debug_assertions)]
const CEF_TARGET: &'static str = "Debug"; 
#[cfg(not(debug_assertions))]
const CEF_TARGET: &'static str = "Release";
fn main() {
  link();
}

fn get_cef_path() -> std::path::PathBuf {
  let cwd = std::env::current_dir().unwrap();
  let mut cef_path = cwd.clone();
  cef_path.push("..");
  cef_path.push("..");
  cef_path.push("cef");
  cef_path
}

fn link() {
  let cef_path = get_cef_path();
  if !cef_path.exists() {
    panic!("cargo:warning=Extract and rename cef binary (minimal) distro to {:?}", cef_path);
  }

  if cfg!(target_os = "linux") {
    // println!("cargo:rustc-link-lib=gtk-x11-2.0");
    // println!("cargo:rustc-link-lib=gdk-x11-2.0");
    // println!("cargo:rustc-link-lib=gtk-3.so.0");
    println!("cargo:rustc-link-lib=X11");
  }
  // Tell cargo to tell rustc to link the system shared library.
  let mut cef_bin = cef_path.clone();
  cef_bin.push(CEF_TARGET);
  let lib = if cfg!(target_os = "windows") {
    println!("cargo:rustc-link-search={}", cef_bin.display()); 
    "libcef" 
  } else if cfg!(target_os = "macos") {
    println!("cargo:rustc-link-search=framework={}", cef_bin.display());
    "framework=Chromium Embedded Framework"
  } else { 
    println!("cargo:rustc-link-search={}", cef_bin.display());
    "cef" 
  };
  println!("cargo:rustc-link-lib={}", lib);
}