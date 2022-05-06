/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import org.eclipse.set.browser.lib.CStruct;

/**
 * Helper class to to wrap native CEF handlers into Java Handlers with callbacks
 * 
 * @author Stuecker
 *
 * @param <T>
 *            the native CEF handler
 */
public abstract class AbstractHandler<T extends CStruct> {
	protected T handler;

	/**
	 * Disposes the internal handler
	 */
	public void dispose() {
		handler.free();
	}

	/**
	 * @return the native handler
	 */
	public T get() {
		return handler;
	}
}
