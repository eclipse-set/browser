/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_cookie_visitor_t;
import org.eclipse.set.browser.swt.WebBrowser;

/**
 * Visitor to read CEF Cookies for the WebBrowser class
 * 
 * @author Stuecker
 */
public class CookieVisitor {
	private final long cefCookieVisitor = ChromiumLib
			.allocate_cef_cookie_visitor_t(this);

	private CompletableFuture<Boolean> cookieVisited;

	/**
	 * Disposes the object
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_cookie_visitor_t(cefCookieVisitor);
	}

	/**
	 * Attempts to retrieve the requested cookie for WebBrowser
	 * 
	 * IMPROVE: Handle this in chromium_swt instead
	 */
	public void getCookie() {
		cookieVisited = new CompletableFuture<>();
		final boolean result = cef_cookie_visitor_t
				.cefswt_get_cookie(WebBrowser.CookieUrl, cefCookieVisitor);
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
	private int visit(final long self, final long cefcookie, final int count,
			final int total, final int delete) {
		final String name = cef_cookie_visitor_t
				.cefswt_cookie_to_java(cefcookie);
		if (WebBrowser.CookieName != null
				&& WebBrowser.CookieName.equals(name)) {
			final String value = cef_cookie_visitor_t
					.cefswt_cookie_value(cefcookie);
			WebBrowser.CookieValue = value;
			cookieVisited.complete(Boolean.TRUE);
			return 0;
		}
		return 1;
	}
}
