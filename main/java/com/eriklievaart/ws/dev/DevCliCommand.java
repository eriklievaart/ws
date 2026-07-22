package com.eriklievaart.ws.dev;

import com.eriklievaart.ws.dev.cmd.CmdFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DevCliCommand {

	private DevCliType type;
	private List<String> args = new ArrayList<>();
	private List<CmdFlag> flags = new ArrayList<>();

	public DevCliCommand(DevCliType type) {
		this.type = type;
		this.args = args;
	}

	public DevCliType getType() {
		return type;
	}

	public List<String> getArguments() {
		return Collections.unmodifiableList(args);
	}

	public void addArgument(String value) {
		args.add(value);
	}

	public boolean hasArguments() {
		return args.size() > 0;
	}

	public String getSingleArgument() {
		return args.get(0);
	}

	public List<CmdFlag> getFlags() {
		return Collections.unmodifiableList(flags);
	}

	public CmdFlag getSingleFlag() {
		return flags.get(0);
	}

	public boolean hasFlags() {
		return !flags.isEmpty();
	}

	public void addFlag(CmdFlag flag) {
		flags.add(flag);
	}

	public void invoke(DevContext context) {
		type.invoke(this, context);
	}
}
