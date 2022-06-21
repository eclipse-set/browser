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
 * Java Handler for cef_focus_handler_t
 * 
 * @author Stuecker
 */
public class FocusHandler {
	private final Chromium browser;
	private final long cefFocusHandler = ChromiumLib
			.allocate_cef_focus_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public FocusHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_focus_handler_t(cefFocusHandler);
	}

	/**
	 * @return the cef_focus_handler_t pointer
	 */
	public long get() {
		return cefFocusHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	private void on_got_focus(final long focusHandler, final long id) {
		browser.on_got_focus();
	}

	@SuppressWarnings("unused") // Called from JNI
	private int on_set_focus(final long focusHandler, final long id,
			final int focusSource) {
		return browser.on_set_focus();
	}

	@SuppressWarnings("unused") // Called from JNI
	private void on_take_focus(final long focusHandler, final long id,
			final int next) {
		browser.on_take_focus(next);
	}
}
