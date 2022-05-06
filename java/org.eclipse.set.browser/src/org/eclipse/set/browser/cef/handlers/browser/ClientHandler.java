/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_client_t;
import org.eclipse.swt.internal.Callback;

/**
 * Handler wrapper for cef_client_t callbacks
 * 
 * @author Stuecker
 */
public class ClientHandler extends AbstractBrowserHandler<cef_client_t> {
	private final ContextMenuHandler contextMenuHandler;
	private final DisplayHandler displayHandler;
	private final FocusHandler focusHandler;
	private final Callback get_context_menu_handler_cb = new Callback(this,
			"get_context_menu_handler", long.class, new Type[] { long.class });
	private final Callback get_display_handler_cb = new Callback(this,
			"get_display_handler", long.class, new Type[] { long.class });
	private final Callback get_focus_handler_cb = new Callback(this,
			"get_focus_handler", long.class, new Type[] { long.class });
	private final Callback get_jsdialog_handler_cb = new Callback(this,
			"get_jsdialog_handler", long.class, new Type[] { long.class });

	private final Callback get_life_span_handler_cb = new Callback(this,
			"get_life_span_handler", long.class, new Type[] { long.class });

	private final Callback get_load_handler_cb = new Callback(this,
			"get_load_handler", long.class, new Type[] { long.class });

	private final Callback get_request_handler_cb = new Callback(this,
			"get_request_handler", long.class, new Type[] { long.class });

	private final JSDialogHandler jsDialogHandler;

	private final LifeSpanHandler lifeSpanHandler;

	private final LoadHandler loadHandler;

	private final Callback on_process_message_received_cb = new Callback(this,
			"on_process_message_received", int.class, new Type[] { long.class,
					long.class, long.class, int.class, long.class });

	private final RequestHandler requestHandler;

	/**
	 * @param browser
	 *            the browser
	 */
	public ClientHandler(final Chromium browser) {
		super(browser);

		requestHandler = new RequestHandler(browser);
		contextMenuHandler = new ContextMenuHandler(browser);
		displayHandler = new DisplayHandler(browser);
		focusHandler = new FocusHandler(browser);
		jsDialogHandler = new JSDialogHandler(browser);
		lifeSpanHandler = new LifeSpanHandler(browser);
		loadHandler = new LoadHandler(browser);

		handler = new cef_client_t();
		handler.get_context_menu_handler = get_context_menu_handler_cb
				.getAddress();
		handler.get_display_handler = get_display_handler_cb.getAddress();
		handler.get_focus_handler = get_focus_handler_cb.getAddress();
		handler.get_jsdialog_handler = get_jsdialog_handler_cb.getAddress();
		handler.get_life_span_handler = get_life_span_handler_cb.getAddress();
		handler.get_load_handler = get_load_handler_cb.getAddress();
		handler.get_request_handler = get_request_handler_cb.getAddress();
		handler.on_process_message_received = on_process_message_received_cb
				.getAddress();

		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		get_context_menu_handler_cb.dispose();
		get_display_handler_cb.dispose();
		get_focus_handler_cb.dispose();
		get_jsdialog_handler_cb.dispose();
		get_life_span_handler_cb.dispose();
		get_request_handler_cb.dispose();
		get_load_handler_cb.dispose();
		on_process_message_received_cb.dispose();

		focusHandler.dispose();
		lifeSpanHandler.dispose();
		loadHandler.dispose();
		displayHandler.dispose();
		requestHandler.dispose();
		jsDialogHandler.dispose();
		contextMenuHandler.dispose();
	}

	@SuppressWarnings("unused") // JNI Call
	long get_context_menu_handler(final long client) {
		return contextMenuHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_display_handler(final long client) {
		return displayHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_focus_handler(final long client) {
		return focusHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_jsdialog_handler(final long client) {
		return jsDialogHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_life_span_handler(final long client) {
		return lifeSpanHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_load_handler(final long client) {
		return loadHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	long get_request_handler(final long client) {
		return requestHandler.get().ptr;
	}

	@SuppressWarnings("unused") // JNI Call
	int on_process_message_received(final long client, final long id,
			final long frame, final int source, final long processMessage) {
		return browser.on_process_message_received(source, processMessage);
	}
}
