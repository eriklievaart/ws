package com.eriklievaart.ws.repo.pom;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eriklievaart.ws.config.dependency.DependencyReference;

public class Pom {

	private Pom parent = null;
	private DependencyReference specification;
	private Map<String, String> properties = new Hashtable<>();
	private Map<String, String> managed = new Hashtable<>();
	private List<Pom> dependencies = new ArrayList<>();

	public Pom(DependencyReference dependency) {
		if (dependency == null) {
			throw new IllegalArgumentException("dependency cannot be null!");
		}
		this.specification = dependency;
	}

	public void setParent(Pom pom) {
		parent = pom;
	}

	public DependencyReference getSpecification() {
		return specification;
	}

	public Optional<Pom> getParent() {
		return parent == null ? Optional.empty() : Optional.of(parent);
	}

	public List<Pom> getDependencies() {
		return dependencies;
	}

	public void addDependency(Pom pom) {
		dependencies.add(pom);
	}

	public void putAll(Map<String, String> map) {
		properties.putAll(map);
	}

	public Map<String, String> getProperties() {
		Map<String, String> map = parent == null ? new Hashtable<>() : parent.getProperties();
		map.putAll(properties);
		return map;
	}

	public void setManaged(List<DependencyReference> list) {
		for (DependencyReference reference : list) {
			if (reference.versionContainsProperty()) {
				System.out.println("unknown version for: " + reference);
			} else {
				managed.put(shortKey(reference), reference.getVersion());
			}
		}
	}

	public void updateVersion(DependencyReference reference) {
		if (reference.versionContainsProperty()) {
			String property = reference.getVersionProperty();
			if (properties.containsKey(property)) {
				reference.setVersion(properties.get(property));
				return;
			}
		}
		if (reference.isSnapshot()) {
			String key = shortKey(reference);
			if (managed.containsKey(key)) {
				reference.setVersion(managed.get(key));
			}
			if (parent != null) {
				parent.updateVersion(reference);
			}
		}
	}

	private String shortKey(DependencyReference reference) {
		return reference.getArtifactId() + ":" + reference.getGroupId();
	}

	@Override
	public String toString() {
		return "Pom[" + specification + "]";
	}
}
