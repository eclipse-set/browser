/**
 * Copyright (c) 2023 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.win32;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper class to get CEF resource files from the bundle
 */
public class CEFResource {
	private static boolean hasBeenExtracted = false;

	private static Path getTempPathForExtraction() {
		return Paths.get(System.getenv("APPDATA"), "Eclipse SET", "cef", "dev", "extracted").toAbsolutePath();
	}

	/**
	 * @return the path to the extracted CEF files
	 */
	public static Path getPath() {
		final Bundle bundle = FrameworkUtil.getBundle(CEFResource.class.getClassLoader()).orElseThrow();
		Path bundleLocation = FileLocator.getBundleFileLocation(bundle).orElseThrow().toPath().toAbsolutePath();
		if (Files.isDirectory(bundleLocation))
			return bundleLocation.resolve("cef");

		// Due to a M2E limitation, Eclipse-BundleShape is not respected when working
		// with a maven dependency from within Eclipse (Tycho handles this correctly for
		// production builds). For development, instead extract the jar to a temporary
		// path, once per execution
		Path tempPath = getTempPathForExtraction();

		if (hasBeenExtracted) {
			return tempPath.resolve("cef");
		}
		hasBeenExtracted = true;
		try (Stream<Path> files = Files.walk(tempPath)) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			// ignore
		}
		try (JarFile jar = new JarFile(bundleLocation.toString())) {
			// Create directories
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					new File(tempPath.resolve(entry.getName()).toString()).mkdirs();
				}
			}

			// Write files
			entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					File file = new File(tempPath.resolve(entry.getName()).toString());
					try (InputStream is = jar.getInputStream(entry); FileOutputStream os = new FileOutputStream(file)) {
						is.transferTo(os);
					}

				}
			}

			return tempPath.resolve("cef");
		} catch (IOException e) {
			// ignore
		}
		return null;
	}
}
