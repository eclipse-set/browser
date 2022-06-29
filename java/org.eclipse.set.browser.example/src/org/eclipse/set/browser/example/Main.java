/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.set.browser.example;

import java.nio.file.Path;

import org.eclipse.set.browser.Browser;
import org.eclipse.set.browser.lib.CEFLibrary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Basic browser example for org.eclipse.set.browser
 * 
 * Adapted from org.eclipse.swt.snippets.Snippet128
 *
 */
public class Main {
	/**
	 * @param args
	 *            arguments
	 */
	public static void main(final String[] args) {
		// Init CEF Library
		CEFLibrary.init(
				Path.of(System.getProperty("org.eclipse.set.browser.cefpath")));

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Snippet 128");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		shell.setLayout(gridLayout);
		final ToolBar toolbar = new ToolBar(shell, SWT.NONE);
		final ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
		itemBack.setText("Back");
		final ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
		itemForward.setText("Forward");
		final ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
		itemStop.setText("Stop");
		final ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
		itemRefresh.setText("Refresh");
		final ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
		itemGo.setText("Go");

		GridData data = new GridData();
		data.horizontalSpan = 3;
		toolbar.setLayoutData(data);

		final Label labelAddress = new Label(shell, SWT.NONE);
		labelAddress.setText("Address");

		final Text location = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		location.setLayoutData(data);

		final Browser browser;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (final SWTError e) {
			System.out.println(
					"Could not instantiate Browser: " + e.getMessage());
			display.dispose();
			return;
		}
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);

		final Label status = new Label(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		status.setLayoutData(data);

		final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.END;
		progressBar.setLayoutData(data);

		/* event handling */
		final Listener listener = event -> {
			final ToolItem item = (ToolItem) event.widget;
			final String string = item.getText();
			if (string.equals("Back")) {
				browser.back();
			} else if (string.equals("Forward")) {
				browser.forward();
			} else if (string.equals("Stop")) {
				browser.stop();
			} else if (string.equals("Refresh")) {
				browser.refresh();
			} else if (string.equals("Go")) {
				browser.setUrl(location.getText());
			}
		};
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(final ProgressEvent event) {
				if (event.total == 0) {
					return;
				}
				final int ratio = event.current * 100 / event.total;
				progressBar.setSelection(ratio);
			}

			@Override
			public void completed(final ProgressEvent event) {
				progressBar.setSelection(0);
			}
		});
		browser.addStatusTextListener(event -> status.setText(event.text));
		browser.addLocationListener(LocationListener.changedAdapter(event -> {
			if (event.top) {
				location.setText(event.location);
			}
		}));
		itemBack.addListener(SWT.Selection, listener);
		itemForward.addListener(SWT.Selection, listener);
		itemStop.addListener(SWT.Selection, listener);
		itemRefresh.addListener(SWT.Selection, listener);
		itemGo.addListener(SWT.Selection, listener);
		location.addListener(SWT.DefaultSelection,
				e -> browser.setUrl(location.getText()));

		shell.open();
		browser.setUrl("http://eclipse.org");

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}