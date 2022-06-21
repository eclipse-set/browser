/**
 * Copyright (c) 2022 DB Netz AG and others.
 * Copyright (c) 2020 Equo
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Guillermo Zunino, Equo - initial implementation
 */
package org.eclipse.set.browser.cef;

import java.net.HttpCookie;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.set.browser.cef.handlers.AppHandler;
import org.eclipse.set.browser.cef.handlers.CookieVisitor;
import org.eclipse.set.browser.lib.CEFLibrary;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.swt.WebBrowser;
import org.eclipse.swt.widgets.Display;

/**
 * Static functionality for the Chromium Implementation
 */
public class ChromiumStatic {
	/**
	 * Number of active browsers
	 */
	public static AtomicInteger browsers = new AtomicInteger(0);

	/**
	 * Number of browsers currently being destroyed
	 */
	public static int disposingAny = 0;
	/**
	 * List of all browser instances
	 */
	public static Map<Integer, Chromium> instances = new HashMap<>();
	/**
	 * Number of instances
	 */
	public static int INSTANCES = 0;

	/**
	 * Whether chromium is shutting down globally
	 */
	public static boolean shuttingDown;
	private static AppHandler app;
	private static CookieVisitor cookieVisitor;

	private static MessageLoop messageLoop = new MessageLoop();

	/**
	 * @return the message loop
	 */
	public static MessageLoop getMessageLoop() {
		return messageLoop;
	}

	/**
	 * Shuts down the CEF implementation
	 */
	public static void shutdown() {
		if (app == null) {
			return;
		}
		if (browsers.get() == 0) {
			messageLoop.shutdown();
			ChromiumLib.cefswt_shutdown();

			cookieVisitor.dispose();
			app.dispose();
			app = null;
		} else if (!shuttingDown) {
			shuttingDown = true;
		}
	}

	private static int getDebugPort() {
		try {
			return Integer.parseInt(System.getProperty(
					"org.eclipse.swt.chromium.remote-debugging-port", "0"));
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	private static void setupCookies() {
		WebBrowser.NativeClearSessions = ChromiumLib::cefswt_delete_cookies;
		WebBrowser.NativeSetCookie = () -> {
			final List<HttpCookie> cookies = HttpCookie
					.parse(WebBrowser.CookieValue);
			for (final HttpCookie cookie : cookies) {
				long age = cookie.getMaxAge();
				if (age != -1) {
					age = Instant.now().plusSeconds(age).getEpochSecond();
				}
				WebBrowser.CookieResult = ChromiumLib.cefswt_set_cookie(
						WebBrowser.CookieUrl, cookie.getName(),
						cookie.getValue(), cookie.getDomain(), cookie.getPath(),
						cookie.getSecure() ? 1 : 0, cookie.isHttpOnly() ? 1 : 0,
						age);
				break;
			}
		};
		WebBrowser.NativeGetCookie = () -> cookieVisitor.getCookie();
	}

	static synchronized void initCEF(final Display display) {
		if (app == null) {
			CEFLibrary.loadLibraries();
			setupCookies();
			app = new AppHandler();
			cookieVisitor = new CookieVisitor();

			ChromiumLib.cefswt_init(app.get(),
					CEFLibrary.getSubprocessExePath(), CEFLibrary.getCEFPath(),
					CEFLibrary.getTempPath(), "de-DE", getDebugPort());

			display.disposeExec(() -> {
				if (ChromiumStatic.app == null || ChromiumStatic.shuttingDown) {
					// already shutdown
					return;
				}
				ChromiumStatic.shutdown();
			});
		}

	}
}
