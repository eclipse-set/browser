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

import org.eclipse.swt.internal.C;

@SuppressWarnings("javadoc")
public class ChromiumLib extends C {
	public static final int JSDIALOGTYPE_ALERT = 0;
	public static final int JSDIALOGTYPE_CONFIRM = 1;
	public static final int JSDIALOGTYPE_PROMPT = 2;

	/**
	 * Creates a native cef_client_t object and sets up `handler` to handle
	 * callbacks
	 * 
	 * For each CEF callback in cef_client_t, the native code looks up whether a
	 * method of the same name and a corresponding signature exists in
	 * `handler`. If so, the handler is set up to call the Java method
	 * 
	 * Note that this allocates memory which must be manually free'd with
	 * deallocate_*
	 * 
	 * @param handler
	 *            the handler for callbacks
	 * 
	 * @return a raw pointer to a cef_client_t to be passed to other methods
	 */
	public static final native long allocate_cef_app_t(final Object handler);

	public static final native long allocate_cef_browser_process_handler_t(
			final Object handler);

	public static final native long allocate_cef_client_t(final Object handler);

	public static final native long allocate_cef_context_menu_handler_t(
			final Object handler);

	public static final native long allocate_cef_cookie_visitor_t(
			final Object handler);

	public static final native long allocate_cef_display_handler_t(
			final Object handler);

	public static final native long allocate_cef_download_handler_t(
			final Object handler);

	public static final native long allocate_cef_focus_handler_t(
			final Object handler);

	public static final native long allocate_cef_jsdialog_handler_t(
			final Object handler);

	public static final native long allocate_cef_life_span_handler_t(
			final Object handler);

	public static final native long allocate_cef_load_handler_t(
			final Object handler);

	public static final native long allocate_cef_request_handler_t(
			final Object handler);

	public static final native int cef_cookie_visitor_t_sizeof();

	public static final native int cef_popup_features_t_sizeof();

	public static final native int cef_string_visitor_t_sizeof();

	/**
	 * @param callback
	 *            cast=(void *)
	 */
	public static final native void cefswt_auth_callback(long callback,
			String user, String password, int cont);

	/** @method flags=no_gen */
	public static final native String cefswt_cefstring_to_java(long string);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_close_browser(long browser,
			int force);

	/**
	 * @param callback
	 *            cast=(void *)
	 */
	public static final native void cefswt_context_menu_cancel(long callback);

	/** @method flags=no_gen */
	public static final native String cefswt_cookie_to_java(long cookie);

	/** @method flags=no_gen */
	public static final native String cefswt_cookie_value(long cookie);

	/**
	 * @param hwnd
	 *            cast=(void *)
	 * @param clientHandler
	 *            cast=(void *)
	 */
	public static final native long cefswt_create_browser(long hwnd, String url,
			long clientHandler, int w, int h, int js, int cefBgColor);

	/** @method flags=no_gen */
	public static final native String cefswt_cstring_to_java(long string);

	public static final native void cefswt_delete_cookies();

	/**
	 * @param callback
	 *            cast=(void *)
	 * @param default_prompt_text
	 *            cast=(void *)
	 */
	public static final native void cefswt_dialog_close(long callback, int i,
			long default_prompt_text);

	public static final native int cefswt_do_message_loop_work();

	/**
	 * @param browser
	 *            cast=(void *)
	 * @param callback
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_eval(long browser, String script,
			int id, long callback);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_execute(long browser, String script);

	/**
	 * @param bs
	 *            cast=(void *)
	 */
	public static final native void cefswt_free(long bs);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_function(long browser,
			String name, int id);

	/**
	 * @param msg
	 *            cast=(void *)
	 * @param callback
	 *            cast=(void *)
	 */
	public static final native void cefswt_function_arg(long msg, int index,
			long callback);

	/**
	 * @param msg
	 *            cast=(void *)
	 * @param ret
	 *            flags=no_in
	 */
	public static final native void cefswt_function_id(long msg,
			FunctionSt ret);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_function_return(long browser,
			int id, int port, int returnType, String ret);

