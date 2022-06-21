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
 * Java Handler for cef_display_handler_t
 * 
 * @author Stuecker
 */
public class DisplayHandler {
	private final Chromium browser;
	private final long cefDisplayHandler = ChromiumLib
			.allocate_cef_display_handler_t(this);

	DisplayHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_display_handler_t(cefDisplayHandler);
	}

	/**
	 * @return the cef_context_menu_t pointer
	 */
	public long get() {
		return cefDisplayHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	void on_address_change(final long self, final long id, final long frame,
			final long url) {
		browser.on_address_change(frame, url);
	}

	@SuppressWarnings("unused") // Called from JNI
	void on_status_message(final long self, final long id, final long status) {
		browser.on_status_message(status);
	}

	@SuppressWarnings("unused") // Called from JNI
	void on_title_change(final long self, final long id, final long title) {
		browser.on_title_change(title);
	}
}
