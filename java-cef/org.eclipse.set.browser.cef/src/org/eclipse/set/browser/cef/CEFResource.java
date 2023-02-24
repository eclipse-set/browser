/**
 * Copyright (c) 2023 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

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
		final Optional<Bundle> bundle = FrameworkUtil
				.getBundle(CEFResource.class.getClassLoader());
		if (bundle.isPresent()) {
			return FileLocator.getBundleFileLocation(bundle.get()).orElseThrow()
					.toPath().toAbsolutePath();
		}

		// Fall back to resource resolution
		final URL fileUrl = CEFResource.class.getClassLoader()
				.getResource("cef/libcef.dll");
		// Transform into URI. Avoid using URL#toURI() as it does not handle
		// special characters correctly
		try {
			return new File(new URI(fileUrl.getProtocol(),
					fileUrl.getUserInfo(), fileUrl.getHost(), fileUrl.getPort(),
					fileUrl.getPath(), fileUrl.getQuery(), fileUrl.getRef()))
							.toPath().getParent().toAbsolutePath();
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
