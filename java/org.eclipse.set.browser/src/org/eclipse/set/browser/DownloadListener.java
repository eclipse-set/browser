/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Listener for downloads
 * 
 * @author Stuecker
 */
public interface DownloadListener {
	/**
	 * Called before a download starts.
	 * 
	 * @param suggestedName
	 *            the suggested filename
	 * @param url
	 *            the file url
	 * @return a path to the file location to download or an empty optional to
	 *         cancel the download.
	 */
	Optional<Path> beforeDownload(final String suggestedName, final String url);

	/**
	 * Called when a download finishes.
	 * 
	 * @param success
	 *            whether the file was successfully downloaded
	 * @param path
	 *            the file path
	 */
	void downloadFinished(final boolean success, final Path path);
}
