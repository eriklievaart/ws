package com.eriklievaart.ws.repo.pom;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.sax.SaxContext;
import com.eriklievaart.ws.repo.sax.SaxPartialHandler;

public class SaxParentHandler implements SaxPartialHandler {

	private Map<String, String> properties = new Hashtable<>();

	@Override
	public String contextPath() {
		return "project/parent";
	}

	@Override
	public void text(String value, SaxContext context) {
		properties.put(context.getElementName(), value);
	}

	public Optional<DependencyReference> getParent() {
		if (!properties.containsKey("artifactId")) {
			return Optional.empty();
		}
		DependencyReference reference = new DependencyReference(properties.get("artifactId"));
		reference.setGroupId(properties.get("groupId"));
		reference.setVersion(properties.get("version"));
		return Optional.of(reference);
	}
}
