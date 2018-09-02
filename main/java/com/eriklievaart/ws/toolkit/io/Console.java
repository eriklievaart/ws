package com.eriklievaart.ws.toolkit.io;

public class Console {

	public static void printError(String message) {
		System.out.println("\033[1;31;40m" + message + "\033[0m");
	}
}
