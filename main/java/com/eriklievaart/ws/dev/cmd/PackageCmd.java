package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;

import java.io.File;
import java.util.List;

public class PackageCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		File root = context.project.getSourceJavaDir();
		if (!cmd.hasArguments()) {
			printPackages(findAllDirectories(root));
			return;
		}
		String query = cmd.getSingleArgument();
		List<String> matches = findPackagesWithNameContains(root, query);
		if (matches.size() == 0) {
			System.out.println("package not found: '" + query + "'");
			System.out.println();
			return;
		}
		if (matches.size() == 1) {
			String path = matches.get(0);
			context.directory = new File(root, path);
		}
		printPackages(matches);
	}

	private List<String> findPackagesWithNameContains(File root, String pattern) {
		SearchContext query = new SearchContext();
		query.addPredicate(file -> file.isDirectory());
		query.addPredicate(file -> file.getName().contains(pattern));
		new SearchEngine(root).query(query);
		return query.getResults();
	}

	private List<String> findAllDirectories(File root) {
		SearchContext context = new SearchContext();
		context.addPredicate(file -> file.isDirectory());
		new SearchEngine(root).query(context);
		return context.getResults();
	}

	private void printPackages(List<String> list) {
		for (String pkg: list) {
			System.out.println(pkg.replace('/', '.').replace('\\', '.'));
		}
		System.out.println();
	}
}
