package com.eriklievaart.ws.toolkit.io;

public class IORuntimeException extends RuntimeException {

	public IORuntimeException(String message) {
		super(message);
	}

	public IORuntimeException(Exception e) {
		super(e);
	}

	public IORuntimeException(String message, Exception e) {
		super(message, e);
	}
}
