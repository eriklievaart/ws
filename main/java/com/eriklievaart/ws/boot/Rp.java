package com.eriklievaart.ws.boot;

import com.eriklievaart.ws.repo.Repo;

public class Rp {

	public static void main(String[] args) {
		if (args.length < 1) {
			StringBuilder builder = new StringBuilder();
			builder.append("usage:\n");
			builder.append("\t repo @           lists available snapshots\n");
			builder.append("\t repo /[query]    search for downloaded library\n");
			System.err.println(builder);
			return;
		}
		for (String arg : args) {
			Repo.showInfo(arg.replaceFirst("^/", ""));
		}
	}
}
