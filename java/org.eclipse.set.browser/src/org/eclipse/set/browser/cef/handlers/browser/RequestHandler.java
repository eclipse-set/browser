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
 * Java Handler for cef_request_handler_t
 * 
 * @author Stuecker
 */
public class RequestHandler {
	private final Chromium browser;
	private final long cefRequestHandler = ChromiumLib
			.allocate_cef_request_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public RequestHandler(final Chromium browser) {
		this.browser = browser;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_request_handler_t(cefRequestHandler);
	}

	/**
	 * @return the cef_request_handler_t pointer
	 */
	public long get() {
		return cefRequestHandler;
	}

	@SuppressWarnings("unused") // Called from JNI
	int get_auth_credentials(final long self, final long id,
			final long origin_url, final int isProxy, final long host,
			final int port, final long realm, final long scheme,
			final long callback) {
		return browser.get_auth_credentials(id, origin_url, host, port, realm,
				callback);
	}

	@SuppressWarnings("unused") // Called from JNI
	int on_before_browse(final long self, final long id, final long frame,
			final long request, final int user_gesture, final int is_redirect) {
		return browser.on_before_browse(id, frame, request);
	}

}
