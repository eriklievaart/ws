package com.eriklievaart.ws.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.eriklievaart.ws.cli.CliArguments;
import com.eriklievaart.ws.config.EclipsePaths;
import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.toolkit.io.FileUtils;

public class Workspaces {

	private static final String ARROW = " -> ";
	private static Map<String, Workspace> workspaces = new Hashtable<>();

	static {
		load();
	}

	public static void list() {
		System.out.println("workspaces" + workspaces.keySet());
	}

	public static void generateAll() {
		Set<String> projects = new HashSet<>();

		iterateWorkspaces(ws -> {
			createWorkspace(ws.getName());
			projects.addAll(ws.getProjects());
		});
		for (String project : projects) {
			Eclipse.generateProjectMetadata(project);
		}
		Antastic.generateMetadata();
	}

	public static void createWorkspace(String workspace) {
		if (workspace.equals(CliArguments.ALL_WORKSPACES)) {
			generateAll();
		} else {
			withWorkspace(workspace, ws -> {
				Eclipse.createWorkspace(ws);
				ws.getProjects().forEach(p -> {
					Eclipse.generateProjectMetadata(p);
				});
			});
		}
	}

	public static void defineWorkspace(String name, List<String> projects) {
		if (workspaces.containsKey(name)) {
			System.out.println("overwriting existing workspace " + name);
			workspaces.remove(name);
		}
		Workspace workspace = new Workspace(name);
		for (String project : projects) {
			workspace.addProject(project);
		}
		workspaces.put(name, workspace);
		System.out.println("workspace stored: " + workspace);
		createWorkspace(name);
		save();
	}

	public static void deleteWorkspace(String name) {
		withWorkspace(name, workspace -> {
			workspaces.remove(name);
			System.out.println("deleted: " + workspace);
			save();
		});
	}

	public static void iterateWorkspaces(Consumer<Workspace> consumer) {
		for (Workspace workspace : workspaces.values()) {
			consumer.accept(workspace);
		}
	}

	public static void iterateProjects(Consumer<String> consumer) {
		List<String> projects = new ArrayList<>();
		for (Workspace workspace : workspaces.values()) {
			for (String project : workspace.getProjects()) {
				if (!projects.contains(project)) {
					projects.add(project);
				}
			}
		}
		Collections.sort(projects);
		for (String project : projects) {
			consumer.accept(project);
		}
	}

	private static void load() {
		File file = EclipsePaths.getWorkspacesFile();
		workspaces.clear();
		if (file.exists()) {
			for (Workspace workspace : WorkspacesIO.parseLines(FileUtils.readLines(file))) {
				workspaces.put(workspace.getName(), workspace);
			}
		} else {
			ConsoleUtils.printError("*warning*: file does not exist " + file + " no workspaces found");
		}
	}

	private static void save() {
		FileUtils.writeLines(EclipsePaths.getWorkspacesFile(), WorkspacesIO.getLines(workspaces.values()));
		Antastic.generateMetadata();
	}

	public static void showInfo(String workspace) {
		if (workspace.equals(CliArguments.ALL_WORKSPACES)) {
			iterateWorkspaces(System.out::println);
		} else {
			withWorkspace(workspace, System.out::println);
		}
	}

	public static void linkProjects(String workspaceName, List<String> projects) {
		withWorkspace(workspaceName, workspace -> {
			for (String projectName : projects) {
				if (workspace.containsProject(projectName)) {
					System.out.println("link already exists: " + workspaceName + ARROW + projectName);
				} else {
					System.out.println("added new link: " + workspaceName + ARROW + projectName);
					workspace.addProject(projectName);
					Eclipse.generateProjectMetadata(projectName);
					save();
				}
			}
			System.out.println(workspace);
		});
	}

	public static void unlinkProjects(String workspaceName, List<String> projects) {
		withWorkspace(workspaceName, workspace -> {
			for (String projectName : projects) {
				if (workspace.containsProject(projectName)) {
					System.out.println("removing link: " + workspaceName + ARROW + projectName);
					workspace.removeProject(projectName);
					save();
				} else {
					ConsoleUtils.printError("*error*: link does not exist: " + workspaceName + ARROW + projectName);
				}
			}
			System.out.println(workspace);
		});
	}

	private static void withWorkspace(String name, Consumer<Workspace> consumer) {
		if (!workspaces.containsKey(name)) {
			ConsoleUtils.printError("error: workspace does not exist => " + name);
			return;
		}
		consumer.accept(workspaces.get(name));
	}
}
