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
 * Java Handler for cef_client_t (for popups)
 * 
 * @author Stuecker
 */
public class PopupClientHandler extends AbstractBrowserHandler<cef_client_t> {
	private final Callback get_life_span_handler_cb = new Callback(this,
			"get_life_span_handler", long.class, new Type[] { long.class });

	private final PopupLifeSpanHandler popupLifeSpanHandler;

	/**
	 * @param browser
	 *            the browser
	 */
	public PopupClientHandler(final Chromium browser) {
		super(browser);
		popupLifeSpanHandler = new PopupLifeSpanHandler(browser);

		handler = new cef_client_t();
		handler.get_life_span_handler = get_life_span_handler_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		get_life_span_handler_cb.dispose();
	}

	@SuppressWarnings({ "unused" }) // JNI
	private long get_life_span_handler(final long client) {
		return popupLifeSpanHandler.get().ptr;
	}
}
