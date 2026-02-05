package com.eriklievaart.ws.process.index;

import com.eriklievaart.toolkit.lang.api.check.Check;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ReferenceTokenizerU {

	@Test
	public void stripStrings() {
		String noStrings = "List list = Collections.unmodifiableList(input)";
		Check.isEqual(ReferenceTokenizer.stripStrings(noStrings), noStrings);

		String hasString = "System.out.println(\"Collections\");";
		Check.isEqual(ReferenceTokenizer.stripStrings(hasString), "System.out.println();");
	}

	@Test
	public void stripMultiline() {
		ReferenceTokenizer tokenizer = new ReferenceTokenizer();
		tokenizer.append("public static void main(String[] args) {");
		tokenizer.append("	/*");
		tokenizer.append("	This Is A Comment");
		tokenizer.append("	*/");
		tokenizer.append("	System.exit(0);");
		tokenizer.append("}");

		Assertions.assertThat(tokenizer.listTokens()).containsExactly("String", "System");
	}

	@Test
	public void stripMultilineOnSingleLine() {
		ReferenceTokenizer tokenizer = new ReferenceTokenizer();
		tokenizer.append("	/** This Is The Main Method */");
		tokenizer.append("	public static void main(String[] args) {");
		tokenizer.append("	}");

		Assertions.assertThat(tokenizer.listTokens()).containsExactly("String");
	}
}
