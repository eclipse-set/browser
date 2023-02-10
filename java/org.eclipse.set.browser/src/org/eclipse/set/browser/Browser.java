/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser;

import org.eclipse.swt.widgets.Composite;

/**
 * Browser implementation
 * 
 * @author Stuecker
 */
public class Browser extends org.eclipse.set.browser.swt.Browser {

	/**
	 * @param parent
	 *            The parent element
	 * @param style
	 *            The SWT style
	 */
	public Browser(final Composite parent, final int style) {
		super(parent, style);
	}

	/**
	 * @return the download listener
	 */
	public DownloadListener getDownloadListener() {
		return webBrowser.getDownloadListener();
	}

	/**
	 * Registers a request handler to handle https://hostname requests.
	 * 
	 * @param hostname
	 *            the hostname to handle queries for
	 * @param handler
	 *            the handler
	 */
	public void registerRequestHandler(final String hostname,
			final RequestHandler handler) {
		webBrowser.registerRequestHandler(hostname, handler);
	}

	/**
	 * @param listener
	 *            the new download listener
	 */
	public void setDownloadListener(final DownloadListener listener) {
		webBrowser.setDownloadListener(listener);
	}

	/**
	 * @param listener
	 *            the new console listener
	 */
	public void setConsoleListener(final ConsoleListener listener) {
		webBrowser.setConsoleListener(listener);
	}
}
