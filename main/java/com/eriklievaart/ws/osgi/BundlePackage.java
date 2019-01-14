package com.eriklievaart.ws.osgi;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.toolkit.io.FileUtils;

public class BundlePackage implements ManifestSource {

	private final Set<String> apiExport = new HashSet<>();
	private final Set<ImportStatement> apiImport = new HashSet<>();

	private final String project;
	private final String bundle;
	private final File javaDir;

	public BundlePackage(String project, String bundle) {
		this.project = project;
		this.bundle = bundle;
		this.javaDir = ResourcePaths.getSourceJavaDir(project, bundle);

		index();
	}

	private void index() {
		index(ResourcePaths.getSourcePackageDir(project, bundle));
	}

	private void index(File file) {
		String path = file.getAbsolutePath();

		if (file.isDirectory()) {
			if (path.endsWith("/api") || path.contains("/api/")) {
				String pkg = path.substring(javaDir.getAbsolutePath().length() + 1).replaceAll("[\\/]", ".");
				apiExport.add(pkg);
			}
			for (File child : file.listFiles()) {
				index(child);
			}
		}
		if (file.isFile() && file.getName().endsWith(".java")) {
			indexImports(file);
		}
	}

	private void indexImports(File file) {
		for (String line : FileUtils.readLines(file)) {
			if (line.startsWith("public") || line.startsWith("private") || line.startsWith("protected")) {
				return;
			}
			if (line.startsWith("import")) {
				apiImport.add(new ImportStatement(extractPackage(line), file));
			}
		}
	}

	static String extractPackage(String line) {
		return line.replaceFirst("import\\s++", "").replaceFirst("[.][^.]++$", "");
	}

	public String getBundleName() {
		return bundle;
	}

	@Override
	public File getManifestFile() {
		return ResourcePaths.getBundleManifestFile(project, bundle);
	}

	@Override
	public boolean isManifestAlreadyPresent() {
		return getManifestFile().isFile();
	}

	@Override
	public File getActivatorFile() {
		return ResourcePaths.getBundleActivatorFile(project, bundle);
	}

	@Override
	public boolean isActivatorPresent() {
		return getActivatorFile().exists();
	}

	@Override
	public String getActivatorClass() {
		return ResourcePaths.getBundleActivatorClass(project, bundle);
	}

	@Override
	public Set<String> getApiPackages() {
		return Collections.unmodifiableSet(apiExport);
	}

	@Override
	public Set<ImportStatement> getImports() {
		return apiImport;
	}

	@Override
	public String getBasePackage() {
		return ResourcePaths.getBundlePackage(project, bundle);
	}
}
