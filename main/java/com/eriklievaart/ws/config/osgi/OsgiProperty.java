package com.eriklievaart.ws.config.osgi;

public class OsgiProperty {

	private String key;
	private String value;
	private boolean comment;

	public OsgiProperty(String key, String value, boolean comment) {
		this.key = key;
		this.value = value;
		this.comment = comment;
	}

	public boolean isComment() {
		return comment;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
