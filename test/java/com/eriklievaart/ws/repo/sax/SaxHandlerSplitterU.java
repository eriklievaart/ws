package com.eriklievaart.ws.repo.sax;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;

import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class SaxHandlerSplitterU {

	@Test
	public void wildcardAndPatternMatch() throws IOException {
		String xml = "<root><match><value>findme</value></match></root>";
		TestHandler handler = new TestHandler("root/match", "value");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(handler));
		Check.isEqual(handler.get("value"), "findme");
	}

	@Test
	public void wildcardMismatch() throws IOException {
		String xml = "<root><mismatch><value>findme</value></mismatch></root>";
		TestHandler handler = new TestHandler("root/match", "value");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(handler));
		Check.isNull(handler.get("value"));
	}

	@Test
	public void patternMismatch() throws IOException {
		String xml = "<root><match><value>findme</value></match></root>";
		TestHandler handler = new TestHandler("root/match", "pattern");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(handler));
		Check.isNull(handler.get("value"), "findme");
	}

	@Test
	public void patternRegex() throws IOException {
		String xml = "<root><match><value>findme</value></match></root>";
		TestHandler handler = new TestHandler("root/match", "val.++");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(handler));
		Check.isEqual(handler.get("value"), "findme");
	}

	@Test
	public void patternPartialMatch() throws IOException {
		String xml = "<root><match><value>findme</value></match></root>";
		TestHandler handler = new TestHandler("root", "value");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(handler));
		Check.isNull(handler.get("value"), "findme");
	}

	@Test
	public void multiHandler() throws IOException {
		String xml = "<root><match><first>one</first><second>two</second></match></root>";
		TestHandler first = new TestHandler("root/match", "first");
		TestHandler second = new TestHandler("root/match", "second");

		SaxSupport.parse(StreamTool.toInputStream(xml), new SaxHandlerSplitter(first, second));

		Check.isEqual(first.get("first"), "one");
		Check.isNull(first.get("second"));

		Check.isNull(second.get("first"));
		Check.isEqual(second.get("second"), "two");
	}

	private static class TestHandler implements SaxPartialHandler {
		private String expression;
		private String pattern;

		private Map<String, String> map = new Hashtable<>();

		public TestHandler(String expression, String pattern) {
			this.expression = expression;
			this.pattern = pattern;
		}

		@Override
		public String contextPath() {
			return expression;
		}

		@Override
		public void enterContext() {
		}

		@Override
		public void leaveContext() {
		}

		@Override
		public boolean evaluateText(String tail) {
			return tail.matches(pattern);
		}

		@Override
		public void text(String value, SaxContext context) {
			map.put(context.getElementName(), value);
		}

		public String get(String element) {
			return map.get(element);
		}
	}
}
