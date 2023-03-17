package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxPropertiesHandlerU {

	@Test
	public void parsePropertiesSimple() throws IOException {
		String xml = "<project><properties><jetty.url>https://www.eclipse.org/jetty/</jetty.url></properties></project>";
		SaxPropertiesHandler testable = new SaxPropertiesHandler();

		SaxSupport.parse(StreamTool.toInputStream(xml), testable);
		Check.isEqual(testable.get("jetty.url"), "https://www.eclipse.org/jetty/");
	}

	@Test
	public void parsePropertiesProjectVersion() throws IOException {
		String xml = "<project><version>1.2</version></project>";
		SaxPropertiesHandler testable = new SaxPropertiesHandler();

		SaxSupport.parse(StreamTool.toInputStream(xml), testable);
		Check.isEqual(testable.get("project.version"), "1.2");
	}

	@Test
	public void parsePropertiesParentVersion() throws IOException {
		String xml = "<project><parent><version>1.2</version></parent></project>";
		SaxPropertiesHandler testable = new SaxPropertiesHandler();

		SaxSupport.parse(StreamTool.toInputStream(xml), testable);
		Check.isEqual(testable.get("project.parent.version"), "1.2");
	}

	@Test
	public void parsePropertiesRealPom() throws IOException {
		SaxPropertiesHandler testable = new SaxPropertiesHandler();

		String xml = "/pom/parent-dependency-management.xml";
		SaxSupport.parse(getClass().getResourceAsStream(xml), testable);

		Check.isEqual(testable.get("ant.version"), "1.10.13");
	}
}
