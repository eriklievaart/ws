package com.eriklievaart.ws.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PackageSorter {

	private List<String> importOrder = new ArrayList<>();

	{
		importOrder.add("org.osgi.");
		importOrder.add("java.");
		importOrder.add("javax.");
		importOrder.add("com.eriklievaart.");
	}

	public List<String> sortPackages(Collection<String> imports) {
		List<String> result = new ArrayList<>(imports);
		Collections.sort(result, this::compare);
		return result;
	}

	private int compare(String a, String b) {
		int groupDiff = getIndex(a) - getIndex(b);
		return groupDiff == 0 ? a.compareTo(b) : groupDiff;
	}

	private int getIndex(String statement) {
		for (int i = 0; i < importOrder.size(); i++) {
			if (statement.startsWith(importOrder.get(i))) {
				return i;
			}
		}
		return importOrder.size() + 1;
	}

}
