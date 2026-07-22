package com.eriklievaart.ws.dev;

public class Terminal {

	public static void println(String line) {
		System.out.println(line);
	}

	public static void printInRed(String red) {
		System.out.println("\033[31m" + red + "\033[0m");
	}

	public static void printInBlue(String blue) {
		System.out.println("\033[34m" + blue + "\033[0m");
	}
}
