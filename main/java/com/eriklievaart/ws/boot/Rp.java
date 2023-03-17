package com.eriklievaart.ws.boot;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.Repo;
import com.eriklievaart.ws.workspace.ProjectDependencies;

public class Rp {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			StringBuilder builder = new StringBuilder();
			builder.append("usage:\n");
			builder.append("\t repo @                        lists available snapshots \n");
			builder.append("\t repo -i [project] [query]...  install dependencies \n");
			System.err.println(builder);
			return;
		}
		if (args[0].equals("-i")) {
			addDependencies(args);
			return;
		}
		if (args[0].equals("-t")) {
			showDependencyTree(args);
			return;
		}
		findDependencies(args);
	}

	private static void findDependencies(String[] args) {
		for (String arg : args) {
			Repo.showInfo(arg.replaceFirst("^/", ""));
		}
	}

	private static void showDependencyTree(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("*error*: missing argument dependency!");
			return;
		}
		DependencyReference dependency = DependencyReference.of(args[1]);
		System.out.println("looking up dependency tree for " + dependency);
		if (dependency.isLocal()) {
			System.err.println(dependency + " does not have a dependency tree, because it is local!");
			return;

		} else {
			System.out.println();
			Repo.showDependencyTree(dependency);
		}
	}

	private static void addDependencies(String[] args) throws IOException {
		ProjectDependencies project = new ProjectDependencies(args[1]);
		List<String> dependencies = Arrays.asList(args);
		project.addDependencies(dependencies.subList(2, dependencies.size()));
	}
}
