package com.eriklievaart.ws.process.whitespace;

import java.io.File;
import java.util.List;

import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.toolkit.io.FileUtils;

public class WhitespaceProcessor {

	private File file;
	private File root;

	public WhitespaceProcessor(File root, File file) {
		this.root = root;
		this.file = file;
	}

	public void removeTrailingWitespace() {
		List<String> lines = FileUtils.readLines(file);
		if (fixWhitespace(lines)) {
			FileUtils.writeLines(file, lines);
		}
	}

	private boolean fixWhitespace(List<String> lines) {
		boolean changed = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String fixed = line.replaceFirst("\\s++$", "");

			if (line.length() != fixed.length()) {
				changed = true;
				lines.add(i, fixed);
				lines.remove(i + 1);
				ConsoleUtils.printWarning("removed trailing whitespace " + getPath() + "(" + i + "): " + fixed.trim());
			}
		}
		return changed;
	}

	private String getPath() {
		return "." + file.getAbsolutePath().substring(root.getAbsolutePath().length());
	}
}
