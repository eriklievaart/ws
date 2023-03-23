package com.eriklievaart.ws.repo.pom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.sax.SaxContext;
import com.eriklievaart.ws.repo.sax.SaxPartialHandler;

public class SaxDependenciesHandler implements SaxPartialHandler {

	private static final Set<String> EVALUATE = getElementsToEvaluate();
	private Map<String, String> properties = new Hashtable<>();
	private List<DependencyReference> dependencies = new ArrayList<>();

	@Override
	public String contextPath() {
		return "project/dependencies/dependency";
	}

	private static Set<String> getElementsToEvaluate() {
		List<String> elements = new ArrayList<>();

		elements.add("artifactId");
		elements.add("groupId");
		elements.add("version");
		elements.add("scope");

		return new HashSet<>(elements);
	}

	@Override
	public void enterContext() {
		properties.clear();
	}

	@Override
	public void leaveContext() {
		boolean isTestArtifact = properties.containsKey("scope") && properties.get("scope").equals("test");
		boolean missingArtifactId = !properties.containsKey("artifactId");
		if (isTestArtifact || missingArtifactId) {
			properties.clear();
			return;
		}
		DependencyReference reference = new DependencyReference(properties.get("artifactId"));
		reference.setGroupId(properties.get("groupId"));
		if (properties.containsKey("version")) {
			reference.setVersion(properties.get("version"));
		}
		dependencies.add(reference);
		properties.clear();
	}

	@Override
	public boolean evaluateText(String tail) {
		return EVALUATE.contains(tail);
	}

	@Override
	public void text(String value, SaxContext context) {
		properties.put(context.getElementName(), value);
	}

	public List<DependencyReference> getDependencies() {
		return dependencies;
	}
}
