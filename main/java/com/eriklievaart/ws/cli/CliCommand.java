package com.eriklievaart.ws.cli;

public class CliCommand {

	public CommandType command;
	public CliArguments arguments;

	public CliCommand(CommandType command, CliArguments arguments) {
		this.command = command;
		this.arguments = arguments;
	}
}
