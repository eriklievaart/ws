package com.eriklievaart.ws.repo.sax;

public interface SaxPartialHandler {

	@SuppressWarnings("unused")
	public default boolean evaluateText(String tail) {
		return true;
	}

	public String contextPath();

	public void text(String value, SaxContext context);

	public default void enterContext() {
	}

	public default void leaveContext() {
	}
}
