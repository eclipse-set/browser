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
 * JNI methods for cef_browser_t
 */
public class cef_browser_t {
	/**
	 * Closes the browser
	 * 
	 * @param browser
	 *            browser handle
	 * @param force
	 *            (boolean) whether to force close
	 */
	public static final native void cefswt_close_browser(long browser,
			int force);

	/**
	 * @param browser
	 *            browser handle
	 * @param script
	 *            the script to evaluate
	 * @param id
	 *            id
	 * @param callback
	 *            handle to a callback
	 * @return whether evaluation succeeded
	 */
	public static final native boolean cefswt_eval(long browser, String script,
			int id, long callback);

	/**
	 * @param browser
	 *            browser handle
	 * @param script
	 *            the script to execute
	 */
	public static final native void cefswt_execute(long browser, String script);

	/**
	 * frees the browser
	 * 
	 * @param browser
	 *            browser handle
	 */
	public static final native void cefswt_free(long browser);

	/**
	 * Registers a Javascript function
	 * 
	 * @param browser
	 *            browser handle
	 * @param name
	 *            function name
	 * @param id
	 *            function id
	 */
	public static final native void cefswt_function(long browser, String name,
			int id);

	/**
	 * @param browser
	 *            browser handle
	 * @return the browser id
	 */
	public static final native int cefswt_get_id(long browser);

	/**
	 * @param browser
	 *            browser handle
	 * @param visitor
	 *            cef_string_visitor_t handle
	 */
	public static final native void cefswt_get_text(long browser, long visitor);

	/**
	 * @param browser
	 *            browser handle
	 * @return the browser url (string handle)
	 */
	public static final native long cefswt_get_url(long browser);

	/**
	 * @param browser
	 *            browser handle
	 * @param url
	 *            url to load
	 * @param bytes
	 *            post data
	 * @param length
	 *            post data length
	 * @param headers
	 *            headers
	 * @param length2
	 *            header length
	 */
	public static final native void cefswt_load_url(long browser, String url,
			byte[] bytes, int length, String headers, int length2);

	/**
	 * Reloads the browser
	 * 
	 * @param browser
	 *            browser handle
	 */
	public static final native void cefswt_reload(long browser);

	/**
	 * @param browser
	 *            browser handle
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 */
	public static final native void cefswt_resized(long browser, int width,
			int height);

	/**
	 * @param browser
	 *            browser handle
	 * @param focus
	 *            whether the browser has focus
	 * @param shell_hwnd
	 *            window handle
	 */
	public static final native void cefswt_set_focus(long browser,
			boolean focus, long shell_hwnd);

	/**
	 * Stops the browser
	 * 
	 * @param browser
	 *            browser handle
	 */
	public static final native void cefswt_stop(long browser);

}
