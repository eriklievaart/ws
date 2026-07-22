package com.eriklievaart.ws.dev;

import com.eriklievaart.ws.dev.cmd.HelpCmd;

public class DevCli {

	private DevContext context = new DevContext();

	public void welcome() {
		clearTerminal();
		System.out.println("welcome to dev support\n");
		HelpCmd.usage();
	}

	public void processInput(String input) {
		if (input == null || input.matches("q(uit)?")) {
			System.exit(0);
		}
		if (input.isEmpty()) {
			return;
		}
		clearTerminal();
		System.out.println(input);
		System.out.println();

		DevCliParser.parse(input).invoke(context);
		showContext();
	}

	private void clearTerminal() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	private void showContext() {
		if (context.project == null) {
			return;
		}
		System.out.println("project: " + context.project.getName());
		System.out.println("directory: " + DevPaths.subDirectory(context.project.getGitDir(), context.directory));

		if(context.clz != null) {
			System.out.println("class: " + DevPaths.qualifiedName(context.clz));
		}
		System.out.println();
	}
}
