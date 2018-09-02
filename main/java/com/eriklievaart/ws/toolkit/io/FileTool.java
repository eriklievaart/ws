package com.eriklievaart.ws.toolkit.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileTool {

	public static void writeStringToFile(String data, File file) {
		try {
			file.getParentFile().mkdirs();
			StreamTool.writeString(data, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeIOException(e);
		}
	}

	public static List<String> readLines(File file) {
		try (InputStream is = new FileInputStream(file)) {
			return StreamTool.readLines(is);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public static void writeLines(File file, Collection<String> lines) throws IOException {
		file.getParentFile().mkdirs();
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {

			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		}
	}

	public static void copyFile(File from, File to) throws IOException {
		File[] children = from.listFiles();
		if (children == null || children.length == 0) {
			if (from.isDirectory()) {
				to.mkdirs();
			} else {
				to.getParentFile().mkdirs();
				Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} else {
			for (File child : children) {
				copyFile(child, new File(to, child.getName()));
			}
		}
	}

	public static void copyFile(File from, OutputStream to) throws IOException {
		StreamTool.copyStream(new FileInputStream(from), to);
	}

	public static void delete(File delete) {
		if (delete == null) {
			return;
		}
		if (delete.isDirectory()) {
			for (File file : delete.listFiles()) {
				delete(file);
			}
		}
		delete.delete();
	}

	public static void moveFile(File from, File to) throws IOException {
		to.getParentFile().mkdirs();
		Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static String toString(File file) throws IOException {
		return String.join("\r\n", readLines(file));
	}

	public static List<File> listFiles(File file) {
		List<File> files = new ArrayList<>();

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				files.addAll(listFiles(child));
			}
		}
		if (file.isFile() && file.getName().endsWith(".java")) {
			files.add(file);
		}
		return files;
	}
}