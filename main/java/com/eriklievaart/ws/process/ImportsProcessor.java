package com.eriklievaart.ws.process;

import com.eriklievaart.ws.process.index.ReferenceTokenizer;
import com.eriklievaart.ws.process.index.TypeIndex;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ImportsProcessor implements LineProcessor {

	private File file;
	private TypeIndex types;

	boolean changed;
	String ownPkg;
	Map<String, String> imports = new Hashtable<>();
	ReferenceTokenizer tokenizer = new ReferenceTokenizer();

	public ImportsProcessor(File file, TypeIndex types) {
		this.file = file;
		this.types = types;
	}

	@Override
	public boolean modify(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();
			if (line.isBlank() || isSingleLineComment(line)) {
				continue;
			}
			if (line.startsWith("package")) {
				ownPkg = line.replaceFirst("\\S++ ", "").replaceFirst(";$", "");
				continue;
			}
			if (line.startsWith("import ")) {
				storeImport(line);
				continue;
			}
			tokenizer.append(line);
		}
		fixReferences(lines);
		return changed;
	}

	private void fixReferences(List<String> lines) {
		tokenizer.remove(file.getName().replaceFirst(".java$", ""));
		removeUnused(lines);
		addMissing(lines);
	}

	public void addMissing(List<String> lines) {
		for (String type : tokenizer.listTokens()) {
			if (!imports.containsKey(type) && !types.isJavaLang(type)) {
				String qualified = lookupImport(type);
				if (qualified == null) {
					System.out.println(file.getName() + ": missing import " + type);
				} else if (!qualified.equals(ownPkg + "." + type)) {
					System.out.println(file.getName() + " adding import " + type);
					imports.put(type, qualified);
					changed = true;
				}
			}
		}
		removeAllImports(lines);
		generateImports(lines);
	}

	private String lookupImport(String type) {
		boolean main = file.getAbsolutePath().contains("/main/");
		return main ? types.lookupInMain(type) : types.lookupInMainOrTest(type);
	}

	private void removeAllImports(List<String> lines) {
		int index = 0;
		while (index < lines.size()) {
			String line = lines.get(index);
			if (line.isBlank() || line.startsWith("import ") || line.startsWith("package ")) {
				lines.remove(index);
			} else {
				break;
			}
		}
	}

	void generateImports(List<String> lines) {
		int insert = 0;
		lines.add(insert++, "package " + ownPkg + ";");

		List<String> list = new ArrayList<>(imports.values());
		Collections.sort(list);

		char last = ' ';
		for (String i : list) {
			if (i.charAt(0) != last) {
				lines.add(insert++, "");
				last = i.charAt(0);
			}
			lines.add(insert++, "import " + i + ";");
		}
		lines.add(insert++, "");
	}

	private void removeUnused(List<String> lines) {
		for (String type : new ArrayList<>(imports.keySet())) {
			if (!tokenizer.contains(type) || new String(ownPkg + "." + type).equals(imports.get(type))) {
				String qualified = imports.remove(type);
				removeImport(qualified, lines);
				changed = true;
			}
		}
	}

	private void removeImport(String qualified, List<String> lines) {
		String remove = "import " + qualified + ";";
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).equals(remove)) {
				System.out.println(file.getName() + " removing unused " + remove);
				lines.remove(i);
				return;
			}
		}
		throw new Error("cannot remove import: " + qualified);
	}

	private void storeImport(String line) {
		String qualified = line.replaceFirst("\\S++ ", "").replaceFirst(";$", "");
		String base = qualified.replaceFirst(".*[.]", "");
		imports.put(base, qualified);
	}

	private boolean isSingleLineComment(String line) {
		return line.startsWith("//");
	}
}
