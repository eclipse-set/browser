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

///
/// Popup window features.
///
@SuppressWarnings("javadoc")
public class cef_popup_features_t {
	public static final int sizeof = ChromiumLib.cef_popup_features_t_sizeof();
	public int height;
	public int heightSet;
	public int menuBarVisible;
	public int scrollbarsVisible;
	public int statusBarVisible;
	public int toolBarVisible;
	public int width;
	public int widthSet;
	public int x;
	public int xSet;
	public int y;

	public int ySet;
}