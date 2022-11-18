/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_browser_t;

/**
 * Java Handler for cef_scheme_handler_factory_t
 */
public class SchemeHandlerFactory {
	private final Map<Integer, Chromium> browsers;

	private final long cefSchemeHandlerFactory;
	private final String name;

	/**
	 * @param name
	 *            the hostname to register
	 * @param browser
	 *            the browser instance to use
	 */
	public SchemeHandlerFactory(final String name, final Chromium browser) {
		this.browsers = new HashMap<>();

		this.name = name;
		this.cefSchemeHandlerFactory = ChromiumLib
				.allocate_cef_scheme_handler_factory_t(this);
		ChromiumLib.cefswt_register_http_host(name, cefSchemeHandlerFactory);
		addBrowser(browser);
	}

	/**
	 * @param browser
	 *            the browser to add this hostname to
	 */
	@SuppressWarnings("boxing")
	public void addBrowser(final Chromium browser) {
		this.browsers.put(browser.getBrowserId(), browser);
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.cefswt_register_http_host(name, 0);
		ChromiumLib.deallocate_cef_scheme_handler_factory_t(
				cefSchemeHandlerFactory);
	}

	/**
	 * @return the cef_app_t pointer
	 */
	public long get() {
		return cefSchemeHandlerFactory;
	}

	/**
	 * @param browser
	 *            the browser to remove
	 * @return whether this factory has no more assigned browsers
	 */
	public boolean removeBrowser(final Chromium browser) {
		@SuppressWarnings("boxing")
		final Integer id = browser.getBrowserId();
		browsers.remove(id);
		return browsers.isEmpty();
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private long create(final long self, final long browser_id,
			final long frame, final long scheme_name, final long request) {
		// Find the associated Chromium instance for this browser
		@SuppressWarnings("boxing")
		final Integer id = cef_browser_t.cefswt_get_id(browser_id);
		final Chromium browser = this.browsers.get(id);
		if (browser == null) {
			// Fall back to default handler
			return 0;
		}
		// Handle the request
		final ResourceHandler resHandler = browser.onRequestCustomHandler(name);
		if (resHandler != null) {
			return resHandler.get();
		}
		return 0;
	}

}
