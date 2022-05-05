package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_load_handler_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_load_handler_t
 * 
 * @author Stuecker
 */
public class LoadHandler extends AbstractBrowserHandler<cef_load_handler_t> {

	private final Callback on_loading_state_change_cb = new Callback(this,
			"on_loading_state_change", void.class, new Type[] { long.class,
					long.class, int.class, int.class, int.class });

	/**
	 * @param browser
	 *            the browser
	 */
	public LoadHandler(final Chromium browser) {
		super(browser);
		handler = new cef_load_handler_t();
		handler.on_loading_state_change = on_loading_state_change_cb
				.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_loading_state_change_cb.dispose();
	}

	@SuppressWarnings({ "unused" }) // JNI
	void on_loading_state_change(final long self_, final long id,
			final int isLoading, final int canGoBack, final int canGoForward) {
		browser.on_loading_state_change(isLoading, canGoBack, canGoForward);
	}
}
