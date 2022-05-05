package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_client_t;
import org.eclipse.swt.internal.Callback;

public class PopupClientHandler extends AbstractBrowserHandler<cef_client_t> {
	private final Callback get_life_span_handler_cb = new Callback(this,
			"get_life_span_handler", long.class, new Type[] { long.class });

	private final PopupLifeSpanHandler popupLifeSpanHandler;

	public PopupClientHandler(final Chromium browser) {
		super(browser);
		popupLifeSpanHandler = new PopupLifeSpanHandler(browser);

		handler = new cef_client_t();
		handler.get_life_span_handler = get_life_span_handler_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		get_life_span_handler_cb.dispose();
	}

	long get_life_span_handler(final long client) {
		return popupLifeSpanHandler.get().ptr;
	}
}
