package com.eriklievaart.ws.process.index;

public class MockTypeIndex extends TypeIndex {

	public void add(String qualified) {
		LibraryIndex single = new LibraryIndex();
		single.add(qualified);
		super.indexes.add(single);
	}
}
