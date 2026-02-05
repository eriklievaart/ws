package com.eriklievaart.ws.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.ws.process.index.MockTypeIndex;
import com.eriklievaart.ws.process.index.TypeIndex;
import com.eriklievaart.ws.toolkit.io.StreamUtils;

public class ImportsProcessorU {

	@Test
	public void modifyValid() throws IOException {
		ImportsProcessor testable = createImportsProcessor();
		boolean modified = testable.modify(StreamUtils.readLines(getClass().getResourceAsStream("/imports/Ignore")));
		Check.isFalse(modified);
	}

	@Test
	public void modifyAddMissingProjectImport() throws IOException {
		List<String> lines = StreamUtils.readLines(getClass().getResourceAsStream("/imports/MissingFromProject"));

		MockTypeIndex index = new MockTypeIndex();
		index.add("my.pkg.MyType");
		ImportsProcessor testable = createImportsProcessor(index);

		boolean modified = testable.modify(lines);
		Assertions.assertThat(lines).contains("import java.util.Arrays;");
		Assertions.assertThat(lines).contains("import java.util.List;");
		Assertions.assertThat(lines).contains("import my.pkg.MyType;");
		Check.isTrue(modified);
	}

	@Test
	public void modifyAddMissingJavaImport() throws IOException {
		List<String> lines = StreamUtils.readLines(getClass().getResourceAsStream("/imports/MissingFromJava"));

		TypeIndex index = new TypeIndex();
		index.loadCorePackages();
		ImportsProcessor testable = createImportsProcessor(index);

		boolean modified = testable.modify(lines);
		Assertions.assertThat(lines).contains("import java.util.Arrays;");
		Assertions.assertThat(lines).contains("import java.util.Collections;");
		Assertions.assertThat(lines).contains("import java.util.List;");
		Check.isTrue(modified);
	}

	@Test
	public void modifyDontAddReferencedInString() throws IOException {
		List<String> lines = StreamUtils.readLines(getClass().getResourceAsStream("/imports/InString"));

		MockTypeIndex index = new MockTypeIndex();
		index.add("java.util.Collections");
		ImportsProcessor testable = createImportsProcessor(index);

		boolean modified = testable.modify(lines);
		Check.isFalse(modified);
	}

	@Test
	public void modifyUnusedImports() throws IOException {
		ImportsProcessor testable = createImportsProcessor();
		List<String> lines = StreamUtils.readLines(getClass().getResourceAsStream("/imports/Unused"));
		int count = lines.size();

		boolean modified = testable.modify(lines);
		Check.isTrue(modified);
		Check.isEqual(lines.size(), count - 1);
	}

	@Test
	public void generateImports() {
		ImportsProcessor processor = createImportsProcessor();
		processor.ownPkg = "com.dummy";
		processor.imports.put("List", "java.util.List");
		processor.imports.put("Dummy", "com.eriklievaart.Dummy");

		String pkg = "package com.dummy;";
		String importDummy = "import com.eriklievaart.Dummy;";
		String importList = "import java.util.List;";
		String type = "public class Fake {}";

		List<String> lines = new ArrayList<>();
		lines.add(type);
		processor.generateImports(lines);
		Assertions.assertThat(lines).containsExactly(pkg, "", importDummy, "", importList, "", type);
	}

	public ImportsProcessor createImportsProcessor() {
		return createImportsProcessor(new TypeIndex());
	}

	private ImportsProcessor createImportsProcessor(TypeIndex index) {
		return new ImportsProcessor(new File("/tmp/dummy/Dummy.java"), index);
	}
}
