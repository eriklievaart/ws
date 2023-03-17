package com.eriklievaart.ws.repo.pom;

import java.io.IOException;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxDependencyManagementHandlerU {

	@Test
	public void skipStandardDependencies() throws IOException {
		SaxDependencyManagementHandler testable = new SaxDependencyManagementHandler();

		try (InputStream is = getClass().getResourceAsStream("/pom/dependency-x2.xml")) {
			SaxSupport.parse(is, testable);
		}
		// should only look in <dependencyManagement> not in <dependencies>
		CheckCollection.isEmpty(testable.getDependencies());
	}

	@Test
	public void parseDependencyManagement() throws IOException {
		SaxDependencyManagementHandler testable = new SaxDependencyManagementHandler();

		try (InputStream is = getClass().getResourceAsStream("/pom/parent-dependency-management.xml")) {
			SaxSupport.parse(is, testable);
		}
		DependencyReference guice = new DependencyReference("guice");
		guice.setGroupId("com.google.inject");
		guice.setVersion("${guice.version}");
		Assertions.assertThat(testable.getDependencies().contains(guice));

		CheckCollection.isSize(testable.getDependencies(), 205);
	}
}
