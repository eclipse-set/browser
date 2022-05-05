package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_context_menu_handler_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_context_menu_handler_t
 * 
 * @author Stuecker
 */
public class ContextMenuHandler
		extends AbstractBrowserHandler<cef_context_menu_handler_t> {
	private final Callback run_context_menu_cb = new Callback(this,
			"run_context_menu", int.class, new Type[] { long.class, long.class,
					long.class, long.class, long.class, long.class });

	/**
	 * @param browser
	 *            the browser
	 */
	public ContextMenuHandler(final Chromium browser) {
		super(browser);

		handler = new cef_context_menu_handler_t();
		handler.run_context_menu = run_context_menu_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		run_context_menu_cb.dispose();
	}

	@SuppressWarnings({ "unused" }) // JNI
	int run_context_menu(final long self, final long id, final long frame,
			final long params, final long model, final long callback) {
		return browser.run_context_menu(callback);
	}
}
