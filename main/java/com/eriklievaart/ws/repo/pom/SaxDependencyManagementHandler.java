package com.eriklievaart.ws.repo.pom;

public class SaxDependencyManagementHandler extends SaxDependenciesHandler {

	@Override
	public String contextPath() {
		return "project/dependencyManagement/dependencies/dependency";
	}
}
