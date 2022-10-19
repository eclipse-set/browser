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
import java.util.function.Consumer;

import org.eclipse.set.browser.lib.ChromiumLib;

/**
 * Visitor to read CEF Strings for the WebBrowser class
 * 
 * @author Stuecker
 */
public class StringVisitor {
	private final long cefStringVisitor = ChromiumLib
			.allocate_cef_string_visitor_t(this);

	private final Consumer<String> consumer;

	private final CompletableFuture<Boolean> textReady;

	/**
	 * @param consumer
	 *            a consumer for the visited string
	 * @param stringReady
	 *            a completable future to indicate whether the consumer has been
	 *            called
	 */
	public StringVisitor(final Consumer<String> consumer,
			final CompletableFuture<Boolean> stringReady) {
		this.consumer = consumer;
		this.textReady = stringReady;
	}

	/**
	 * Disposes the string visitor
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_string_visitor_t(cefStringVisitor);
	}

	/**
	 * @return the cef_string_visitor_t
	 */
	public long get() {
		return cefStringVisitor;
	}

	@SuppressWarnings({ "boxing", "unused" }) // called via JNI
	private void visit(final long self, final long cefString) {
		final String newtext = cefString != 0
				? ChromiumLib.cefswt_cefstring_to_java(cefString)
				: null;

		if (newtext != null) {
			consumer.accept(newtext);
		}

		textReady.complete(newtext != null);
	}
}
