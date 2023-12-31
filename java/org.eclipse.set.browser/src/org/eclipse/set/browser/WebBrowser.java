/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

/**
 * WebBrowser implementation for SET Browser extensions
 * 
 * @author Stuecker
 */
public abstract class WebBrowser
		extends org.eclipse.set.browser.swt.WebBrowser {
	/**
	 * Default download listener which does not download anything
	 */
	public static final DownloadListener defaultDownloadListener = new DownloadListener() {
		@Override
		public Optional<Path> beforeDownload(final String suggestedName,
				final String url) {
			return Optional.empty();
		}

		@Override
		public void downloadFinished(final boolean success, final Path path) {
			// Do nothing
		}
	};
	protected HashMap<String, RequestHandler> requestHandlers = new HashMap<>();

	DownloadListener downloadListener = defaultDownloadListener;

	/**
	 * @return the download listener
	 */
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

	/**
	 * Registers a request handler to handle https://hostname requests.
	 * 
	 * @param hostname
	 *            the hostname to handle queries for
	 * @param handler
	 *            the handler
	 */
	public abstract void registerRequestHandler(final String hostname,
			final RequestHandler handler);

	/**
	 * @param listener
	 *            the new download listener
	 */
	public void setDownloadListener(final DownloadListener listener) {
		downloadListener = listener;
	}

	protected ConsoleListener consoleListener;

	/**
	 * @param listener
	 *            the new console listener
	 */
	public void setConsoleListener(final ConsoleListener listener) {
		consoleListener = listener;
	}
}
