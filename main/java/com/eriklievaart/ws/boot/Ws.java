package com.eriklievaart.ws.boot;

import com.eriklievaart.ws.cli.Cli;
import com.eriklievaart.ws.cli.CliInputException;
import com.eriklievaart.ws.cli.CommandType;
import com.eriklievaart.ws.cli.ProjectType;
import com.eriklievaart.ws.toolkit.io.ConsoleUtils;
import com.eriklievaart.ws.workspace.Workspaces;

public class Ws {

	public static void main(String[] args) {
		if (args.length > 0) {
			runArguments(args);
		} else {
			runInteractive();
		}
	}

	private static void runArguments(String[] args) {
		try {
			Cli.parseAndInvoke(String.join(" ", args));

		} catch (CliInputException e) {
			e.printStackTrace();
		}
	}

	private static void runInteractive() {
		printHeader();

		while (true) {
			processInput();
		}
	}

	private static void processInput() {
		String input = System.console().readLine();
		if (input == null || input.matches("q(uit)?")) {
			System.exit(0);
		}
		if (input.isEmpty()) {
			return;
		}
		try {
			Cli.parseAndInvoke(input.trim());

		} catch (CliInputException e) {
			System.out.println();
			ConsoleUtils.printError("*error*: " + e.getMessage());
			System.out.println();
			printHeader();
		}
		System.out.println();
	}

	private static void printHeader() {
		Workspaces.list();
		int maxPad = 0;
		for (CommandType commandType : CommandType.values()) {
			maxPad = Math.max(maxPad, commandType.name().length() + 1);
		}
		System.out.println();
		for (CommandType command : CommandType.values()) {
			StringBuilder builder = new StringBuilder(command.name().toLowerCase());
			while (builder.length() < maxPad) {
				builder.append(' ');
			}
			builder.append(" [workspace]");
			if (command.getProjectType() == ProjectType.REQUIRED) {
				builder.append(" [project]...");
			}
			System.out.println(builder);
		}
		System.out.println();
	}
}