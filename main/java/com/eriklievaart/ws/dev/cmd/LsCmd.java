package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;
import com.eriklievaart.ws.dev.Terminal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class LsCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		File dir = context.directory;
		printDirectories(dir);
		printFiles(dir);
	}

	private void printDirectories(File parent) {
		for (String name: createChildList(parent, file -> file.isDirectory())) {
			Terminal.printInBlue(name);
		}
	}

	private void printFiles(File parent) {
		for (String name: createChildList(parent, file -> file.isFile())) {
			System.out.println(name);
		}
		System.out.println();
	}

	private List<String> createChildList(File parent, Predicate<File> predicate) {
		List<String> names = new ArrayList<>();
		for (File file: parent.listFiles()) {
			if (predicate.test(file)) {
				names.add(file.getName());
			}
		}
		Collections.sort(names);
		return names;
	}
}
