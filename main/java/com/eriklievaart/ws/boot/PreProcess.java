package com.eriklievaart.ws.boot;

import java.io.File;
import java.io.IOException;

import com.eriklievaart.ws.process.JlsModifierSubstitutor;
import com.eriklievaart.ws.toolkit.io.FileTool;

public class PreProcess {

	public static void main(String[] args) throws IOException {
		if (args == null || args.length == 0) {
			throw new IOException("At least one directory should be passed as a command line argument.");
		}

		for (String arg : args) {
			File root = new File(arg);
			if (!root.isDirectory()) {
				throw new IOException("not a directory: " + root);
			}
			processRoot(root);
		}
	}

	private static void processRoot(File root) throws IOException {
		for (File file : FileTool.listFiles(root)) {
			JlsModifierSubstitutor sub = new JlsModifierSubstitutor(file.getAbsolutePath(), FileTool.readLines(file));
			if (sub.hasJlsViolations()) {
				FileTool.writeLines(file, sub.getLines());
			}
		}
	}
}
