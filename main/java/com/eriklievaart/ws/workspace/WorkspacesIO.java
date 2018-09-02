package com.eriklievaart.ws.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.eriklievaart.ws.toolkit.io.LineFilter;

public class WorkspacesIO {


	static List<Workspace> parseLines(List<String> lines) {
		List<Workspace> parsed = new ArrayList<>();
		for (String line : LineFilter.filterEmptyAndComments(lines)) {

			String name = line.trim().replaceFirst("\\s.++", "");
			Workspace workspace = new Workspace(name);
			String[] projects = line.trim().replaceFirst("\\S++\\s++", "").split("\\s+");
			for (String project : projects) {
				workspace.addProject(project);
			}
			parsed.add(workspace);
		}
		return parsed;
	}

	static List<String> getLines(Collection<Workspace> workspaces) {
		List<String> lines = new ArrayList<>();
		for (Workspace workspace : workspaces) {
			lines.add(workspace.getName() + " " + String.join(" ", workspace.getProjects()));
		}
		Collections.sort(lines);
		return lines;
	}
}
