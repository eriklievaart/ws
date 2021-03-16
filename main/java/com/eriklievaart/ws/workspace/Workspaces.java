package com.eriklievaart.ws.workspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import com.eriklievaart.ws.cli.CliArguments;
import com.eriklievaart.ws.config.EclipsePaths;
import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.runtime.RuntimeCommand;
import com.eriklievaart.ws.runtime.RuntimeInvoker;
import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.toolkit.io.FileUtils;
import com.eriklievaart.ws.toolkit.io.IORuntimeException;
import com.eriklievaart.ws.toolkit.io.PropertiesUtils;

public class Workspaces {

	private static final String ARROW = " -> ";
	private static Map<String, Workspace> workspaces = new Hashtable<>();
	private static List<String> features = Arrays.asList("java", "osgi", "jar", "war", "zip", "install", "application");

	static {
		load();
	}

	public static void list() {
		System.out.println("workspaces" + workspaces.keySet());
	}

	public static void createWorkspace(String workspace) {
		if (workspace.equals(CliArguments.ALL_WORKSPACES)) {
			createAllWorkSpaces();
		} else {
			createNamedWorkspace(workspace);
		}
	}

	public static void createAllWorkSpaces() {
		Set<String> projects = new HashSet<>();

		iterateWorkspaces(ws -> {
			System.out.println();
			System.out.println("# generate " + ws.getName() + " #");
			createNamedWorkspace(ws.getName());
			projects.addAll(ws.getProjects());
		});
		Antastic.generateMetadata();
	}

	private static void createNamedWorkspace(String workspace) {
		withWorkspace(workspace, ws -> {
			Eclipse.createWorkspace(ws);
			ws.getProjects().forEach(p -> {
				Eclipse.generateProjectMetadata(p);
			});
		});
	}

	public static void defineWorkspace(String name, List<String> projects) {
		Workspace workspace = workspaces.getOrDefault(name, new Workspace(name));
		removeMissingProjects(workspace, projects);

		for (String project : projects) {
			linkProjectNoSave(workspace, project);
			System.out.println();
		}
		workspaces.put(name, workspace);
		System.out.println("workspace stored: " + workspace);
		createWorkspace(name);
		save();
	}

	private static void removeMissingProjects(Workspace workspace, List<String> keep) {
		ArrayList<String> fullCopy = new ArrayList<>(workspace.getProjects());
		for (String project : fullCopy) {
			if (!keep.contains(project)) {
				workspace.removeProject(project);
			}
		}
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
					System.out.println("already linked: " + workspaceName + ARROW + projectName);
				} else {
					linkProject(workspace, projectName);
				}
			}
			System.out.println(workspace);
		});
	}

	private static void linkProject(Workspace workspace, String projectName) {
		linkProjectNoSave(workspace, projectName);
		save();
	}

	private static void linkProjectNoSave(Workspace workspace, String projectName) {
		if (!ResourcePaths.getGitDir(projectName).isDirectory()) {
			init(projectName);
		}
		if (workspace.containsProject(projectName)) {
			System.out.println("already linked: " + workspace.getName() + ARROW + projectName);
			return;
		}
		workspace.addProject(projectName);
		Eclipse.generateProjectMetadata(projectName);
		System.out.println("added new link: " + workspace.getName() + ARROW + projectName);
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

	private static void init(String project) {
		File propertyFile = ResourcePaths.getAntPropertyFile(project);
		Properties properties = propertyFile.exists() ? PropertiesUtils.load(propertyFile) : new Properties();

		if (properties.isEmpty()) {
			ConsoleUtils.println("Select features to enable for project '" + project + "'");
			ConsoleUtils.println(features.toString());
			selectFeatures(properties);
		}
		File gitDir = ResourcePaths.getGitDir(project);
		gitDir.mkdirs();
		RuntimeInvoker.invoke(new RuntimeCommand("git", "init"), gitDir);

		if (Eclipse.isJavaEnabled(properties)) {
			createEmptyDependenciesFile(project);
		}
		if (!properties.isEmpty()) {
			PropertiesUtils.store(properties, propertyFile);
		}
	}

	private static void createEmptyDependenciesFile(String project) {
		try {
			File file = ResourcePaths.getDependencyFile(project);
			file.getParentFile().mkdirs();
			file.createNewFile();

		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	private static void selectFeatures(Properties properties) {
		String input = System.console().readLine();
		List<String> enabled = Arrays.asList(input.toLowerCase().split("[^a-zA-Z]++"));
		for (String feature : enabled) {
			if (feature.length() > 0) {
				if (features.contains(feature)) {
					properties.put("enable." + feature, "true");
				} else {
					ConsoleUtils.printWarning("skipping unknown feature " + feature);
				}
			}
		}
		properties.put("target", getTarget(new HashSet<>(enabled)));
	}

	private static String getTarget(HashSet<String> enabled) {
		if (enabled.contains("osgi")) {
			if (enabled.contains("application")) {
				return "master-osgi-deploy";
			} else if (enabled.contains("install")) {
				return "master-osgi-install";
			}
		}
		if (enabled.contains("application")) {
			return "master-jar-deploy";
		} else if (enabled.contains("install")) {
			return "master-install";
		}
		return enabled.contains("java") ? "master-resolve" : "master-clean";
	}
}
