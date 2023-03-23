package com.eriklievaart.ws.repo;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class XmlBuilderU {

	@Test
	public void createPath() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project/parent");
		Check.isEqual(builder.toString(), "<project><parent></parent></project>");
	}

	@Test
	public void createText() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project/parent");
		builder.multiText("version=3.2|groupId=org.jetty");

		String expect = "<project><parent><version>3.2</version><groupId>org.jetty</groupId></parent></project>";
		Check.isEqual(builder.toString(), expect);
	}

	@Test
	public void createElement() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project/dependencies");
		builder.createElement("dependency");

		String expect = "<project><dependencies><dependency></dependency></dependencies></project>";
		Check.isEqual(builder.toString(), expect);
	}

	@Test
	public void createElementNested() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project");
		builder.createElement("dependencies/dependency");

		String expect = "<project><dependencies><dependency></dependency></dependencies></project>";
		Check.isEqual(builder.toString(), expect);
	}

	@Test
	public void createElementWithNestedContent() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project/dependencies");
		builder.createElement("dependency", b -> b.multiText("artifactId=aid|groupId=grp"));

		String expect = "<project><dependencies><dependency><artifactId>aid</artifactId><groupId>grp</groupId></dependency></dependencies></project>";
		Check.isEqual(builder.toString(), expect);
	}

	@Test
	public void createElementNestedWithNestedContent() {
		XmlBuilder builder = new XmlBuilder();
		builder.into("/project");
		builder.createElement("dependencies/dependency", b -> b.multiText("version=1.0"));

		String expect = "<project><dependencies><dependency><version>1.0</version></dependency></dependencies></project>";
		Check.isEqual(builder.toString(), expect);
	}
}
