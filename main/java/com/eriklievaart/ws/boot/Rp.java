package com.eriklievaart.ws.boot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.repo.Repo;
import com.eriklievaart.ws.workspace.ProjectDependencies;

public class Rp {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			StringBuilder builder = new StringBuilder();
			builder.append("usage:\n");
			builder.append("\t repo @                        list snapshots \n");
			builder.append("\t repo [project]                list project dependencies \n");
			builder.append("\t repo -i [project] [query]...  install dependencies \n");
			builder.append("\t repo -t [dependency]          show maven dependency tree\n");
			builder.append("\t repo /[query]...              search repo \n");
			System.err.println(builder);
			return;
		}
		selectFlag(args[0], args);
	}

	private static void selectFlag(String flag, String[] args) throws IOException {
		if (flag.startsWith("/")) {
			findDependencies(args);

		} else if (flag.equals("-i")) {
			addDependencies(tail(args));

		} else if (flag.equals("-t")) {
			showDependencyTree(tail(args));

		} else {
			listProject(args);
		}
	}

	private static List<String> tail(String[] args) {
		List<String> tail = new ArrayList<>();
		for (int i = 1; i < args.length; i++) {
			tail.add(args[i]);
		}
		return tail;
	}

	private static void listProject(String[] args) {
		if (args.length == 0) {
			System.out.println("*error*: missing argument [project]");
			return;
		}
		for (String arg : args) {
			new ProjectDependencies(arg).dump();
		}
	}

	private static void findDependencies(String[] args) {
		for (String arg : args) {
			Repo.showInfo(arg.replaceFirst("^/", ""));
		}
	}

	private static void showDependencyTree(List<String> args) throws IOException {
		if (args.isEmpty()) {
			System.err.println("*error*: missing argument [dependency]!");
			return;
		}
		DependencyReference dependency = DependencyReference.of(args.remove(0));
		System.out.println("looking up dependency tree for " + dependency);
		if (dependency.isLocal()) {
			System.err.println(dependency + " does not have a dependency tree, because it is local!");
			return;

		} else {
			System.out.println();
			Repo.showDependencyTree(dependency);
		}
	}

	private static void addDependencies(List<String> dependencies) throws IOException {
		new ProjectDependencies(dependencies.remove(0)).addDependencies(dependencies);
	}
}
