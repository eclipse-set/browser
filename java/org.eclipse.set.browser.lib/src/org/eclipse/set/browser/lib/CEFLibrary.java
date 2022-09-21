/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.lib;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.FileLocator;

/**
 * Helper class for accessing and loading CEF Binaries
 * 
 * @author Stuecker
 */
public class CEFLibrary {
	private static Path cef_path;
	private static final String CHROME_ELF = "chrome_elf";
	private static final String CHROMIUM_SUBPROCESS = "chromium_subp.exe";
	private static final String JNI_LIB = "chromium_jni";
	private static final String LIBCEF = "libcef";

	/**
	 * @return the path to the CEF binaries/resources
	 */
	public static String getCEFPath() {
		return cef_path.toAbsolutePath().toString();
	}

	/**
	 * @return the path to the Chromium subprocess binary
	 */
	public static String getSubprocessExePath() {
		final URI uri = getResourceFileURI(CHROMIUM_SUBPROCESS);
		return Paths.get(uri).toAbsolutePath().toString();
	}

	/**
	 * @return a path for temporary files for the browser
	 */
	public static String getTempPath() {
		return Paths.get(System.getenv("APPDATA"), "Eclipse SET", "cef")
				.toAbsolutePath().toString();
	}

	/**
	 * @param cefPath
	 *            path to CEF binaries
	 */
	public static void init(final Path cefPath) {
		cef_path = cefPath;
	}

	/**
	 * Loads the CEF Libraries
	 * 
	 * Make sure to call init() beforehand
	 */
	public static void loadLibraries() {
		System.load(getCEFLibraryFile(CHROME_ELF).getAbsolutePath());
		System.load(getCEFLibraryFile(LIBCEF).getAbsolutePath());
		System.load(getLibraryFile(JNI_LIB).getAbsolutePath());
	}

	private static File getCEFLibraryFile(final String library) {
		final String mapLibraryName = System.mapLibraryName(library);
		return cef_path.resolve(mapLibraryName).toFile();
	}

	private static File getLibraryFile(final String library) {
		final String mapLibraryName = System.mapLibraryName(library);
		return new File(getResourceFileURI(mapLibraryName));
	}

	private static URI getResourceFileURI(final String name) {
		final URL url = CEFLibrary.class.getClassLoader().getResource(name);
		try {
			// Ensure we have a file url (and not a bundle URL)
			final URL fileUrl = FileLocator.toFileURL(url);
			// Transform into URI. Avoid using URL#toURI() as it does not handle
			// special characters correctly
			return new URI(fileUrl.getProtocol(), fileUrl.getUserInfo(),
					fileUrl.getHost(), fileUrl.getPort(), fileUrl.getPath(),
					fileUrl.getQuery(), fileUrl.getRef());

		} catch (final IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
