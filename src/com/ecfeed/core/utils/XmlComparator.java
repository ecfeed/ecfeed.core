/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlComparator {


	public static boolean areXmlsEqual(String xml1, String xml2) {

		IntegerHolder position1 = new IntegerHolder(0);
		IntegerHolder position2 = new IntegerHolder(0);

		for(;;) {

			String tag1 = getTag(xml1, position1);
			String tag2 = getTag(xml2, position2);

			if (!StringHelper.isEqual(tag1, tag2)) {
				return false; // TODO MO-RE report exception with tags which differ
			}

			if (tag1 == null || tag2 == null) {
				break;
			}
		}

		return true;
	}

	private static String getTag(String xml, IntegerHolder inOutFromPosition) {

		if (xml == null) {
			return null;
		}

		int begIndex = xml.indexOf("<", inOutFromPosition.get());

		if (begIndex == -1) {
			return null;
		}

		int endIndex = xml.indexOf(">", begIndex + 1);

		if (endIndex == -1) {
			return null;
		}

		String tag = xml.substring(begIndex, endIndex + 1);

		inOutFromPosition.set(endIndex + 1);

		return tag;
	}

	public static boolean containsConsecutiveTags(String xml, String stringWithTagsToFind) {

		List<String> tagsFromXml = convertXmlToListOfTags(xml);
		List<String> tagsToFind = convertXmlToListOfTags(stringWithTagsToFind);;

		int indexOfSublist = Collections.indexOfSubList(tagsFromXml, tagsToFind);

		if (indexOfSublist < 0) {
			return false;
		}

		return true;
	}

	private static List<String> convertXmlToListOfTags(String xml) {

		List<String> result = new ArrayList<>();

		IntegerHolder position = new IntegerHolder(0);

		for(;;) {

			String tag = getTag(xml, position);

			if (tag == null) {
				break;
			}

			result.add(tag);
		}

		return result;
	}


}
