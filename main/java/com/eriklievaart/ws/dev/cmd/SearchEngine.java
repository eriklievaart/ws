package com.eriklievaart.ws.dev.cmd;

import java.io.File;

public class SearchEngine {

	private File root;

	public SearchEngine(File root) {
		this.root = root;
	}

	public void query(SearchContext context) {
		query(root, context);
	}

	public void query(File file, SearchContext context) {
		if (context.traverse(file)) {
			for(File child: file.listFiles()) {
				query(child, context);
			}
		}
		if(!file.equals(root) && context.test(file)) {
			context.addResult(file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1));
		}
	}
}
