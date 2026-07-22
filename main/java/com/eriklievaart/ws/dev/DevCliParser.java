package com.eriklievaart.ws.dev;

import com.eriklievaart.ws.dev.cmd.CmdFlag;

import java.util.ArrayList;
import java.util.List;

public class DevCliParser {

	private static final String CMD_REGEX = "[a-z0-9]++";

	public static DevCliCommand parse(String input) {
		DevCliType type = resolveDevCliType(input.trim().replaceFirst(" .*", ""));
		DevCliCommand command = new DevCliCommand(type);
		String[] cmdToFlags = input.split("\\s+-");

		String head = cmdToFlags[0].trim();
		parseFlags(command, subList(cmdToFlags, 1));
		if (head.contains(" ")) {
			parseArguments(command, head.replaceFirst("\\S+\\s+", "").trim());
		}
		return command;
	}

	public static void parseFlags(DevCliCommand command, List<String> flags) {
		for (String flag: flags) {
			parseFlag(command, flag);
		}
	}

	public static void parseFlag(DevCliCommand command, String raw) {
		String flag = "-" + raw.replaceFirst(" .*", "");
		List<String> arguments = subList(raw.split(" +"), 1);
		command.addFlag(new CmdFlag(flag, arguments));
	}

	public static void parseArguments(DevCliCommand command, String raw) {
		String[] args = raw.split(" +");
		for (String arg: args) {
			command.addArgument(arg);
		}
	}

	private static DevCliType resolveDevCliType(String raw) {
		for (DevCliType type: DevCliType.values()) {
			if (type.name().startsWith(raw.toUpperCase())) {
				return type;
			}
		}
		throw new DevCliException("unknown command: '" + raw + "'");
	}

	private static List<String> subList(String[] array, int start) {
		int i = start;

		List<String> list = new ArrayList<>();
		while (i < array.length) {
			list.add(array[i++]);
		}
		return list;
	}
}
