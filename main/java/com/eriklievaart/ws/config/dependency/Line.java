package com.eriklievaart.ws.config.dependency;

public class Line {

	private final int number;
	private final String all;
	private String text;

	public Line(int number, String text) {
		this.number = number;
		this.all = text.trim();
		this.text = text.trim();
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		text = value;
	}

	public int getLineNumber() {
		return number;
	}

	public String createMessage(String suffix) {
		return number + ") " + all + " => " + suffix;
	}

	public void verify(boolean test, String suffix) {
		if (!test) {
			throw new RuntimeException(createMessage(suffix));
		}
	}

	@Override
	public String toString() {
		return number + ": " + text;
	}
}
