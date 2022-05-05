package org.eclipse.set.browser.cef.handlers;

import org.eclipse.set.browser.lib.CStruct;

public abstract class AbstractHandler<T extends CStruct> {
	protected T handler;

	public void dispose() {
		handler.free();
	}

	public T get() {
		return handler;
	}
}
