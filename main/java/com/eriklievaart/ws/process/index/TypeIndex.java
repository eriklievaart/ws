package com.eriklievaart.ws.process.index;

import com.eriklievaart.ws.toolkit.io.IORuntimeException;
import com.eriklievaart.ws.toolkit.io.StreamUtils;
import com.eriklievaart.ws.toolkit.io.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public String lookup(String type) {
		for (LibraryIndex library : indexes) {
			if (library.contains(type)) {
				return library.get(type);
			}
		}
		return null;
	}

	public void scanDirectory(File root) {
		indexes.add(new ProjectTypeIndex(root, lang).getProjectIndex());
	}

	public void scanJar(File jar) {
		LibraryIndex index = new LibraryIndex();
		for (String path: ZipUtils.listPathsInJar(jar)) {
			index.add(path);
		}
		indexes.add(index);
	}
}
