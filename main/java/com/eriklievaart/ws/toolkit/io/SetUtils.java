package com.eriklievaart.ws.toolkit.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetUtils {
	/**
	 * Create a Set from the specified elements.
	 */
	public static <E> Set<E> of(final E... elements) {
		Set<E> set = new HashSet<>();
		set.addAll(Arrays.asList(elements));
		return set;
	}
}
