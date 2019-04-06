package com.eriklievaart.ws.config;

import java.io.File;

import com.eriklievaart.ws.toolkit.io.UrlUtils;

public class EclipsePaths {
	private static final String WORKSPACES_FILE = "@home@/Development/git/ws/main/static/workspaces.txt";
	private static final String WORKSPACE_DIR = "@home@/Development/workspace/@workspace@";

	private static final String TEMPLATE_ECLIPSE_DIR = "@home@/Development/git/ws/main/static/eclipse/";
	private static final String TEMPLATE_OXYGEN_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "oxygen.epf");
	private static final String TEMPLATE_JAVA_PROJECT_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, ".project-java");
	private static final String TEMPLATE_BASIC_PROJECT_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, ".project-basic");
	private static final String TEMPLATE_CLASSPATH_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, ".classpath");
	private static final String TEMPLATE_WORKBENCH_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "workbench.xmi");
	private static final String TEMPLATE_TYPE_FILTER_FILE = UrlUtils.append(TEMPLATE_ECLIPSE_DIR, "typefilter.txt");

	public static File getWorkspacesFile() {
		return new File(new PropertyReplacer().apply(WORKSPACES_FILE));
	}

	public static File getTemplateClasspathFile(String project) {
		return new File(PropertyReplacer.project(project).apply(TEMPLATE_CLASSPATH_FILE));
	}

	public static File getTemplateTypeFilterFile() {
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

	public static File getTemplateJavaProjectFile() {
		return new File(new PropertyReplacer().apply(TEMPLATE_JAVA_PROJECT_FILE));
	}

	public static File getTemplateBasicProjectFile() {
		return new File(new PropertyReplacer().apply(TEMPLATE_BASIC_PROJECT_FILE));
	}
}
