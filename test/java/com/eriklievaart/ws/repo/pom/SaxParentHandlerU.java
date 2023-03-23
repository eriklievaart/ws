package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.ws.repo.XmlBuilder;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxParentHandlerU {

	@Test
	public void parsePropertiesHasParent() throws IOException {
		SaxParentHandler testable = new SaxParentHandler();

		XmlBuilder xml = new XmlBuilder("/project/parent");
		xml.multiText("artifactId=jetty-project|groupId=org.eclipse.jetty|version=11.0.14");
		SaxSupport.parse(xml.toString(), testable);

		Check.isEqual(testable.getParent().get().getArtifactId(), "jetty-project");
	}

	@Test
	public void parsePropertiesNoParent() throws IOException {
		SaxParentHandler testable = new SaxParentHandler();

		XmlBuilder xml = new XmlBuilder("/project");
		SaxSupport.parse(xml.toString(), testable);

		Check.isTrue(testable.getParent().isEmpty());
	}
}
