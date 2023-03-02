/**
 * Copyright (c) 2023 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.win32;

import java.nio.file.Path;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper class to get CEF resource files from the bundle
 */
public class CEFResource {
	/**
	 * @return the path to the extracted CEF files
	 */
	public static Path getPath() {
		final Bundle bundle = FrameworkUtil.getBundle(CEFResource.class.getClassLoader()).orElseThrow();
		return FileLocator.getBundleFileLocation(bundle).orElseThrow().toPath().toAbsolutePath().resolve("cef");
	}
}
