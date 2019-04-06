package com.eriklievaart.ws.toolkit.io;

public class ConsoleUtils {

	public static void printWarning(String message) {
		System.out.println("\033[1;33;40m" + message + "\033[0m");
	}

	public static void printError(String message) {
		System.out.println("\033[1;31;40m" + message + "\033[0m");
	}

	public static void println(String message) {
		System.out.println(message);
	}
}
