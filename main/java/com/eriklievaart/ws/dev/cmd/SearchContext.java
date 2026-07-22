package com.eriklievaart.ws.dev.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SearchContext {

	private List<Predicate<File>> predicates = new ArrayList<>();
	private Set<String> results = new HashSet<>();

	public void addPredicate(Predicate<File> predicate) {
		predicates.add(predicate);
	}

	public boolean traverse(File file) {
		if (file.isFile()) {
			return false;
		}
		String name = file.getName();
		if (name.equals(".git") || name.equals("test")) {
			return false;
		}
		return true;
	}

	public boolean test(File file) {
		for (Predicate predicate: predicates) {
			if (!predicate.test(file)) {
				return false;
			}
		}
		return true;
	}

	public void addResult(String value) {
		results.add(value);
	}

	public List<String> getResults() {
		List<String> clone = new ArrayList<>(results);
		Collections.sort(clone);
		return clone;
	}
}
