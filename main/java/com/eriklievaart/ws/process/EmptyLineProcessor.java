package com.eriklievaart.ws.process;

import java.util.List;

public class EmptyLineProcessor implements LineProcessor {

	@Override
	public boolean modify(List<String> lines) {
		boolean changed = false;

		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i).trim();
			if (line.isEmpty()) {
				lines.remove(i);
				changed = true;

			} else if (!line.matches("[}]++")) {
				break;
			}
		}
		return changed;
	}
}
