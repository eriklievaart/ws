package com.eriklievaart.ws.toolkit.io;

import java.util.ArrayList;
import java.util.List;

public class LineFilter {

	public static List<String> filterEmptyAndComments(List<String> input){
		List<String> result = new ArrayList<String>();
		for (String line : input) {
			if(line.isEmpty() || line.startsWith("#") || line.trim().isEmpty()) {
				continue;
			}
			result.add(line);
		}
		return result;
	}

}
