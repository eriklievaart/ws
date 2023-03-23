package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import com.eriklievaart.ws.config.dependency.DependencyReference;

public interface PomSource {

	public String get(DependencyReference dependency) throws IOException;
}
