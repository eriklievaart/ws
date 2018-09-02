package com.eriklievaart.ws.config.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eriklievaart.ws.config.PropertyReplacer;
import com.eriklievaart.ws.migrate.Migrate;
import com.eriklievaart.ws.toolkit.io.FileTool;

public class DependencyConfigParser {

	private static Pattern headerPattern = Pattern.compile("^\\[([^=\\]]*+)(?:=([^\\]]*+))?\\]");

	public static List<Header> parse(File file) {
		return parse(FileTool.readLines(file));
	}

	public static List<Header> parse(List<String> lines) {
		List<Header> headers = new ArrayList<>();
		List<String> migrated = Migrate.migrate(lines);

		for (int i = 0; i < migrated.size(); i++) {
			Line line = new Line(i, migrated.get(i).replaceFirst("#.*", ""));

			if (line.getText().startsWith("[")) {
				headers.add(parseHeader(line));

			} else if (line.getText().length() > 0) {
				parseDependencyReference(headers, line);
			}
		}
		return headers;
	}

	static Header parseHeader(Line line) {
		Matcher matcher = headerPattern.matcher(line.getText());
		line.verify(matcher.find(), "invalid header");
		line.verify(matcher.group().length() == line.getText().length(), "trailing text");

		String name = matcher.group(1);
		line.verify(name.length() > 0, "missing header name");
		Header header = new Header(name);

		String location = matcher.group(2);
		if (location != null) {
			line.verify(location.trim().length() > 0, "missing location");
			header.setLocation(new File(new PropertyReplacer().apply(location)));
		}
		return header;
	}

	private static void parseDependencyReference(List<Header> headers, Line line) {
		for (LibType lib : LibType.values()) {
			String prefix = lib.getDir() + " ";
			if (line.getText().startsWith(prefix)) {
				headers.add(new Header(lib.getDir()));
				line.setText(line.getText().replaceFirst("^[^ ]++\\s++", ""));
				break;
			}
		}
		if (headers.isEmpty()) {
			throw new RuntimeException(line.createMessage("expecting header!"));
		}
		headers.get(headers.size() - 1).addDependencyReference(parseDependencyReference(line));
	}

	static DependencyReference parseDependencyReference(Line line) {
		String[] split = line.getText().split("[;: \t]++");

		DependencyReference dependency = new DependencyReference(split[0]);
		if (split.length > 1) {
			dependency.setGroupId(split[1]);
		}
		if (split.length > 2) {
			dependency.setVersion(split[2]);
		}
		return dependency;
	}
}
