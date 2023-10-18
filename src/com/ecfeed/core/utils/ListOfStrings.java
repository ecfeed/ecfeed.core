package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.List;

public class ListOfStrings {

	List<String> fStrings;

	public ListOfStrings() {

		fStrings = new ArrayList<>();
	}

	@Override
	public String toString() {
		return fStrings.toString();
	}

	public void add(String string) {

		fStrings.add(string);
	}

	public boolean isEmpty() {

		if (fStrings.size() == 0) {
			return true;
		}

		return false;
	}

	public void addIfUnique(String string) {

		if (fStrings.contains(string)) {
			return;
		}

		fStrings.add(string);
	}

	public List<String> getCollectionOfStrings() {

		return fStrings;
	}

	public String getFirstString() {

		return fStrings.get(0);
	}

	public boolean contains(String strg) {

		if (fStrings.contains(strg)) {
			return true;
		}

		return false;
	}

	public String getErrorsAsOneString() {

		String text = "";

		for (String error : fStrings) {

			text += error + "\n";
		}

		return text;
	}

	public String getAsStringWithSeparators(String separator) {

		int listSize = fStrings.size();
		int lastIndex = listSize - 1;

		String convertedString = "";

		for (int index = 0; index < listSize; index++) {

			convertedString += fStrings.get(index);

			if (index < lastIndex) {
				convertedString += separator;
			}
		}

		return convertedString;
	}

}
