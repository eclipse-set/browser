package org.eclipse.set.browser.cef.handlers.browser;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.cef.handlers.AbstractHandler;
import org.eclipse.set.browser.lib.CStruct;

public abstract class AbstractBrowserHandler<T extends CStruct>
		extends AbstractHandler<T> {
	protected final Chromium browser;

	AbstractBrowserHandler(final Chromium browser) {
		this.browser = browser;
	}
}
