package com.nmbs.util;

/**
 * Constants enumerating the HTTP status codes.
 */
public interface HttpStatusCodes {

	// Everything is fine, the request was processed successful. The document in
	// the response body, if any, is a representation of a resource.
	public static final int SC_OK = 200;
	// The server sends this status code when it successfully created a new
	// account. Response headers: The Location header contains the canonical URI
	// to the new resource. Entity-body: The response entity body contains the
	// JSON representation of the newly created account.
	public static final int SC_CREATED = 201;
	// The request message sent by the client application has an invalid format
	// or simply does not make sense. The message section in the response
	// contains a description of what is wrong with the request message.
	public static final int SC_BAD_REQUEST = 400;
	// The user is not authorized to access the resource specified. This code
	// will be used when the API key passed was not valid or has expired. In
	// case of a protected resource, this response code is used when the
	// authentication information is invalid.
	public static final int SC_UNAUTHORIZED = 401;
	// The URL specified does not map to a resource.
	public static final int SC_NOT_FOUND = 404;
	// The client application tried to use an HTTP method that this resource
	// does not support.
	public static final int SC_METHOD_NOT_ALLOWED = 405;
	// A fatal exception occured during the execution of the request handler.
	public static final int SC_INTERNAL_SERVER_ERROR = 500;
	// The back-office system raised an exception during the execution of a
	// request. The message section in the response contains a description of
	// what went wrong during the execution of the request.
	public static final int SC_BACK_OFFICE_EXCEPTION_STATE = 510;
}
