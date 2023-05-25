package com.eriklievaart.ws.config;

import java.io.File;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.toolkit.io.UrlUtils;

public class ResourcePaths {

	private static final String REPO_DIR = "@home@/Development/repo";
	private static final String INDEX_FILE = UrlUtils.append(REPO_DIR, "index.txt");
	private static final String REMOTE_DIR = UrlUtils.append(REPO_DIR, "remote");
	private static final String SNAPSHOT_FILE = UrlUtils.append(REPO_DIR, "local/@project@/snapshot/@project@.jar");
	private static final String POM_FILE = UrlUtils.append(REPO_DIR, "pom/@group@/@artifact@/@version@.xml");

	private static final String PROJECT_DIR = "@home@/Development/project/@project@";
	private static final String DEPENDENCY_FILE = "@home@/Development/git/@project@/main/config/dependencies.txt";
	private static final String ANT_PROPERTY_FILE = "@home@/Development/git/@project@/main/config/ant.properties";
	private static final String LIB_DIR = "@home@/Development/project/@project@/lib";
	private static final String MASTER_BUILD_FILE = "@home@/Development/git/ant/master.xml";
	private static final String MASTER_FILTER_FILE = "@home@/Development/git/ant/filter.ini";

	private static final String ANTASTIC_CONFIG_FILE = "@home@/Applications/antastic/data/antastic.ini";

	private static final String SOURCE_GIT_DIR = "@home@/Development/git/@project@";
	private static final String SOURCE_JAVA_DIR = UrlUtils.append(SOURCE_GIT_DIR, "main/java");
	private static final String SOURCE_PACKAGE_DIR = UrlUtils.append(SOURCE_JAVA_DIR, "com/eriklievaart/@project@");
	private static final String SOURCE_MASTER_ACTIVATOR_FILE = UrlUtils.append(SOURCE_PACKAGE_DIR, "Activator.java");
	private static final String SOURCE_MASTER_PACKAGE = "com.eriklievaart.@project@";
	private static final String SOURCE_MASTER_ACTIVATOR_CLASS = "com.eriklievaart.@project@.Activator";
	private static final String SOURCE_BUNDLE_DIR = UrlUtils.append(SOURCE_PACKAGE_DIR, "@bundle@");
	private static final String SOURCE_BUNDLE_OSGI_FILE = UrlUtils.append(SOURCE_BUNDLE_DIR, "osgi.txt");
	private static final String SOURCE_BUNDLE_ACTIVATOR_FILE = UrlUtils.append(SOURCE_BUNDLE_DIR, "Activator.java");
	private static final String SOURCE_BUNDLE_PACKAGE = "com.eriklievaart.@project@.@bundle@";
	private static final String SOURCE_BUNDLE_ACTIVATOR_CLASS = "com.eriklievaart.@project@.@bundle@.Activator";

	private static final String BUILD_MANIFEST_DIR = "/tmp/build/@project@/spool/manifest";
	private static final String BUILD_MASTER_MANIFEST = UrlUtils.append(BUILD_MANIFEST_DIR, "@project@.txt");
	private static final String BUILD_BUNDLE_MANIFEST = UrlUtils.append(BUILD_MANIFEST_DIR, "@project@-@bundle@.txt");

	private static final String DESTINATION_PROJECT_DIR = "@home@/Development/project/@project@";
	private static final String DESTINATION_PROJECT_FILE = UrlUtils.append(DESTINATION_PROJECT_DIR, ".project");
	private static final String DESTINATION_CLASSPATH_FILE = UrlUtils.append(DESTINATION_PROJECT_DIR, ".classpath");

	public static File getSnapshotFile(String project) {
		return new File(PropertyReplacer.project(project).apply(SNAPSHOT_FILE));
	}

	public static File getSourceJar(File jar) {
		File parent = jar.getParentFile();
		String name = jar.getName();
		if (!jar.getName().endsWith("jar")) {
			throw new RuntimeException("Not a jar! " + jar);
		}
		return new File(parent.getPath().replace("/bundle", "/src"), name.replaceFirst("\\.jar$", "-src.jar"));
	}

	public static String getRemoteDir() {
		return new PropertyReplacer().apply(REMOTE_DIR);
	}

