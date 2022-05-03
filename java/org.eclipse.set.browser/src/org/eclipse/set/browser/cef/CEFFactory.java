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
package org.eclipse.set.browser.cef;

import org.eclipse.set.browser.bin.lib.cef_app_t;
import org.eclipse.set.browser.bin.lib.cef_base_ref_counted_t;
import org.eclipse.set.browser.bin.lib.cef_browser_process_handler_t;
import org.eclipse.set.browser.bin.lib.cef_client_t;
import org.eclipse.set.browser.bin.lib.cef_context_menu_handler_t;
import org.eclipse.set.browser.bin.lib.cef_cookie_visitor_t;
import org.eclipse.set.browser.bin.lib.cef_display_handler_t;
import org.eclipse.set.browser.bin.lib.cef_focus_handler_t;
import org.eclipse.set.browser.bin.lib.cef_jsdialog_handler_t;
import org.eclipse.set.browser.bin.lib.cef_life_span_handler_t;
import org.eclipse.set.browser.bin.lib.cef_load_handler_t;
import org.eclipse.set.browser.bin.lib.cef_request_handler_t;
import org.eclipse.set.browser.bin.lib.cef_string_visitor_t;

public class CEFFactory {

	public static enum ReturnType {
		Array(4), Bool(1), Double(0), Error(5), Null(3), Str(2);

		public static ReturnType from(final String v) {
			try {
				final int value = Integer.parseInt(v);
				for (final ReturnType rt : ReturnType.values()) {
					if (rt.intValue() == value) {
						return rt;
					}
				}
			} catch (final NumberFormatException e) {
			}
			throw new IllegalArgumentException(v);
		}

		private final int value;

		private ReturnType(final int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
	}

	public static final int PID_BROWSER = 0;

	public static final int PID_RENDERER = 1;

	public static cef_app_t newApp() {
		final cef_app_t st = new cef_app_t();
		st.base = setBase(st, cef_app_t.sizeof);
		return st;
	}

	public static cef_browser_process_handler_t newBrowserProcessHandler() {
		final cef_browser_process_handler_t st = new cef_browser_process_handler_t();
		st.base = setBase(st, cef_browser_process_handler_t.sizeof);
		return st;
	}

	public static cef_client_t newClient() {
		final cef_client_t st = new cef_client_t();
		System.out.println(cef_client_t.sizeof);
		st.base = setBase(st, cef_client_t.sizeof);
		return st;
	}

	public static cef_context_menu_handler_t newContextMenuHandler() {
		final cef_context_menu_handler_t st = new cef_context_menu_handler_t();
		st.base = setBase(st, cef_context_menu_handler_t.sizeof);
		return st;
	}

	public static cef_cookie_visitor_t newCookieVisitor() {
		final cef_cookie_visitor_t st = new cef_cookie_visitor_t();
		st.base = setBase(st, cef_cookie_visitor_t.sizeof);
		return st;
	}

	public static cef_display_handler_t newDisplayHandler() {
		final cef_display_handler_t st = new cef_display_handler_t();
		st.base = setBase(st, cef_display_handler_t.sizeof);
		return st;
	}

	public static cef_focus_handler_t newFocusHandler() {
		final cef_focus_handler_t st = new cef_focus_handler_t();
		st.base = setBase(st, cef_focus_handler_t.sizeof);
		return st;
	}

	public static cef_jsdialog_handler_t newJsDialogHandler() {
		final cef_jsdialog_handler_t st = new cef_jsdialog_handler_t();
		st.base = setBase(st, cef_jsdialog_handler_t.sizeof);
		return st;
	}

	public static cef_life_span_handler_t newLifeSpanHandler() {
		final cef_life_span_handler_t st = new cef_life_span_handler_t();
		st.base = setBase(st, cef_life_span_handler_t.sizeof);
		return st;
	}

	public static cef_load_handler_t newLoadHandler() {
		final cef_load_handler_t st = new cef_load_handler_t();
		st.base = setBase(st, cef_load_handler_t.sizeof);
		return st;
	}

	public static cef_request_handler_t newRequestHandler() {
		final cef_request_handler_t st = new cef_request_handler_t();
		st.base = setBase(st, cef_request_handler_t.sizeof);
		return st;
	}

	public static cef_string_visitor_t newStringVisitor() {
		final cef_string_visitor_t st = new cef_string_visitor_t();
		st.base = setBase(st, cef_string_visitor_t.sizeof);
		return st;
	}

	private static cef_base_ref_counted_t setBase(final Object st,
			final int sizeof) {
		// System.out.println("J:SIZEOF:" + st.getClass().getSimpleName() + ":"
		// + sizeof);
		final cef_base_ref_counted_t base = new cef_base_ref_counted_t();
		base.size = sizeof;
		base.add_ref = 0;
		base.has_one_ref = 0;
		base.release = 0;
		// base.name = st.getClass().getSimpleName();
		return base;
	}

}
