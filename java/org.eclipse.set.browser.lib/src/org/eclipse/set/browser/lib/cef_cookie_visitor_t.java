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
package org.eclipse.set.browser.lib;

@SuppressWarnings("javadoc")
public class cef_cookie_visitor_t {
	public static final native String cefswt_cookie_to_java(long cookie);

	public static final native String cefswt_cookie_value(long cookie);

	public static final native void cefswt_delete_cookies();

	public static final native boolean cefswt_get_cookie(String url,
			long visitor);

	public static final native boolean cefswt_set_cookie(String url,
			String name, String value, String domain, String path, int secure,
			int httpOnly, double maxAge);
}