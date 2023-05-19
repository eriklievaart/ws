package com.eriklievaart.ws.boot;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.eriklievaart.ws.config.osgi.OsgiProperties;

public class OsgiConfig {

	public static void main(String[] args) throws IOException {
		if (args == null || args.length != 1) {
			throw new IOException("Exactly one directory should be passed as a command line argument: the deploy dir");
		}
		File root = new File(args[0]);
		File config = new File(root, "osgi.properties");
		OsgiProperties properties = new OsgiProperties();
		properties.load(config);
		addMissingProperties(root, properties);
		properties.store(config);
	}

	private static void addMissingProperties(File root, OsgiProperties properties) throws IOException {
		String project = root.getName();
		String home = System.getProperty("user.home");

		properties.ensureKeyExists("felix.cache.rootdir", "/tmp/felix/" + project);

		Set<String> bundles = getBundleList(root);
		if (bundles.contains("toolkit.logging")) {
			File logConfig = new File(root, "logging.ini");
			logConfig.createNewFile();
			properties.ensureKeyExists("com.eriklievaart.toolkit.logging.config.file", logConfig.getAbsolutePath());
		}
		if (bundles.contains("jl-core")) {
			properties.ensureKeyExists("org.apache.felix.http.whiteboardEnabled", "true");
			properties.ensureKeyExists("org.osgi.service.http.port", "8000");
		}
		if (bundles.contains("jl-freemarker")) {
			String resources = home + "/Development/git/" + project + "/main/resources";
			properties.ensureKeyExists("com.eriklievaart.jl.freemarker.path", resources);
			properties.ensureKeyExists("com.eriklievaart.jl.freemarker.timeout", "0");
		}
	}

	private static Set<String> getBundleList(File root) {
		Set<String> bundles = new HashSet<>();
		for (String bundle : new File(root, "bundle").list()) {
			if (bundle.endsWith(".jar")) {
				bundles.add(bundle.replaceFirst(".jar$", ""));
			}
		}
		return bundles;
	}
}
