/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
pub mod cef;

use jni::objects::JObject;
use jni::JNIEnv;
use chromium_jni_utils::FromJava;
use chromium_jni_utils::FromJavaMember;

impl FromJavaMember for cef::cef_base_ref_counted_t {
    fn from_java_member(env: JNIEnv, object: JObject, name: &str) -> cef::cef_base_ref_counted_t
    {
        let obj = env.get_field(object, name, "Lorg/eclipse/set/browser/cef/lib/cef_base_ref_counted_t;").unwrap().l().unwrap();
        return FromJava::from_java(env, obj);
    }
}