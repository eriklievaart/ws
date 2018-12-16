package com.eriklievaart.ws.cli;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CliU {

	@Test(expected = CliInputException.class)
	public void parseIncomplete() throws CliInputException {
		Cli.parse("incomplete");
	}

	@Test
	public void parseInfo() throws CliInputException {
		CliCommand parsed = Cli.parse("info q");
		Assertions.assertThat(parsed.command).isEqualTo(CommandType.INFO);
		Assertions.assertThat(parsed.arguments.getWorkspace()).isEqualTo("q");
		Assertions.assertThat(parsed.arguments.getProjects()).isEmpty();
	}

	@Test
	public void parseMultiple() throws CliInputException {
		CliCommand parsed = Cli.parse("info +");
		Assertions.assertThat(parsed.command).isEqualTo(CommandType.INFO);
		Assertions.assertThat(parsed.arguments.getWorkspace()).isEqualTo("+");
		Assertions.assertThat(parsed.arguments.getProjects()).isEmpty();
	}

	@Test
	public void parseMultiProject() throws CliInputException {
		CliCommand parsed = Cli.parse("define work proj other");
		Assertions.assertThat(parsed.command).isEqualTo(CommandType.DEFINE);
		Assertions.assertThat(parsed.arguments.getWorkspace()).isEqualTo("work");
		Assertions.assertThat(parsed.arguments.getProjects()).containsExactly("proj", "other");
	}

	@Test
	public void parsePartialCommand() throws CliInputException {
		CliCommand parsed = Cli.parse("t q");
		Assertions.assertThat(parsed.command).isEqualTo(CommandType.TRASH);
		Assertions.assertThat(parsed.arguments.getWorkspace()).isEqualTo("q");
		Assertions.assertThat(parsed.arguments.getProjects()).isEmpty();
	}

	@Test
	public void parseCommandReversedWithWorkspace() throws CliInputException {
		CliCommand parsed = Cli.parse("q l toolkit");
		Assertions.assertThat(parsed.command).isEqualTo(CommandType.LINK);
		Assertions.assertThat(parsed.arguments.getWorkspace()).isEqualTo("q");
		Assertions.assertThat(parsed.arguments.getProjects()).containsExactly("toolkit");
	}

	@Test(expected = CliInputException.class)
	public void parseAndInvokeMissingProject() throws CliInputException {
		Cli.parseAndInvoke("link q"); // should have project as argument
	}

	@Test(expected = CliInputException.class)
	public void parseAndInvokeExtraProject() throws CliInputException {
		Cli.parseAndInvoke("trash q toolkit"); // project where not expected
	}
}
