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
/// Implement this structure to receive string values asynchronously.
///
@SuppressWarnings("javadoc")
public class cef_string_visitor_t {
	public static final int sizeof = ChromiumLib.cef_string_visitor_t_sizeof();
	///
	/// Base structure.
	///
	public cef_base_ref_counted_t base;

	/** @field flags=no_gen */
	public long ptr;
	/** @field flags=no_gen */
	public int refs;
	///
	/// Method that will be executed.
	///
	/** @field cast=(void*) */
	public long visit;

	/** @field flags=no_gen */
	public Callback visit_cb;

	public cef_string_visitor_t() {
		base = new cef_base_ref_counted_t(sizeof);
	}
}