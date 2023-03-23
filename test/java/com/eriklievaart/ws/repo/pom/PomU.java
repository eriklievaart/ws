package com.eriklievaart.ws.repo.pom;

import java.util.Arrays;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.ws.config.dependency.DependencyReference;

public class PomU {

	@Test
	public void updateVersionFromProperty() {
		DependencyReference reference = new DependencyReference("commons-csv");
		reference.setGroupId("org.apache.commons");
		reference.setVersion("${csv}");

		Pom pom = new Pom(new DependencyReference("pom"));
		pom.putAll(MapTool.of("csv", "1.9.0"));

		Check.isTrue(reference.versionContainsProperty());
		pom.updateVersion(reference);
		Check.isFalse(reference.versionContainsProperty());
		Check.isEqual(reference.getVersion(), "1.9.0");
	}

	@Test
	public void updateVersionFromParentProperty() {
		DependencyReference reference = new DependencyReference("commons-csv");
		reference.setGroupId("org.apache.commons");
		reference.setVersion("${csv}");

		Pom pom = new Pom(new DependencyReference("pom"));
		Pom parent = new Pom(new DependencyReference("parent"));
		parent.putAll(MapTool.of("csv", "1.9.0"));
		pom.setParent(parent);

		Check.isTrue(reference.versionContainsProperty());
		pom.updateVersion(reference);

		// Should the property in a parent pom substitute in the child pom?
		// I am assuming not for now
		Check.isTrue(reference.versionContainsProperty());
	}

	@Test
	public void updateVersionFromManagedDependency() {
		DependencyReference reference = new DependencyReference("commons-csv");
		reference.setGroupId("org.apache.commons");

		Pom pom = new Pom(new DependencyReference("pom"));

		DependencyReference managed = new DependencyReference("commons-csv");
		managed.setGroupId("org.apache.commons");
		managed.setVersion("1.9.0");
		pom.setManaged(Arrays.asList(managed));

		Check.isEqual(reference.getVersion(), "@snapshot");
		pom.updateVersion(reference);
		Check.isEqual(reference.getVersion(), "1.9.0");
	}

	@Test
	public void updateVersionFromParentManagedDependency() {
		DependencyReference reference = new DependencyReference("commons-csv");
		reference.setGroupId("org.apache.commons");

		Pom pom = new Pom(new DependencyReference("pom"));
		Pom parent = new Pom(new DependencyReference("parent"));
		pom.setParent(parent);

		DependencyReference managed = new DependencyReference("commons-csv");
		managed.setGroupId("org.apache.commons");
		managed.setVersion("1.9.0");
		parent.setManaged(Arrays.asList(managed));

		Check.isEqual(reference.getVersion(), "@snapshot");
		pom.updateVersion(reference);
		Check.isEqual(reference.getVersion(), "1.9.0");
	}
}
