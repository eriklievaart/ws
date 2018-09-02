package com.eriklievaart.ws.config.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Header {

	private String name;
	private File location;
	private List<DependencyReference> references = new ArrayList<DependencyReference>();

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
		ArrayList<DependencyReference> result = new ArrayList<DependencyReference>(references);
		Collections.sort(result);
		return result;
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
