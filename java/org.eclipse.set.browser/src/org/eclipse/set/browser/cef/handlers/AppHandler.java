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
import org.eclipse.set.browser.lib.cef_command_line_t;

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

	@SuppressWarnings({ "unused", "static-method" }) // Called via JNI
	private void on_before_command_line_processing(final long app, final long process_type, final long command_line) {
		// Disable updating Chromium components from Google servers
		cef_command_line_t.cefswt_append_switch(command_line, "use-gl", "angle");
		cef_command_line_t.cefswt_append_switch(command_line, "use-angle", "vulkan");

		cef_command_line_t.cefswt_append_switch(command_line, "disable-component-update", null);

		// If debugging is enabled, allow remote debugging
		if (ChromiumStatic.getCEFConfiguration().DebugPort != 0) {
			cef_command_line_t.cefswt_append_switch(command_line, "remote-allow-origins", "*");
		}
	}
}
