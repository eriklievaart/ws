package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;

public class HelpCmd implements Cmd {

	public void execute(DevCliCommand cmd, DevContext context) {
		usage();
	}

	public static void usage() {
		System.out.println("usage:");
		System.out.println("\thelp [cmd]?        # get help about commands");
		System.out.println("\trepo [repo]?       # select a repo");
		System.out.println("\tclass [name]?      # select a class");
		System.out.println("\tpackage [name]?    # select a package");
		System.out.println("\tsearch [query]?    # search for files");
		System.out.println("");
	}
}
