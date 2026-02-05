package com.eriklievaart.ws.process.index;

import java.util.Map;
import java.util.Hashtable;

public class LibraryIndex {

	private Map<String, String> typeToQualified = new Hashtable<>();

	public void add(String qualified) {
		String base = qualified.replaceFirst(".*[.]", "");
		typeToQualified.put(base, qualified);
	}

	public boolean contains(String base) {
		return typeToQualified.containsKey(base);
	}

	public String get(String base) {
		return typeToQualified.get(base);
	}
}
