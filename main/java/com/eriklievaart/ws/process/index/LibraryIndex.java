package com.eriklievaart.ws.process.index;

import java.util.Hashtable;
import java.util.Map;

public class LibraryIndex {

	private boolean test = false;
	private Map<String, String> typeToQualified = new Hashtable<>();

	public boolean isMain() {
		return !test;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean value) {
		this.test = value;
	}

	public void add(String qualified) {
		String base = qualified.replaceFirst(".*[./\\\\]", "");
		typeToQualified.put(base, qualified.replaceAll("[/\\\\]", "."));
	}

	public boolean contains(String base) {
		return typeToQualified.containsKey(base);
	}

	public String get(String base) {
		return typeToQualified.get(base);
	}
}
