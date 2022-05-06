/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.lang.reflect.Type;

import org.eclipse.set.browser.lib.cef_app_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_app_t
 * 
 * @author Stuecker
 */
public class AppHandler extends AbstractHandler<cef_app_t> {
	private final BrowserProcessHandler browserProcessHandler = new BrowserProcessHandler();
	private final Callback get_browser_process_handler_cb = new Callback(this,
			"get_browser_process_handler", long.class,
			new Type[] { long.class });

	/**
	 * Constructor
	 */
	public AppHandler() {
		handler = new cef_app_t();
		handler.get_browser_process_handler = get_browser_process_handler_cb
				.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		get_browser_process_handler_cb.dispose();

		browserProcessHandler.dispose();
	}

	@SuppressWarnings({ "unused" }) // JNI
	private long get_browser_process_handler(final long app) {
		return browserProcessHandler.get().ptr;
	}
}
