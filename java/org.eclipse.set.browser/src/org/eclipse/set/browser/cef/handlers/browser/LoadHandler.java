/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_load_handler_t
 * 
 * @author Stuecker
 */
public class LoadHandler {
	private final Chromium browser;
	private final long cefLoadHandler = ChromiumLib
			.allocate_cef_load_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public LoadHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_load_handler_t(cefLoadHandler);
	}

	/**
	 * @return the cef_client_t pointer
	 */
	public long get() {
		return cefLoadHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	void on_loading_state_change(final long self_, final long id,
			final int isLoading, final int canGoBack, final int canGoForward) {
		browser.on_loading_state_change(isLoading, canGoBack, canGoForward);
	}
}
