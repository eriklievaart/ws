package com.eriklievaart.ws.cli;

import java.util.Arrays;

public class Cli {

	private static final String ID_REGEX = "[-_.a-z0-9]++";

	public static void parseAndInvoke(String input) throws CliInputException {
		CliCommand cli = parse(input.trim());
		if (cli.arguments.isAllWorkspaces() && !cli.command.isMultipleWorkspacesSupported()) {
			throw new CliInputException("Cannot run " + cli.command + " for all projects");
		}
		cli.command.invoke(cli.arguments);
	}

	static CliCommand parse(String input) throws CliInputException {
		String[] split = input.split("\\s++", 3);
		if (split.length < 2) {
			throw new CliInputException("Invalid command! Expecting at least 2 words, found " + Arrays.asList(split));
		}
		CommandType command = parseCommandType(split[0]);
		String workspace = split[1];
		if (command == null) {
			command = parseCommandType(split[1]);
			workspace = split[0];
		}
		if (command == null) {
			throw new CliInputException("Not a valid command: " + split[0]);
		}
		CliArguments arguments = new CliArguments(workspace);
		if (split.length == 3) {
			arguments.addProjects(split[2].split("\\s++"));
		}
		return new CliCommand(command, arguments);
	}

	private static CommandType parseCommandType(String command) {
		for (CommandType type : CommandType.values()) {
			if (type.name().startsWith(command.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

	public static void checkIdentifier(String id) {
		if (id == null || !id.matches(ID_REGEX)) {
			throw new RuntimeException("Invalid identifier '" + id + "' must match " + ID_REGEX);
		}
	}
}