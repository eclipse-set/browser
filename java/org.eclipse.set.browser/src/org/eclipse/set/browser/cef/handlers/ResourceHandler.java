/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.cef.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.set.browser.RequestHandler;
import org.eclipse.set.browser.RequestHandler.Request;
import org.eclipse.set.browser.RequestHandler.Response;
import org.eclipse.set.browser.lib.ChromiumLib;
import org.eclipse.set.browser.lib.cef_download_item_t;
import org.eclipse.set.browser.lib.cef_request_t;
import org.eclipse.set.browser.lib.cef_response_t;

/**
 * Implementation for cef_resource_handler_t
 */
public class ResourceHandler {
	class RequestImpl implements Request {
		private long cefRequest;

		@Override
		public String getHeader(final String headerName) {
			return cef_request_t.cefswt_request_get_header_by_name(cefRequest,
					headerName);
		}

		@Override
		public String getMethod() {
			return cef_request_t.cefswt_request_get_method(cefRequest);
		}

		@Override
		public String getURL() {
			return cef_request_t.cefswt_request_get_url(cefRequest);
		}

	}

	class ResponseImpl implements Response {
		private long cefResponse;
		private InputStream responseStream;

		@Override
		public void setHeader(final String headerName,
				final String headerValue) {
			cef_response_t.cefswt_response_set_header(cefResponse, headerName,
					headerValue);
		}

		@Override
		public void setMimeType(final String mimeType) {
			cef_response_t.cefswt_response_set_mime_type(cefResponse, mimeType);
		}

		@Override
		public void setResponseData(final InputStream stream) {
			responseStream = stream;
		}

		@Override
		public void setResponseData(final String data) {
			setResponseData(new ByteArrayInputStream(data.getBytes()));
		}

		@Override
		public void setStatus(final int statusCode) {
			cef_response_t.cefswt_response_set_status_code(cefResponse,
					statusCode);
		}

	}

	private final long cefResourceHandler = ChromiumLib
			.allocate_cef_resource_handler_t(this);

	private final RequestImpl request = new RequestImpl();
	private final RequestHandler requestHandler;
	private final ResponseImpl response = new ResponseImpl();

	/**
	 * @param requestHandler
	 *            the request handler
	 */
	public ResourceHandler(final RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	/**
	 * Disposes the handler
	 */
	public void dispose() {
		ChromiumLib.deallocate_cef_resource_handler_t(cefResourceHandler);
	}

	/**
	 * @return the cef_app_t pointer
	 */
	public long get() {
		return cefResourceHandler;
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private void get_response_headers(final long self, final long cef_response,
			final long response_length, final long redirectUrl)
			throws IOException {
		response.cefResponse = cef_response;
		
		// Ensure mime type and a response is always set
		response.setMimeType("text/plain");
		response.setResponseData("");

		try {
			requestHandler.onRequest(request, response);
		} catch (final Exception e) {
			response.setMimeType("text/plain");
			response.setResponseData("Internal server error");
			response.setStatus(500);
		}

		ChromiumLib.cefswt_set_intptr(response_length,
				response.responseStream.available());
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private int open(final long self, final long cef_request,
			final long handle_request_ptr, final long callback) {
		// Immediately handle the request
		ChromiumLib.cefswt_set_intptr(handle_request_ptr, 1);
		request.cefRequest = cef_request;
		return 1;
	}

	@SuppressWarnings({ "unused" }) // Called via JNI
	private int read(final long self, final long data_out,
			final int bytes_to_read, final long bytes_read_ptr,
			final long callback) throws IOException {
		// Read bytes into the CEF response
		final byte[] bytes = response.responseStream.readNBytes(bytes_to_read);

		ChromiumLib.cefswt_set_intptr(bytes_read_ptr, bytes.length);
		if (bytes.length > 0) {
			cef_download_item_t.cefswt_copy_bytes(data_out, bytes,
					bytes.length);
			return 1;
		}
		// No further bytes, close the input stream
		response.responseStream.close();
		return 0;
	}
}
