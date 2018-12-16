package com.eriklievaart.ws.osgi;

import java.io.File;

public class ImportStatement {

	private String statement;
	private File file;

	public ImportStatement(String line, File file) {
		this.statement = line;
		this.file = file;
	}

	public boolean startsWith(String pkg) {
		return statement.startsWith(pkg);
	}

	public String getImport() {
		return statement;
	}

	public boolean isApi() {
		return statement.endsWith(".api") || statement.contains(".api.");
	}

	public File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		return statement.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImportStatement) {
			return equals((ImportStatement) obj);
		}
		return false;
	}

	public boolean equals(ImportStatement other) {
		return statement.equals(other.statement);
	}

	@Override
	public String toString() {
		return file + ": " + statement;
	}
}
