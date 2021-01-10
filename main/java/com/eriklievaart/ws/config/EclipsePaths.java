package com.eriklievaart.ws.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.eriklievaart.ws.toolkit.io.UrlUtils;

public class EclipsePaths {

	private static final String WORKSPACE_DIR = "@home@/Development/workspace/@workspace@";

	private static final String WORKSPACES_FILE = "@home@/Development/git/ws/main/static/workspaces.txt";

	private static final String RESOURCE_DIR = "@home@/Development/git/ws/main/resources";
	private static final String TEMPLATE_JAVA_PROJECT_RESOURCE = "/template/.project-java";
	private static final String TEMPLATE_BASIC_PROJECT_RESOURCE = "/template/.project-basic";
	private static final String TEMPLATE_CLASSPATH_RESOURCE = "/template/.classpath";

	private static final String TEMPLATE_ECLIPSE_DIR = UrlUtils.append("@home@/Development/git/ws/main/static/eclipse");
	private static final String TEMPLATE_OXYGEN_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "oxygen.epf");
	private static final String TEMPLATE_WORKBENCH_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "workbench.xmi");
	private static final String TEMPLATE_TYPE_FILTER_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "typefilter.txt");

	public static File getWorkspacesFile() {
		return new File(new PropertyReplacer().apply(WORKSPACES_FILE));
	}

	public static InputStream getTemplateJavaProjectInputStream() {
		return fileOrResource(TEMPLATE_JAVA_PROJECT_RESOURCE);
	}

	public static InputStream getTemplateBasicProjectInputStream() {
		return fileOrResource(TEMPLATE_BASIC_PROJECT_RESOURCE);
	}

	public static InputStream getTemplateClasspathInputStream() {
		return fileOrResource(TEMPLATE_CLASSPATH_RESOURCE);
	}

	private static InputStream fileOrResource(String path) {
		File file = new File(new PropertyReplacer().apply(RESOURCE_DIR + path));
		if (file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace(); // should never happen, because of if(file.exists())
			}
		}
		return EclipsePaths.class.getResourceAsStream(path);
	}

	public static File getTemplateTypeFilterInputStream() {
		return new File(new PropertyReplacer().apply(TEMPLATE_TYPE_FILTER_FILE));
	}

	public static File getTemplateOxygenFile() {
		return new File(new PropertyReplacer().apply(TEMPLATE_OXYGEN_FILE));
	}

	public static File getTemplateWorkbench() {
		return new File(new PropertyReplacer().apply(TEMPLATE_WORKBENCH_FILE));
	}

	public static File getWorkspaceRootDir(String workspace) {
		return new File(PropertyReplacer.workspace(workspace).apply(WORKSPACE_DIR));
	}

	public static File getDestinationWorkbench(String workspace) {
		return new File(getWorkspaceRootDir(workspace), ".metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi");
	}
}
