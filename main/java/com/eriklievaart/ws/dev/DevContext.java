package com.eriklievaart.ws.dev;

import com.eriklievaart.ws.workspace.Project;

import java.io.File;

public class DevContext {

	public Project project = null;
	public File directory = null;
	public File clz = null;

	public boolean isProjectSelected() {
		return project != null;
	}
}
