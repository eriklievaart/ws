package com.eriklievaart.ws.repo.pom;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.XmlBuilder;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxDependenciesHandlerU {

	@Test
	public void parseDependencyTest() throws IOException {
		SaxDependenciesHandler testable = new SaxDependenciesHandler();

		XmlBuilder xml = new XmlBuilder("/project/dependencies/dependency");
		xml.multiText("artifactId=slf4j-api|groupId=org.slf4j|version=2.0.6|scope=test");

		SaxSupport.parse(xml.toString(), testable);
		// dependency has test scope, so should be skipped
		CheckCollection.isEmpty(testable.getDependencies());
	}

	@Test
	public void parseDependencyWithVersion() throws IOException {
		SaxDependenciesHandler testable = new SaxDependenciesHandler();

		XmlBuilder xml = new XmlBuilder("/project/dependencies/dependency");
		xml.multiText("artifactId=slf4j-api|groupId=org.slf4j|version=2.0.6");

		SaxSupport.parse(xml.toString(), testable);

		List<DependencyReference> dependencies = testable.getDependencies();
		DependencyReference dependency = CollectionTool.getSingle(dependencies);

		Check.isEqual(dependency.getArtifactId(), "slf4j-api");
		Check.isEqual(dependency.getGroupId(), "org.slf4j");
		Check.isEqual(dependency.getVersion(), "2.0.6");
	}

	@Test
	public void parseDependencyX2() throws IOException {
		SaxDependenciesHandler testable = new SaxDependenciesHandler();

		XmlBuilder xml = new XmlBuilder("/project/dependencies");
		xml.createElement("dependency", b -> {
			b.multiText("artifactId=slf4j-api|groupId=org.slf4j|version=2.0.6");
		});
		xml.createElement("dependency", b -> {
			b.multiText("artifactId=jetty-util|groupId=org.eclipse.jetty|version=11.0.14");
		});

		SaxSupport.parse(xml.toString(), testable);
		List<DependencyReference> dependencies = testable.getDependencies();

		DependencyReference slf4j = new DependencyReference("slf4j-api");
		slf4j.setGroupId("org.slf4j");
		slf4j.setVersion("2.0.6");

		DependencyReference util = new DependencyReference("jetty-util");
		util.setGroupId("org.eclipse.jetty");
		util.setVersion("11.0.14");

		Assertions.assertThat(dependencies).containsExactly(slf4j, util);
	}
}
