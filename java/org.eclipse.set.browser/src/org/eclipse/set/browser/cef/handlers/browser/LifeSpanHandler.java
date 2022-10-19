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
import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.cef.MessageLoop;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_browser_t;

/**
 * Java Handler for cef_life_span_handler_t
 * 
 * @author Stuecker
 */
public class LifeSpanHandler {
	private final Chromium browser;
	private final long cefLifeSpanHandler = ChromiumLib
			.allocate_cef_life_span_handler_t(this);

	/**
	 * @param browser
	 *            the browser
	 */
	public LifeSpanHandler(final Chromium browser) {
		this.browser = browser;
	}

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

	@SuppressWarnings({ "unused", "static-method" }) // Called from JNI
	private int do_close(final long plifeSpanHandler, final long id) {
		// Return 1 (true) to indicate that we will close the window ourselves
		return 1;
	}

	@SuppressWarnings("unused") // Called from JNI
	private void on_after_created(final long self, final long id) {
		if (id != 0) {
			ChromiumStatic.browsers.incrementAndGet();
		}
		browser.on_after_created(id);
	}

	@SuppressWarnings({ "unused", "static-method", "hiding", "boxing" }) // Called
																			// from
	// JNI
	private void on_before_close(final long plifeSpanHandler,
			final long browser) {
		try {

			final int id = cef_browser_t.cefswt_get_id(browser);
			ChromiumStatic.instances.remove(id).on_before_close(browser);
			final int decrementAndGet = ChromiumStatic.browsers
					.decrementAndGet();
			cef_browser_t.cefswt_free(browser);
			ChromiumStatic.disposingAny--;
			if (decrementAndGet == 0 && ChromiumStatic.shuttingDown) {
				ChromiumStatic.shutdown();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused") // Called from JNI
	private int on_before_popup(final long self, final long id,
			final long frame, final long target_url,
			final long target_frame_name, final int target_disposition,
			final int user_gesture, final long popupFeaturesPtr,
			final long windowInfo, final long client, final long settings,
			final long extraInfo, final long no_javascript_access) {
		final MessageLoop messageLoop = ChromiumStatic.getMessageLoop();
		messageLoop.pause();
		messageLoop.disablePump();
		final int ret = browser.on_before_popup(popupFeaturesPtr, windowInfo,
				client);
		messageLoop.unpause();
		return ret;
	}
}
