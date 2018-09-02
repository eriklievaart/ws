package com.eriklievaart.ws.config.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.ws.config.dependency.DependencyConfigParser;
import com.eriklievaart.ws.config.dependency.DependencyReference;
import com.eriklievaart.ws.config.dependency.Header;
import com.eriklievaart.ws.config.dependency.Line;

public class DependencyConfigParserU {

	@Test
	public void parseDependencyReferencePartial() {
		String text = "toolkit";
		DependencyReference reference = DependencyConfigParser.parseDependencyReference(new Line(1, text));

		Assertions.assertThat(reference.getArtifactId()).isEqualTo(text);
		Assertions.assertThat(reference.isLocal()).isTrue();
		Assertions.assertThat(reference.isSnapshot()).isTrue();
	}

	@Test
	public void parseDependencyReferenceFull() {
		String text = "guice:com.google.inject:1.0";
		DependencyReference reference = DependencyConfigParser.parseDependencyReference(new Line(1, text));

		Assertions.assertThat(reference.getArtifactId()).isEqualTo("guice");
		Assertions.assertThat(reference.getGroupId()).isEqualTo("com.google.inject");
		Assertions.assertThat(reference.getVersion()).isEqualTo("1.0");
	}

	@Test
	public void parseHeaderSingleBracket() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "["));
		}).hasMessageContaining("invalid header");
	}

	@Test
	public void parseHeaderEmpty() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "[]"));
		}).hasMessageContaining("missing header name");
	}

	@Test
	public void parseHeaderMissingLocation() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "[tmp=]"));
		}).hasMessageContaining("missing location");
	}

	@Test
	public void parseHeaderMissingName() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "[=/home]"));
		}).hasMessageContaining("missing header name");
	}

	@Test
	public void parseHeaderTrailingText() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "[bla] appel"));
		}).hasMessageContaining("trailing");
	}

	@Test
	public void parseHeaderMissingBracket() {
		Assertions.assertThatThrownBy(() -> {

			DependencyConfigParser.parseHeader(new Line(1, "[main-compile"));
		}).hasMessageContaining("invalid header");
	}

	@Test
	public void parseHeaderShort() {
		Header header = DependencyConfigParser.parseHeader(new Line(1, "[main-compile]"));
		Assertions.assertThat(header.getName()).isEqualTo("main-compile");
	}

	@Test
	public void parseHeaderLong() {
		Header header = DependencyConfigParser.parseHeader(new Line(1, "[temp=/tmp]"));
		Assertions.assertThat(header.getName()).isEqualTo("temp");
		Assertions.assertThat(header.getLocation()).isEqualTo(new File("/tmp"));
	}

	@Test
	public void parseHeaderProperty() {
		Header header = DependencyConfigParser.parseHeader(new Line(1, "[home=@home@]"));
		Assertions.assertThat(header.getName()).isEqualTo("home");
		Assertions.assertThat(header.getLocation().getAbsolutePath()).doesNotContain("@");
	}

	@Test
	public void parseEmpty() {
		List<String> lines = new ArrayList<>();
		lines.add("");

		List<Header> result = DependencyConfigParser.parse(lines);
		Assertions.assertThat(result).isEmpty();
	}

	@Test
	public void parseDependencyWithoutHeader() {
		List<String> lines = new ArrayList<>();
		lines.add("toolkit");

		Assertions.assertThatThrownBy(() -> {
			DependencyConfigParser.parse(lines);
		}).hasMessageContaining("expecting header");
	}

	@Test
	public void parseHeader() {
		List<String> lines = new ArrayList<>();
		lines.add("[main-compile]");

		List<Header> result = DependencyConfigParser.parse(lines);

		Assertions.assertThat(result).hasSize(1);
		Assertions.assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(new Header("compile"));
	}

	@Test
	public void parseHeaderWithLocation() {
		List<String> lines = new ArrayList<>();
		lines.add("[tmp=/tmp]");

		List<Header> result = DependencyConfigParser.parse(lines);

		Assertions.assertThat(result).hasSize(1);
		Assertions.assertThat(result.get(0))
				.isEqualToComparingFieldByFieldRecursively(new Header("tmp", new File("/tmp")));
	}

	@Test
	public void parseSingleEntry() {
		List<String> lines = new ArrayList<>();
		lines.add("[main-compile]");
		lines.add("toolkit");

		List<Header> result = DependencyConfigParser.parse(lines);
		Assertions.assertThat(result).hasSize(1);

		Header expected = new Header("compile");
		expected.addDependencyReference(new DependencyReference("toolkit"));
		Assertions.assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(expected);
	}

	@Test
	public void parseSingleEntryWithDetails() {
		List<String> lines = new ArrayList<>();
		lines.add("[main-compile]");
		lines.add("junit;org.junit;4.7");

		List<Header> result = DependencyConfigParser.parse(lines);
		Assertions.assertThat(result).hasSize(1);

		Header expected = new Header("compile");
		DependencyReference dependency = new DependencyReference("junit");
		dependency.setGroupId("org.junit");
		dependency.setVersion("4.7");
		expected.addDependencyReference(dependency);
		Assertions.assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(expected);
	}

	@Test
	public void parseMultipleEntryWithDetails() {
		List<String> lines = new ArrayList<>();
		lines.add("[main-compile]");
		lines.add("commons-logging commons-logging 1.2");
		lines.add("[test-compile]");
		lines.add("junit;org.junit;4.7");

		List<Header> result = DependencyConfigParser.parse(lines);
		Assertions.assertThat(result).hasSize(2);

		Header main = new Header("compile");
		DependencyReference logging = new DependencyReference("commons-logging");
		logging.setGroupId("commons-logging");
		logging.setVersion("1.2");
		main.addDependencyReference(logging);
		Assertions.assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(main);

		Header test = new Header("test");
		DependencyReference dependency = new DependencyReference("junit");
		dependency.setGroupId("org.junit");
		dependency.setVersion("4.7");
		test.addDependencyReference(dependency);
		Assertions.assertThat(result.get(1)).isEqualToComparingFieldByFieldRecursively(test);
	}
}
