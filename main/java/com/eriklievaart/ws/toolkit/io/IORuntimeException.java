package com.eriklievaart.ws.toolkit.io;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

	public IORuntimeException(String message) {
		super(message);
	}

	public IORuntimeException(IOException e) {
		super(e);
	}

	public IORuntimeException(String message, IORuntimeException e) {
		super(message, e);
	}
}
