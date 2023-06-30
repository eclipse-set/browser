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
 * JNI Interface for cef_download_item_t-related functions
 *
 */
public class cef_command_line_t {
	/**
	 * Appends a switch to the CEF command line
	 * 
	 * @param command_line
	 *            the command line
	 * @param cmdSwitch
	 * 			  the switch to append
	 * @param value 
	 * 				switch value (or null if no value)
	 */
	public static native void cefswt_append_switch(
			final long command_line, final String cmdSwitch, String value);
}