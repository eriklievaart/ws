package com.eriklievaart.ws.process.index;

import com.eriklievaart.toolkit.ant.api.AntProperties;
import com.eriklievaart.toolkit.lang.api.check.Check;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TypeIndexU {

	@Test
	public void scanDirectory() throws IOException {
		File directory = AntProperties.getJavaSourceDir().getDirectoryOrHomeDir("Development/git/ws/main/java");

		TypeIndex testable = new TypeIndex();
		testable.scanDirectory(directory);
		String lookup = testable.lookup("TypeIndex");
		Check.isEqual(lookup, "com.eriklievaart.ws.process.index.TypeIndex");
	}
}
