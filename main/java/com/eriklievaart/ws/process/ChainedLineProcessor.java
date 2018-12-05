package com.eriklievaart.ws.process;

import java.util.Arrays;
import java.util.List;

public class ChainedLineProcessor implements LineProcessor {

	private final List<LineProcessor> processors;

	public ChainedLineProcessor(LineProcessor... processors) {
		this(Arrays.asList(processors));
	}

	public ChainedLineProcessor(List<LineProcessor> processors) {
		this.processors = processors;
	}

	@Override
	public boolean modify(List<String> lines) {
		boolean changed = false;
		for (LineProcessor processor : processors) {
			changed = changed | processor.modify(lines);
		}
		return changed;
	}
}
