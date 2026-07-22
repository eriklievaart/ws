package com.eriklievaart.ws.dev;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;
import com.eriklievaart.toolkit.mock.BombSquad;
import com.eriklievaart.ws.dev.cmd.CmdFlag;

import org.junit.Test;

public class DevCliParserU {

	@Test
	public void parseType() {
		Check.isEqual(DevCliParser.parse("help").getType(), DevCliType.HELP);
		Check.isEqual(DevCliParser.parse("repo").getType(), DevCliType.REPO);
		BombSquad.diffuse("unknown", () -> {
			DevCliParser.parse("unknown");
		});
	}

	@Test
	public void parseArgs() {
		CheckCollection.isEmpty(DevCliParser.parse("help").getArguments());
		CheckCollection.isSize(DevCliParser.parse("help me").getArguments(), 1);
		Check.isEqual(DevCliParser.parse("help me").getSingleArgument(), "me");
	}

	@Test
	public void parseFlags() {
		CheckCollection.isEmpty(DevCliParser.parse("help").getFlags());
		CheckCollection.isEmpty(DevCliParser.parse("help me").getFlags());

		CmdFlag flag = CollectionTool.getSingle(DevCliParser.parse("class -c Dummy").getFlags());
		Check.isEqual(flag.getFlag(), "-c");
	}
}
