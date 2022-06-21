/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_app_t
 * 
 * @author Stuecker
 */
public class AppHandler {
	private final BrowserProcessHandler browserProcessHandler = new BrowserProcessHandler();
	private final long cefAppHandler = ChromiumLib.allocate_cef_app_t(this);

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		browserProcessHandler.dispose();
		ChromiumLib.deallocate_cef_app_t(cefAppHandler);
	}

	/**
	 * @return the cef_app_t pointer
	 */
	public long get() {
		return cefAppHandler;
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private long get_browser_process_handler(final long app) {
		return browserProcessHandler.get();
	}
}
