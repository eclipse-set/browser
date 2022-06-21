/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_client_t (for popups)
 * 
 * @author Stuecker
 */
public class PopupClientHandler {
	private final long cefClientHandler = ChromiumLib
			.allocate_cef_client_t(this);

	private final PopupLifeSpanHandler popupLifeSpanHandler = new PopupLifeSpanHandler();

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		popupLifeSpanHandler.dispose();
	}

	/**
	 * @return the cef_client_t pointer
	 */
	public long get() {
		return cefClientHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	private long get_life_span_handler(final long client) {
		return popupLifeSpanHandler.get();
	}
}
