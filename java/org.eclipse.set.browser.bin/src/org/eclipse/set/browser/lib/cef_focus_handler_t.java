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

import org.eclipse.swt.internal.C;

///
/// Implement this structure to handle events related to focus. The functions of
/// this structure will be called on the UI thread.
///
@SuppressWarnings("javadoc")
public class cef_focus_handler_t extends CStruct {
	public static final int sizeof = ChromiumLib.cef_focus_handler_t_sizeof();
	///
	/// Base structure.
	///
	public cef_base_ref_counted_t base;
	///
	/// Called when the browser component has received focus.
	///
	/** @field cast=(void*) */
	public long on_got_focus;

	///
	/// Called when the browser component is requesting focus. |source|
	/// indicates
	/// where the focus request is originating from. Return false (0) to allow
	/// the
	/// focus to be set or true (1) to cancel setting the focus.
	///
	/** @field cast=(void*) */
	public long on_set_focus;
	///
	/// Called when the browser component is about to loose focus. For instance,
	/// if
	/// focus was on the last HTML element and the user pressed the TAB key.
	/// |next|
	/// will be true (1) if the browser is giving focus to the next component
	/// and
	/// false (0) if the browser is giving focus to the previous component.
	///
	/** @field cast=(void*) */
	public long on_take_focus;

	public cef_focus_handler_t() {
		base = new cef_base_ref_counted_t(sizeof);
	}

	@Override
	public void allocate() {
		ptr = C.malloc(sizeof);
		ChromiumLib.memmove(ptr, this);
	}
}