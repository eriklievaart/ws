package com.eriklievaart.ws.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A command that can be invoked on the native shell.
 *
 * @author Erik Lievaart
 */
public class RuntimeCommand {

	private final List<String> cmdarray = new ArrayList<>();

	public RuntimeCommand(final String... cmd) {
		cmdarray.addAll(Arrays.asList(cmd));
	}

	/**
	 * Returns the command and its arguments in order.
	 */
	public String[] cmd() {
		return cmdarray.size() == 0 ? null : cmdarray.toArray(new String[0]);
	}

	@Override
	public String toString() {
		return cmdarray.toString();
	}
}
