package com.eriklievaart.ws.config;

import java.io.File;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.ws.config.dependency.DependencyReference;

public class ResourcePathsU {

	@Test
	public void getPomFile() {
		DependencyReference dependency = new DependencyReference("jetty-util");
		dependency.setGroupId("org.eclipse.jetty");
		dependency.setVersion("10.0.13");

		File file = ResourcePaths.getPomFile(dependency);
		String expect = "/Development/repo/pom/org.eclipse.jetty/jetty-util/10.0.13.xml";
		Check.isEqual(file.getPath(), System.getProperty("user.home") + expect);
	}
}
