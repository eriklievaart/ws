package com.eriklievaart.ws.osgi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.SetTool;
import com.eriklievaart.toolkit.mock.BombSquad;

public class ManifestGeneratorU {

	@Test
	public void manifestPresent() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.setManifestAlreadyPresent(true);
		Optional<String> optional = ManifestGenerator.generateManifestBody(source);
		Assert.assertFalse(optional.isPresent());
	}

	@Test
	public void manifestMissing() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.setManifestAlreadyPresent(false);
		Optional<String> optional = ManifestGenerator.generateManifestBody(source);
		Assert.assertTrue(optional.isPresent());
	}

	@Test
	public void activatorPresent() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.setActivatorPresent(true);
		source.setActivatorClass("com.example.Activator");
		String result = ManifestGenerator.generateManifestBody(source).get();
		Assertions.assertThat(result).contains("Bundle-Activator: com.example.Activator");
	}

	@Test
	public void activatorMissing() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.setActivatorPresent(false);
		source.setActivatorClass("com.example.Activator");
		String result = ManifestGenerator.generateManifestBody(source).get();
		Assertions.assertThat(result).doesNotContain("com.example.Activator");
	}

	@Test
	public void getExportString() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.addApiPackage("com.eriklievaart.project.api");
		String export = ManifestGenerator.getExportString(source).get();
		Assertions.assertThat(export).isEqualTo("com.eriklievaart.project.api");
	}

	@Test
	public void getExportStringMultiLine() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.addApiPackage("com.eriklievaart.project.api");
		source.addApiPackage("com.eriklievaart.project.api.event");
		String export = ManifestGenerator.getExportString(source).get();
		Assertions.assertThat(export).isEqualTo("com.eriklievaart.project.api,\n com.eriklievaart.project.api.event");
	}

	@Test
	public void generateManifestBodyExport() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.addApiPackage("com.example.api");
		String result = ManifestGenerator.generateManifestBody(source).get();
		Assertions.assertThat(result).contains("com.example.api");
	}

	@Test
	public void generateManifestBodyExportMultiple() throws Exception {
		DummyManifestSource source = new DummyManifestSource();
		source.addApiPackage("com.example.api");
		source.addApiPackage("com.example.api.event");
		String result = ManifestGenerator.generateManifestBody(source).get();
		Assertions.assertThat(result).contains("com.example.api", "com.example.api.event");
	}

	@Test
	public void getImportString() throws Exception {
		String api = "com.eriklievaart.project.api";
		String base = "com.eriklievaart.other";
		Assertions.assertThat(ManifestGenerator.getImportString(Arrays.asList(api), base).get()).isEqualTo(api);
	}

	@Test
	public void getImportStringMultiLine() throws Exception {
		List<String> api = Arrays.asList("javax.swing", "javax.swing.event");
		String base = "com.eriklievaart.other";
		String expect = "javax.swing,\n javax.swing.event";
		Assertions.assertThat(ManifestGenerator.getImportString(api, base).get()).isEqualTo(expect);
	}

	@Test
	public void getImportStringFilterBundle() throws Exception {
		List<String> imports = ListTool.of("com.eriklievaart.project.api");
		String base = "com.eriklievaart.project";
		Assert.assertFalse(ManifestGenerator.getImportString(imports, base).isPresent());
	}

	@Test
	public void getImportStringFilterJava() throws Exception {
		List<String> imports = ListTool.of("java.util");
		String base = "com.eriklievaart.project";
		Assert.assertFalse(ManifestGenerator.getImportString(imports, base).isPresent());
	}

	@Test
	public void validateImportsTrailingApi() {
		DummyManifestSource source = new DummyManifestSource();
		source.setBasePackage("com.eriklievaart.project.bundle");
		source.addImport("com.eriklievaart.project.api");
		ManifestGenerator.validateImports(source);
	}

	@Test
	public void validateImportsNestedApi() {
		DummyManifestSource source = new DummyManifestSource();
		source.setBasePackage("com.eriklievaart.project.bundle");
		source.addImport("com.eriklievaart.project.api.nested");
		ManifestGenerator.validateImports(source);
	}

	@Test
	public void validateImportsInternalFail() {
		DummyManifestSource source = new DummyManifestSource();
		source.setBasePackage("com.eriklievaart.project.bundle");
		source.addImport("com.eriklievaart.project.internal");

		BombSquad.diffuse(RuntimeException.class, "invalid import", () -> {
			ManifestGenerator.validateImports(source);
		});
	}

	@Test
	public void validateImportsSameBundle() {
		DummyManifestSource source = new DummyManifestSource();
		source.setBasePackage("com.eriklievaart.project.bundle");
		source.addImport("com.eriklievaart.project.bundle");
		ManifestGenerator.validateImports(source);
	}

	@Test
	public void validateImportsExternal() {
		DummyManifestSource source = new DummyManifestSource();
		source.setBasePackage("com.eriklievaart.project.bundle");
		source.addImport("com.example");
		ManifestGenerator.validateImports(source);
	}

	@Test
	public void generateManifestBodyFull() throws IOException {
		Optional<String> result = ManifestGenerator.generateManifestBody(new ManifestSource() {
			@Override
			public boolean isManifestAlreadyPresent() {
				return false;
			}

			@Override
			public boolean isActivatorPresent() {
				return true;
			}

			@Override
			public File getManifestFile() {
				return null;
			}

			@Override
			public File getActivatorFile() {
				return null;
			}

			@Override
			public String getBasePackage() {
				return "com.eriklievaart.q.ui";
			}

			@Override
			public String getActivatorClass() {
				return "com.eriklievaart.q.ui.Activator";
			}

			@Override
			public Set<String> getApiPackages() {
				return SetTool.of("com.eriklievaart.q.ui.api");
			}

			@Override
			public Set<ImportStatement> getImports() {
				return SetTool.of(new ImportStatement("import com.eriklievaart.api.QUi", null));
			}
		});
		StringBuilder expect = new StringBuilder();
		expect.append("Bundle-Activator: com.eriklievaart.q.ui.Activator\n");
		expect.append("Export-Package: com.eriklievaart.q.ui.api\n");
		expect.append("Import-Package: import com.eriklievaart.api.QUi\n");
		Assertions.assertThat(result.get()).isEqualTo(expect.toString());
	}
}