	public static File getIndexFile() {
		return new File(new PropertyReplacer().apply(INDEX_FILE));
	}

	public static File getGitDir(String project) {
		return new File(PropertyReplacer.project(project).apply(SOURCE_GIT_DIR));
	}

	public static File getDependencyFile(String project) {
		return new File(PropertyReplacer.project(project).apply(DEPENDENCY_FILE));
	}

	public static File getLibRootDir(String project) {
		return new File(PropertyReplacer.project(project).apply(LIB_DIR));
	}

	public static File getProjectDir(String project) {
		return new File(PropertyReplacer.project(project).apply(PROJECT_DIR));
	}

	public static File getDestinationProjectDir(String project) {
		return new File(PropertyReplacer.project(project).apply(DESTINATION_PROJECT_DIR));
	}

	public static File getDestinationProjectFile(String project) {
		return new File(PropertyReplacer.project(project).apply(DESTINATION_PROJECT_FILE));
	}

	public static File getMasterBuildFile() {
		return new File(new PropertyReplacer().apply(MASTER_BUILD_FILE));
	}

	public static File getMasterFilterFile() {
		return new File(new PropertyReplacer().apply(MASTER_FILTER_FILE));
	}

	public static File getDestinationAntaticConfigFile() {
		return new File(new PropertyReplacer().apply(ANTASTIC_CONFIG_FILE));
	}

	public static File getDestinationClasspathFile(String project) {
		return new File(PropertyReplacer.project(project).apply(DESTINATION_CLASSPATH_FILE));
	}

	public static File getAntPropertyFile(String project) {
		return new File(PropertyReplacer.project(project).apply(ANT_PROPERTY_FILE));
	}

	public static File getBundleManifestFile(String project, String bundle) {
		return new File(PropertyReplacer.bundle(project, bundle).apply(BUILD_BUNDLE_MANIFEST));
	}

	public static File getBundleActivatorFile(String project, String bundle) {
		return new File(PropertyReplacer.bundle(project, bundle).apply(SOURCE_BUNDLE_ACTIVATOR_FILE));
	}

	public static String getBundlePackage(String project, String bundle) {
		return PropertyReplacer.bundle(project, bundle).apply(SOURCE_BUNDLE_PACKAGE);
	}

	public static String getBundleActivatorClass(String project, String bundle) {
		return PropertyReplacer.bundle(project, bundle).apply(SOURCE_BUNDLE_ACTIVATOR_CLASS);
	}

	public static File getMasterManifestFile(String project) {
		return new File(PropertyReplacer.project(project).apply(BUILD_MASTER_MANIFEST));
	}

	public static File getMasterActivatorFile(String project) {
		return new File(PropertyReplacer.project(project).apply(SOURCE_MASTER_ACTIVATOR_FILE));
	}

	public static String getMasterActivatorClass(String project) {
		return PropertyReplacer.project(project).apply(SOURCE_MASTER_ACTIVATOR_CLASS);
	}

	public static String getMasterPackage(String project) {
		return PropertyReplacer.project(project).apply(SOURCE_MASTER_PACKAGE);
	}

	public static File getSourcePackageDir(String project) {
		return new File(PropertyReplacer.project(project).apply(SOURCE_PACKAGE_DIR));
	}

	public static File getSourcePackageBundleDir(String project, String bundle) {
		return new File(PropertyReplacer.bundle(project, bundle).apply(SOURCE_BUNDLE_DIR));
	}

	public static File getSourceJavaBundleDir(String project, String bundle) {
		return new File(PropertyReplacer.bundle(project, bundle).apply(SOURCE_JAVA_DIR));
	}

	public static File getSourceBundleOsgiConfigFile(String project, String bundle) {
		return new File(PropertyReplacer.bundle(project, bundle).apply(SOURCE_BUNDLE_OSGI_FILE));
	}

	public static File getPomFile(DependencyReference dependency) {
		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@group@", dependency.getGroupId());
		replacer.replace("@artifact@", dependency.getArtifactId());
		replacer.replace("@version@", dependency.getVersion());
		return new File(replacer.apply(POM_FILE));
	}
}
