package com.eriklievaart.ws.process.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eriklievaart.ws.toolkit.io.IORuntimeException;
import com.eriklievaart.ws.toolkit.io.StreamUtils;
import com.eriklievaart.ws.toolkit.io.ZipUtils;

public class TypeIndex {

	protected Set<String> lang = new HashSet<>();
	protected List<LibraryIndex> indexes = new ArrayList<>();

	public TypeIndex() {
		loadJavaLang();
	}

	public void loadJavaLang() {
		try {
			for (String type : StreamUtils.readLines(getClass().getResourceAsStream("/java.lang.txt"))) {
				if (!type.isBlank()) {
					lang.add(type);
				}
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public void loadCorePackages() {
		LibraryIndex library = new LibraryIndex();
		try {
			for (String type : StreamUtils.readLines(getClass().getResourceAsStream("/java-imports.txt"))) {
				if (!type.isBlank()) {
					library.add(type);
				}
			}
			indexes.add(library);

		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public boolean isJavaLang(String type) {
		return lang.contains(type);
	}

	public String lookupInMain(String type) {
		for (LibraryIndex library : indexes) {
			if (library.isMain() && library.contains(type)) {
				return library.get(type);
			}
		}
		return null;
	}

	public String lookupInMainOrTest(String type) {
		for (LibraryIndex library : indexes) {
			if (library.contains(type)) {
				return library.get(type);
			}
		}
		return null;
	}

	public void scanDirectory(File root) {
		LibraryIndex index = new ProjectTypeIndex(root, lang).getProjectIndex();
		if (root.getAbsolutePath().contains("/test/")) {
			index.setTest(true);
		}
		indexes.add(index);
	}

	public void scanMainJar(File jar) {
		LibraryIndex index = scanJar(jar);
		index.setTest(true);
		indexes.add(index);
	}

	public void scanTestJar(File jar) {
		LibraryIndex index = scanJar(jar);
		indexes.add(index);
	}

	private LibraryIndex scanJar(File jar) {
		LibraryIndex index = new LibraryIndex();
		for (String path : ZipUtils.listPathsInJar(jar)) {
			index.add(path.replaceFirst(".java$", ""));
		}
		return index;
	}
}
