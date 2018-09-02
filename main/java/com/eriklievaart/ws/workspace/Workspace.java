package com.eriklievaart.ws.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eriklievaart.ws.cli.Cli;

public class Workspace {

	private String name;
	private Set<String> projects = new HashSet<>();

	public Workspace(String name) {
		Cli.checkIdentifier(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addProject(String project) {
		projects.add(project);
	}

	public void removeProject(String project) {
		projects.remove(project);
	}

	public boolean containsProject(String project) {
		return projects.contains(project);
	}

	public List<String> getProjects() {
		ArrayList<String> result = new ArrayList<>(projects);
		Collections.sort(result);
		return result;
	}

	@Override
	public String toString() {
		return "Workspace[" + name + " -> " + String.join(", ", projects) + "]";
	}
}
