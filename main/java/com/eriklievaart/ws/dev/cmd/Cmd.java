package com.eriklievaart.ws.dev.cmd;

import com.eriklievaart.ws.dev.DevCliCommand;
import com.eriklievaart.ws.dev.DevContext;

public interface Cmd {

	public void execute(DevCliCommand cmd, DevContext context);
}
