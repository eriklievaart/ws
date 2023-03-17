package com.eriklievaart.ws.config.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Header {

	private String name;
	private File location;
	private List<DependencyReference> references = new ArrayList<>();

	public Header(String name) {
		this.name = name;
	}

	public Header(String name, File location) {
		this.name = name;
		this.location = location;
	}

	public File getLocation() {
		return location;
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void addDependencyReference(DependencyReference reference) {
		references.add(reference);
	}

	public List<DependencyReference> getDependencies() {
		ArrayList<DependencyReference> result = new ArrayList<>(references);
		sortAndRemoveDuplicates(result);
		return result;
	}

	private void sortAndRemoveDuplicates(ArrayList<DependencyReference> result) {
		Collections.sort(result);
		DependencyReference last = null;

		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).equals(last)) {
				result.remove(i--);
			}
			last = result.get(i);
		}
	}

	@Override
	public String toString() {
		if (location == null) {
			return "Header[" + name + "]";
		} else {
			return "Header[" + name + "=" + location.getAbsolutePath() + "]";
		}
	}
}
