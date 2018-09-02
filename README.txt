
ws - an application for workspace management
The recommended way to install ws is using the accompanying (separate repo) installer script.

This project's binary has several main methods:

* com.eriklievaart.ws.boot.Ws
* com.eriklievaart.ws.boot.Snapshot
* com.eriklievaart.ws.boot.PreProcess
* com.eriklievaart.ws.boot.ResolveProject

Ws is an interactive client for defining workspaces and projects.
Ws generates eclipse metadata for the configured structure.
It also generate antastic metadata so that projects can be built simply by selecting the correct target.

Snapshot and ResolveProject are used for dependency resolution.
Snapshot is invoked with a single argument, the location of a jar.
This jar will be stored in the repository as a snapshot.
The artifactId will be the filename after the extension has been dropped.
ResolveProject is invoked with a single argument, the name of a project.
ResolveProject resolves all of the artifacts configured in dependencies.txt.
Snapshots will be updated to the lastest available local version.

PreProcess takes at least one directory as an argument.
The directory will be scanned for java files and PreProcess will apply fixes to the source code.
For example, it ensures java keywords are sorted in the order specified by the JLS.

Ws has been designed to have no compile time or runtime dependencies (excluding tests).
It assumes a specific file structure, which allows it to work with minimal configuration.
Software sources and artifacts are assigned to the following locations:

[user.home]/Development/git => one git repo per project
[user.home]/Development/project => one eclipse project (will be generated) per project
[user.home]/Development/workspace => one eclipse workspace (will be generated) per workspace

Projects can be configured with a minimum of project specific configuration files:

[user.home]/Development/git/[project]/main/config/ant.properties => properties denoting build process
[user.home]/Development/git/[project]/main/config/dependencies.txt => specify dependencies in the format described next


Dependencies
Dependencies are marked in the dependencies.txt file as follows

[header]
[artifactId] [groupId]? [version]?

The header can be any of the following
* compile => required at compile time and at run time
* provided => required at compile time, NOT at run time (may be provided by server)
* run => required at run time, NOT at compile time
* test => only tests may compile and run against this dependency

artifactId, groupId and version are maven equivalents and will be downloaded from maven central if available.
The groupId and the version are optional if the artifact is already registered in the repo.
Projects that are not in remote repositories will be assigned the groupId @local.
Snapshots are assigned the version @snapshot

sample dependencies.txt:

[compile]
toolkit @local @snapshot
commons-io commons-io 2.4
guice com.google.inject 1.0

[test]
junit junit 4.7


Interactive mode
Simply start the jar from the command line:

java -jar ws.jar

The interactive client is easy to use as it prints all available commands.
Here is how to define a workspace named "foo" with two projects name "bar" and "baz":

define foo bar baz

Use info to list the projects in a workspace:

info foo

Project links can later be added and removed using the link and unlink commands:

unlink foo bar
link foo bar

To remove a workspace, use the trash command

trash foo

Trash is safe to use, it only deletes the reference to the workspace.
It does not delete any files on disk.
Generating Eclipse metadata is done automatically on changes, but can be manually invoked:

generate foo

Invoke ws with a single argument 'generate' to generate metadata for all configured workspaces.

java -jar ws.jar generate


