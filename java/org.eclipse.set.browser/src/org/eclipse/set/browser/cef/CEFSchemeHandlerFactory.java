/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.set.browser.cef.handlers.SchemeHandlerFactory;

/**
 * Handler for custom CEF hostnames
 */
public class CEFSchemeHandlerFactory {
	private final Map<String, SchemeHandlerFactory> schemeHandlers = new HashMap<>();

	void deregisterSchemeHandler(final Chromium browser,
			final String hostname) {
		if (schemeHandlers.containsKey(hostname)) {
			if (schemeHandlers.get(hostname).removeBrowser(browser)) {
				schemeHandlers.remove(hostname);
			}
		}
	}

	void registerSchemeHandler(final Chromium browser, final String hostname) {
		if (schemeHandlers.containsKey(hostname)) {
			schemeHandlers.get(hostname).addBrowser(browser);
		} else {
			schemeHandlers.put(hostname,
					new SchemeHandlerFactory(hostname, browser));
		}
	}
}
