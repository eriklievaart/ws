package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.XmlBuilder;

public class PomResolverI {

	@Test
	public void loadPomBasic() throws IOException {
		XmlBuilder xml = new XmlBuilder("project/properties");
		xml.multiText("commons.componentid=csv|maven.compiler.source=1.6|commons.encoding=UTF-8");

		DependencyReference dependency = new DependencyReference("commons-csv");
		dependency.setGroupId("org.apache.commons");
		dependency.setVersion("1.9.0");

		MockPomSource poms = new MockPomSource();
		poms.put(dependency, xml.toString());
		PomResolver testable = new PomResolver(poms);

		Pom pom = testable.loadPom(dependency);
		Check.isEqual(pom.getProperties().get("commons.componentid"), "csv");
		Check.isEqual(pom.getProperties().get("commons.encoding"), "UTF-8");
	}

	@Test
	public void loadPomWithDependencyVersionInProperty() throws IOException {
		XmlBuilder xml = new XmlBuilder("project");
		xml.createElement("properties", b -> b.multiText("junit.version=4.13.1"));
		xml.createElement("dependencies/dependency", b -> {
			b.multiText("artifactId=junit|groupId=junit|version=${junit.version}");
		});

		DependencyReference parent = new DependencyReference("slf4j-parent");
		parent.setGroupId("org.slf4j");
		parent.setVersion("2.0.5");

		DependencyReference junit = new DependencyReference("junit");
		junit.setGroupId("junit");
		junit.setVersion("4.13.1");

		MockPomSource poms = new MockPomSource();
		poms.put(parent, xml.toString());
		poms.put(junit, "<project></project>");
		PomResolver testable = new PomResolver(poms);

		Pom pom = testable.loadPom(parent);
		Check.isEqual(pom.getSpecification(), parent);

		Pom dependency = CollectionTool.getSingle(pom.getDependencies());
		Check.isEqual(dependency.getSpecification(), junit);
	}

	@Test
	public void loadPomWithParent() throws IOException {
		XmlBuilder csvXml = new XmlBuilder("project/parent");
		csvXml.multiText("artifactId=commons-parent|groupId=org.apache.commons|version=35");

		XmlBuilder parentXml = new XmlBuilder("project/properties");
		parentXml.multiText("commons.componentid=${project.artifactId}|commons.encoding=iso-8859-1");

		DependencyReference commonsCsv = new DependencyReference("commons-csv");
		commonsCsv.setGroupId("org.apache.commons");
		commonsCsv.setVersion("1.9.0");

		DependencyReference commonsParent = new DependencyReference("commons-parent");
		commonsParent.setGroupId("org.apache.commons");
		commonsParent.setVersion("35");

		MockPomSource poms = new MockPomSource();
		poms.put(commonsCsv, csvXml.toString());
		poms.put(commonsParent, parentXml.toString());
		PomResolver testable = new PomResolver(poms);

		Pom pom = testable.loadPom(commonsCsv);
		// inherited property
		Check.isEqual(pom.getProperties().get("commons.encoding"), "iso-8859-1");

		Pom parent = pom.getParent().get();
		Check.isEqual(parent.getSpecification(), commonsParent);
		Check.isEqual(parent.getProperties().get("commons.encoding"), "iso-8859-1");
	}

	@Test
	public void loadPomWithManagedDependencyInParent() throws IOException {
		XmlBuilder coreXml = new XmlBuilder("project");
		coreXml.createElement("parent", b -> {
			coreXml.multiText("artifactId=logback-parent|groupId=ch.qos.logback|version=1.4.6");
		});
		coreXml.into("dependencies");
		coreXml.createElement("dependency", b -> {
			b.multiText("artifactId=jakarta.servlet-api|groupId=jakarta.servlet");
		});
		// test dependency to ignore
		coreXml.createElement("dependency", b -> {
			b.multiText("artifactId=mockito-core|groupId=org.mockito|scope=test");
		});

		XmlBuilder parentXml = new XmlBuilder("project");
		parentXml.createElement("properties", b -> {
			b.multiText("jakarta.servlet.version=5.0.0");
		});
		parentXml.into("dependencyManagement/dependencies");
		parentXml.createElement("dependency", b -> {
			b.multiText("artifactId=jakarta.servlet-api|groupId=jakarta.servlet|version=${jakarta.servlet.version}");
		});

		DependencyReference coreRef = new DependencyReference("logback-core");
		coreRef.setGroupId("ch.qos.logback");
		coreRef.setVersion("1.4.6");

		DependencyReference parentRef = new DependencyReference("logback-parent");
		parentRef.setGroupId("ch.qos.logback");
		parentRef.setVersion("1.4.6");

		DependencyReference expected = new DependencyReference("jakarta.servlet-api");
		expected.setGroupId("jakarta.servlet");
		expected.setVersion("5.0.0");

		MockPomSource poms = new MockPomSource();
		poms.put(coreRef, coreXml.toString());
		poms.put(parentRef, parentXml.toString());
		poms.put(expected, "<project></project>");
		PomResolver testable = new PomResolver(poms);

		Pom pom = testable.loadPom(coreRef);
		DependencyReference dependency = CollectionTool.getSingle(pom.getDependencies()).getSpecification();
		Check.isEqual(dependency, expected);
	}
}
