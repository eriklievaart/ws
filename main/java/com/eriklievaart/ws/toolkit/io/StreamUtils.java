package com.eriklievaart.ws.toolkit.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StreamUtils {

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	public static List<String> readLines(InputStream is) throws IOException {
		try {
			List<String> lines = new ArrayList<>();

			try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while ((line = in.readLine()) != null) {
					lines.add(line);
				}
			}
			return lines;
		} finally {
			is.close();
		}
	}

	public static String toString(InputStream is) {
		try (Scanner scanner = new Scanner(is)) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

	public static String toString(InputStream is, String encoding) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtils.copyStream(is, baos);
		return baos.toString(encoding);
	}

	public static String toString(Reader reader) throws IOException {
		try {
			BufferedReader in = new BufferedReader(reader);
			StringBuilder result = new StringBuilder();

			String line = null;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} finally {
			reader.close();
		}
	}

	public static InputStream toInputStream(String data) throws IOException {
		return new ByteArrayInputStream(data.getBytes("UTF-8"));
	}

	public static void writeString(String data, OutputStream os) {
		try (PrintWriter writer = new PrintWriter(os)) {
			writer.write(data);
		}
	}
}
