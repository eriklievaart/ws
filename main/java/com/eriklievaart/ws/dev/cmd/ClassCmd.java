package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevCliException;
import com.eriklievaart.ws.dev.DevContext;
import com.eriklievaart.ws.dev.Terminal;
import com.eriklievaart.ws.toolkit.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClassCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		if (cmd.hasFlags()) {
			processFlag(cmd, context);
		} else {
			selectClass(cmd, context);
		}
	}

	private void processFlag(DevCliCommand cmd, DevContext context) {
		CmdFlag flag = cmd.getSingleFlag();

		switch (flag.getFlag()) {
			case "-c":
				createClass(flag, context);
				break;

			case "-d":
				dump(flag, context);
				break;

			default:
				Terminal.printInRed("unknown flag: " + cmd.getType().getLabel() + " " + flag.getFlag() + "\n");
		}
	}

	private void createClass(CmdFlag flag, DevContext context) {
		checkIsJavaDirectory(context.directory);
		String clz = flag.getSingleArgument();
		String name = clz.replaceFirst("[.].*", "") + ".java";
		File file = new File(context.directory, name);
		try {
			file.createNewFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void checkIsJavaDirectory(File file) {
		String path = file.getAbsolutePath();
		DevCliException.unless(path.contains("/java/") || path.endsWith("/java"), "invalid directory: " + path);
	}

	private void dump(CmdFlag flag, DevContext context) {
		int count = flag.checkArgumentCount(0, 1);
		if (count == 1) {
			boolean classFound = selectClass(flag.getSingleArgument(), context);
			if (!classFound) {
				return;
			}
		}
		File file = context.clz;
		DevCliException.on(file == null || !file.isFile(), "no class selected!");
		System.out.println(FileUtils.toString(file) + "\n");
	}

	private void selectClass(DevCliCommand cmd, DevContext context) {
		if (!cmd.hasArguments()) {
			printClasses(findAllClasses(context.directory));
			return;
		}
		selectClass(cmd.getSingleArgument(), context);
	}

	private boolean selectClass(String query, DevContext context) {
		List<String> matches = findClassesWithNameContains(context.directory, query);
		if (matches.size() == 0) {
			context.clz = null;
			System.out.println("class not found: '" + query + "'");
			System.out.println();
			return false;
		}
		if (matches.size() != 1) {
			printClasses(matches);
			return false;
		}
		String path = matches.get(0);
		context.clz = new File(context.directory, path);
		context.directory = context.clz.getParentFile();
		return true;
	}

	private List<String> findClassesWithNameContains(File root, String pattern) {
		SearchContext query = new SearchContext();
		query.addPredicate(file -> file.isFile());
		query.addPredicate(file -> file.getName().contains(pattern));
		new SearchEngine(root).query(query);
		return query.getResults();
	}

	private List<String> findAllClasses(File root) {
		SearchContext context = new SearchContext();
		context.addPredicate(file -> file.isFile());
		context.addPredicate(file -> file.getName().toLowerCase().endsWith(".java"));
		new SearchEngine(root).query(context);
		return context.getResults();
	}

	private void printClasses(List<String> list) {
		for (String name: list) {
			System.out.println(name.replaceFirst(".*/java/", "").replaceAll("[/\\\\]", "."));
		}
		System.out.println();
	}
}
