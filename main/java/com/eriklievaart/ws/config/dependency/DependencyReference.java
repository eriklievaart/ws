package com.eriklievaart.ws.config.dependency;

public class DependencyReference implements Comparable<DependencyReference> {
	private static final String LOCAL = "@local";
	private static final String SNAPSHOT = "@snapshot";

	private String artifactId;
	private String groupId = LOCAL;
	private String version = SNAPSHOT;

	public DependencyReference(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int compareTo(DependencyReference o) {
		if (isLocal() ^ o.isLocal()) {
			return isLocal() ? -1 : 1;
		}
		return artifactId.compareTo(o.artifactId);
	}

	public boolean isLocal() {
		return groupId.equals(LOCAL);
	}

	public boolean isSnapshot() {
		return version.equals(SNAPSHOT);
	}

	@Override
	public String toString() {
		return "Dependency[" + artifactId + ", " + groupId + ", " + version + "]";
	}
}
