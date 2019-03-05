package com.ecfeed.core.utils;

import java.util.ArrayList;

public class MessageStack {

	private ArrayList<String> fMessages;

	public MessageStack() {
		fMessages = new ArrayList<String>();
	}

	public void addMessage(String message) {
		fMessages.add(0, message);
	}

	public boolean isEmpty() {
		
		if (fMessages.isEmpty())
			return true;
		
		return false;
	}
	
	public String getLongMessage() {

		StringBuilder sb = new StringBuilder();

		boolean first = true;

		for (String message : fMessages) {

			if (!first) {
				sb.append("  ");
			}

			sb.append(message);
			first = false;
		}

		return sb.toString();
	}

}
