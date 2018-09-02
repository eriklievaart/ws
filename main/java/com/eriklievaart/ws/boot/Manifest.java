package com.eriklievaart.ws.boot;

import com.eriklievaart.ws.osgi.BundleMetadata;
import com.eriklievaart.ws.osgi.ManifestGenerator;
import com.eriklievaart.ws.workspace.Project;

public class Manifest {

	public static void main(String[] args) {
		if (args.length < 1) {
			throw new RuntimeException("ws invalid arguments! expecting [PROJECT]");
		}
		String projectName = args[0];
		System.out.println("Generating manifests for " + projectName + "\n");

		Project project = new Project(projectName);
		BundleMetadata metadata = project.scanBundleMetadata();
		new ManifestGenerator(metadata).generateManifestFiles();
	}
}
