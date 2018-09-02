package com.eriklievaart.ws.config.dependency;

public enum LibType {
	COMPILE, BUNDLE, RUN, PROVIDED, TEST;

	public String getDir() {
		return toString().toLowerCase();
	}

	public static LibType parse(String value) {
		return valueOf(value.toUpperCase());
	}
}
