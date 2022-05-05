package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_display_handler_t;
import org.eclipse.swt.internal.Callback;

public class DisplayHandler extends AbstractBrowserHandler<cef_display_handler_t> {
	private final Callback on_address_change_cb = new Callback(this,
			"on_address_change", void.class,
			new Type[] { long.class, long.class, long.class, long.class });

	private final Callback on_status_message_cb = new Callback(this,
			"on_status_message", void.class,
			new Type[] { long.class, long.class, long.class });

	private final Callback on_title_change_cb = new Callback(this,
			"on_title_change", void.class,
			new Type[] { long.class, long.class, long.class });

	DisplayHandler(final Chromium browser) {
		super(browser);
		handler = new cef_display_handler_t();
		handler.on_title_change = on_title_change_cb.getAddress();
		handler.on_address_change = on_address_change_cb.getAddress();
		handler.on_status_message = on_status_message_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_address_change_cb.dispose();
		on_status_message_cb.dispose();
		on_title_change_cb.dispose();
	}

	void on_address_change(final long self, final long id, final long frame,
			final long url) {
		browser.on_address_change(id, frame, url);
	}

	void on_status_message(final long self, final long id, final long status) {
		browser.on_status_message(id, status);
	}

	void on_title_change(final long self, final long id, final long title) {
		browser.on_title_change(id, title);
	}
}
