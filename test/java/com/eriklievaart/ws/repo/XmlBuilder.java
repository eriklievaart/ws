package com.eriklievaart.ws.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import com.eriklievaart.toolkit.io.api.UrlTool;

public class XmlBuilder {

	private StringBuilder head = new StringBuilder();
	private Stack<String> stack = new Stack<>();

	public XmlBuilder() {
	}

	public XmlBuilder(String root) {
		into(root);
	}

	public void into(String path) {
		for (String element : UrlTool.removeLeadingSlashes(path).split("/")) {
			nestedElement(element);
		}
	}

	public void multiText(String multi) {
		for (String element : multi.split("\\|")) {
			String[] elementToText = element.split("=", 2);
			nestedElement(elementToText[0]);
			head.append(elementToText[1]);
			popElement();
		}
	}

	public void createElement(String element) {
		nestedElement(element);
		popElement();
	}

	public void createElement(String element, Consumer<XmlBuilder> consumer) {
		nestedElement(element);
		consumer.accept(this);
		popElement();
	}

	private void popElement() {
		head.append("</").append(stack.pop()).append(">");
	}

	private void nestedElement(String element) {
		head.append("<").append(element).append(">");
		stack.add(element);
	}

	@Override
	public String toString() {
		List<String> list = new ArrayList<>(stack);
		Collections.reverse(list);
		return head.toString() + "</" + String.join("></", list) + ">";
	}
}
