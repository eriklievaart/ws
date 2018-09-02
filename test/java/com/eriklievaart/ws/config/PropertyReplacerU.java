package com.eriklievaart.ws.config;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PropertyReplacerU {

	@Test
	public void replaceHome(){
		String replaced = new PropertyReplacer().apply("@home@");
		Assertions.assertThat(replaced).doesNotContain("@");
	}

	@Test
	public void replaceMiss(){
		String replaced = new PropertyReplacer().apply("@foo@");
		Assertions.assertThat(replaced).isEqualTo("@foo@");
	}

	@Test
	public void replaceCustom(){
		PropertyReplacer replacer = new PropertyReplacer();
		replacer.replace("@bar@", "foo");

		String replaced = replacer.apply("@bar@");
		Assertions.assertThat(replaced).isEqualTo("foo");
	}
}
