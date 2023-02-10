/**
 * Copyright (c) 2023 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef;

import org.eclipse.set.browser.lib.CEFLibrary;

/**
 * Configuration for CEF
 */
public class CEFConfiguration {
	/**
	 * Log severity.
	 * 
	 * Maps to cef_log_severity_t
	 */
	public enum LOG_SEVERITY {
		/**
		 * Default logging
		 */
		DEFAULT(0),
		/**
		 * Verbose logging
		 */
		VERBOSE(1),
		/**
		 * Info logging
		 */
		INFO(2),
		/**
		 * Warning logging
		 */
		WARNING(3),
		/**
		 * Error logging
		 */
		ERROR(4),
		/**
		 * Fatal logging
		 */
		FATAL(5),
		/**
		 * Disable logging to file for all messages, and to stderr for messages
		 * with severity less than FATAL.
		 */
		DISABLE(99);

		private final int value;

		LOG_SEVERITY(final int value) {
			this.value = value;
		}

		/**
		 * @return value
		 */
		public int getValue() {
			return value;
		}
	}

	private static int getDebugPort() {
		try {
			return Integer.parseInt(System.getProperty(
					"org.eclipse.set.browser.remote-debugging-port", "0"));
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Port for remote cef debugging. 0 to disable
	 */
	public int DebugPort = getDebugPort();
	/**
	 * Browser locale
	 */
	public String Locale = System.getProperty("org.eclipse.set.browser.locale",
			"en-US");
	/**
	 * Logfile for browser messages
	 */
	public String LogPath = CEFLibrary.getTempPath() + "/cef_lib.log";
	/**
	 * Loglevel for browser messages
	 */
	public LOG_SEVERITY LogLevel = LOG_SEVERITY.INFO;

	/**
	 * String to append to the User agent header
	 */
	public String UserAgentProduct = System.getProperty(
			"org.eclipse.set.browser.user-agent-product", "Eclipse SET");
}
