package com.eriklievaart.ws.repo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.toolkit.io.StreamTool;
import com.eriklievaart.ws.toolkit.io.UrlTool;

public class Maven {

	private static final List<String> MIRRORS = new ArrayList<>();

	static {
		MIRRORS.add("https://repo1.maven.org/maven2");
		MIRRORS.add("https://mirrors.ibiblio.org/pub/mirrors/maven2");
		MIRRORS.add("https://maven.antelink.com/content/repositories/central/");
		MIRRORS.add("https://maven2.mirrors.skynet.be/pub/maven2/");
		MIRRORS.add("https://repo.maven.apache.org/maven2");
		MIRRORS.add("http://repository.springsource.com/maven/bundles/release");
	}

	public static void download(DependencyReference dependency, File destination) {
		String url = UrlTool.append(MIRRORS.get(0), getMavenPath(dependency));
		try {
			httpGet(url, destination);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			if (url.endsWith(".jar")) {
				httpGet(url.replaceFirst("\\.jar$", "-sources.jar"), ResourcePaths.getSourceJar(destination));
			}
		} catch (IOException e) {
			// it still works fine without sources
		}
	}

	private static void httpGet(String url, File destination) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();

		System.out.println("GET URL : " + url + " status " + responseCode);
		destination.getParentFile().mkdirs();
		try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(destination)) {
			StreamTool.copyStream(is, fos);
		}
		if (destination.length() == 0) {
			destination.delete();
		}
	}

	public static String getMavenPath(DependencyReference dependency) {
		String group = dependency.getGroupId().replace('.', '/');
		String jar = dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar";
		return UrlTool.append(group, dependency.getArtifactId(), dependency.getVersion(), jar);
	}

}
