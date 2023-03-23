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
		openAndCloseTags(element.split("/"));
	}

	public void createElement(String element, Consumer<XmlBuilder> consumer) {
		String[] path = element.split("/");
		openTags(path);
		consumer.accept(this);
		closeTags(path);
	}

	private void openAndCloseTags(String[] tags) {
		openTags(tags);
		closeTags(tags);
	}

	private void closeTags(String[] tags) {
		for (int i = tags.length - 1; i >= 0; i--) {
			closeTag(tags[i]);
		}
	}

	private void openTags(String[] tags) {
		for (String tag : tags) {
			openTag(tag);
		}
	}

	private void openTag(String tag) {
		head.append("<").append(tag).append(">");
	}

	private void closeTag(String tag) {
		head.append("</").append(tag).append(">");
	}

	private void popElement() {
		closeTag(stack.pop());
	}

	private void nestedElement(String element) {
		openTag(element);
		stack.add(element);
	}

	@Override
	public String toString() {
		List<String> list = new ArrayList<>(stack);
		Collections.reverse(list);
		return head.toString() + "</" + String.join("></", list) + ">";
	}
}
