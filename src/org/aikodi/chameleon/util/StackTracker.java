package org.aikodi.chameleon.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Dirty utility class for debugging.
 * 
 * @author Marko van Dooren
 */
public class StackTracker {

	public static void mark(String string) {
		_labels.add(string);
	}
	
	public static void poll(String string) {
		Util.debug(_labels.contains(string));
	}
	
	private final static Set<String> _labels = new HashSet<>();
}
