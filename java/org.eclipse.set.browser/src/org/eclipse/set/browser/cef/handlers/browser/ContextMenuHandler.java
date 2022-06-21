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
 * Java Handler for cef_context_menu_handler_t
 * 
 * @author Stuecker
 */
public class ContextMenuHandler {
	private final long cefContextMenuHandler = ChromiumLib
			.allocate_cef_context_menu_handler_t(this);
	Chromium browser;

	/**
	 * @param browser
	 *            the browser
	 */
	public ContextMenuHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib
				.deallocate_cef_context_menu_handler_t(cefContextMenuHandler);
	}

	/**
	 * @return the cef_context_menu_t pointer
	 */
	public long get() {
		return cefContextMenuHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	int run_context_menu(final long self, final long id, final long frame,
			final long params, final long model, final long callback) {
		return browser.run_context_menu(callback);
	}
}
