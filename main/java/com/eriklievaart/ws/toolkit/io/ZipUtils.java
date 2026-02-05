package com.eriklievaart.ws.toolkit.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

public class ZipUtils {

	public static List<String> listPathsInJar(File jar) {
		List<String> files = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(jar)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".java")) {
					files.add(entry.getName());
				}
			}
		} catch (IOException ioe) {
			throw new IORuntimeException(ioe);
		}
		return files;
	}
}
