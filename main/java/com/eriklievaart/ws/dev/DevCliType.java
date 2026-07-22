package com.eriklievaart.ws.dev;

import com.eriklievaart.ws.dev.cmd.CdCmd;
import com.eriklievaart.ws.dev.cmd.ClassCmd;
import com.eriklievaart.ws.dev.cmd.HelpCmd;
import com.eriklievaart.ws.dev.cmd.LsCmd;
import com.eriklievaart.ws.dev.cmd.PackageCmd;
import com.eriklievaart.ws.dev.cmd.RepoCmd;
import com.eriklievaart.ws.dev.cmd.SearchCmd;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.BiConsumer;

public enum DevCliType {

	CD((cmd, ctx) -> new CdCmd().execute(cmd, ctx)),

	CL((cmd, ctx) -> new CdCmd().executeAndLs(cmd, ctx)),

	CLASS((cmd, ctx) -> new ClassCmd().execute(cmd, ctx)),

	HELP((cmd, ctx) -> new HelpCmd().execute(cmd, ctx)),

	LS((cmd, ctx) -> new LsCmd().execute(cmd, ctx)),

	PACKAGE((cmd, ctx) -> new PackageCmd().execute(cmd, ctx)),

	REPO((cmd, ctx) -> new RepoCmd().execute(cmd, ctx)),

	SEARCH((cmd, ctx) -> new SearchCmd().execute(cmd, ctx));

	private final BiConsumer<DevCliCommand, DevContext> consumer;
	private static final EnumSet<DevCliType> PORTABLE = EnumSet.of(DevCliType.HELP, DevCliType.REPO);

	private DevCliType(BiConsumer<DevCliCommand, DevContext> consumer) {
		this.consumer = consumer;
	}

	public String getLabel() {
		return name().toLowerCase();
	}

	public static boolean isValid(String value) {
		return Arrays.stream(values()).anyMatch(e -> e.name().equals(value.toUpperCase()));
	}

	public void invoke(DevCliCommand cmd, DevContext ctx) {
		if (PORTABLE.contains(cmd.getType()) || ctx.isProjectSelected()) {
			consumer.accept(cmd, ctx);
		} else {
			System.out.println("select a repo first!");
			System.out.println();
		}
	}
}
