/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.lib;

/**
 * JNI Interface for cef_request_t-related functions
 *
 */
public class cef_request_t {
	/**
	 * @param cef_request_t
	 *            request handle
	 * @param name
	 *            header name
	 * @return the header value
	 */
	public static final native String cefswt_request_get_header_by_name(
			long cef_request_t, String name);

	/**
	 * @param cef_request_t
	 *            request handle
	 * @return the request method (e.g. GET/POST/PUT)
	 */
	public static final native String cefswt_request_get_method(
			long cef_request_t);

	/**
	 * @param cef_request_t
	 *            request handle
	 * @return the request url
	 */
	public static final native String cefswt_request_get_url(
			long cef_request_t);

}
