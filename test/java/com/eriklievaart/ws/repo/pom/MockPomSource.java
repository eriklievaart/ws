package com.eriklievaart.ws.repo.pom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.ws.config.dependency.DependencyReference;

public class MockPomSource implements PomSource {

	private Map<DependencyReference, String> dependencyToXml = new Hashtable<>();

	public void put(DependencyReference dependency, String xml) {
		dependencyToXml.put(dependency, xml);
	}

	@Override
	public String get(DependencyReference dependency) throws IOException {
		CheckCollection.isPresent(dependencyToXml, dependency);
		return dependencyToXml.get(dependency);
	}
}
