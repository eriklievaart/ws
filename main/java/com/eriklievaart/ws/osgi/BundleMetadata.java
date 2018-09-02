package com.eriklievaart.ws.osgi;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.eriklievaart.ws.config.ResourcePaths;

public class BundleMetadata implements ManifestSource {

	private String project;
	private Map<String, BundlePackage> bundles = new Hashtable<>();

	public BundleMetadata(String project) {
		this.project = project;
		indexBundles();
	}

	private void indexBundles() {
		File dir = ResourcePaths.getSourcePackageDir(project);
		for (String bundle : dir.list()) {
			bundles.put(bundle, new BundlePackage(project, bundle));
		}
	}

	public void eachBundle(Consumer<BundlePackage> consumer) {
		bundles.values().forEach(consumer);
	}

	public String getProjectName() {
		return project;
	}

	@Override
	public File getManifestFile() {
		return ResourcePaths.getMasterManifestFile(project);
	}

	@Override
	public boolean isManifestAlreadyPresent() {
		return getManifestFile().isFile();
	}

	@Override
	public File getActivatorFile() {
		return ResourcePaths.getMasterActivatorFile(project);
	}

	@Override
	public boolean isActivatorPresent() {
		return getActivatorFile().isFile();
	}

	@Override
	public String getActivatorClass() {
		return ResourcePaths.getMasterActivatorClass(project);
	}

	@Override
	public Set<String> getApiPackages() {
		HashSet<String> set = new HashSet<>();
		for (BundlePackage bundle : bundles.values()) {
			set.addAll(bundle.getApiPackages());
		}
		return set;
	}

	@Override
	public Set<ImportStatement> getImports() {
		HashSet<ImportStatement> set = new HashSet<>();
		for (BundlePackage bundle : bundles.values()) {
			set.addAll(bundle.getImports());
		}
		return set;
	}

	@Override
	public String getBasePackage() {
		return ResourcePaths.getMasterPackage(project);
	}
}