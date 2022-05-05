package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_focus_handler_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_focus_handler_t
 * 
 * @author Stuecker
 */
public class FocusHandler extends AbstractBrowserHandler<cef_focus_handler_t> {
	private final Callback on_got_focus_cb = new Callback(this, "on_got_focus",
			void.class, new Type[] { long.class, long.class });
	private final Callback on_set_focus_cb = new Callback(this, "on_set_focus",
			int.class, new Type[] { long.class, long.class, int.class });
	private final Callback on_take_focus_cb = new Callback(this,
			"on_take_focus", void.class,
			new Type[] { long.class, long.class, int.class });

	/**
	 * @param browser
	 *            the browser
	 */
	public FocusHandler(final Chromium browser) {
		super(browser);

		handler = new cef_focus_handler_t();
		handler.on_got_focus = on_got_focus_cb.getAddress();
		handler.on_set_focus = on_set_focus_cb.getAddress();
		handler.on_take_focus = on_take_focus_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_got_focus_cb.dispose();
		on_set_focus_cb.dispose();
		on_take_focus_cb.dispose();
	}

	@SuppressWarnings({ "unused" }) // JNI
	private void on_got_focus(final long focusHandler, final long id) {
		browser.on_got_focus();
	}

	@SuppressWarnings({ "unused" }) // JNI
	private int on_set_focus(final long focusHandler, final long id,
			final int focusSource) {
		return browser.on_set_focus();
	}

	@SuppressWarnings({ "unused" }) // JNI
	private void on_take_focus(final long focusHandler, final long id,
			final int next) {
		browser.on_take_focus(next);
	}
}
