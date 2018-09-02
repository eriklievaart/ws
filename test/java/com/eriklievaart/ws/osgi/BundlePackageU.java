package com.eriklievaart.ws.osgi;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BundlePackageU {

	@Test
	public void extractPackage() throws Exception {
		String pkg = BundlePackage.extractPackage("import org.junit.Test;");
		Assertions.assertThat(pkg).isEqualTo("org.junit");
	}

	@Test
	public void extractPackageWhitespace() throws Exception {
		String pkg = BundlePackage.extractPackage("import org.junit.Test ;	");
		Assertions.assertThat(pkg).isEqualTo("org.junit");
	}
}
