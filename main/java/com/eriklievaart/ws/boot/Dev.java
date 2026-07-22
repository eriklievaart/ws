package com.eriklievaart.ws.boot;

import com.eriklievaart.ws.dev.DevCli;
import com.eriklievaart.ws.dev.DevCliException;
import com.eriklievaart.ws.dev.Terminal;

public class Dev {

	public static void main(String[] args) {
		DevCli cli = new DevCli();
		cli.welcome();

		while (true) {
			try {
				String line = System.console().readLine();
				cli.processInput(line == null ? line : line.trim());

			} catch (DevCliException e) {
				Terminal.printInRed(e.getMessage() + "\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
