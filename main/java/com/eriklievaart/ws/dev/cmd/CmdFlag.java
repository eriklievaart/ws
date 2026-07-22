package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliException;

import java.util.ArrayList;
import java.util.List;

public class CmdFlag {

	private String flag;
	private List<String> arguments = new ArrayList<>();

	public CmdFlag(String flag, List<String> args) {
		this.flag = flag;
		arguments.addAll(args);
	}

	public String getFlag() {
		return flag;
	}

	public String getSingleArgument() {
		return arguments.get(0);
	}

	public int checkArgumentCount(int min, int max) {
		int count = arguments.size();
		boolean valid = min <= count && count <= max;
		DevCliException.unless(valid, "invalid argument count: " + count);
		return count;
	}

	public int getArgumentCount() {
		return arguments.size();
	}
}
