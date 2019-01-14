package com.eriklievaart.ws.process.whitespace;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class WhitespaceFileScannerU extends SandboxTest {

	public static void main(String[] args) {
		new WhitespaceFileScanner().removeTrailingWhitespace(new File("/home/eazy/Development/git/hufront"));
	}

	@Before
	public void init() {
		super.createSandbox();
	}

	@After
	public void cleanup() {
		super.deleteSandboxFiles();
	}

	@Test
	public void removeTrailingWhitespace() {
		SystemFile file = systemFile("clean.tpl");
		file.getContent().writeString("abc   \t\n");
		new WhitespaceFileScanner().removeTrailingWhitespace(file(""));
		Check.isEqual(file.getContent().readString(), "abc\n");
	}

	@Test
	public void removeTrailingWhitespaceIgnoreFile() {
		SystemFile file = systemFile("clean.ignore");
		file.getContent().writeString("abc   \t\n");
		new WhitespaceFileScanner().removeTrailingWhitespace(file(""));
		Check.isEqual(file.getContent().readString(), "abc   \t\n");
	}
}
