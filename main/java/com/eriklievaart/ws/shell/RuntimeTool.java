package com.eriklievaart.ws.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.eriklievaart.ws.toolkit.io.StreamTool;

public class RuntimeTool {

	public static void execute(String[] args) {
		try {
			Process process = Runtime.getRuntime().exec(args);
			dumpStream(process.getErrorStream(), System.err);
			dumpStream(process.getInputStream(), System.out);
			process.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dumpStream(InputStream is, OutputStream os) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StreamTool.copyStream(is, os);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
