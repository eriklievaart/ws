package com.eriklievaart.ws.process.index;

import java.io.File;
import java.util.Set;

public class ProjectTypeIndex {

	private int skipPath;
	private int skipExtension = 5;

	private Set<String> lang;
	private LibraryIndex index = new LibraryIndex();

	public ProjectTypeIndex(File root, Set<String> lang) {
		this.lang = lang;
		this.skipPath = root.getPath().length() + 1;
		scan(root);
	}

	public void scan(File file) {

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				scan(child);
			}

		} else {
			String name = file.getName();
			if (name.endsWith(".java")) {
				validateNotJavaLang(name.replaceFirst("[.]java$", ""));
				String full = file.getPath();
				String path = full.substring(skipPath, full.length() - skipExtension);
				index.add(path.replace('/', '.'));
			}
		}
	}

	public void validateNotJavaLang(String type) {
		if (lang.contains(type) && !type.equals("Logger")) {
			throw new Error("project should not contain class names conflicting with java.lang: " + type);
		}
	}

	public LibraryIndex getProjectIndex() {
		return index;
	}
}
