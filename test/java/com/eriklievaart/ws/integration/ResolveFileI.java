package com.eriklievaart.ws.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.ws.config.dependency.DependencyConfigParser;
import com.eriklievaart.ws.repo.DependencyResolver;
import com.eriklievaart.ws.toolkit.io.FileTool;

public class ResolveFileI {

	private File root;
	private File lib;
	private File dependencies;

	@Before
	public void setup() {
		root = new File("/tmp/sandbox/" + System.currentTimeMillis());
		dependencies = new File(root, "dependencies.txt");
		lib = new File(root, "lib");
		lib.mkdirs();
	}

	@Test
	public void resolveFile() throws IOException {
		String header = "[root=" + lib.getAbsolutePath() + "]";
		String resolve = "junit junit 4.7";
		FileTool.writeLines(dependencies, Arrays.asList(header, resolve));

		DependencyResolver.resolve(DependencyConfigParser.parse(dependencies));

		File downloaded = new File(lib, "junit.jar");
		Assertions.assertThat(downloaded).exists();

		downloaded.delete();
		dependencies.delete();
		lib.delete();
		root.delete();
	}

	@Test
	public void resolveFile2() throws IOException {
		String headerA = "[root=" + lib.getAbsolutePath() + "/a]";
		String resolveA = "junit junit 4.7";
		String headerB = "[root=" + lib.getAbsolutePath() + "/b]";
		String resolveB = "junit junit 4.7";
		FileTool.writeLines(dependencies, Arrays.asList(headerA, resolveA, headerB, resolveB));

		DependencyResolver.resolve(DependencyConfigParser.parse(dependencies));

		File a = new File(lib, "a/junit.jar");
		File b = new File(lib, "b/junit.jar");

		Assertions.assertThat(a).exists();
		Assertions.assertThat(b).exists();

		a.delete();
		b.delete();
	}
}
