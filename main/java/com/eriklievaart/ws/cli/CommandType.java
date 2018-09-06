package com.eriklievaart.ws.cli;

import java.util.EnumSet;
import java.util.function.Consumer;

import com.eriklievaart.ws.workspace.Antastic;
import com.eriklievaart.ws.workspace.Workspaces;

public enum CommandType {

	ANTASTIC(c -> Antastic.generateMetadata()),

	DEFINE(c -> Workspaces.defineWorkspace(c.getWorkspace(), c.getProjects())),

	TRASH(c -> Workspaces.deleteWorkspace(c.getWorkspace())),

	GENERATE(c -> Workspaces.createWorkspace(c.getWorkspace())),

	INFO(c -> Workspaces.showInfo(c.getWorkspace())),

	LINK(c -> Workspaces.linkProjects(c.getWorkspace(), c.getProjects())),

	UNLINK(c -> Workspaces.unlinkProjects(c.getWorkspace(), c.getProjects()));

	private static final EnumSet<CommandType> SUPPORTS_MULTIPLE_WORKSPACES = EnumSet.of(ANTASTIC, GENERATE, INFO);
	private static final EnumSet<CommandType> EXPECTS_PROJECT = EnumSet.of(DEFINE, LINK, UNLINK);

	private final Consumer<CliArguments> consumer;

	private CommandType(Consumer<CliArguments> consumer) {
		this.consumer = consumer;
	}

	public boolean isMultipleWorkspacesSupported() {
		return SUPPORTS_MULTIPLE_WORKSPACES.contains(this);
	}

	public ProjectType getProjectType() {
		return EXPECTS_PROJECT.contains(this) ? ProjectType.REQUIRED : ProjectType.NONE;
	}

	public void invoke(CliArguments command) throws CliInputException {
		if (EXPECTS_PROJECT.contains(this) && command.getProjects().isEmpty()) {
			throw new CliInputException("Project(s) required for command " + this);
		}
		if (!EXPECTS_PROJECT.contains(this) && !command.getProjects().isEmpty()) {
			throw new CliInputException("Project(s) supplied, but none expected " + this);
		}
		String projects = command.getProjects().isEmpty() ? "" : " => " + command.getProjects();
		System.out.println("invoking " + name() + " on " + command.getWorkspace() + projects + "\n");
		consumer.accept(command);
	}
}
