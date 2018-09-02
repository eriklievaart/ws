package com.eriklievaart.ws.cli;

import java.util.ArrayList;
import java.util.List;

public class CliArguments {
	public static final String ALL_WORKSPACES = "+";

	private String workspace;
	private List<String> projects = new ArrayList<>();

	public CliArguments(String workspace) throws CliInputException {
		if (!workspace.equals(ALL_WORKSPACES)) {
			Cli.checkIdentifier(workspace);
		}
		this.workspace = workspace;
	}

	public void addProjects(String... add) throws CliInputException {
		for (String item : add) {
			Cli.checkIdentifier(item);
			projects.add(item);
		}
	}

	public List<String> getProjects() {
		return projects;
	}

	public String getWorkspace() {
		return workspace;
	}

	public boolean isAllWorkspaces() {
		return workspace.equals(ALL_WORKSPACES);
	}
}