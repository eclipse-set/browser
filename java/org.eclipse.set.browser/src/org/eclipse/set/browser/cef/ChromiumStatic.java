package org.eclipse.set.browser.cef;

import java.net.HttpCookie;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.set.browser.WebBrowser;
import org.eclipse.set.browser.cef.handlers.AppHandler;
import org.eclipse.set.browser.cef.handlers.CookieVisitor;
import org.eclipse.set.browser.lib.CEFLibrary;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.swt.widgets.Display;

public class ChromiumStatic {
	public static AtomicInteger browsers = new AtomicInteger(0);

	public static int disposingAny = 0;
	public static Map<Integer, Chromium> instances = new HashMap<>();
	public static int INSTANCES = 0;

	public static boolean shuttingDown;
	private static AppHandler app;

	private static String cefPath;
	private static String cefrustPath;
	private static CookieVisitor cookieVisitor;

	private static MessageLoop messageLoop = new MessageLoop();

	/**
	 * @return the message loop
	 */
	public static MessageLoop getMessageLoop() {
		return messageLoop;
	}

	public static void shutdown() {
		if (app == null) {
			return;
		}
		if (browsers.get() == 0) {
			ChromiumLib.cefswt_shutdown();

			cookieVisitor.dispose();
			app.dispose();
			app = null;
		} else if (!shuttingDown) {
			shuttingDown = true;
		}
	}

	public static boolean useSwtDialogs() {
		return !"native"
				.equals(System.getProperty("swt.chromium.dialogs", "swt"));
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

	static int cefColor(final int a, final int r, final int g, final int b) {
		return a << 24 | r << 16 | g << 8 | b << 0;
	}

	static synchronized void initCEF(final Display display) {
		if (app == null) {
			CEFLibrary.loadLibraries();
			cefrustPath = CEFLibrary.getJNIPath();
			cefPath = CEFLibrary.getCEFPath();
			setupCookies();
			app = new AppHandler();
			cookieVisitor = new CookieVisitor();

			int debugPort = 0;
			try {
				debugPort = Integer.parseInt(System.getProperty(
						"org.eclipse.swt.chromium.remote-debugging-port", "0"));
			} catch (final NumberFormatException e) {
				debugPort = 0;
			}
			ChromiumLib.cefswt_init(app.get().ptr, cefrustPath, cefPath, "",
					debugPort);

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