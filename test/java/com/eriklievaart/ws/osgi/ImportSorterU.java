package com.eriklievaart.ws.osgi;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ImportSorterU {

	@Test
	public void sortImports() throws Exception {
		List<String> imports = new ArrayList<>();
		imports.add("java.util");
		imports.add("javax.swing.event");
		imports.add("com.eriklievaart.toolkit");
		imports.add("java.lang");
		imports.add("javax.swing");
		imports.add("org.osgi.framework");

		List<String> sorted = new PackageSorter().sortPackages(imports);
		Assertions.assertThat(sorted).containsExactly("org.osgi.framework", "java.lang", "java.util", "javax.swing",
				"javax.swing.event", "com.eriklievaart.toolkit");
	}
}
