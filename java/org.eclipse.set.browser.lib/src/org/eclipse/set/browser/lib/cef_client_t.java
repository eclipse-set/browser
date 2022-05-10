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

///
/// Implement this structure to provide handler implementations.
///
@SuppressWarnings("javadoc")
public class cef_client_t extends CStruct {
	public static final int sizeof = ChromiumLib.cef_client_t_sizeof();
	///
	/// Base structure.
	///
	public cef_base_ref_counted_t base;
	public long get_audio_handler;
	public long get_command_handler;
	///
	/// Return the handler for context menus. If no handler is provided the
	/// default
	/// implementation will be used.
	///
	/** @field cast=(void*) */
	public long get_context_menu_handler;
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
	public long get_frame_handler;

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
	///
	/// Return the handler for browser load status events.
	///
	/** @field cast=(void*) */
	public long get_load_handler;
	public long get_print_handler;

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
	///
	/// Called when a new message is received from a different process. Return
	/// true
	/// (1) if the message was handled or false (0) otherwise. Do not keep a
	/// reference to or attempt to access the message outside of this callback.
	///
	/** @field cast=(void*) */
	public long on_process_message_received;

	public cef_client_t() {
		base = new cef_base_ref_counted_t(sizeof);
	}

	@Override
	public void allocate() {
		ptr = C.malloc(sizeof);
		ChromiumLib.memmove(ptr, this);
	}

}