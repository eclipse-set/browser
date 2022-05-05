package org.eclipse.set.browser.cef;

import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.swt.widgets.Display;

public class MessageLoop {
	private static final int LOOP = 75;

	public boolean loopDisable;

	public boolean loopShutdown = false;
	public Runnable loopWorkRunnable = () -> {
		final Display display = Display.getCurrent();
		if (display == null || display.isDisposed()) {
			return;
		}
		loop_work();
	};
	public boolean pumpDisable;
	private Runnable loopWork;

	public void disablePump() {
		pumpDisable = true;
	}

	public void pause() {
		loopDisable = true;
	}

	public void restartLoop(final Display display, final int ms) {
		if (loopWork != null) {
			display.timerExec(-1, loopWork);
			display.timerExec(LOOP + ms, loopWork);
		}
	}

	/**
	 * Note: May be called from other threads
	 * 
	 * @param delay
	 */
	public void scheduleMessagePumpWork(final int delay) {
		if (pumpDisable) {
			return;
		}

		final Display display = Display.getDefault();

		final Runnable scheduleWork = () -> {
			restartLoop(display, delay);
			display.timerExec(-1, loopWorkRunnable);
			display.timerExec(delay, loopWorkRunnable);
		};

		if (Display.getCurrent() != null) {
			if (delay <= 0) {
				restartLoop(display, 0);
				display.asyncExec(loopWorkRunnable);
			} else {
				scheduleWork.run();
			}
		} else {
			if (delay <= 0) {
				display.asyncExec(() -> {
					restartLoop(display, 0);
					loopWorkRunnable.run();
				});
			} else {
				display.asyncExec(scheduleWork);
			}
		}

	}

	public void shutdown() {
		loopShutdown = true;
	}

	public void start() {
		loopWork = () -> {
			if (!loopShutdown && !Display.getDefault().isDisposed()) {
				loop_work();
				Display.getDefault().timerExec(LOOP * 2, loopWork);
			}
		};
		Display.getDefault().timerExec(LOOP, loopWork);
	}

	public void stop() {
		loopShutdown = true;
	}

	public void unpause() {
		loopDisable = false;
	}

	private void loop_work() {
		if (!loopDisable) {
			if (ChromiumLib.cefswt_do_message_loop_work() == 0) {
				System.err.println("error looping chromium");
			}
			if (pumpDisable) {
				pumpDisable = false;
			}
		}
	}
}
