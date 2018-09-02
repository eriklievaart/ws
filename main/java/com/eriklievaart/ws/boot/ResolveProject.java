package com.eriklievaart.ws.boot;

import java.io.IOException;

import com.eriklievaart.ws.workspace.Project;

public class ResolveProject {

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			throw new RuntimeException("ws invalid arguments! expecting [PROJECT]");
		}
		String projectName = args[0];
		System.out.println("Resolving dependencies of " + projectName + "\n");

		Project project = new Project(projectName);
		project.resolveAll();
	}
}