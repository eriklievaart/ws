
package com.eriklievaart.ws.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.toolkit.io.FileTool;

public class Antastic {

	public static void generateMetadata() {
		List<String> lines = new ArrayList<>();
		File file = ResourcePaths.getDestinationAntaticConfigFile();
		System.out.println("Generating antastic metadata in " + file);

		addMasterBuildFile(lines);
		iterateProjects(lines);
		iterateWorkspaces(lines);

		FileTool.writeLines(file, lines);
	}

	public static void addMasterBuildFile(List<String> lines) {
		lines.add("buildfile");
		lines.add("\tpath=" + ResourcePaths.getMasterBuildFile().getAbsolutePath());
		lines.add("\tfilter=" + ResourcePaths.getMasterFilterFile().getAbsolutePath());
	}

	public static void iterateWorkspaces(final List<String> lines) {
		Workspaces.iterateWorkspaces(ws -> {
			lines.add("group");
			lines.add("\tname=" + ws.getName());
			lines.add("\tprojects=" + String.join(", ", ws.getProjects()));
		});
	}

	private static void iterateProjects(List<String> lines) {
		Workspaces.iterateProjects(project -> {
			lines.add("project");
			lines.add("\tname=" + project);
			lines.add("\tfile=" + ResourcePaths.getDestinationProjectDir(project).getAbsolutePath());
			lines.add("\tproperties=" + ResourcePaths.getAntPropertyFile(project).getAbsolutePath());
		});
	}
}
