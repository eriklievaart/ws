package com.eriklievaart.ws.repo.pom;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.ws.repo.sax.SaxContext;
import com.eriklievaart.ws.repo.sax.SaxPartialHandler;

public class SaxPropertiesHandler implements SaxPartialHandler {

	private Map<String, String> properties = new Hashtable<>();

	@Override
	public String contextPath() {
		return "project";
	}

	@Override
	public boolean evaluateText(String tail) {
		return true;
	}

	@Override
	public void text(String value, SaxContext context) {
		if (context.getPath().equals("project/version")) {
			properties.put("project.version", value);
		}
		if (context.getPath().equals("project/parent/version")) {
			properties.put("project.parent.version", value);
		}
		if (context.getPath().startsWith("project/properties")) {
			properties.put(context.getElementName(), value);
		}
	}

	public String get(String key) {
		return properties.get(key);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}
}
