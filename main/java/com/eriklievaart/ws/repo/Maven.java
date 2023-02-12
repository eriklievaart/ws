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
import com.eriklievaart.ws.toolkit.io.IORuntimeException;
import com.eriklievaart.ws.toolkit.io.StreamUtils;
import com.eriklievaart.ws.toolkit.io.UrlUtils;

public class Maven {

	private static final List<String> MIRRORS = new ArrayList<>();

	static {
		MIRRORS.add("https://repo1.maven.org/maven2");
		MIRRORS.add("https://mvnrepository.com/artifact");
		MIRRORS.add("https://maven2.mirrors.skynet.be/pub/maven2/");
		MIRRORS.add("https://repo.maven.apache.org/maven2");
		MIRRORS.add("http://repository.springsource.com/maven/bundles/release");
	}

	public static void download(DependencyReference dependency, File destination) {
		downloadBinary(dependency, destination);
		downloadSources(dependency, destination);
	}

	private static void downloadBinary(DependencyReference dependency, File destination) {
		for (String mirror : MIRRORS) {
			String url = UrlUtils.append(mirror, getMavenPath(dependency));
			try {
				httpGet(url, destination);
				return;
			} catch (IOException e) {
				continue;
			}
		}
		throw new IORuntimeException("Unable to download " + dependency);
	}

	private static void downloadSources(DependencyReference dependency, File destination) {
		File sourceJar = ResourcePaths.getSourceJar(destination);
		if (!sourceJar.exists()) {
			for (String mirror : MIRRORS) {
				String url = UrlUtils.append(mirror, getMavenPath(dependency).replaceFirst("\\.jar$", "-sources.jar"));
				try {
					if (url.endsWith(".jar")) {
						httpGet(url, sourceJar);
					}
					return;
				} catch (IOException e) {
					continue; // can still build without sources
				}
			}
		}
	}

	private static void httpGet(String url, File destination) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(5000);
		int responseCode = connection.getResponseCode();

		System.out.println("GET URL : " + url + " status " + responseCode);
		destination.getParentFile().mkdirs();
		try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(destination)) {
			StreamUtils.copyStream(is, fos);
		}
		if (destination.length() == 0) {
			destination.delete();
		}
	}

	public static String getMavenPath(DependencyReference dependency) {
		String group = dependency.getGroupId().replace('.', '/');
		String jar = dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar";
		return UrlUtils.append(group, dependency.getArtifactId(), dependency.getVersion(), jar);
	}
}
