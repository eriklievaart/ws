package com.eriklievaart.ws.workspace;

import java.io.IOException;

import com.eriklievaart.ws.osgi.BundleMetadata;

public class Project {

	private final String name;

	public Project(String name) {
		this.name = name;
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