package com.eriklievaart.ws.process.index;

import com.eriklievaart.toolkit.ant.api.AntProperties;
import com.eriklievaart.toolkit.lang.api.check.Check;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TypeIndexU {

	@Test
	public void scanMainDirectory() throws IOException {
		File directory = AntProperties.getJavaSourceDir().getDirectoryOrHomeDir("Development/git/ws/main/java");

		TypeIndex testable = new TypeIndex();
		testable.scanDirectory(directory);
		Check.isEqual(testable.lookupInMain("TypeIndex"), "com.eriklievaart.ws.process.index.TypeIndex");
	}

	@Test
	public void scanTestDirectory() throws IOException {
		File directory = AntProperties.getTestJavaSourceDir().getDirectoryOrHomeDir("Development/git/ws/test/java");

		TypeIndex testable = new TypeIndex();
		testable.scanDirectory(directory);
		Check.isNull(testable.lookupInMain("TypeIndexU"));
		Check.isEqual(testable.lookupInMainOrTest("TypeIndexU"), "com.eriklievaart.ws.process.index.TypeIndexU");
	}
}
