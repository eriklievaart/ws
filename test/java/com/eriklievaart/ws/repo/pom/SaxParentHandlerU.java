package com.eriklievaart.ws.repo.pom;

import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.ws.repo.sax.SaxSupport;

public class SaxParentHandlerU {

	@Test
	public void parsePropertiesHasParent() throws IOException {
		SaxParentHandler testable = new SaxParentHandler();
		SaxSupport.parse(getClass().getResourceAsStream("/pom/hasparent.xml"), testable);
		Check.isEqual(testable.getParent().get().getArtifactId(), "jetty-project");
	}

	@Test
	public void parsePropertiesNoParent() throws IOException {
		SaxParentHandler testable = new SaxParentHandler();
		SaxSupport.parse(getClass().getResourceAsStream("/pom/noparent.xml"), testable);
		Check.isTrue(testable.getParent().isEmpty());
	}
}
