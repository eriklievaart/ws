package com.eriklievaart.ws.boot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.eriklievaart.ws.process.ChainedLineProcessor;
import com.eriklievaart.ws.process.EmptyLineProcessor;
import com.eriklievaart.ws.process.JlsModifierProcessor;
import com.eriklievaart.ws.process.whitespace.WhitespaceFileScanner;
import com.eriklievaart.ws.toolkit.io.FileUtils;

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
			System.out.println("running preprocess in " + root);
			processRoot(root);
			new WhitespaceFileScanner().removeTrailingWhitespace(root);
		}
	}

	private static void processRoot(File root) throws IOException {
		for (File file : FileUtils.listJavaFiles(root)) {
			List<String> lines = FileUtils.readLines(file);
			JlsModifierProcessor jls = new JlsModifierProcessor(file.getAbsolutePath());
			EmptyLineProcessor empty = new EmptyLineProcessor();
			ChainedLineProcessor processor = new ChainedLineProcessor(jls, empty);
			if (processor.modify(lines)) {
				FileUtils.writeLines(file, lines);
			}
		}
	}
}
