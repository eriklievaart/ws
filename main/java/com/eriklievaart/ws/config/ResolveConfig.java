package com.eriklievaart.ws.config;

import java.util.Objects;

import com.eriklievaart.ws.config.dependency.LibType;

public class ResolveConfig {

	private static final String PROPERTY = "skip.test.compile";
	private final boolean resolveTest = !Objects.equals(System.getProperty(PROPERTY), "true");

	public boolean isComplete() {
		return resolveTest;
	}

	public boolean resolve(LibType lib) {
		if (resolveTest || lib != LibType.TEST) {
			return true;
		}
		System.out.println("*warning* not resolving test libs due to property " + PROPERTY);
		return false;
	}
}
