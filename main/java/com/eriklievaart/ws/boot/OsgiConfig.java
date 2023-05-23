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

	private static void addMissingProperties(File root, OsgiProperties osgi) throws IOException {
		String project = root.getName();

		osgi.ensureKeyExists("felix.cache.rootdir", "/tmp/felix/" + project);

		Set<String> bundles = getBundleList(root);
		addToolkitProperties(root, bundles, osgi);
		addJavalightningProperties(project, bundles, osgi);
	}

	private static void addToolkitProperties(File root, Set<String> bundles, OsgiProperties osgi) throws IOException {
		if (bundles.contains("toolkit-logging")) {
			new File(root, "logging.ini").createNewFile();
			if (osgi.missingKey("com.eriklievaart.toolkit.logging.config.file")) {
				osgi.ensureKeyExists("com.eriklievaart.toolkit.logging.config.dir", root.getAbsolutePath());
			}
		}
	}

	private static void addJavalightningProperties(String project, Set<String> bundles, OsgiProperties osgi) {
		String home = System.getProperty("user.home");
		if (bundles.contains("jl-core")) {
			osgi.ensureKeyExists("org.apache.felix.http.whiteboardEnabled", "true");
			osgi.ensureKeyExists("org.osgi.service.http.port", "8000");
		}
		if (bundles.contains("jl-dev")) {
			osgi.ensureKeyExists("com.eriklievaart.jl.dev", "true");
		}
		if (bundles.contains("jl-freemarker")) {
			String resources = home + "/Development/git/" + project + "/main/resources";
			osgi.ensureKeyExists("com.eriklievaart.jl.freemarker.path", resources);
			osgi.ensureKeyExists("com.eriklievaart.jl.freemarker.timeout", "0");
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
