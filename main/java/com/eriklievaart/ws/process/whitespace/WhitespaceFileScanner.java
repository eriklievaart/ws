package com.eriklievaart.ws.process.whitespace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.eriklievaart.ws.toolkit.io.SetUtils;

public class WhitespaceFileScanner {
	private Set<String> extensions = SetUtils.of("tpl", "ftl", "ftlh", "properties", "txt", "css", "sh", "java");
	private Set<String> skip = SetUtils.of(".git", "zip");

	public void removeTrailingWhitespace(File root) {
		List<File> examine = findFilesToExamine(root);
		for (File file : examine) {
			new WhitespaceProcessor(root, file).removeTrailingWitespace();
		}
	}

	private List<File> findFilesToExamine(File file) {
		List<File> result = new ArrayList<>();

		if (file.isFile() && extensions.contains(file.getName().replaceFirst(".*[.]", ""))) {
			return Arrays.asList(file);
		}
		if (!file.isDirectory() || skip.contains(file.getName())) {
			return result;
		}
		File[] children = file.listFiles();
		if (children == null) {
			return result;
		}
		for (File child : children) {
			result.addAll(findFilesToExamine(child));
		}
		return result;
	}
}