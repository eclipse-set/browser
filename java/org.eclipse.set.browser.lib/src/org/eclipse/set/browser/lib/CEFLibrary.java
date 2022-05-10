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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper class for accessing and loading CEF Binaries
 * 
 * @author Stuecker
 */
public class CEFLibrary {
	private static final String CEF_DIR = "cef";
	private static Path cef_path;
	private static Class<?> cefSupplierClass;
	private static final String CHROME_ELF = "chrome_elf";
	private static final String JNI_LIB = "chromium_jni";
	private static final String LIBCEF = "libcef";

	/**
	 * @return the path to the CEF binaries/resources
	 */
	public static String getCEFPath() {
		return cef_path.toAbsolutePath().toString();
	}

	/**
	 * @return the path to the JNI/subprocess binaries
	 */
	public static String getJNIPath() {
		try {
			return getLibraryFile(JNI_LIB).getParentFile().getAbsolutePath();
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param supplierClass
	 *            class which provides the CEF resources
	 */
	public static void init(final Class<?> supplierClass) {
		CEFLibrary.cefSupplierClass = supplierClass;

		try {
			final URI uri = CEFLibrary.cefSupplierClass.getClassLoader()
					.getResource(CEF_DIR).toURI();
			final Bundle bundle = FrameworkUtil
					.getBundle(CEFLibrary.cefSupplierClass);
			if (bundle == null && !uri.isOpaque()) {
				cef_path = Path.of(new File(uri).getAbsolutePath());
			} else {
				// Extract from jar into temporary directory
				cef_path = Files.createTempDirectory("set_cef_");

				if (bundle != null) {
					final String location = bundle.getLocation()
							.replace("reference:", "jar:") + "!/cef";
					extractFiles(URI.create(location), cef_path);
				} else {
					extractFiles(CEFLibrary.cefSupplierClass.getClassLoader()
							.getResource(CEF_DIR).toURI(), cef_path);
				}
			}

		} catch (final URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
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

	private static void extractFile(final InputStream resourceStream,
			final Path destinationFile) throws IOException {
		Files.createDirectories(destinationFile.getParent());

		try (OutputStream output = new FileOutputStream(
				destinationFile.toFile())) {
			resourceStream.transferTo(output);
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void extractFiles(final URI uri, final Path destination)
			throws IOException {
		try (final FileSystem fileSystem = FileSystems.newFileSystem(uri,
				Collections.<String, Object> emptyMap())) {
			final Path cefPath = fileSystem.getPath(CEF_DIR);
			// Walk contents of the file
			try (final Stream<Path> walk = Files.walk(cefPath)) {
				for (final Iterator<Path> it = walk.iterator(); it.hasNext();) {
					final Path path = it.next();
					if (Files.isDirectory(path)) {
						continue;
					}

					// Rewrite path so that the contents of CEF_DIR end up in
					// the root
					final Path destinationFile = destination
							.resolve(cefPath.relativize(path).toString());

					// Extract the file
					try (InputStream resourceStream = CEFLibrary.cefSupplierClass
							.getClassLoader()
							.getResourceAsStream(path.toString())) {
						extractFile(resourceStream, destinationFile);
					}
				}
			}
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
