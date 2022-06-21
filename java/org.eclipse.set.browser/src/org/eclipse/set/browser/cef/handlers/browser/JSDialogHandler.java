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
 * Java Handler for cef_jsdialog_handler_t
 * 
 * @author Stuecker
 */
public class JSDialogHandler {
	private final Chromium browser;
	private final long cefJSDialogHandler = ChromiumLib
			.allocate_cef_jsdialog_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public JSDialogHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_jsdialog_handler_t(cefJSDialogHandler);
	}

	/**
	 * @return the cef_focus_handler_t pointer
	 */
	public long get() {
		return cefJSDialogHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	private int on_before_unload_dialog(final long self_, final long id,
			final long msg, final int is_reload, final long callback) {
		return browser.on_before_unload_dialog(msg, is_reload, callback);
	}

	@SuppressWarnings("unused") // Called from JNI
	private void on_dialog_closed(final long self_, final long id) {
		browser.on_dialog_closed();
	}

	@SuppressWarnings("unused") // Called from JNI
	private int on_jsdialog(final long self_, final long id,
			final long origin_url, final int dialog_type,
			final long message_text, final long default_prompt_text,
			final long callback, final int suppress_message) {
		return browser.on_jsdialog(origin_url, dialog_type, message_text,
				default_prompt_text, callback);
	}
}
