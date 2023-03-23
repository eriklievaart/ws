package com.eriklievaart.ws.repo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.pom.MavenPomSource;
import com.eriklievaart.ws.repo.pom.Pom;
import com.eriklievaart.ws.repo.pom.PomResolver;
import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.toolkit.io.FileUtils;
import com.eriklievaart.ws.toolkit.io.IORuntimeException;

public class Repo {

	private static Map<String, DependencyReference> index = new Hashtable<>();

	static {
		load();
	}

	public static void showInfo(String search) {
		List<String> keys = new ArrayList<>(index.keySet());
		Collections.sort(keys);

		if (search.equals("@")) {
			for (String key : keys) {
				if (index.get(key).isSnapshot()) {
					System.out.println(key);
				}
			}
		} else {
			for (String key : keys) {
				if (key.contains(search)) {
					System.out.println(index.get(key).getInstallString());
				}
			}
		}
	}

	public static void showDependencyTree(DependencyReference dependency) throws IOException {
		Pom pom = new PomResolver(new MavenPomSource()).loadPom(dependency);
		System.out.println();
		printPom(pom, 0);
	}

	private static void printPom(Pom pom, int depth) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			builder.append("  ");
		}
		System.out.println(builder.append(pom.getSpecification().getShortString()));

		for (Pom child : pom.getDependencies()) {
			printPom(child, depth + 1);
		}
	}

	public void storeSnapshot(File jar) {
		if (!jar.getName().toLowerCase().endsWith(".jar")) {
			throw new RuntimeException("File name should end with jar: " + jar);
		}
		String artifactId = jar.getName().replaceFirst(".[^.]++$", "");
		File source = ResourcePaths.getSourceJar(jar);
		File repoJar = ResourcePaths.getSnapshotFile(artifactId);
		File repoSource = ResourcePaths.getSourceJar(repoJar);

		try {
			FileUtils.copyFile(jar, repoJar);
			FileUtils.copyFile(source, repoSource);
		} catch (IORuntimeException e) {
			throw new IORuntimeException("unable to store snapshot of " + artifactId, e);
		}
		addToIndex(new DependencyReference(artifactId));
	}

	public void resolve(DependencyReference dependency, File destination) {
		destination.getParentFile().mkdirs();
		System.out.println("resolving " + dependency);
		if (!contains(dependency)) {
			install(dependency);
		}
		try {
			File stored = lookup(dependency);
			System.out.println("\tcopying jar " + stored + " => " + destination);
			FileUtils.copyFile(stored, destination);
		} catch (IORuntimeException e) {
			throw new RuntimeException("Unable to copy dependency " + dependency, e);
		}
	}

	private void install(DependencyReference dependency) {
		if (dependency.isLocal() || dependency.getVersion() == null) {
			throw new RuntimeException("Unable to resolve " + dependency);
		}
		Maven.download(dependency, getRepoJarFile(dependency));
		addToIndex(dependency);
	}

	private File getRepoJarFile(DependencyReference dependency) {
		if (dependency.isLocal()) {
			return ResourcePaths.getSnapshotFile(dependency.getArtifactId());
		}
		return new File(ResourcePaths.getRemoteDir(), Maven.getMavenPath(dependency));
	}

	public DependencyReference normalize(DependencyReference dependency) {
		return index.getOrDefault(dependency.getArtifactId(), dependency);
	}

	private File lookup(DependencyReference dependency) {
		DependencyReference reference = index.get(dependency.getArtifactId());
		if (reference == null) {
			throw new RuntimeException("\tDependency not found! " + dependency);
		} else {
			File jar = getRepoJarFile(reference);
			if (!jar.exists()) {
				install(reference);
			}
			return jar;
		}
	}

	public File getSourceJar(DependencyReference dependency) {
		return ResourcePaths.getSourceJar(lookup(dependency));
	}

	public void assignDetails(DependencyReference incomplete) {
		DependencyReference complete = index.get(incomplete.getArtifactId());
		incomplete.setGroupId(complete.getGroupId());
		incomplete.setVersion(complete.getVersion());
	}

	private void addToIndex(DependencyReference dependency) {
		System.out.println("adding to index (or replacing): " + dependency);
		index.put(dependency.getArtifactId(), dependency);
		store();
	}

	private boolean contains(DependencyReference dependency) {
		return index.containsKey(dependency.getArtifactId());
	}

	private static void store() {
		List<DependencyReference> references = new ArrayList<>(index.values());
		Collections.sort(references);

		List<String> lines = new ArrayList<>();
		for (DependencyReference reference : references) {
			lines.add(reference.getArtifactId() + ":" + reference.getGroupId() + ":" + reference.getVersion());
		}
		FileUtils.writeLines(ResourcePaths.getIndexFile(), lines);
	}

	private static void load() {
		index.clear();
		File file = ResourcePaths.getIndexFile();
		if (file.exists()) {
			load(file);
		} else {
			ConsoleUtils.printError("*warning*: repo does not exist: " + file);
		}
	}

	private static void load(File file) {
		try {
			List<String> lines = FileUtils.readLines(file);
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i).trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				DependencyReference dependency = parseDependency(line);
				index.put(dependency.getArtifactId(), dependency);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static DependencyReference parseDependency(String line) {
		String[] split = line.split("\\s*:\\s*");
		if (split.length != 3) {
			throw new RuntimeException("invalid index entry " + line);
		}
		DependencyReference dependency = new DependencyReference(split[0]);
		dependency.setGroupId(split[1]);
		dependency.setVersion(split[2]);
		return dependency;
	}

	public long getTimestamp(DependencyReference dependency) {
		if (!dependency.isSnapshot()) {
			throw new RuntimeException("method only supported for snapshots: " + dependency);
		}
		return lookup(dependency).lastModified();
	}
}