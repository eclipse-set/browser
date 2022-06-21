/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Java Handler for cef_life_span_handler_t (for popups)
 * 
 * @author Stuecker
 */
public class PopupLifeSpanHandler {
	private static final long LOOP = 75;
	private final long cefLifeSpanHandler = ChromiumLib
			.allocate_cef_life_span_handler_t(this);

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_life_span_handler_t(cefLifeSpanHandler);
	}

	/**
	 * @return the cef_life_span_handler_t pointer
	 */
	public long get() {
		return cefLifeSpanHandler;
	}

	@SuppressWarnings({ "static-method", "unused" }) // Called from JNI
	private void on_after_created(final long self, final long id) {
		try {
			// not sleeping here causes deadlock with multiple window.open
			Thread.sleep(LOOP);
		} catch (final InterruptedException e) {
			// skip
		}
	}

	@SuppressWarnings({ "static-method", "unused" }) // Called from JNI
	private void on_before_close(final long plifeSpanHandler, final long id) {
		ChromiumStatic.disposingAny--;
	}

	@SuppressWarnings({ "static-method", "unused" }) // Called from JNI
	private int popup_do_close(final long plifeSpanHandler, final long id) {
		ChromiumStatic.disposingAny++;
		return 0;
	}
}
