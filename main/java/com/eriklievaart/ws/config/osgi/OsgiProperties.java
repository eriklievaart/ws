package com.eriklievaart.ws.config.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eriklievaart.ws.toolkit.io.FileUtils;

public class OsgiProperties {

	private boolean changed = false;
	private Set<String> keys = new HashSet<>();
	private List<OsgiProperty> properties = new ArrayList<>();

	public void ensureKeyExists(String key, String value) {
		if (keys.add(key)) {
			properties.add(new OsgiProperty(key, value, false));
			changed = true;
		}
	}

	public void load(File config) {
		if (!config.isFile()) {
			return;
		}
		loadMap(config).forEach((k, v) -> {
			String key = k.replaceFirst("^#*\\s*", "");
			keys.add(key);
			properties.add(new OsgiProperty(key, v, k.startsWith("#")));
		});
	}

	private Map<String, String> loadMap(File file) {
		Map<String, String> map = new Hashtable<>();
		for (String line : FileUtils.readLines(file)) {
			if (line.contains("=")) {
				String[] keyToValue = line.split("=", 2);
				map.put(keyToValue[0].trim(), keyToValue[1].trim());
			}
		}
		return map;
	}

	public void store(File file) {
		if (!changed) {
			System.out.println("no changes, ok: " + file);
			return;
		}
		System.out.println("saving changes to: " + file);
		FileUtils.writeStringToFile(createFileContents(), file);
	}

	private String createFileContents() {
		StringBuilder builder = new StringBuilder("\n");

		List<OsgiProperty> copy = new ArrayList<>(properties);
		Collections.sort(copy);
		appendCategory("felix.", copy, builder);
		appendCategory("org.", copy, builder);
		copy.forEach(property -> appendProperty(property, builder));

		return builder.toString();
	}

	private void appendCategory(String prefix, List<OsgiProperty> copy, StringBuilder builder) {
		for (int i = 0; i < copy.size(); i++) {
			OsgiProperty property = copy.get(i);
			if (property.getKey().startsWith(prefix)) {
				appendProperty(property, builder);
				copy.remove(i--);
			}
		}
		builder.append("\n");
	}

	private void appendProperty(OsgiProperty property, StringBuilder builder) {
		if (property.isComment()) {
			builder.append("#");
		}
		builder.append(property.getKey()).append("=").append(property.getValue()).append("\n");
	}
}
