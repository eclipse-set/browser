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

///
/// All ref-counted framework structures must include this structure first.
///
@SuppressWarnings("javadoc")
public class cef_base_ref_counted_t {
	///
	/// Called to increment the reference count for the object. Should be
	/// called
	/// for every new copy of a pointer to a given object.
	///
	/** @field cast=(void*) */
	public long add_ref = 0;

	///
	// Returns true (1) if the current reference count is at least 1.
	///
	public long has_at_least_one_ref = 0;
	///
	/// Returns true (1) if the current reference count is 1.
	///
	/** @field cast=(void*) */
	public long has_one_ref = 0;
	///
	/// Called to decrement the reference count for the object. If the
	/// reference
	/// count falls to 0 the object should self-delete. Returns true (1) if
	/// the
	/// resulting reference count is 0.
	///
	/** @field cast=(void*) */
	public long release = 0;
	///
	/// Size of the data structure.
	///
	/** @field cast=(size_t) */
	public int size;

	public cef_base_ref_counted_t(final int sizeof) {
		size = sizeof;
	}

}