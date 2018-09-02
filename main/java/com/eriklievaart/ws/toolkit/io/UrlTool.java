package com.eriklievaart.ws.toolkit.io;

/**
 * Utility class for working with URL String representations.
 *
 * @author Erik Lievaart
 */
public class UrlTool {

	private UrlTool() {
	}

	public static String append(String a, String b) {
		return a.replaceAll("[/\\\\]++$", "") + "/" + b.replaceFirst("^[/\\\\]++", "");
	}

	public static String append(String... path) {
		String result = path[0];
		for (int i = 1; i < path.length; i++) {
			result = append(result, path[i]);
		}
		return result;
	}

}