package com.eriklievaart.ws.config.osgi;

public class OsgiProperty implements Comparable<OsgiProperty> {

	private String key;
	private String value;
	private boolean comment;

	public OsgiProperty(String key, String value, boolean comment) {
		if (key == null || key.isBlank()) {
			throw new RuntimeException("property key is blank!");
		}
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

	@Override
	public int compareTo(OsgiProperty o) {
		return key.compareTo(o.key);
	}
}
