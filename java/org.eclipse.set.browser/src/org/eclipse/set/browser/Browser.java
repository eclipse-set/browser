package org.eclipse.set.browser;

import org.eclipse.swt.widgets.Composite;

/**
 * Browser implementation
 * 
 * @author Stuecker
 */
public class Browser extends org.eclipse.set.browser.swt.Browser {

	/**
	 * @param parent
	 *            The parent element
	 * @param style
	 *            The SWT style
	 */
	public Browser(final Composite parent, final int style) {
		super(parent, style);
	}

	/**
	 * @return the download listener
	 */
	public DownloadListener getDownloadListener() {
		return webBrowser.getDownloadListener();
	}

	/**
	 * @param listener
	 *            the new download listener
	 */
	public void setDownloadListener(final DownloadListener listener) {
		webBrowser.setDownloadListener(listener);
	}

}
