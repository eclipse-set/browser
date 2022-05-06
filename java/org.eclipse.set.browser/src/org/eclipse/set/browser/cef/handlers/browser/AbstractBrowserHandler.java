/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers.browser;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.cef.handlers.AbstractHandler;
import org.eclipse.set.browser.lib.CStruct;

/**
 * AbstractHandler with a browser component
 * 
 * @author Stuecker
 *
 * @param <T>
 *            Native CEF Structure
 */
public abstract class AbstractBrowserHandler<T extends CStruct>
		extends AbstractHandler<T> {
	protected final Chromium browser;

	AbstractBrowserHandler(final Chromium browser) {
		this.browser = browser;
	}
}
