package com.eriklievaart.ws.repo.sax;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandlerSplitter extends DefaultHandler {

	private Stack<String> stack = new Stack<>();
	private List<SaxPartialHandler> handlers;

	public SaxHandlerSplitter(SaxPartialHandler... handlers) {
		this.handlers = Arrays.asList(handlers);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		stack.push(qName);

		for (SaxPartialHandler handler : handlers) {
			if (handler.contextPath().equals(getPath())) {
				handler.enterContext();
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String text = new String(ch, start, length).trim();
		if (text.isBlank()) {
			return;
		}
		String path = getPath();

		for (SaxPartialHandler handler : handlers) {
			String filter = handler.contextPath();
			if (!path.startsWith(filter)) {
				continue;
			}
			String tail = subPath(path, filter);
			if (handler.evaluateText(tail)) {
				String element = stack.peek();
				SaxContext context = new SaxContext(element, path);
				handler.text(text, context);
			}
		}
	}

	private String subPath(String path, String filter) {
		String sub = path.substring(filter.length());
		return sub.startsWith("/") ? sub.substring(1) : sub;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		for (SaxPartialHandler handler : handlers) {
			if (handler.contextPath().equals(getPath())) {
				handler.leaveContext();
			}
		}
		stack.pop();
	}

	private String getPath() {
		return String.join("/", stack);
	}
}