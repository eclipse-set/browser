package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.cef_jsdialog_handler_t;
import org.eclipse.swt.internal.Callback;

public class JSDialogHandler extends AbstractBrowserHandler<cef_jsdialog_handler_t> {

	private final Callback on_before_unload_dialog_cb = new Callback(this,
			"on_before_unload_dialog", int.class, new Type[] { long.class,
					long.class, long.class, int.class, long.class });

	private final Callback on_dialog_closed_cb = new Callback(this,
			"on_dialog_closed", void.class,
			new Type[] { long.class, long.class });

	private final Callback on_jsdialog_cb = new Callback(this, "on_jsdialog",
			int.class, new Type[] { long.class, long.class, long.class,
					int.class, long.class, long.class, long.class, int.class });

	public JSDialogHandler(final Chromium browser) {
		super(browser);

		handler = new cef_jsdialog_handler_t();
		if (ChromiumStatic.useSwtDialogs()) {
			handler.on_jsdialog = on_jsdialog_cb.getAddress();
		}
		handler.on_dialog_closed = on_dialog_closed_cb.getAddress();
		handler.on_before_unload_dialog = on_before_unload_dialog_cb
				.getAddress();

		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_before_unload_dialog_cb.dispose();
		on_dialog_closed_cb.dispose();
		on_jsdialog_cb.dispose();
	}

	private int on_before_unload_dialog(final long self_, final long id,
			final long msg, final int is_reload, final long callback) {
		return browser.on_before_unload_dialog(id, msg, is_reload, callback);
	}

	private void on_dialog_closed(final long self_, final long id) {
		browser.on_dialog_closed(id);
	}

	private int on_jsdialog(final long self_, final long id,
			final long origin_url, final int dialog_type,
			final long message_text, final long default_prompt_text,
			final long callback, final int suppress_message) {
		return browser.on_jsdialog(id, origin_url, dialog_type, message_text,
				default_prompt_text, callback);
	}
}
