package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;

import java.util.List;

public class SearchCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		SearchEngine engine = new SearchEngine(context.project.getGitDir());

		if (cmd.hasArguments()) {
			SearchContext query = createSearchContext(cmd);
			engine.query(query);
			printPaths(query.getResults());
		} else {
			System.out.println("no query specified");
		}
	}

	public SearchContext createSearchContext(DevCliCommand cmd) {
		SearchContext query = new SearchContext();
		query.addPredicate(file -> file.getName().contains(cmd.getSingleArgument()));
		return query;
	}

	public void printPaths(List<String> paths) {
		for (String path: paths) {
			System.out.println(path);
		}
		System.out.println();
	}
}
