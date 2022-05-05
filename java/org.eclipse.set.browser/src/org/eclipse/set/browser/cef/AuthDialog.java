package org.eclipse.set.browser.cef;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class AuthDialog extends Dialog {

	public AuthDialog(final Shell parent) {
		super(parent);
	}

	public void open(final AuthenticationEvent authEvent, final String realm) {
		final Shell parent = getParent();
		final Shell shell = new Shell(parent,
				SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Authentication Required");
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		shell.setLayout(layout);

		final Label info = new Label(shell, SWT.WRAP);
		final StringBuilder infoText = new StringBuilder(authEvent.location);
		infoText.append(" is requesting you username and password.");
		if (realm != null) {
			infoText.append(" The site says: \"").append(realm).append("\"");
		}
		info.setText(infoText.toString());
		info.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		final Label label1 = new Label(shell, SWT.NONE);
		label1.setText("User Name: ");
		final Text username = new Text(shell, SWT.SINGLE | SWT.BORDER);
		username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label label2 = new Label(shell, SWT.NONE);
		label2.setText("Password: ");
		final Text password = new Text(shell, SWT.SINGLE | SWT.BORDER);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		password.setEchoChar('*');

		final Composite bar = new Composite(shell, SWT.NONE);
		bar.setLayoutData(new GridData(SWT.END, SWT.END, false, true, 2, 1));
		bar.setLayout(new GridLayout(2, true));

		final Button cancelButton = new Button(bar, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, event -> {
			authEvent.doit = false;
			shell.close();
		});
		final GridData cancelData = new GridData(SWT.CENTER, SWT.END, false,
				false);
		cancelData.widthHint = 80;
		cancelButton.setLayoutData(cancelData);

		final Button okButton = new Button(bar, SWT.PUSH);
		okButton.setText("Ok");
		okButton.addListener(SWT.Selection, event -> {
			authEvent.user = username.getText();
			authEvent.password = password.getText();
			shell.close();
		});
		final GridData okData = new GridData(SWT.CENTER, SWT.END, false, false);
		okData.minimumWidth = SWT.DEFAULT;
		okData.widthHint = 80;
		okButton.setLayoutData(okData);

		shell.pack();
		shell.open();
		final Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}