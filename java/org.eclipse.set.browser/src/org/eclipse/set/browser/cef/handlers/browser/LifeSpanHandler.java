package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.cef.MessageLoop;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_life_span_handler_t;
import org.eclipse.swt.internal.Callback;

public class LifeSpanHandler
		extends AbstractBrowserHandler<cef_life_span_handler_t> {
	private final Callback do_close_cb = new Callback(this, "do_close",
			int.class, new Type[] { long.class, long.class });

	private final Callback on_after_created_cb = new Callback(this,
			"on_after_created", void.class,
			new Type[] { long.class, long.class });

	private final Callback on_before_close_cb = new Callback(this,
			"on_before_close", void.class,
			new Type[] { long.class, long.class });

	private final Callback on_before_popup_cb = new Callback(this,
			"on_before_popup", int.class,
			new Type[] { long.class, long.class, long.class, long.class,
					long.class, int.class, int.class, long.class, long.class,
					long.class, long.class, int.class });

	public LifeSpanHandler(final Chromium browser) {
		super(browser);

		handler = new cef_life_span_handler_t();
		handler.on_before_close = on_before_close_cb.getAddress();
		handler.on_after_created = on_after_created_cb.getAddress();
		handler.on_before_popup = on_before_popup_cb.getAddress();
		handler.do_close = do_close_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_before_close_cb.dispose();
		on_after_created_cb.dispose();
		on_before_popup_cb.dispose();
		do_close_cb.dispose();
	}

	private int do_close(final long self, final long id) {
		return browser.do_close(id);
	}

	private void on_after_created(final long self, final long id) {
		if (id != 0) {
			ChromiumStatic.browsers.incrementAndGet();
		}

		browser.on_after_created(id);
	}

	private void on_before_close(final long plifeSpanHandler,
			final long browser) {
		final int id = ChromiumLib.cefswt_get_id(browser);
		ChromiumStatic.instances.remove(id).on_before_close(browser);

		final int decrementAndGet = ChromiumStatic.browsers.decrementAndGet();

		ChromiumLib.cefswt_free(browser);
		ChromiumStatic.disposingAny--;
		if (decrementAndGet == 0 && ChromiumStatic.shuttingDown) {
			ChromiumStatic.shutdown();
		}
	}

	private int on_before_popup(final long self, final long id,
			final long frame, final long target_url,
			final long target_frame_name, final int target_disposition,
			final int user_gesture, final long popupFeaturesPtr,
			final long windowInfo, final long client, final long settings,
			final int no_javascript_access) {
		final MessageLoop messageLoop = ChromiumStatic.getMessageLoop();
		messageLoop.pause();
		messageLoop.disablePump();
		final int ret = browser.on_before_popup(id, popupFeaturesPtr,
				windowInfo, client);
		messageLoop.unpause();
		return ret;
	}
}