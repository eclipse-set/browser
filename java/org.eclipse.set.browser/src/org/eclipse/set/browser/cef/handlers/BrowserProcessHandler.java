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

import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.cef_browser_process_handler_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_browser_process_handler_t
 * 
 * @author Stuecker
 */
public class BrowserProcessHandler
		extends AbstractHandler<cef_browser_process_handler_t> {
	private final Callback on_schedule_message_pump_work_cb = new Callback(this,
			"on_schedule_message_pump_work", void.class,
			new Type[] { long.class, int.class, int.class });

	/**
	 * Constructor
	 */
	public BrowserProcessHandler() {
		handler = new cef_browser_process_handler_t();
		handler.on_schedule_message_pump_work = on_schedule_message_pump_work_cb
				.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_schedule_message_pump_work_cb.dispose();
	}

	@SuppressWarnings({ "static-method", "unused" }) // JNI
	void on_schedule_message_pump_work(final long pbrowserProcessHandler,
			final int delay, final int _delay2) {
		if (ChromiumStatic.browsers.get() <= 0
				|| ChromiumStatic.disposingAny > 0) {
			return;
		}
		ChromiumStatic.getMessageLoop().scheduleMessagePumpWork(delay);
	}
}