	/**
	 * @param visitor
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_get_cookie(String url,
			long visitor);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native int cefswt_get_id(long browser);

	/**
	 * @param browser
	 *            cast=(void *)
	 * @param visitor
	 *            cast=(void *)
	 */
	public static final native void cefswt_get_text(long browser, long visitor);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native long cefswt_get_url(long browser);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_go_back(long browser);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_go_forward(long browser);

	/**
	 * @param app
	 *            cast=(void *)
	 */
	public static final native void cefswt_init(long app, String subprocessPath,
			String cefPath, String tempPath, String locale, int debugPort);

	/**
	 * @param frame
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_is_main_frame(long frame);

	/**
	 * @param browser
	 *            cast=(void *)
	 * @param that
	 *            cast=(void *)
	 */
	public static final native boolean cefswt_is_same(long browser, long that);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_load_url(long browser, String url,
			byte[] bytes, int length, String headers, int length2);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_reload(long browser);

	/** @method flags=no_gen */
	public static final native String cefswt_request_to_java(long request);

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_resized(long browser, int width,
			int height);

	public static final native boolean cefswt_set_cookie(String url,
			String name, String value, String domain, String path, int secure,
			int httpOnly, double maxAge);

	/**
	 * @param browser
	 *            cast=(void *)
	 * @param shell_hwnd
	 *            cast=(void *)
	 */
	public static final native void cefswt_set_focus(long browser,
			boolean focus, long shell_hwnd);

	/**
	 * @param windowInfo
	 *            cast=(void *)
	 * @param client
	 *            cast=(void *)
	 * @param clientHandler
	 *            cast=(void *)
	 * @param handle
	 *            cast=(void *)
	 */
	public static final native void cefswt_set_window_info_parent(
			long windowInfo, long client, long clientHandler, long handle,
			int x, int y, int w, int h);

	public static final native void cefswt_shutdown();

	/**
	 * @param browser
	 *            cast=(void *)
	 */
	public static final native void cefswt_stop(long browser);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_app_t
	 */
	public static final native void deallocate_cef_app_t(final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by
	 *            allocate_cef_browser_process_handler_t
	 */
	public static final native void deallocate_cef_browser_process_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_client_t
	 */
	public static final native void deallocate_cef_client_t(final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by
	 *            allocate_cef_context_menu_handler_t
	 */
	public static final native long deallocate_cef_context_menu_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_cookie_visitor_t
	 */
	public static final native long deallocate_cef_cookie_visitor_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_display_handler_t
	 */
	public static final native long deallocate_cef_display_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by
	 *            allocate_cef_download_handler_t
	 */
	public static final native long deallocate_cef_download_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_focus_handler_t
	 */
	public static final native long deallocate_cef_focus_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by
	 *            allocate_cef_jsdialog_handler_t
	 */
	public static final native long deallocate_cef_jsdialog_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by
	 *            allocate_cef_life_span_handler_t
	 */
	public static final native long deallocate_cef_life_span_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_load_handler_t
	 */
	public static final native long deallocate_cef_load_handler_t(
			final long object);

	/**
	 * Deallocates memory
	 * 
	 * @param object
	 *            the memory address allocated by allocate_cef_request_handler_t
	 */
	public static final native long deallocate_cef_request_handler_t(
			final long object);

	/**
	 * @param dest
	 *            cast=(void *)
	 * @param src
	 *            cast=(const void *),flags=no_out
	 */
	public static final native void memmove(cef_popup_features_t dest,
			long src);

	/**
	 * @param dest
	 *            cast=(void *)
	 * @param src
	 *            cast=(const void *),flags=no_out
	 */
	public static final native void memmove(long dest,
			cef_cookie_visitor_t src);

	/**
	 * @param dest
	 *            cast=(void *)
	 * @param src
	 *            cast=(const void *),flags=no_out
	 */
	public static final native void memmove(long dest,
			cef_string_visitor_t src);
}
