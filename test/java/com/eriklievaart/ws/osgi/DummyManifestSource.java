package com.eriklievaart.ws.osgi;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DummyManifestSource implements ManifestSource {

	private File manifestFile;
	private boolean manifestAlreadyPresent;
	private File activatorFile;
	private boolean activatorPresent;
	private String activatorClass;
	private Set<String> apiPackages = new HashSet<>();
	private Set<ImportStatement> imports = new HashSet<>();
	private String basePackage;

	@Override
	public File getManifestFile() {
		return manifestFile;
	}

	@Override
	public boolean isManifestAlreadyPresent() {
		return manifestAlreadyPresent;
	}

	@Override
	public File getActivatorFile() {
		return activatorFile;
	}

	@Override
	public boolean isActivatorPresent() {
		return activatorPresent;
	}

	@Override
	public String getActivatorClass() {
		return activatorClass;
	}

	@Override
	public Set<String> getApiPackages() {
		return apiPackages;
	}

	@Override
	public Set<ImportStatement> getImports() {
		return imports;
	}

	@Override
	public String getBasePackage() {
		return basePackage;
	}

	public void setManifestFile(File manifestFile) {
		this.manifestFile = manifestFile;
	}

	public void setManifestAlreadyPresent(boolean manifestAlreadyPresent) {
		this.manifestAlreadyPresent = manifestAlreadyPresent;
	}

	public void setActivatorFile(File activatorFile) {
		this.activatorFile = activatorFile;
	}

	public void setActivatorPresent(boolean activatorPresent) {
		this.activatorPresent = activatorPresent;
	}

	public void setActivatorClass(String activatorClass) {
		this.activatorClass = activatorClass;
	}

	public void setApiPackages(Set<String> apiPackages) {
		this.apiPackages = apiPackages;
	}

	public void setImports(Set<ImportStatement> imports) {
		this.imports = imports;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void addApiPackage(String pkg) {
		apiPackages.add(pkg);
	}

	public void addImport(String pkg) {
		imports.add(new ImportStatement(pkg, null));
	}
}
