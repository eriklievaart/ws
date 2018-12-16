package com.eriklievaart.ws.toolkit.io;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException {

	public RuntimeIOException(IOException e) {
		super(e);
	}

	public RuntimeIOException(String message, RuntimeIOException e) {
		super(message, e);
	}
}
