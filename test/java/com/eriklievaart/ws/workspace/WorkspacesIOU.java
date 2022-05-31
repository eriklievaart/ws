package com.eriklievaart.ws.workspace;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class WorkspacesIOU {

	@Test
	public void getAndParseLines() {
		Workspace antastic = new Workspace("antastic");
		antastic.addProject("antastic");
		antastic.addProject("toolkit");

		Workspace blog = new Workspace("blog");
		blog.addProject("blog");
		blog.addProject("jl");
		blog.addProject("toolkit");

		List<String> lines = WorkspacesIO.getLines(Arrays.asList(antastic, blog));
		List<Workspace> parsed = WorkspacesIO.parseLines(lines);

		String expectedLine1 = "antastic antastic toolkit";
		String expectedLine2 = "blog blog jl toolkit";
		Assertions.assertThat(lines).containsExactly(expectedLine1, expectedLine2);

		Assertions.assertThat(parsed).hasSize(2);
		Assertions.assertThat(parsed.get(0)).isEqualToComparingFieldByField(antastic);
		Assertions.assertThat(parsed.get(1)).isEqualToComparingFieldByField(blog);
	}
}
