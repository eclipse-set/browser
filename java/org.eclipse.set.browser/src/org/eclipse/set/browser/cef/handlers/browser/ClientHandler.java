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
 * Handler wrapper for cef_client_t callbacks
 * 
 * @author Stuecker
 */
public class ClientHandler {
	private final long cefClientHandler = ChromiumLib
			.allocate_cef_client_t(this);

	private final ContextMenuHandler contextMenuHandler;

	private final DisplayHandler displayHandler;
	private final DownloadHandler downloadHandler;
	private final FocusHandler focusHandler;
	private final JSDialogHandler jsDialogHandler;
	private final LifeSpanHandler lifeSpanHandler;
	private final LoadHandler loadHandler;
	private final RequestHandler requestHandler;

	protected final Chromium browser;

	/**
	 * @param browser
	 *            the browser
	 */
	public ClientHandler(final Chromium browser) {
		this.browser = browser;
		requestHandler = new RequestHandler(browser);
		downloadHandler = new DownloadHandler(browser);
		contextMenuHandler = new ContextMenuHandler(browser);
		displayHandler = new DisplayHandler(browser);
		focusHandler = new FocusHandler(browser);
		jsDialogHandler = new JSDialogHandler(browser);
		lifeSpanHandler = new LifeSpanHandler(browser);
		loadHandler = new LoadHandler(browser);
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		focusHandler.dispose();
		lifeSpanHandler.dispose();
		loadHandler.dispose();
		displayHandler.dispose();
		requestHandler.dispose();
		jsDialogHandler.dispose();
		contextMenuHandler.dispose();
		downloadHandler.dispose();

		ChromiumLib.deallocate_cef_client_t(cefClientHandler);
	}

	/**
	 * @return the cef_client_t pointer
	 */
	public long get() {
		return cefClientHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_context_menu_handler(final long client) {
		return contextMenuHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_display_handler(final long client) {
		return displayHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_download_handler(final long client) {
		return downloadHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_focus_handler(final long client) {
		return focusHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_jsdialog_handler(final long client) {
		return jsDialogHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_life_span_handler(final long client) {
		return lifeSpanHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_load_handler(final long client) {
		return loadHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_request_handler(final long client) {
		return requestHandler.get();
	}

	@SuppressWarnings("unused") // Called from JNI
	private int on_process_message_received(final long client, final long id,
			final long frame, final int source, final long processMessage) {
		return browser.on_process_message_received(source, processMessage);
	}
}
