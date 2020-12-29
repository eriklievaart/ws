package com.eriklievaart.ws.workspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.eriklievaart.ws.config.EclipsePaths;
import com.eriklievaart.ws.config.PropertyReplacer;
import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.config.dependency.LibType;
import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.toolkit.io.FileUtils;
import com.eriklievaart.ws.toolkit.io.UrlUtils;

public class Eclipse {
	private static final String TYPE_FILTER_KEY = "/instance/org.eclipse.jdt.ui/org.eclipse.jdt.ui.typefilter.enabled=";
	private static final String JAVA_PROPERTY = "enable.java";
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
			FileUtils.copyFile(template, destination);
		}
	}

	private static void updateTypeFilters() throws IOException {
		File oxygen = EclipsePaths.getTemplateOxygenFile();

		List<String> lines = FileUtils.readLines(oxygen);
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith(TYPE_FILTER_KEY)) {
				lines.add(i, TYPE_FILTER_KEY + joinTypeFilters());
				lines.remove(i + 1);
				break;
			}
		}
		FileUtils.writeLines(oxygen, lines);
	}

	private static StringBuilder joinTypeFilters() throws IOException {
		StringBuilder builder = new StringBuilder();

		for (String line : FileUtils.readLines(EclipsePaths.getTemplateTypeFilterFile())) {
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
			if (isJavaProject(project)) {
				generateJavaProjectMetadata(project);
			} else {
				generateBasicProjectMetadata(project);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void generateJavaProjectMetadata(String project) throws IOException {
		String projectData = readAndReplace(EclipsePaths.getTemplateJavaProjectFile(), project);
		FileUtils.writeStringToFile(projectData, ResourcePaths.getDestinationProjectFile(project));

		String classpathData = readAndReplace(EclipsePaths.getTemplateClasspathFile(project), project);
		classpathData = classpathData.replace("@lib@", libEntries(project)).replace("@jdk@", "JavaSE-11");
		FileUtils.writeStringToFile(classpathData, ResourcePaths.getDestinationClasspathFile(project));
	}

	private static void generateBasicProjectMetadata(String project) throws IOException {
		String projectData = readAndReplace(EclipsePaths.getTemplateBasicProjectFile(), project);
		FileUtils.writeStringToFile(projectData, ResourcePaths.getDestinationProjectFile(project));
	}

	private static boolean isJavaProject(String project) {
		Properties properties = new Properties();
		File file = ResourcePaths.getAntPropertyFile(project);

		if (file.isFile()) {
			try (InputStream is = new FileInputStream(file)) {
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isJavaEnabled(properties);
	}

	public static boolean isJavaEnabled(Properties properties) {
		if (!properties.containsKey(JAVA_PROPERTY)) {
			return false;
		}
		return properties.get(JAVA_PROPERTY).toString().trim().toLowerCase().equals("true");
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
				ConsoleUtils.printError("sources not available: " + sources);
			}
			builder.append(getClassPathEntry(lib, dependency, sources));
		});
		return builder.toString();
	}

	private static String getClassPathEntry(LibType lib, DependencyReference dependency, File sources) {
		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@jar@", UrlUtils.append("lib", lib.getDir(), dependency.getVersionedFileName()));
		replacer.replace("@sources@", sources.getAbsolutePath());
		return replacer.apply("\t<classpathentry kind=\"lib\" path=\"@jar@\" sourcepath=\"@sources@\"/>\n");
	}

	private static String readAndReplace(File file, String project) throws IOException {
		String data = FileUtils.toString(file);

		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@project@", project);
		return replacer.apply(data);
	}
}
