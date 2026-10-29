/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
use jni::objects::{JClass, JObject, JValue};
use jni::sys::jlong;
use jni::strings::JNIString;
use jni::errors::ThrowRuntimeExAndDefault;
use jni::EnvUnowned;

fn set_int_field(
    env: &mut jni::Env,
    object: &JObject,
    name: &str,
    value: i32,
) -> jni::errors::Result<()> {
    env.set_field(
        object,
        JNIString::new(name),
        jni::jni_sig!("I"),
        JValue::Int(value),
    )
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_memmove__Lorg_eclipse_set_browser_lib_cef_1popup_1features_1t_2J(
    mut env: EnvUnowned,
    _class: JClass,
    destination: JObject,
    source: *mut chromium::cef::_cef_popup_features_t,
) {
    let source_object = unsafe { *source };
    env.with_env(|env| -> jni::errors::Result<()> {
        set_int_field(env, &destination, "x", source_object.x)?;
        set_int_field(env, &destination, "xSet", source_object.xSet)?;
        set_int_field(env, &destination, "y", source_object.y)?;
        set_int_field(env, &destination, "ySet", source_object.ySet)?;
        set_int_field(env, &destination, "width", source_object.width)?;
        set_int_field(env, &destination, "widthSet", source_object.widthSet)?;
        set_int_field(env, &destination, "height", source_object.height)?;
        set_int_field(env, &destination, "heightSet", source_object.heightSet)?;
        Ok(())
    })
    .resolve::<ThrowRuntimeExAndDefault>();
}

#[no_mangle]
pub extern "C" fn Java_org_eclipse_set_browser_lib_ChromiumLib_cefswt_1function_1id(
    mut _env: EnvUnowned,
    _class: JClass,
    arg0: jlong,
    arg1: JObject,
) {
    // IMPROVE: Avoid creating this object here
    let mut func_st = chromium_swt::FunctionSt {
        args: 0,
        id: 0,
        port: 0,
    };
    unsafe {
        chromium_swt::cefswt_function_id(
            arg0 as *mut chromium::cef::_cef_process_message_t,
            &mut func_st,
        );
    }
    _env.with_env(|env| -> jni::errors::Result<()> {
        set_int_field(env, &arg1, "args", func_st.args.try_into().unwrap())?;
        set_int_field(env, &arg1, "id", func_st.id)?;
        set_int_field(env, &arg1, "port", func_st.port)?;
        Ok(())
    })
    .resolve::<ThrowRuntimeExAndDefault>();
}
