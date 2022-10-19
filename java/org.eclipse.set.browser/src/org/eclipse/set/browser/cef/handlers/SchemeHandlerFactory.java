/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.util.List;
import java.util.Optional;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_scheme_handler_factory_t
 */
public class SchemeHandlerFactory {
	private final List<Chromium> browsers;

	private final long cefSchemeHandlerFactory;
	private final String name;

	/**
	 * @param name
	 *            the hostname to register
	 * @param browser
	 *            the browser instance to use
	 */
	public SchemeHandlerFactory(final String name, final Chromium browser) {
		this.browsers = List.of(browser);
		this.name = name;
		this.cefSchemeHandlerFactory = ChromiumLib
				.allocate_cef_scheme_handler_factory_t(this);
		ChromiumLib.cefswt_register_http_host(name, cefSchemeHandlerFactory);
	}

	/**
	 * @param browser
	 *            the browser to add this hostname to
	 */
	public void addBrowser(final Chromium browser) {
		browsers.add(browser);

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
		browsers.remove(browser);
		return browsers.isEmpty();
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private long create(final long self, final long browser_id,
			final long frame, final long scheme_name, final long request) {
		// Find the associated Chromium instance for this browser
		final Optional<Chromium> browser = this.browsers.stream().filter(
				b -> ChromiumLib.cefswt_is_same(b.getCEFBrowser(), browser_id))
				.findFirst();
		if (browser.isPresent()) {
			// Handle the request
			final ResourceHandler resHandler = browser.get()
					.onRequestCustomHandler(name);
			if (resHandler != null) {
				return resHandler.get();
			}
		}
		// Fall back to default handler
		return 0;
	}

}
