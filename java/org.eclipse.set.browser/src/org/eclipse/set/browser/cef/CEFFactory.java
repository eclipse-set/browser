/**
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

/**
 * CEF Factory
 */
public class CEFFactory {

	/**
	 * Enumeration for Return Types received from JNI
	 */
	@SuppressWarnings("javadoc")
	public enum ReturnType {
		Array(4), Bool(1), Double(0), Error(5), Null(3), Str(2);

		/**
		 * @param v
		 * @return the return type
		 */
		public static ReturnType from(final String v) {
			try {
				final int value = Integer.parseInt(v);
				for (final ReturnType rt : ReturnType.values()) {
					if (rt.intValue() == value) {
						return rt;
					}
				}
				// Unknown return type provided
				throw new IllegalArgumentException(v);
			} catch (final NumberFormatException e) {
				throw new IllegalArgumentException(v, e);
			}
		}

		private final int value;

		private ReturnType(final int value) {
			this.value = value;
		}

		/**
		 * @return the integral value
		 */
		public int intValue() {
			return value;
		}
	}

	/**
	 * Browser Process ID
	 */
	public static final int PID_BROWSER = 0;

	/**
	 * Renderer Process ID
	 */
	public static final int PID_RENDERER = 1;

}
