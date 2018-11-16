package com.eriklievaart.ws.workspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import com.eriklievaart.ws.config.ResourcePaths;
import com.eriklievaart.ws.config.dependency.DependencyConfigParser;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.config.dependency.Header;
import com.eriklievaart.ws.config.dependency.LibType;
import com.eriklievaart.ws.repo.Repo;
import com.eriklievaart.ws.toolkit.io.Console;
import com.eriklievaart.ws.toolkit.io.FileTool;

public class ProjectDependencies {

	private final String project;
	private final Repo repo = new Repo();
	private final Map<LibType, Header> index = new TreeMap<>();
	private final File file;

	public ProjectDependencies(String project) {
		this.project = project;
		file = ResourcePaths.getDependencyFile(project);

		if (file.exists()) {
			parseDependencies();

		} else {
			System.out.println("*warning* file not found => " + file);
		}
	}

	private void parseDependencies() {
		for (Header header : DependencyConfigParser.parse(file)) {
			LibType lib = LibType.parse(header.getName());

			if (index.containsKey(lib)) {
				for (DependencyReference dependency : header.getDependencies()) {
					index.get(lib).addDependencyReference(dependency);
				}
			} else {
				index.put(lib, header);
			}
		}
	}

	public void writeback() throws IOException {
		List<String> lines = new ArrayList<>();
		lines.add("");
		for (Header header : index.values()) {
			lines.add("[" + header.getName().toLowerCase() + "]");
			for (DependencyReference dependency : header.getDependencies()) {
				repo.assignDetails(dependency);
				lines.add(dependency.getArtifactId() + " " + dependency.getGroupId() + " " + dependency.getVersion());
			}
			lines.add("");
		}
		if (!file.exists() || !isTheSame(lines, FileTool.readLines(file))) {
			FileTool.writeLines(file, lines);
		}
	}

	private boolean isTheSame(List<String> a, List<String> b) {
		if (a.size() != b.size()) {
			return false;
		}
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i))) {
				return false;
			}
		}
		return true;
	}

	public void resolveAll() {
		for (Header header : index.values()) {
			Set<String> keep = new HashSet<>();

			for (DependencyReference dependency : header.getDependencies()) {
				File jar = getProjectJar(header, dependency);
				repo.resolve(dependency, jar);
				keep.add(jar.getName());
			}
			for (File lib : getLibDir(header).listFiles()) {
				if (!keep.contains(lib.getName())) {
					Console.printWarning("Deleting dependency: " + lib);
					lib.delete();
				}
			}
		}
	}

	private File getProjectJar(Header header, DependencyReference reference) {
		LibType.parse(header.getName()); // header name must be a valid LibType
		String path = reference.getArtifactId() + ".jar";
		return new File(getLibDir(header), path);
	}

	private File getLibDir(Header header) {
		return new File(ResourcePaths.getLibRootDir(project), header.getName());
	}

	public void iterate(BiConsumer<LibType, DependencyReference> consumer) {
		index.forEach((type, header) -> {
			for (DependencyReference reference : header.getDependencies()) {
				consumer.accept(type, reference);
			}
		});
	}

	public File getSourceJar(DependencyReference dependency) {
		return repo.getSourceJar(dependency);
	}
}