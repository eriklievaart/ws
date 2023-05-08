package com.eriklievaart.ws.repo.pom;

import java.io.File;
import java.io.IOException;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.Maven;
import com.eriklievaart.ws.toolkit.io.FileUtils;

public class MavenPomSource implements PomSource {

	@Override
	public String get(DependencyReference dependency) throws IOException {
		File file = ResourcePaths.getPomFile(dependency);
		System.out.println("pom for " + dependency + ":\n" + file + "\n");
		Maven.downloadPom(dependency, file);
		return FileUtils.toString(file);
	}
}
