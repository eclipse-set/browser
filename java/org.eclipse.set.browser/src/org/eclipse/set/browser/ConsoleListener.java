/**
 * Copyright (c) 2023 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser;

/**
 * Listener for console messages
 */
public interface ConsoleListener {
	/**
	 * @param logLevel
	 *            log level
	 * @param message
	 *            the message
	 * @param source
	 *            the source of the message
	 * @param line
	 *            the line of the source of the message
	 */
	void onConsoleMessage(final int logLevel, final String message,
			final String source, final int line);
}
