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
package org.eclipse.set.browser.lib;

import org.eclipse.swt.internal.Callback;

///
/// Implement this structure to provide handler implementations.
///
@SuppressWarnings("javadoc")
public class cef_client_t {
	public static final int sizeof = ChromiumLib.cef_client_t_sizeof();
	///
	/// Base structure.
	///
	public cef_base_ref_counted_t base;
	///
	/// Return the handler for context menus. If no handler is provided the
	/// default
	/// implementation will be used.
	///
	/** @field cast=(void*) */
	public long get_context_menu_handler;
	/** @field flags=no_gen */
	public Callback get_context_menu_handler_cb;
	///
	/// Return the handler for dialogs. If no handler is provided the default
	/// implementation will be used.
	///
	/** @field cast=(void*) */
	public long get_dialog_handler;
	///
	/// Return the handler for browser display state events.
	///
	/** @field cast=(void*) */
	public long get_display_handler;
	/** @field flags=no_gen */
	public Callback get_display_handler_cb;
	///
	/// Return the handler for download events. If no handler is returned
	/// downloads
	/// will not be allowed.
	///
	/** @field cast=(void*) */
	public long get_download_handler;
	///
	/// Return the handler for drag events.
	///
	/** @field cast=(void*) */
	public long get_drag_handler;
	///
	/// Return the handler for find result events.
	///
	/** @field cast=(void*) */
	public long get_find_handler;
	///
	/// Return the handler for focus events.
	///
	/** @field cast=(void*) */
	public long get_focus_handler;
	/** @field flags=no_gen */
	public Callback get_focus_handler_cb;
	///
	/// Return the handler for geolocation permissions requests. If no handler
	/// is
	/// provided geolocation access will be denied by default.
	///
	/** @field cast=(void*) */
	public long get_geolocation_handler;
	///
	/// Return the handler for JavaScript dialogs. If no handler is provided the
	/// default implementation will be used.
	///
	/** @field cast=(void*) */
	public long get_jsdialog_handler;
	/** @field flags=no_gen */
	public Callback get_jsdialog_handler_cb;
	///
	/// Return the handler for keyboard events.
	///
	/** @field cast=(void*) */
	public long get_keyboard_handler;

	///
	/// Return the handler for browser life span events.
	///
	/** @field cast=(void*) */
	public long get_life_span_handler;
	/** @field flags=no_gen */
	public Callback get_life_span_handler_cb;
	///
	/// Return the handler for browser load status events.
	///
	/** @field cast=(void*) */
	public long get_load_handler;
	/** @field flags=no_gen */
	public Callback get_load_handler_cb;
	///
	/// Return the handler for off-screen rendering events.
	///
	/** @field cast=(void*) */
	public long get_render_handler;
	///
	/// Return the handler for browser request events.
	///
	/** @field cast=(void*) */
	public long get_request_handler;
	/** @field flags=no_gen */
	public Callback get_request_handler_cb;
	///
	/// Called when a new message is received from a different process. Return
	/// true
	/// (1) if the message was handled or false (0) otherwise. Do not keep a
	/// reference to or attempt to access the message outside of this callback.
	///
	/** @field cast=(void*) */
	public long on_process_message_received;
	/** @field flags=no_gen */
	public Callback on_process_message_received_cb;

	/** @field flags=no_gen */
	public long ptr;

}