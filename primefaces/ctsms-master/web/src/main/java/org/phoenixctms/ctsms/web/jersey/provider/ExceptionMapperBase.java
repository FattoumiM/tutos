package org.phoenixctms.ctsms.web.jersey.provider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

abstract class ExceptionMapperBase {

	protected class ExceptionJs {

		@SerializedName("exception")
		private String type;
		@SerializedName("message")
		private String message;
		@SerializedName("errorCode")
		private String code;
		@SerializedName("data")
		private Object data;

		public ExceptionJs(Throwable t) {
			if (t != null) {
				type = t.getClass().getName();
				if (t instanceof JsonSyntaxException && ((JsonSyntaxException) t).getCause() != null) {
					message = ((JsonSyntaxException) t).getCause().getMessage();
				} else {
					message = t.getMessage();
					try {
						code = (String) t.getClass().getMethod(EXCEPTION_ERROR_CODE_GETTER_METHOD_NAME).invoke(t);
					} catch (Exception e) {
					}
					try {
						data = t.getClass().getMethod(EXCEPTION_DATA_GETTER_METHOD_NAME).invoke(t);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private final static String EXCEPTION_ERROR_CODE_GETTER_METHOD_NAME = "getErrorCode";
	private final static String EXCEPTION_DATA_GETTER_METHOD_NAME = "getData";

	protected ResponseBuilder buildJsonResponse(int status, Throwable t) {
		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(new ExceptionJs(t));
	}

	protected ResponseBuilder buildJsonResponse(Status status, Throwable t) {
		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(new ExceptionJs(t));
	}
}
