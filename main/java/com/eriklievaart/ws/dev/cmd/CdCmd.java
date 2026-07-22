package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;

import java.io.File;

public class CdCmd implements Cmd {

	public void executeAndLs(DevCliCommand cmd, DevContext context) {
		execute(cmd, context);
		new LsCmd().execute(cmd, context);
	}

	public void execute(DevCliCommand cmd, DevContext context) {
		String arg = cmd.getSingleArgument();
		File dir = context.directory;
		File root = context.project.getGitDir();
		if (arg.equals("/")) {
			context.directory = root;
		} else if (arg.equals("..")) {
			File parent = dir.getParentFile();
			if (parent.getAbsolutePath().startsWith(root.getAbsolutePath())) {
				context.directory = parent;
			}
		} else {
			File child = new File(dir, arg);
			if(child.isDirectory()) {
				context.directory = child;
			} else {
				System.out.println("no such child: " + arg);
				System.out.println();
			}
		}
	}
}
