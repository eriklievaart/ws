package com.eriklievaart.ws.runtime;

public class RuntimeStreams {

	private RuntimeOutput out;
	private RuntimeOutput err;

	public RuntimeStreams() {
		out = new RuntimeOutput() {
			@Override
			public void println(String line) {
				System.out.println(line);
			}
		};
		err = new RuntimeOutput() {
			@Override
			public void println(String line) {
				System.out.println(line);
			}
		};
	}

	public RuntimeStreams(RuntimeOutput out, RuntimeOutput err) {
		this.out = out;
		this.err = err;
	}

	public RuntimeOutput getNormalOutput() {
		return out;
	}

	public RuntimeOutput getErrorOutput() {
		return err;
	}

	public void setNormalOutput(RuntimeOutput output) {
		out = output;
	}

	public void setErrorOutput(RuntimeOutput output) {
		err = output;
	}

	public void debug(String message) {
		System.out.println(message);
	}

	public void thrown(Exception e) {
		err.println(e.getMessage());
	}
}
