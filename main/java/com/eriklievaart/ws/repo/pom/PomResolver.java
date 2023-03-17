package com.eriklievaart.ws.repo.pom;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.Maven;
import com.eriklievaart.ws.repo.sax.SaxHandlerSplitter;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class PomResolver {

	private static Map<DependencyReference, Pom> cache = new Hashtable<>();

	public static Pom loadPom(DependencyReference dependency) throws IOException {
		if (cache.containsKey(dependency)) {
			return cache.get(dependency);
		}
		if (dependency.isSnapshot()) {
			throw new IllegalArgumentException("Cannot get pom for snapshot: " + dependency);
		}
		File file = ResourcePaths.getPomFile(dependency);
		System.out.println("pom for " + dependency + ":\n" + file + "\n");
		Maven.downloadPom(dependency, file);

		Pom pom = new Pom(dependency);
		cache.put(dependency, pom);
		parsePom(file, pom);

		return pom;
	}

	private static void parsePom(File file, Pom pom) throws IOException {
		SaxDependenciesHandler deps = new SaxDependenciesHandler();
		SaxPropertiesHandler props = new SaxPropertiesHandler();
		SaxParentHandler par = new SaxParentHandler();
		SaxDependencyManagementHandler man = new SaxDependencyManagementHandler();
		SaxSupport.parse(file, new SaxHandlerSplitter(par, deps, props, man));

		pom.putAll(props.getProperties());

		Optional<DependencyReference> optionalParent = par.getParent();
		if (optionalParent.isPresent()) {
			pom.setParent(loadPom(optionalParent.get()));
		}

		List<DependencyReference> managed = man.getDependencies();
		resolve(managed, pom.getProperties());
		pom.setManaged(managed);

		loadDependencies(pom, deps.getDependencies());
	}

	private static void loadDependencies(Pom pom, List<DependencyReference> dependencies) throws IOException {
		for (DependencyReference d : dependencies) {
			pom.updateVersion(d);
			if (d.versionContainsProperty() || d.isSnapshot()) {
				System.out.println("**warning**: unable to resolve version for " + d + "\n" + pom + "\n");

			} else {
				pom.addDependency(loadPom(d));
			}
		}
	}

	private static void resolve(List<DependencyReference> dependencies, Map<String, String> properties) {
		for (DependencyReference dependency : dependencies) {
			if (dependency.versionContainsProperty()) {
				String property = dependency.getVersionProperty();
				if (properties.containsKey(property)) {
					dependency.setVersion(properties.get(property));
				}
			}
		}
	}
}
