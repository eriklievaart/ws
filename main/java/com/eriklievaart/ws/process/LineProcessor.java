package com.eriklievaart.ws.process;

import java.util.List;

public interface LineProcessor {

	public boolean modify(List<String> lines);

}