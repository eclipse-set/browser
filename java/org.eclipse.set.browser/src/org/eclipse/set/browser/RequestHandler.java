/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser;

import java.io.InputStream;

/**
 * Request handler
 */
public interface RequestHandler {
	/**
	 * Interface for accessing the incoming HTTP Request
	 */
	public interface Request {
		/**
		 * @param headerName
		 *            the name of the header
		 * @return the header value
		 */
		public String getHeader(String headerName);

		/**
		 * @return the HTTP Method (GET/POST/PUT/...) used
		 */
		public String getMethod();

		/**
		 * @return the URL requested
		 */
		public String getURL();

	}

	/**
	 * Response to be provided to the browser
	 */
	public interface Response {
		/**
		 * @param headerName
		 *            the header to set
		 * @param headerValue
		 *            the value to set
		 */
		public void setHeader(String headerName, String headerValue);

		/**
		 * @param mimeType
		 *            the mime type
		 */
		public void setMimeType(String mimeType);

		/**
		 * @param stream
		 *            a stream containing the data to be returned to the browser
		 */
		public void setResponseData(InputStream stream);

		/**
		 * @param data
		 *            the data to be returned to the browser
		 */
		public void setResponseData(String data);

		/**
		 * @param statusCode
		 *            the status code
		 */
		public void setStatus(int statusCode);
	}

	/**
	 * @param request
	 *            the incoming request. This object *must not* be preserved
	 *            outside this function call.
	 * @param response
	 *            the outgoing response. This object *must not* be preserved
	 *            outside this function call.
	 */
	public void onRequest(Request request, Response response);
}
