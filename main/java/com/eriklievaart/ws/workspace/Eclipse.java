package com.eriklievaart.ws.workspace;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.eriklievaart.ws.config.EclipsePaths;
import com.eriklievaart.ws.config.PropertyReplacer;
import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.config.dependency.LibType;
import com.eriklievaart.ws.toolkit.io.Console;
import com.eriklievaart.ws.toolkit.io.FileTool;
import com.eriklievaart.ws.toolkit.io.UrlTool;

public class Eclipse {
	private static final String TYPE_FILTER_KEY = "/instance/org.eclipse.jdt.ui/org.eclipse.jdt.ui.typefilter.enabled=";
	private static final String SEMICOLON = ";";

	public static void createWorkspace(Workspace workspace) {
		try {
			updateTypeFilters();
			EclipsePaths.getWorkspaceRootDir(workspace.getName()).mkdirs();
			copyWorkbenchConfig(workspace.getName());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void copyWorkbenchConfig(String name) throws IOException {
		File template = EclipsePaths.getTemplateWorkbench();
		File destination = EclipsePaths.getDestinationWorkbench(name);
		if (!destination.exists()) {
			FileTool.copyFile(template, destination);
		}
	}

	private static void updateTypeFilters() throws IOException {
		File oxygen = EclipsePaths.getTemplateOxygenFile();

		List<String> lines = FileTool.readLines(oxygen);
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith(TYPE_FILTER_KEY)) {
				lines.add(i, TYPE_FILTER_KEY + joinTypeFilters());
				lines.remove(i + 1);
				break;
			}
		}
		FileTool.writeLines(oxygen, lines);
	}

	private static StringBuilder joinTypeFilters() throws IOException {
		StringBuilder builder = new StringBuilder();

		for (String line : FileTool.readLines(EclipsePaths.getTemplateTypeFilterFile())) {
			String trimmed = line.trim();
			if (trimmed.length() > 0 && !line.startsWith("#")) {
				builder.append(trimmed);
				if (!line.endsWith(SEMICOLON)) {
					builder.append(SEMICOLON);
				}
			}
		}
		return builder;
	}

	public static void generateProjectMetadata(String project) {
		try {
			String projectData = readAndReplace(EclipsePaths.getTemplateProjectFile(), project);
			FileTool.writeStringToFile(projectData, ResourcePaths.getDestinationProjectFile(project));

			String classpathData = readAndReplace(EclipsePaths.getTemplateClasspathFile(project), project);
			classpathData = classpathData.replace("@lib@", libEntries(project));
			FileTool.writeStringToFile(classpathData, ResourcePaths.getDestinationClasspathFile(project));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CharSequence libEntries(String project) {
		final StringBuilder builder = new StringBuilder();
		ProjectDependencies dependencies = new ProjectDependencies(project);
		dependencies.resolveAll();

		dependencies.iterate((lib, dependency) -> {
			if (lib == LibType.RUN) {
				return;
			}
			File sources = dependencies.getSourceJar(dependency);
			if (sources.length() == 0) {
				sources.delete();
			}
			if (!sources.isFile()) {
				Console.printError("sources not available: " + sources);
			}
			builder.append(getClassPathEntry(lib, dependency, sources));
		});
		return builder.toString();
	}

	private static String getClassPathEntry(LibType lib, DependencyReference dependency, File sources) {
		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@jar@", UrlTool.append("lib", lib.getDir(), dependency.getVersionedFileName()));
		replacer.replace("@sources@", sources.getAbsolutePath());
		return replacer.apply("\t<classpathentry kind=\"lib\" path=\"@jar@\" sourcepath=\"@sources@\"/>\n");
	}

	private static String readAndReplace(File file, String project) throws IOException {
		String data = FileTool.toString(file);

		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@project@", project);
		return replacer.apply(data);
	}
}
