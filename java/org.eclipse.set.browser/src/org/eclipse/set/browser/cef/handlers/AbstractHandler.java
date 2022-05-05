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
