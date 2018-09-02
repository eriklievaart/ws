package com.eriklievaart.ws.config;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class PropertyReplacer {

	private Map<String, String> replacements = new Hashtable<>();

	public PropertyReplacer() {
		replacements.put("@home@", System.getProperty("user.home"));
	}

	public String apply(String format) {
		String result = format;
		for (Entry<String, String> entry : replacements.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public PropertyReplacer replace(String name, String replacement) {
		replacements.put(name, replacement);
		return this;
	}

	public static PropertyReplacer project(String project) {
		return addProjectReplacement(new PropertyReplacer(), project);
	}

	public static PropertyReplacer bundle(String project, String bundle) {
		return addProjectReplacement(new PropertyReplacer(), project).replace("@bundle@", bundle);
	}

	public static PropertyReplacer workspace(String workspace) {
		return new PropertyReplacer().replace("@workspace@", workspace);
	}

	private static PropertyReplacer addProjectReplacement(PropertyReplacer replacer, String project) {
		return replacer.replace("@project@", project);
	}
}