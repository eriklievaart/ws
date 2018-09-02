package com.eriklievaart.ws.repo;

import java.io.File;
import java.util.List;

import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.config.dependency.Header;

public class DependencyResolver {

	public static void resolve(List<Header> headers) {
		Repo repo = new Repo();

		for (Header header : headers) {
			for (DependencyReference dependency : header.getDependencies()) {
				repo.resolve(dependency, new File(header.getLocation(), dependency.getArtifactId() + ".jar"));
			}
		}
	}
}
