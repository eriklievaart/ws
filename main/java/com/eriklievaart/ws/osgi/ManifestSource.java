package com.eriklievaart.ws.osgi;

import java.io.File;
import java.util.Set;

public interface ManifestSource {

	public File getManifestFile();

	public boolean isManifestAlreadyPresent();

	public File getActivatorFile();

	public boolean isActivatorPresent();

	public String getActivatorClass();

	public Set<String> getApiPackages();

	public Set<ImportStatement> getImports();

	public String getBasePackage();
}
