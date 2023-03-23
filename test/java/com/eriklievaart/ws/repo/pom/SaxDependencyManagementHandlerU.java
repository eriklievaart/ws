package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.XmlBuilder;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxDependencyManagementHandlerU {

	@Test
	public void skipStandardDependencies() throws IOException {
		SaxDependencyManagementHandler testable = new SaxDependencyManagementHandler();

		XmlBuilder xml = new XmlBuilder("/project/dependencies/dependency");
		xml.multiText("artifactId=slf4j-api|groupId=org.slf4j|version=2.0.6");

		SaxSupport.parse(xml.toString(), testable);

		// should only look in <dependencyManagement> not in <dependencies>
		CheckCollection.isEmpty(testable.getDependencies());
	}

	@Test
	public void parseDependencyManagement() throws IOException {
		SaxDependencyManagementHandler testable = new SaxDependencyManagementHandler();

		XmlBuilder xml = new XmlBuilder("/project/dependencyManagement/dependencies/dependency");
		xml.multiText("artifactId=guice|groupId=com.google.inject|version=${guice.version}");

		SaxSupport.parse(xml.toString(), testable);

		DependencyReference guice = new DependencyReference("guice");
		guice.setGroupId("com.google.inject");
		guice.setVersion("${guice.version}");
		Assertions.assertThat(testable.getDependencies()).contains(guice);
	}
}
