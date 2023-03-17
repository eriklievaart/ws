package com.eriklievaart.ws.config.dependency;

public class DependencyReference implements Comparable<DependencyReference> {
	private static final String LOCAL = "@local";
	private static final String SNAPSHOT = "@snapshot";

	private String artifactId;
	private String groupId = LOCAL;
	private String version = SNAPSHOT;

	public DependencyReference(String artifactId) {
		if (artifactId == null) {
			throw new IllegalArgumentException("artifactId cannot be <null>!");
		}
		this.artifactId = artifactId;
	}

	public static DependencyReference of(String raw) {
		if (raw.contains(":")) {

			String[] grpArtTypeVer = raw.split(":");
			DependencyReference result = new DependencyReference(grpArtTypeVer[1]);
			result.groupId = grpArtTypeVer[0];
			result.version = grpArtTypeVer[grpArtTypeVer.length - 1];
			return result;

		} else {
			return new DependencyReference(raw);
		}
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

	public String getVersionProperty() {
		if (!version.startsWith("${")) {
			throw new IllegalStateException("version '" + version + "' does not start with property!");
		}
		return version.substring(2).replaceFirst("\\}$", "");
	}

	public String getVersion() {
		if (versionContainsProperty()) {
			throw new RuntimeException(" version contains property! use getVersionProperty() instead.\n" + toString());
		}
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

	public String getVersionedFileName() {
		if (isSnapshot()) {
			return getArtifactId() + ".jar";
		} else {
			return getArtifactId() + "-" + getVersion() + ".jar";
		}
	}

	public String getInstallString() {
		return artifactId + " " + groupId + " " + version;
	}

	public boolean versionContainsProperty() {
		return version.contains("${");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DependencyReference)) {
			return false;
		}
		DependencyReference other = (DependencyReference) obj;
		if (!artifactId.equals(other.artifactId)) {
			return false;
		}
		if (!groupId.equals(other.groupId)) {
			return false;
		}
		return version.equals(other.version);
	}

	@Override
	public int hashCode() {
		return artifactId.hashCode() + groupId.hashCode() + version.hashCode();
	}

	public String getShortString() {
		return artifactId + " | " + groupId + " | " + version;
	}

	@Override
	public String toString() {
		return "Dependency[" + artifactId + ", " + groupId + ", " + version + "]";
	}
}