/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_cookie_visitor_t;
import org.eclipse.set.browser.swt.WebBrowser;
import org.eclipse.swt.internal.Callback;

/**
 * Visitor to read CEF Cookies for the WebBrowser class
 * 
 * @author Stuecker
 */
public class CookieVisitor extends AbstractHandler<cef_cookie_visitor_t> {
	private CompletableFuture<Boolean> cookieVisited;

	private final Callback visit_cb = new Callback(this, "cookieVisitor_visit",
			int.class, new Type[] { long.class, long.class, int.class,
					int.class, int.class });

	/**
	 * Constructor
	 */
	public CookieVisitor() {
		handler = new cef_cookie_visitor_t();
		handler.visit = visit_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		visit_cb.dispose();
	}

	/**
	 * Attempts to retrieve the requested cookie for WebBrowser
	 * 
	 * IMPROVE: Handle this in chromium_swt instead
	 */
	public void getCookie() {
		cookieVisited = new CompletableFuture<>();
		final boolean result = ChromiumLib
				.cefswt_get_cookie(WebBrowser.CookieUrl, handler.ptr);
		if (!result) {
			cookieVisited = null;
			throw new RuntimeException("Failed to get cookies");
		}
		try {
			cookieVisited.get(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException
				| TimeoutException e) {
			// no cookies found
		} finally {
			cookieVisited = null;
		}
	}

	@SuppressWarnings("unused")
	private int cookieVisitor_visit(final long self, final long cefcookie,
			final int count, final int total, final int delete) {
		final String name = ChromiumLib.cefswt_cookie_to_java(cefcookie);
		if (WebBrowser.CookieName != null
				&& WebBrowser.CookieName.equals(name)) {
			final String value = ChromiumLib.cefswt_cookie_value(cefcookie);
			WebBrowser.CookieValue = value;
			cookieVisited.complete(Boolean.TRUE);
			return 0;
		}
		return 1;
	}
}
