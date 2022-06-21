/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_browser_process_handler_t
 * 
 * @author Stuecker
 */
public class BrowserProcessHandler {
	private final long cefBrowserProcessHandler = ChromiumLib
			.allocate_cef_browser_process_handler_t(this);

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_browser_process_handler_t(
				cefBrowserProcessHandler);
	}

	/**
	 * @return the cef_browser_process_handler_t pointer
	 */
	public long get() {
		return cefBrowserProcessHandler;
	}

	@SuppressWarnings({ "static-method", "unused" }) // Called from JNI
	private void on_schedule_message_pump_work(
			final long pbrowserProcessHandler, final long delay) {
		if (ChromiumStatic.browsers.get() <= 0
				|| ChromiumStatic.disposingAny > 0) {
			return;
		}
		ChromiumStatic.getMessageLoop().scheduleMessagePumpWork((int) delay);
	}
}