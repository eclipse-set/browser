package org.eclipse.set.browser.cef.handlers;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.ChromiumStatic;
import org.eclipse.set.browser.lib.cef_browser_process_handler_t;
import org.eclipse.swt.internal.Callback;

public class BrowserProcessHandler
		extends AbstractHandler<cef_browser_process_handler_t> {
	private final Callback on_schedule_message_pump_work_cb = new Callback(this,
			"on_schedule_message_pump_work", void.class,
			new Type[] { long.class, int.class, int.class });

	public BrowserProcessHandler() {
		handler = new cef_browser_process_handler_t();
		handler.on_schedule_message_pump_work = on_schedule_message_pump_work_cb
				.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		on_schedule_message_pump_work_cb.dispose();
	}

	void on_schedule_message_pump_work(final long pbrowserProcessHandler,
			final int delay, final int _delay2) {
		if (ChromiumStatic.browsers.get() <= 0
				|| ChromiumStatic.disposingAny > 0) {
			return;
		}
		ChromiumStatic.getMessageLoop().scheduleMessagePumpWork(delay);
	}
}