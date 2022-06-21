/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_download_item_t;

/**
 * Java Handler for cef_download_handler_t
 * 
 * @author Stuecker
 */
public class DownloadHandler {
	private final Chromium browser;
	private final long cefDownloadHandler = ChromiumLib
			.allocate_cef_download_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public DownloadHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_download_handler_t(cefDownloadHandler);
	}

	/**
	 * @return the cef_focus_handler_t pointer
	 */
	public long get() {
		return cefDownloadHandler;
	}

	@SuppressWarnings({ "unused", "static-method" }) // Called from JNI
	private int can_download(final long download_handler, final long id,
			final long url, final long request_method) {
		return 1;
	}

	@SuppressWarnings({ "unused" }) // Called from JNI
	private void on_before_download(final long download_handler, final long id,
			final long download_item, final long suggested_name,
			final long callback) {
		final Optional<Path> optPath = browser.getDownloadListener()
				.beforeDownload(
						ChromiumLib.cefswt_cefstring_to_java(suggested_name),
						cef_download_item_t.get_url(download_item));

		optPath.ifPresent(path -> cef_download_item_t.before_download_callback(
				callback, path.toAbsolutePath().toString()));
	}

	@SuppressWarnings({ "unused" }) // Called from JNI
	private void on_download_updated(final long download_handler, final long id,
			final long download_item, final long callback) {
		final boolean complete = cef_download_item_t.is_complete(download_item);
		final boolean cancelled = cef_download_item_t
				.is_cancelled(download_item);

		if (complete || cancelled) {
			final String path = cef_download_item_t
					.get_full_path(download_item);
			browser.getDownloadListener().downloadFinished(!cancelled,
					Path.of(path));
		}
	}

}
