package org.eclipse.set.browser.cef.handlers.browser;

import java.lang.reflect.Type;

import org.eclipse.set.browser.cef.Chromium;
import org.eclipse.set.browser.lib.cef_request_handler_t;
import org.eclipse.swt.internal.Callback;

public class RequestHandler extends AbstractBrowserHandler<cef_request_handler_t> {
	private final Callback get_auth_credentials_cb = new Callback(this,
			"get_auth_credentials", int.class,
			new Type[] { long.class, long.class, long.class, int.class,
					long.class, int.class, long.class, long.class,
					long.class });
	private final Callback on_before_browse_cb = new Callback(this,
			"on_before_browse", int.class, new Type[] { long.class, long.class,
					long.class, long.class, int.class });

	public RequestHandler(final Chromium browser) {
		super(browser);

		handler = new cef_request_handler_t();
		handler.get_auth_credentials = get_auth_credentials_cb.getAddress();
		handler.on_before_browse = on_before_browse_cb.getAddress();
		handler.allocate();
	}

	@Override
	public void dispose() {
		super.dispose();
		get_auth_credentials_cb.dispose();
		on_before_browse_cb.dispose();
	}

	int get_auth_credentials(final long self, final long id, final long frame,
			final int isProxy, final long host, final int port,
			final long realm, final long scheme, final long callback) {
		return browser.get_auth_credentials(id, frame, host, port, realm,
				callback);
	}

	int on_before_browse(final long self, final long id, final long frame,
			final long request, final int is_redirect) {
		return browser.on_before_browse(id, frame, request);
	}

}
