package com.eriklievaart.ws.repo.sax;

public class SaxContext {

	private String element;
	private String path;

	public SaxContext(String element, String path) {
		this.element = element;
		this.path = path;
	}

	public String getElementName() {
		return element;
	}

	public String getPath() {
		return path;
	}
}
