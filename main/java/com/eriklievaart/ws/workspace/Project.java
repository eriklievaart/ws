package com.eriklievaart.ws.workspace;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.osgi.BundleMetadata;

import java.io.File;
import java.io.IOException;

public class Project {

	private final String name;

	public Project(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public File getGitDir() {
		return ResourcePaths.getGitDir(name);
	}

	public File getSourceJavaDir() {
		return ResourcePaths.getSourceJavaDir(name);
	}

	public void resolveAll() throws IOException {
		ProjectDependencies dependencies = new ProjectDependencies(name);
		dependencies.resolveAll();
		dependencies.writeback();
		Eclipse.generateProjectMetadata(name);
	}

	public BundleMetadata scanBundleMetadata() {
		return new BundleMetadata(name);
	}

	@Override
	public String toString() {
		return "Project[" + name + "]";
	}
}
