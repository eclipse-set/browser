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
 * JNI Interface for cef_response_t-related functions
 *
 */
public class cef_response_t {
	/**
	 * @param response
	 *            response handle
	 * @param name
	 *            header name
	 * @param value
	 *            header value
	 */
	public static final native void cefswt_response_set_header(long response,
			String name, String value);

	/**
	 * @param cef_response
	 *            response handle
	 * @param mime
	 *            mime type
	 */
	public static final native void cefswt_response_set_mime_type(
			long cef_response, String mime);

	/**
	 * @param response
	 *            response handle
	 * @param status
	 *            status code
	 */
	public static final native void cefswt_response_set_status_code(
			long response, int status);
}
