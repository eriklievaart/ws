package com.eriklievaart.ws.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JlsModifierSubstitutor {
	private static final List<String> SORTED_MODIFIERS = Arrays.asList("public", "protected", "private", "abstract",
			"static", "final", "transient", "volatile", "synchronized", "native", "strictfp");

	private final String identifier;
	private final List<String> lines;
	private boolean jlsViolations = false;

	public JlsModifierSubstitutor(String identifier, List<String> lines) {
		this.identifier = identifier;
		this.lines = lines;
		process();
	}

	private void process() {
		for (int l = 0; l < lines.size(); l++) {
			String trimmed = lines.get(l).trim();
			String[] split = trimmed.split("\\s++");
			int modifierCount = getModifierCount(split);
			List<String> modifiers = new ArrayList<>();
			for (int w = 0; w < modifierCount; w++) {
				modifiers.add(split[w]);
			}
			sortModifiersOnLine(l, modifiers);
		}
	}

	private void sortModifiersOnLine(int lineNumber, List<String> modifiers) {
		boolean needsSort = isSorted(modifiers);

		if (!needsSort) {
			String line = lines.get(lineNumber);
			jlsViolations = true;
			System.out.println("JLS modifier order violation in " + identifier + ":" + lineNumber);
			sortModifiers(modifiers);
			String head = line.replaceAll("\\S++\\s*+", "");
			String tail = line.trim().replaceFirst("(\\s*+\\S++){" + modifiers.size() + "}", "");
			lines.remove(lineNumber);
			String fixed = head + String.join(" ", modifiers) + tail;
			lines.add(lineNumber, fixed);
			System.out.println("- " + line + "\n" + "+" + fixed + "\n");
		}
	}

	private void sortModifiers(List<String> modifiers) {
		Collections.sort(modifiers, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return new Integer(SORTED_MODIFIERS.indexOf(o1)).compareTo(SORTED_MODIFIERS.indexOf(o2));
			}
		});
	}

	private boolean isSorted(List<String> modifiers) {
		int last = 0;
		for (String modifier : modifiers) {
			int current = SORTED_MODIFIERS.indexOf(modifier);
			if (current < last) {
				return false;
			}
			last = current;
		}
		return true;
	}

	private int getModifierCount(String[] split) {
		for (int i = 0; i < split.length; i++) {
			if (!SORTED_MODIFIERS.contains(split[i])) {
				return i;
			}
		}
		return split.length;
	}

	public Collection<String> getLines() {
		return lines;
	}

	public boolean hasJlsViolations() {
		return jlsViolations;
	}
}
