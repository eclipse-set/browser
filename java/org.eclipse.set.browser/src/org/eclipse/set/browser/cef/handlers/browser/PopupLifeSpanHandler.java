package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.cef_life_span_handler_t;
import org.eclipse.swt.internal.Callback;

/**
 * Java Handler for cef_life_span_handler_t (for popups)
 * 
 * @author Stuecker
 */
public class PopupLifeSpanHandler
		extends AbstractBrowserHandler<cef_life_span_handler_t> {
	private static final long LOOP = 75;

	private final Callback on_after_created_cb = new Callback(this,
			"on_after_created", void.class,
			new Type[] { long.class, long.class });

	private final Callback on_before_close_cb = new Callback(this,
			"on_before_close", void.class,
			new Type[] { long.class, long.class });

	private final Callback on_before_popup_cb = new Callback(this,
			"popup_do_close", int.class, new Type[] { long.class, long.class });

	/**
	 * @param browser
	 *            the browser
	 */
	public PopupLifeSpanHandler(final Chromium browser) {
		super(browser);
		handler = new cef_life_span_handler_t();
		handler.on_before_close = on_before_close_cb.getAddress();
		handler.on_after_created = on_after_created_cb.getAddress();
		handler.on_before_popup = on_before_popup_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_before_close_cb.dispose();
		on_after_created_cb.dispose();
		on_before_popup_cb.dispose();
	}

	@SuppressWarnings({ "static-method", "unused" }) // JNI
	private void on_after_created(final long self, final long id) {
		try {
			// not sleeping here causes deadlock with multiple window.open
			Thread.sleep(LOOP);
		} catch (final InterruptedException e) {
			// skip
		}
	}

	@SuppressWarnings({ "static-method", "unused" }) // JNI
	private void on_before_close(final long plifeSpanHandler, final long id) {
		ChromiumStatic.disposingAny--;
	}

	@SuppressWarnings({ "static-method", "unused" }) // JNI
	private int popup_do_close(final long plifeSpanHandler, final long id) {
		ChromiumStatic.disposingAny++;
		return 0;
	}
}
