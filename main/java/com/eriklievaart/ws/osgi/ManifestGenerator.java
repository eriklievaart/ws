package com.eriklievaart.ws.osgi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.eriklievaart.ws.toolkit.io.FileUtils;

public class ManifestGenerator {

	private static final String NL = "\n";
	private BundleMetadata metadata;

	public ManifestGenerator(BundleMetadata metadata) {
		this.metadata = metadata;
	}

	public void generateManifestFiles() {
		metadata.eachBundle(bundle -> {
			generateManifest(bundle);
		});
		generateManifest(metadata);
	}

	private void generateManifest(ManifestSource source) {
		validateImports(source);
		generateManifestBody(source).ifPresent(data -> FileUtils.writeStringToFile(data, source.getManifestFile()));
	}

	static void validateImports(ManifestSource source) {
		for (ImportStatement pkg : source.getImports()) {
			boolean lievaart = pkg.startsWith("com.eriklievaart");
			boolean skipOwnBundle = pkg.startsWith(source.getBasePackage());

			if (lievaart && !skipOwnBundle && !pkg.isApi()) {
				throw new RuntimeException("Invalid import! Not an api package: " + pkg);
			}
		}
	}

	static Optional<String> generateManifestBody(ManifestSource source) {
		if (source.isManifestAlreadyPresent()) {
			return Optional.empty();
		}
		StringBuilder builder = new StringBuilder();
		if (source.isActivatorPresent()) {
			builder.append("Bundle-Activator: ").append(source.getActivatorClass()).append(NL);
		}
		Optional<String> exportOptional = getExportString(source);
		Optional<String> importOptional = getImportString(source);
		exportOptional.ifPresent(exports -> builder.append("Export-Package: ").append(exports).append(NL));
		if (exportOptional.isPresent() || importOptional.isPresent()) {
			builder.append("Import-Package: ").append(mergePackages(exportOptional, importOptional)).append(NL);
		}
		return Optional.of(builder.toString());
	}

	private static String mergePackages(Optional<String> exports, Optional<String> imports) {
		List<String> available = new ArrayList<>();
		exports.ifPresent(string -> available.add(string));
		imports.ifPresent(string -> available.add(string));
		return String.join(",", available);
	}

	static Optional<String> getImportString(ManifestSource source) {
		List<String> imports = source.getImports().stream().map(i -> i.getImport()).collect(Collectors.toList());
		return getImportString(imports, source.getBasePackage());
	}

	static Optional<String> getImportString(List<String> imports, String base) {
		removeUnnecessaryImports(imports, base);

		if (imports.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(String.join(",\n ", new PackageSorter().sortPackages(imports)));
	}

	private static void removeUnnecessaryImports(List<String> imports, String pkg) {
		Iterator<String> iterator = imports.iterator();
		while (iterator.hasNext()) {
			String type = iterator.next();
			if (type.startsWith("java.") || type.startsWith(pkg)) {
				iterator.remove();
			}
		}
	}

	static Optional<String> getExportString(ManifestSource source) {
		Set<String> packages = source.getApiPackages();
		if (packages.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(String.join(",\n ", new PackageSorter().sortPackages(packages)));
	}
}
