package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;
import com.eriklievaart.ws.workspace.Project;
import com.eriklievaart.ws.workspace.Workspaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepoCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		if (!cmd.hasArguments()) {
			printRepos(listAllRepos());
			return;
		}
		String query = cmd.getSingleArgument();
		List<String> repos = filterRepos(query);
		if (repos.isEmpty()) {
			System.out.println("repo not found: '" + query + "'\n");
			return;

		} else if (repos.size() != 1) {
			printRepos(repos);
			return;
		}
		context.project = new Project(repos.get(0));
		context.directory = context.project.getGitDir();
	}

	private List<String> filterRepos(String filter) {
		return listAllRepos().stream().filter(s -> s.contains(filter)).collect(Collectors.toList());
	}

	private List<String> printRepos(List<String> repos) {
		Collections.sort(repos);
		for (int i = 0; i<10 && i < repos.size(); i++) {
			System.out.println(i + ":" + repos.get(i));
		}
		if (repos.size() > 10) {
			System.out.println("...");
		}
		System.out.println("");
		return repos;
	}

	private List<String> listAllRepos() {
		List<String> repos = new ArrayList<>();
		Workspaces.iterateProjects(p -> repos.add(p));
		return repos;
	}
}
