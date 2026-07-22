package com.eriklievaart.ws.dev;

import java.io.File;

public class DevPaths {

	public static String qualifiedName(File file) {
		String path = file.getAbsolutePath().replaceFirst(".*/java/", "").replaceFirst(".java$", "");
		return path.replaceAll("[/\\\\]", ".");
	}

	public static String subDirectory(File parent, File child) {
		int skip = parent.getAbsolutePath().length() + 1;
		String path = child.getAbsolutePath();
		return path.length() <= skip ? "/" : path.substring(skip);
	}
}
