/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.set.browser.cef;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.set.browser.Browser;
import org.eclipse.set.browser.BrowserFunction;
import org.eclipse.set.browser.OpenWindowListener;
import org.eclipse.set.browser.WebBrowser;
import org.eclipse.set.browser.WindowEvent;
import org.eclipse.set.browser.cef.CEFFactory.ReturnType;
import org.eclipse.set.browser.cef.handlers.browser.ClientHandler;
import org.eclipse.set.browser.cef.handlers.browser.PopupClientHandler;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.FunctionSt;
import org.eclipse.set.browser.lib.cef_jsdialog_handler_t;
import org.eclipse.set.browser.lib.cef_popup_features_t;
import org.eclipse.set.browser.lib.cef_string_visitor_t;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.browser.AuthenticationListener;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Chromium extends WebBrowser {
	public static interface EvalReturned {
		void invoke(int loop, int type, long value);
	}

	final class CEFFocusListener implements FocusListener {
		private boolean enabled = true;

		@Override
		public void focusGained(final FocusEvent e) {
			if (!enabled) {
				return;
			}
			browserFocus(true);
		}

		@Override
		public void focusLost(final FocusEvent e) {
			if (!enabled) {
				return;
			}
			enabled = false;
			browserFocus(false);
			enabled = true;
		}
	}

	enum Dispose {
		DoIt, FromBrowser, FromClose, FromDispose, No, Unload, UnloadClosed, WaitIfClosed,
	}

	private static final String DATA_TEXT_URL = "data:text/html;base64,";

	private static int EVAL = 1;

	private static final int LOOP = 75;

	private static final int MAX_PROGRESS = 100;

	private static final String SET_TEXT_URL = "swt.chromium.setText.";

	private static String getPlainUrl(final String url) {
		if (url != null && url.startsWith(DATA_TEXT_URL)) {
			return url.substring(0, DATA_TEXT_URL.length() - 8);
		}
		if (url != null && url.startsWith("file:/")
				&& url.contains(SET_TEXT_URL)) {
			return "about:blank";
		}
		return url;
	}

	static long checkGetAddress(final Callback cb) {
		final long address = cb.getAddress();
		if (address == 0) {
			throw new SWTError(SWT.ERROR_NO_MORE_CALLBACKS);
		}
		return address;
	}

	static void disposeCallback(final Callback cb) {
		if (cb != null) {
			cb.dispose();
		}
	}

	static void doSetUrlPost(final long browser, final String url,
			final String postData, final String[] headers) {
		final byte[] bytes = postData != null
				? postData.getBytes(Charset.forName("ASCII"))
				: null;
		final int bytesLength = postData != null ? bytes.length : 0;
		final int headersLength = headers != null ? headers.length : 0;
		final String joinHeaders = headers == null ? null
				: String.join("::", headers);
		ChromiumLib.cefswt_load_url(browser, url, bytes, bytesLength,
				joinHeaders, headersLength);
	}

	static void freeDelayed(final long ptr) {
		Display.getDefault().asyncExec(() -> C.free(ptr));
	}

	private long browser;
	private boolean canGoBack;

	private boolean canGoForward;

	private ClientHandler clientHandler;

	private final CompletableFuture<Boolean> created = new CompletableFuture<>();

	private Dispose disposing = Dispose.No;

	private CompletableFuture<Boolean> enableProgress = new CompletableFuture<>();

	private FocusListener focusListener;

	private boolean hasFocus;

	private String[] headers;

	private long hwnd;

	private boolean ignoreFirstFocus = true;

	private WindowEvent isPopup;

	private PaintListener paintListener;

	private String postData;

	private String text = "";

	private CompletableFuture<String> textReady;

	private cef_string_visitor_t textVisitor;

	private String url;

	Browser chromium;

	int instance;

	PopupClientHandler popupClientHandler;

	public Chromium() {
	}

	@Override
	public boolean back() {
		if (canGoBack) {
			ChromiumLib.cefswt_go_back(browser);
			return true;
		}
		return false;
	}

	@Override
	public boolean close() {
		if (disposing != Dispose.No || isDisposed()) {
			return false;
		}
		if (browser == 0) {
			return true;
		}
		boolean closed = false;
		disposing = Dispose.FromClose;
		ChromiumLib.cefswt_close_browser(browser, 0);

		final long t = System.currentTimeMillis();
		long end = t + 10000;
		final Shell shell = chromium.getShell();
		final Display display = shell.getDisplay();
		while (!shell.isDisposed() && System.currentTimeMillis() < end) {
			// debug("in close, disposing:"+disposing);
			if (disposing == Dispose.Unload) {
				// debug("in close, disposing:"+disposing);
				end += 1000;
			} else if (disposing == Dispose.UnloadClosed) {
				disposing = Dispose.WaitIfClosed;
				end = System.currentTimeMillis() + LOOP * 2;
			} else if (disposing == Dispose.DoIt) {
				closed = true;
				break;
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (!closed) {
			disposing = Dispose.No;
		}
		return closed;
	}

	@Override
	public void create(final Composite parent, final int style) {
		ChromiumStatic.initCEF(chromium.getDisplay());

		clientHandler = new ClientHandler(this);
		popupClientHandler = new PopupClientHandler(this);

		chromium.setBackground(
				parent.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
		paintListener = new PaintListener() {
			@Override
			public void paintControl(final PaintEvent e) {
				debugPrint("paintControl");
				chromium.removePaintListener(this);
				createBrowser();
				paintListener = null;
			}
		};
		chromium.addPaintListener(paintListener);
	}

	@Override
	public void createFunction(final BrowserFunction function) {
		created.thenRun(() -> {
			checkBrowser();

			for (final BrowserFunction current : functions.values()) {
				if (current.name.equals(function.name)) {
					deregisterFunction(current);
					break;
				}
			}
			function.index = getNextFunctionIndex();
			registerFunction(function);

			if (!ChromiumLib.cefswt_function(browser, function.name,
					function.index)) {
				throw new SWTException("Cannot create BrowserFunction");
			}
		});
	}

	@Override
	public void destroyFunction(final BrowserFunction function) {
		checkBrowser();
		deregisterFunction(function);
	}

	public void dispose() {
		debugPrint("in dispose, disposing " + disposing);
		if (disposing == Dispose.FromDispose || isDisposed()) {
			return;
		}
		final boolean callClose = disposing == Dispose.No;
		disposing = Dispose.FromDispose;
		ChromiumStatic.disposingAny++;
		if (focusListener != null) {
			chromium.removeFocusListener(focusListener);
		}
		focusListener = null;
		if (browser != 0 && callClose) {
			debugPrint("call close_browser");
			ChromiumLib.cefswt_close_browser(browser, 1);
			waitForClose(Display.getDefault());
		}
	}

	public int do_close(final long browser) {
		if (!ChromiumLib.cefswt_is_same(Chromium.this.browser, browser)) {
			debugPrint(
					"DoClose popup:" + Chromium.this.browser + ":" + browser);
			return 0;
		}
		final Display display = Display.getDefault();
		if (!isDisposed() && closeWindowListeners != null) {
			final org.eclipse.swt.browser.WindowEvent event = new org.eclipse.swt.browser.WindowEvent(
					chromium);
			event.display = display;
			event.widget = chromium;
			for (final CloseWindowListener listener : closeWindowListeners) {
				listener.close(event);
			}
		}

		if (disposing == Dispose.FromClose || disposing == Dispose.Unload
				|| disposing == Dispose.UnloadClosed
				|| disposing == Dispose.WaitIfClosed) {
			disposing = Dispose.DoIt;
		} else if (disposing == Dispose.No) {
			if (chromium != null) {
				disposing = Dispose.FromBrowser;
				chromium.dispose();
			}
		}
		clientHandler.dispose();
		popupClientHandler.dispose();
		return 1;
	}

	@Override
	public Object evaluate(final String script) throws SWTException {
		if (!jsEnabled) {
			return null;
		}
		if (browser == 0) {
			if (paintListener != null) {
				chromium.removePaintListener(paintListener);
				paintListener = null;
				createBrowser();
			}
		}
		checkBrowser();
		final Object[] ret = new Object[1];
		final EvalReturned callback = (loop, type, valuePtr) -> {
			if (loop == 1) {
				chromium.getDisplay().readAndDispatch();
			} else {
				final String value = ChromiumLib
						.cefswt_cstring_to_java(valuePtr);
				debugPrint("eval returned: " + type + ":" + value);
				ret[0] = mapType(type, value);
			}
		};
		final Callback callback_cb = new Callback(callback, "invoke",
				void.class, new Type[] { int.class, int.class, long.class });

		final StringBuilder buffer = new StringBuilder("(function() {");
		buffer.append("\n");
		buffer.append(script);
		buffer.append("\n})()");

		final boolean returnSt = ChromiumLib.cefswt_eval(browser,
				buffer.toString(), EVAL++, checkGetAddress(callback_cb));
		disposeCallback(callback_cb);
		if (!returnSt) {
			throw new SWTException("Script that was evaluated failed");
		}
		return ret[0];
	}

	@Override
	public boolean execute(final String script) {
		if (!jsEnabled) {
			return false;
		}
		enableProgress.thenRun(() -> {
			ChromiumLib.cefswt_execute(browser, script);
		});
		return true;
	}

	@Override
	public boolean forward() {
		if (canGoForward) {
			ChromiumLib.cefswt_go_forward(browser);
			return true;
		}
		return false;
	}

	public int get_auth_credentials(final long browser2, final long frame,
			final long host, final int port, final long realm,
			final long callback) {
		if (isDisposed()) {
			return 0;
		}

		final AuthenticationEvent event = new AuthenticationEvent(chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.doit = true;
		String protocol = "http";
		try {
			final URL u = new URL(this.url);
			protocol = u.getProtocol();
		} catch (final MalformedURLException e) {
		}
		final String hostStr = host != 0
				? ChromiumLib.cefswt_cefstring_to_java(host)
				: "";
		final String realmStr = realm != 0
				? ChromiumLib.cefswt_cefstring_to_java(realm)
				: null;
		event.location = protocol + "://" + hostStr;
		debugPrint("get_auth_credentials:" + event.location);
		chromium.getDisplay().syncExec(() -> {
			for (final AuthenticationListener listener : authenticationListeners) {
				listener.authenticate(event);
			}
			if (event.doit && event.user == null && event.password == null) {
				new AuthDialog(chromium.getShell()).open(event, realmStr);
			}
		});
		ChromiumLib.cefswt_auth_callback(callback, event.user, event.password,
				event.doit ? 1 : 0);
		return event.doit ? 1 : 0;
	}

	@Override
	public String getBrowserType() {
		return "chromium";
	}

	@Override
	public String getText() {
		checkBrowser();
		return text;
	}

	@Override
	public String getUrl() {
		if (browser == 0) {
			if (this.url == null) {
				return "about:blank";
			}
			return getPlainUrl(this.url);
		}
		final long urlPtr = ChromiumLib.cefswt_get_url(browser);
		String cefurl = null;
		if (urlPtr != 0) {
			cefurl = ChromiumLib.cefswt_cstring_to_java(urlPtr);
		}
		// debugPrint("getUrl1:" + cefurl);
		if (cefurl == null) {
			cefurl = getPlainUrl(this.url);
		} else {
			cefurl = getPlainUrl(cefurl);
		}
		return cefurl;
	}

	@Override
	public boolean isBackEnabled() {
		return canGoBack;
	}

	@Override
	public boolean isFocusControl() {
		return hasFocus;
	}

	@Override
	public boolean isForwardEnabled() {
		return canGoForward;
	}

	public void on_address_change(final long browser, final long frame,
			final long url) {
		if (isDisposed() || locationListeners == null) {
			return;
		}
		final LocationEvent event = new LocationEvent(chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.doit = true;
		event.location = getPlainUrl(ChromiumLib.cefswt_cefstring_to_java(url));
		event.top = ChromiumLib.cefswt_is_main_frame(frame);
		if (!enableProgress.isDone()) {
			debugPrint("!on_address_change to " + event.location + " "
					+ (event.top ? "main" : "!main"));
			return;
		}
		debugPrint("on_address_change to " + event.location + " "
				+ (event.top ? "main" : "!main"));
		chromium.getDisplay().asyncExec(() -> {
			for (final LocationListener listener : locationListeners) {
				listener.changed(event);
			}
		});
	}

	public void on_after_created(final long browser) {
		if (isDisposed() || visibilityWindowListeners == null) {
			return;
		}
		debugPrint("on_after_created: " + browser);
		if (browser != 0) {
			Chromium.this.browser = browser;
			if (this.isPopup == null) {
				final org.eclipse.swt.graphics.Point size = getChromiumSize();
				ChromiumLib.cefswt_resized(browser, size.x, size.y);
			}
			if (this.isPopup != null && this.url != null) {
				debugPrint("load url after created");
				doSetUrlPost(browser, url, postData, headers);
			} else if (!"about:blank".equals(this.url)) {
				enableProgress.complete(true);
			}
		}
		created.complete(true);

		if (ChromiumStatic.browsers.get() == 1) {
			debugPrint("STARTING MSG LOOP");
			ChromiumStatic.getMessageLoop().start();
		}

		debugPrint("on_after_created handling " + browser);
		if (isDisposed() || visibilityWindowListeners == null) {
			return;
		}
		final org.eclipse.swt.browser.WindowEvent event = new org.eclipse.swt.browser.WindowEvent(
				chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.size = new Point(0, 0);
		event.location = new Point(0, 0);
		if (isPopup != null) {
			event.size = isPopup.size;
			event.location = isPopup.location;
			event.addressBar = isPopup.addressBar;
			event.menuBar = isPopup.menuBar;
			event.statusBar = isPopup.statusBar;
			event.toolBar = isPopup.toolBar;

			if (event.size != null && !event.size.equals(new Point(0, 0))) {
				final Point size = event.size;
				chromium.getShell().setSize(
						chromium.getShell().computeSize(size.x, size.y));
			}

			for (final VisibilityWindowListener listener : visibilityWindowListeners) {
				listener.show(event);
			}
		}
		try {
			// not sleeping here causes deadlock with multiple window.open
			Thread.sleep(LOOP);
		} catch (final InterruptedException e) {
		}
	}

	public int on_before_browse(final long browser2, final long frame,
			final long request) {
		if (isDisposed() || locationListeners == null) {
			return 0;
		}
		if (ChromiumLib.cefswt_is_main_frame(frame)) {
			final LocationEvent event = new LocationEvent(chromium);
			event.display = chromium.getDisplay();
			event.widget = chromium;
			event.doit = true;
			event.location = getPlainUrl(
					ChromiumLib.cefswt_request_to_java(request));
			debugPrint("on_before_browse:" + event.location);
			try {
				ChromiumStatic.getMessageLoop().pause();
				for (final LocationListener listener : locationListeners) {
					listener.changing(event);
				}
			} finally {
				ChromiumStatic.getMessageLoop().unpause();
			}
			if (!event.doit) {
				debugPrint("canceled nav, dependats:"
						+ enableProgress.getNumberOfDependents());
				enableProgress = new CompletableFuture<>();
			}
			return event.doit ? 0 : 1;
		}
		return 0;
	}

	public void on_before_close(final long browser) {
		this.browser = 0;
		this.chromium = null;
		if (textVisitor != null) {
			Display.getCurrent().asyncExec(() -> {
				if (textVisitor != null) {
					freeTextVisitor();
				}
			});
		}
	}

	public int on_before_popup(final long browser, final long popupFeaturesPtr,
			final long windowInfo, final long client) {
		if (isDisposed()) {
			return 1;
		}
		if (openWindowListeners == null) {
			return 0;
		}

		final WindowEvent event = new WindowEvent(chromium);

		final cef_popup_features_t popupFeatures = new cef_popup_features_t();
		ChromiumLib.memmove(popupFeatures, popupFeaturesPtr);

		try {
			// not sleeping here causes deadlock with multiple window.open
			Thread.sleep(LOOP);
		} catch (final InterruptedException e) {
		}
		chromium.getDisplay().syncExec(() -> {
			debugPrint("on_before_popup syncExec" + browser);
			event.display = chromium.getDisplay();
			event.widget = chromium;
			event.required = false;
			event.addressBar = popupFeatures.locationBarVisible == 1;
			event.menuBar = popupFeatures.menuBarVisible == 1;
			event.statusBar = popupFeatures.statusBarVisible == 1;
			event.toolBar = popupFeatures.toolBarVisible == 1;
			final int x = popupFeatures.xSet == 1 ? popupFeatures.x : 0;
			final int y = popupFeatures.ySet == 1 ? popupFeatures.y : 0;
			event.location = popupFeatures.xSet == 1 || popupFeatures.ySet == 1
					? new Point(x, y)
					: null;
			final int width = popupFeatures.widthSet == 1 ? popupFeatures.width
					: 0;
			final int height = popupFeatures.heightSet == 1
					? popupFeatures.height
					: 0;
			event.size = popupFeatures.widthSet == 1
					|| popupFeatures.heightSet == 1 ? new Point(width, height)
							: null;

			for (final OpenWindowListener listener : openWindowListeners) {
				listener.open(event);
			}

			if (event.browser != null) {
				if (((Chromium) event.browser.webBrowser).instance == 0) {
					((Chromium) event.browser.webBrowser)
							.createPopup(windowInfo, client, event);
				} else {
					event.required = true;
				}
			} else if (!event.required) {
				instance = ++ChromiumStatic.INSTANCES;
				ChromiumLib.cefswt_set_window_info_parent(windowInfo, client,
						popupClientHandler.get().ptr, 0,
						event.location != null ? event.location.x : 0,
						event.location != null ? event.location.y : 0,
						event.size != null ? event.size.x : 0,
						event.size != null ? event.size.y : 0);
			}
		});

		if (event.browser == null && event.required) {
			return 1;
		}
		if (event.browser != null && event.required) {
			return 1;
		}
		return 0;
	}

	public int on_before_unload_dialog(final long browser,
			final long message_text, final int is_reload, final long callback) {
		if (disposing == Dispose.FromClose) {
			disposing = Dispose.Unload;

			if (ChromiumStatic.useSwtDialogs()) {
				final String msg = ChromiumLib
						.cefswt_cefstring_to_java(message_text);
				openJsDialog(cef_jsdialog_handler_t.JSDIALOGTYPE_PROMPT,
						"Are you sure you want to leave this page?", msg, 0,
						callback);
				disposing = Dispose.UnloadClosed;
				return 1;
			}
		}
		return 0;
	}

	public void on_dialog_closed(final long browser) {
		if (disposing == Dispose.Unload) {
			disposing = Dispose.UnloadClosed;
		}
	}

	public void on_got_focus(final long browser2) {
		if (!isDisposed()) {
			hasFocus = true;
			if (!isDisposed()
					&& chromium.getDisplay().getFocusControl() != null) {
				chromium.setFocus();
			}
			browserFocus(true);
		}
	}

	public int on_jsdialog(final long browser, final long origin_url,
			final int dialog_type, final long message_text,
			final long default_prompt_text, final long callback) {
		if (isDisposed()) {
			return 0;
		}

		// String prompt =
		// ChromiumLib.cefswt_cefstring_to_java(default_prompt_text);
		final String url = ChromiumLib.cefswt_cefstring_to_java(origin_url);
		final String title = getPlainUrl(url);
		final String msg = ChromiumLib.cefswt_cefstring_to_java(message_text);
		openJsDialog(dialog_type, title, msg, default_prompt_text, callback);
		return 1;
	}

	public void on_loading_state_change(final long browser, final int isLoading,
			final int canGoBack, final int canGoForward) {
		Chromium.this.canGoBack = canGoBack == 1;
		Chromium.this.canGoForward = canGoForward == 1;
		if (isDisposed() || progressListeners == null) {
			return;
		}
		if (isLoading == 0) {
			for (final BrowserFunction function : functions.values()) {
				if (function.index != 0) {
					if (!ChromiumLib.cefswt_function(browser, function.name,
							function.index)) {
						throw new SWTException("Cannot create BrowserFunction");
					}
				}
			}
		}
		updateText();
		if (isPopup != null) {
			textReady.thenRun(() -> enableProgress.complete(true));
		} else if (!enableProgress.isDone() && isLoading == 0) {
			textReady.thenRun(() -> {
				enableProgress.complete(true);
			});
			return;
		} else if (!enableProgress.isDone()) {
			return;
		}
		final ProgressEvent event = new ProgressEvent(chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.current = isLoading == 1 ? 1 : MAX_PROGRESS;
		event.total = MAX_PROGRESS;
		if (isLoading == 1) {
			debugPrint("progress changed");
			for (final ProgressListener listener : progressListeners) {
				listener.changed(event);
			}
		} else {
			textReady.thenRun(() -> {
				debugPrint("progress completed");
				chromium.getDisplay().asyncExec(() -> {
					for (final ProgressListener listener : progressListeners) {
						listener.completed(event);
					}
				});
			});
		}
	}

	public int on_process_message_received(final long browser, final int source,
			final long processMessage) {
		if (source != CEFFactory.PID_RENDERER || !jsEnabled
				|| chromium == null) {
			return 0;
		}
		final FunctionSt fn = new FunctionSt();
		ChromiumLib.cefswt_function_id(processMessage, fn);
		final int id = fn.id;
		if (id < 0) {
			return 0;
		}
		final int argsSize = fn.args;
		final Object[] args = new Object[argsSize];
		for (int i = 0; i < argsSize; i++) {
			final int arg = i;
			final EvalReturned callback = (loop, type, valuePtr) -> {
				if (loop == 0) {
					final String value = ChromiumLib
							.cefswt_cstring_to_java(valuePtr);
					args[arg] = mapType(type, value);
				}
			};
			final Callback callback_cb = new Callback(callback, "invoke",
					void.class,
					new Type[] { int.class, int.class, long.class });
			ChromiumLib.cefswt_function_arg(processMessage, i,
					checkGetAddress(callback_cb));
			disposeCallback(callback_cb);
		}
		final Object ret = functions.get(id).function(args);

		final Object[] returnPair = convertType(ret);
		final ReturnType returnType = (ReturnType) returnPair[0];
		final String returnStr = (String) returnPair[1];
		ChromiumLib.cefswt_function_return(browser, id, fn.port,
				returnType.intValue(), returnStr);

		return 1;
	}

	public int on_set_focus(final long browser) {
		if (ignoreFirstFocus) {
			ignoreFirstFocus = false;
			return 1;
		}
		return 0;
	}

	public void on_status_message(final long browser, final long status) {
		if (isDisposed() || statusTextListeners == null) {
			return;
		}
		final String str = status == 0 ? ""
				: ChromiumLib.cefswt_cefstring_to_java(status);
		final StatusTextEvent event = new StatusTextEvent(chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.text = str;
		for (final StatusTextListener listener : statusTextListeners) {
			listener.changed(event);
		}
	}

	public void on_take_focus(final long browser, final int next) {
		hasFocus = false;
		Control[] tabOrder = chromium.getParent().getTabList();
		if (tabOrder.length == 0) {
			tabOrder = chromium.getParent().getChildren();
		}
		final int indexOf = Arrays.asList(tabOrder).indexOf(chromium);
		if (indexOf != -1) {
			final int newIndex = next == 1 ? indexOf + 1 : indexOf - 1;
			if (newIndex > 0 && newIndex < tabOrder.length
					&& !tabOrder[newIndex].isDisposed()) {
				tabOrder[newIndex].setFocus();
				return;
			}
		}
		if (!isDisposed() && !chromium.getParent().isDisposed()) {
			chromium.getParent().setFocus();
		}
	}

	public void on_title_change(final long browser, final long title) {
		if (isDisposed() || titleListeners == null) {
			return;
		}
		final String full_str = ChromiumLib.cefswt_cefstring_to_java(title);
		final String str = getPlainUrl(full_str);
		final TitleEvent event = new TitleEvent(chromium);
		event.display = chromium.getDisplay();
		event.widget = chromium;
		event.title = str;
		for (final TitleListener listener : titleListeners) {
			listener.changed(event);
		}
	}

	@Override
	public void refresh() {
		jsEnabled = jsEnabledOnNextPage;
		if (browser != 0) {
			ChromiumLib.cefswt_reload(browser);
		}
	}

	public int run_context_menu(final long browser2, final long callback) {
		if (chromium.getMenu() != null) {
			chromium.getMenu().setVisible(true);
			ChromiumLib.cefswt_context_menu_cancel(callback);
			return 1;
		}
		return 0;
	}

	@Override
	public void setBrowser(final Browser browser) {
		this.chromium = browser;
	}

	@Override
	public boolean setText(final String html, final boolean trusted) {
		if (html.contains("file:/")) { // file:/ resources not supported with
										// data:text
			try {
				final Path tmp = Files.createTempFile(SET_TEXT_URL, ".html");
				Files.write(tmp, html.getBytes());
				tmp.toFile().deleteOnExit();
				final boolean s = setUrl(tmp.toUri().toString(), null, null);
				return s;
			} catch (final IOException e) {
			}
		}
		final String texturl = DATA_TEXT_URL
				+ Base64.getEncoder().encodeToString(html.getBytes());
		return setUrl(texturl, null, null);
	}

	@Override
	public boolean setUrl(final String url, final String postData,
			final String[] headers) {
		// if not yet created will be used when created
		this.url = url;
		this.postData = postData;
		this.headers = headers;
		jsEnabled = jsEnabledOnNextPage;
		if (!isDisposed() && browser != 0) {
			debugPrint("set url: " + url);
			doSetUrl(url, postData, headers);
		}
		return true;
	}

	@Override
	public void stop() {
		if (browser != 0) {
			ChromiumLib.cefswt_stop(browser);
		}
	}

	private synchronized void checkBrowser() {
		if (browser == 0) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
	}

	private Object[] convertType(final Object ret) {
		ReturnType returnType = ReturnType.Error;
		String returnStr = "";
		if (ret == null) {
			returnType = ReturnType.Null;
			returnStr = "null";
		} else if (Boolean.class.isInstance(ret)) {
			returnType = ReturnType.Bool;
			returnStr = Boolean.TRUE.equals(ret) ? "1" : "0";
		} else if (Number.class.isInstance(ret)) {
			returnType = ReturnType.Double;
			returnStr = NumberFormat.getInstance(Locale.US).format(ret);
		} else if (String.class.isInstance(ret)) {
			returnType = ReturnType.Str;
			returnStr = ret.toString();
		} else if (ret.getClass().isArray()) {
			returnType = ReturnType.Array;
			final StringBuilder buffer = new StringBuilder();
			buffer.append("\"");
			for (int i = 0; i < Array.getLength(ret); i++) {
				if (i > 0) {
					buffer.append(";");
				}
				final Object[] arrayElem = convertType(Array.get(ret, i));
				buffer.append("'");
				buffer.append(((ReturnType) arrayElem[0]).intValue());
				buffer.append(",");
				buffer.append((String) arrayElem[1]);
				buffer.append("'");
			}
			buffer.append("\"");
			returnStr = buffer.toString();
		} else {
			returnStr = "Unsupported return type " + ret.getClass().getName();
		}
		return new Object[] { returnType, returnStr };
	}

	private void createBrowser() {
		if (this.url == null) {
			this.url = "about:blank";
		}
		prepareBrowser();
		final Display display = chromium.getDisplay();
		final Color bg = chromium.getBackground();
		final Color bgColor = bg != null ? bg
				: display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		final int cefBgColor = ChromiumStatic.cefColor(bgColor.getAlpha(),
				bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());

		final org.eclipse.swt.graphics.Point size = getChromiumSize();

		instance = ++ChromiumStatic.INSTANCES;
		ChromiumStatic.instances.put(instance, this);
		ChromiumLib.cefswt_create_browser(hwnd, url, clientHandler.get().ptr,
				size.x, size.y, jsEnabledOnNextPage ? 1 : 0, cefBgColor);
	}

	private void createPopup(final long windowInfo, final long client,
			final WindowEvent event) {
		if (paintListener != null) {
			chromium.removePaintListener(paintListener);
			paintListener = null;
		} else {
			// TODO: destroy browser first?
			ChromiumStatic.instances.remove(instance);
		}
		instance = ++ChromiumStatic.INSTANCES;
		ChromiumStatic.instances.put(instance, this);
		isPopup = event;

		prepareBrowser();
		final long popupHandle = hwnd;
		debugPrint("popup will use hwnd:" + popupHandle);
		final Point size = new Point(0, 0);
		ChromiumLib.cefswt_set_window_info_parent(windowInfo, client,
				clientHandler.get().ptr, popupHandle, 0, 0, size.x, size.y);
		debugPrint("reparent popup");
	}

	private void debugPrint(final String log) {
		if (true) {
			System.out.println("J" + instance + ":"
					+ Thread.currentThread().getName() + ":" + log
					+ (this.url != null ? " (" + getPlainUrl(this.url) + ")"
							: " empty-url"));
		}
	}

	private CompletableFuture<Void> doSetUrl(final String url,
			final String postData, final String[] headers) {
		return enableProgress.thenRun(() -> {
			debugPrint("load url");
			doSetUrlPost(browser, url, postData, headers);
		});
	}

	private void freeTextVisitor() {
		debugPrint("free text visitor");
		disposeCallback(textVisitor.visit_cb);
		freeDelayed(textVisitor.ptr);
		textVisitor = null;
	}

	private Point getChromiumSize() {
		return DPIUtil.autoScaleUp(chromium.getSize());
	}

	private Object mapType(final int type, final String value)
			throws SWTException {
		if (type == ReturnType.Error.intValue()) {
			if ((SWT.ERROR_INVALID_RETURN_VALUE + "").equals(value)) {
				throw new SWTException(SWT.ERROR_INVALID_RETURN_VALUE);
			}
			throw new SWTException(SWT.ERROR_FAILED_EVALUATE, value);
		} else if (type == ReturnType.Null.intValue()) {
			return null;
		} else if (type == ReturnType.Bool.intValue()) {
			return "1".equals(value) ? Boolean.TRUE : Boolean.FALSE;
		} else if (type == ReturnType.Double.intValue()) {
			return Double.parseDouble(value);
		} else if (type == ReturnType.Array.intValue()) {
			final String value_unquoted = value.substring(1,
					value.length() - 1);
			final String[] elements = value_unquoted
					.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
			final Object[] array = new Object[elements.length];
			for (int i = 0; i < array.length; i++) {
				final String elemUnquoted = elements[i].substring(1,
						elements[i].length() - 1);
				final String[] parts = elemUnquoted
						.split(",(?=(?:[^']*'[^']*')*[^']*$)", 2);
				final ReturnType elemType = CEFFactory.ReturnType
						.from(parts[0]);
				final Object elemValue = mapType(elemType.intValue(), parts[1]);
				array[i] = elemValue;
			}
			return array;
		} else {
			return value;
		}
	}

	private void openJsDialog(final int dialog_type, final String title,
			final String msg, final long default_prompt_text,
			final long callback) {
		int style = SWT.ICON_WORKING;
		switch (dialog_type) {
		case cef_jsdialog_handler_t.JSDIALOGTYPE_ALERT:
			style = SWT.ICON_INFORMATION;
			break;
		case cef_jsdialog_handler_t.JSDIALOGTYPE_CONFIRM:
			style = SWT.ICON_WARNING;
			break;
		case cef_jsdialog_handler_t.JSDIALOGTYPE_PROMPT:
			style = SWT.ICON_QUESTION | SWT.YES | SWT.NO;
			break;
		}
		final Consumer<Integer> close = open -> {
			final int r = open == SWT.OK || open == SWT.YES ? 1 : 0;
			ChromiumLib.cefswt_dialog_close(callback, r, default_prompt_text);
			chromium.getShell().forceActive();
		};
		if (!"test".equals(System.getProperty("swt.chromium.dialogs", ""))) {
			final MessageBox box = new MessageBox(chromium.getShell(), style);
			box.setText(title);
			box.setMessage(msg);
			final int open = box.open();
			close.accept(open);
		} else {
			@SuppressWarnings("unchecked")
			final CompletableFuture<Integer> f = (CompletableFuture<Integer>) chromium
					.getData("swt.chromium.dialogs");
			if (f != null) {
				f.thenAccept(close);
				chromium.setData("swt.chromium.dialogs", null);
				while (!f.isDone()) {
					if (!chromium.getDisplay().readAndDispatch()) {
						chromium.getDisplay().sleep();
					}
				}
			}
		}
	}

	private void prepareBrowser() {
		hwnd = getHandle(chromium);

		chromium.addDisposeListener(e -> {
			debugPrint("disposing chromium");
			dispose();
		});
		focusListener = new CEFFocusListener();
		chromium.addFocusListener(focusListener);

		chromium.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				if (!isDisposed() && browser != 0) {
					final Point size = getChromiumSize();
					ChromiumLib.cefswt_resized(browser, size.x, size.y);
				}
			}
		});
	}

	private void set_text_visitor() {
		textVisitor = new cef_string_visitor_t();
		textVisitor.visit_cb = new Callback(this, "textVisitor_visit",
				void.class, new Type[] { long.class, long.class });
		textVisitor.visit = checkGetAddress(textVisitor.visit_cb);
		textVisitor.ptr = C.malloc(cef_string_visitor_t.sizeof);
		textVisitor.refs = 1;
		ChromiumLib.memmove(textVisitor.ptr, textVisitor);
	}

	private void updateText() {
		if (browser != 0 && !isDisposed() && disposing == Dispose.No) {
			debugPrint("update text");
			if (textVisitor != null) {
				textVisitor.refs++;
			} else {
				set_text_visitor();
			}
			textReady = new CompletableFuture<>();
			ChromiumLib.cefswt_get_text(browser, textVisitor.ptr);
		}
	}

	private void waitForClose(final Display display) {
		if (display == null || display.isDisposed()) {
			return;
		}
		display.asyncExec(() -> {
			if (browser != 0) {
				waitForClose(display);
			}
		});
	}

	protected void browserFocus(final boolean set) {
		if (!isDisposed() && browser != 0) {
			final long parent = Display.getDefault().getActiveShell() == null
					? 0
					: getHandle(chromium.getParent());
			if (chromium.getDisplay().getActiveShell() != chromium.getShell()) {
				return;
			}
			ChromiumLib.cefswt_set_focus(browser, set, parent);
		}
	}

	protected long getHandle(final Composite control) {
		return control.handle;
	}

	boolean isDisposed() {
		return chromium == null || chromium.isDisposed();
	}

	void textVisitor_visit(final long self, final long cefString) {
		if (--textVisitor.refs == 0) {
			freeTextVisitor();
		}

		final String newtext = cefString != 0
				? ChromiumLib.cefswt_cefstring_to_java(cefString)
				: null;
		if (newtext != null) {
			text = newtext;
			textReady.complete(text);
		}
	}

}
