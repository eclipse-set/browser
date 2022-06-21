package org.eclipse.set.browser;

import java.nio.file.Path;
import java.util.Optional;

/**
 * WebBrowser implementation for SET Browser extensions
 * 
 * @author Stuecker
 */
public abstract class WebBrowser
		extends org.eclipse.set.browser.swt.WebBrowser {
	/**
	 * Default download listener which does not download anything
	 */
	public static final DownloadListener defaultDownloadListener = new DownloadListener() {
		@Override
		public Optional<Path> beforeDownload(final String suggestedName,
				final String url) {
			return Optional.empty();
		}

		@Override
		public void downloadFinished(final boolean success, final Path path) {
			// Do nothing
		}
	};
	DownloadListener downloadListener = defaultDownloadListener;

	/**
	 * @return the download listener
	 */
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

	/**
	 * @param listener
	 *            the new download listener
	 */
	public void setDownloadListener(final DownloadListener listener) {
		downloadListener = listener;
	}
}
