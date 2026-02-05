package com.eriklievaart.ws.process.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReferenceTokenizer {

	private boolean inComment = false;
	private Set<String> allCaps = new HashSet<>();
	private Set<String> referenced = new HashSet<>();

	{
		allCaps.add("URL");
		allCaps.add("URI");
	}

	public void append(String line) {
		if (inComment) {
			if (line.contains("*/")) {
				inComment = false;
			}
		} else {
			String stripped = stripStrings(line);
			stripped = stripped.replaceFirst("/[*].*[*]/", "");
			if (stripped.contains("/*")) {
				inComment = true;
			} else {
				extractReferences(stripped);
			}
		}
	}

	public boolean contains(String token) {
		return referenced.contains(token);
	}

	public void remove(String token) {
		referenced.remove(token);
	}

	public List<String> listTokens() {
		List<String> iterate = new ArrayList<>(referenced);
		Collections.sort(iterate);
		return iterate;
	}

	private void extractReferences(String raw) {
		for (String token : raw.split("\\W++")) {
			if (token.length() == 0 || !Character.isUpperCase(token.charAt(0))) {
				continue;
			}
			if(token.matches("^[A-Z_]++$") && !allCaps.contains(token)) {
				continue;
			}
			referenced.add(token);
		}
	}

	static String stripStrings(String raw) {
		return raw.replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "");
	}
}
