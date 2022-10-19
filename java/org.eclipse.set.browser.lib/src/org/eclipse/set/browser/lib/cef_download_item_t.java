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
public class cef_download_item_t {
	/**
	 * Calls the on_before_download callback with the provided path
	 * 
	 * @param callback
	 *            the callback
	 * @param path
	 *            the path to download to
	 */
	public static native void before_download_callback(final long callback,
			final String path);

	/**
	 * @param data_out
	 *            handle to a byte array
	 * @param bytes
	 *            bytes to copy
	 * @param length
	 *            number of bytes to copy
	 */
	public static final native void cefswt_copy_bytes(long data_out,
			byte[] bytes, long length);

	/**
	 * @param download_item
	 *            the cef_download_item_t pointer
	 * @return the path to the file
	 */
	public static native String get_full_path(final long download_item);

	/**
	 * @param download_item
	 *            the cef_download_item_t pointer
	 * @return the download url
	 */
	public static native String get_url(long download_item);

	/**
	 * @param download_item
	 *            the cef_download_item_t pointer
	 * @return whether the download was cancelled (or otherwise interrupted)
	 */
	public static native boolean is_cancelled(final long download_item);

	/**
	 * @param download_item
	 *            the cef_download_item_t pointer
	 * @return whether the download was completed
	 */
	public static native boolean is_complete(final long download_item);

}
