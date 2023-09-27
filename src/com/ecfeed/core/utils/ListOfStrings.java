package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.List;

public class ListOfStrings {

	List<String> fStrings;

	public ListOfStrings() {

		fStrings = new ArrayList<>();
	}

	public void add(String string) {

		fStrings.add(string);
	}

	public void addIfUnique(String string) {

		if (fStrings.contains(string)) {
			return;
		}

		fStrings.add(string);
	}

	public boolean isEmpty() {

		if (fStrings.size() == 0) {
			return true;
		}

		return false;
	}

	public List<String> getCollectionOfStrings() {

		return fStrings;
	}

	public String getFirstString() {

		return fStrings.get(0);
	}

	public String getErrorsAsText() {

		String text = "";

		for (String error : fStrings) {

			text += error + "\n";
		}

		return text;
	}
}
