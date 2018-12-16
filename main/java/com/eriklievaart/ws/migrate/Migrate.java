package com.eriklievaart.ws.migrate;

import java.util.ArrayList;
import java.util.List;

// @Deprecated, delete this file when all existing dependencies.txt have been migrated
public class Migrate {

	public static List<String> migrate(List<String> lines) {
		List<String> result = new ArrayList<>();
		for (String line : lines) {
			if (line.trim().matches("\\[.*\\]")) {
				String name = updateLibType(line.trim().replaceAll("[\\[\\]]", ""));
				result.add(header(name));
			} else if (line.startsWith("main-") || line.startsWith("test-")) {
				String[] split = line.split("[ :;]++", 2);
				result.add(header(updateLibType(split[0])));
				result.add(split[1]);
			} else {
				result.add(line);
			}
		}
		return result;
	}

	private static String header(String name) {
		return "[" + name + "]";
	}

	private static String updateLibType(String old) {
		return old.replaceFirst("main-([^ ]++)", "$1").replaceFirst("test-[^ ]++", "test");
	}
}
