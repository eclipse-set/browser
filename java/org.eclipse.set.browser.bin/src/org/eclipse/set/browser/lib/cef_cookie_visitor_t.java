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
/// Structure to implement for visiting cookie values. The functions of this
/// structure will always be called on the IO thread.
///
@SuppressWarnings("javadoc")
public class cef_cookie_visitor_t extends CStruct {
	public static final int sizeof = ChromiumLib.cef_cookie_visitor_t_sizeof();
	///
	/// Base structure.
	///
	public cef_base_ref_counted_t base;

	///
	/// Method that will be called once for each cookie. |count| is the 0-based
	/// index for the current cookie. |total| is the total number of cookies.
	/// Set
	/// |deleteCookie| to true (1) to delete the cookie currently being visited.
	/// Return false (0) to stop visiting cookies. This function may never be
	/// called if no cookies are found.
	///
	/** @field cast=(void*) */
	public long visit;

	public cef_cookie_visitor_t() {
		base = new cef_base_ref_counted_t(sizeof);
	}

	@Override
	public void allocate() {
		ptr = C.malloc(sizeof);
		ChromiumLib.memmove(ptr, this);
	}
}