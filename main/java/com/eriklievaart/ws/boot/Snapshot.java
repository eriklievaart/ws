package com.eriklievaart.ws.boot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eriklievaart.ws.repo.Repo;

public class Snapshot {

	private static Repo repo = new Repo();

	public static void main(String[] args) {
		if (args.length < 1) {
			throw new RuntimeException("invalid arguments; expecting [JAR]");
		}
		File file = new File(args[0]);

		if (file.isDirectory()) {
			storeJars(scanDirectoryForJars(file));

		} else if (isJar(file)) {
			storeJars(Arrays.asList(file));

		} else {
			throw new Error("*ERROR:* not a jar file: " + file);
		}
	}

	private static List<File> scanDirectoryForJars(File file) {
		List<File> jars = new ArrayList<>();
		for (File child : file.listFiles()) {
			if (isJar(child)) {
				jars.add(child);
			}
		}
		return jars;
	}

	private static void storeJars(List<File> jars) {
		if (jars.size() == 0) {
			throw new Error("no jar files found!");
		}
		for (File jar : jars) {
			System.out.println("ws storing snapshot of jar: " + jar);
			repo.storeSnapshot(jar);
		}
	}

	private static boolean isJar(File file) {
		String name = file.getName().toLowerCase();
		return file.isFile() && !name.endsWith("src.jar") && name.endsWith(".jar");
	}
}
