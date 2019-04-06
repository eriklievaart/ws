package com.eriklievaart.ws.runtime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.eriklievaart.ws.toolkit.io.IORuntimeException;

class RuntimeRunnable implements Runnable {

	private final BufferedReader reader;
	private final RuntimeOutput out;

	public RuntimeRunnable(final InputStream input, final RuntimeOutput out) {
		this.reader = new BufferedReader(new InputStreamReader(input));
		this.out = out;
	}

	@Override
	public void run() {
		try {
			String message = reader.readLine();
			while (message != null) {
				out.println(message);
				message = reader.readLine();
			}

		} catch (Exception e) {
			throw new IORuntimeException("Reading stream interrupted: " + e.getMessage());
		}
	}
}
