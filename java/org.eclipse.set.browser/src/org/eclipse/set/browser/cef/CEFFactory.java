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

public class CEFFactory {

	public static enum ReturnType {
		Array(4), Bool(1), Double(0), Error(5), Null(3), Str(2);

		public static ReturnType from(final String v) {
			try {
				final int value = Integer.parseInt(v);
				for (final ReturnType rt : ReturnType.values()) {
					if (rt.intValue() == value) {
						return rt;
					}
				}
			} catch (final NumberFormatException e) {
			}
			throw new IllegalArgumentException(v);
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

	public static final int PID_BROWSER = 0;

	public static final int PID_RENDERER = 1;

}
