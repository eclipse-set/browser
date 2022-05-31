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
		try {
			final URL url = CEFLibrary.class.getClassLoader()
					.getResource(CHROMIUM_SUBPROCESS);
			return Paths.get(FileLocator.toFileURL(url).toURI())
					.toAbsolutePath().toString();
		} catch (final URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
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
		try {
			System.load(getCEFLibraryFile(CHROME_ELF).getAbsolutePath());
			System.load(getCEFLibraryFile(LIBCEF).getAbsolutePath());
			System.load(getLibraryFile(JNI_LIB).getAbsolutePath());
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static File getCEFLibraryFile(final String library) {
		final String mapLibraryName = System.mapLibraryName(library);
		return cef_path.resolve(mapLibraryName).toFile();
	}

	private static File getLibraryFile(final String library)
			throws URISyntaxException {
		final String mapLibraryName = System.mapLibraryName(library);
		final URL libraryUrl = CEFLibrary.class.getClassLoader()
				.getResource(mapLibraryName);
		try {
			return new File(FileLocator.toFileURL(libraryUrl).toURI());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
