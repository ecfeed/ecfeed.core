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
import java.util.Comparator;
import java.util.List;

public class ChoiceConversionList {

	private List<ChoiceConversionItem> fChoiceConversionItems;

	public ChoiceConversionList() {
		fChoiceConversionItems = new ArrayList<>();
	}

	public void addItem(
			String sourceChoiceQualifiedName, 
			ChoiceConversionOperation choiceConversionOperation,
			String dstChoiceQualifiedName) {

		ChoiceConversionItem choiceConversionItem1 = 
				new ChoiceConversionItem(
						sourceChoiceQualifiedName,
						choiceConversionOperation,
						dstChoiceQualifiedName);

		fChoiceConversionItems.add(choiceConversionItem1);
	}

	public List<ChoiceConversionItem> createSortedCopyOfConversionItems() {

		List<ChoiceConversionItem> sortedChoiceConversionItems = new ArrayList<>(fChoiceConversionItems);

		Comparator<ChoiceConversionItem> comparator = new Comparator<ChoiceConversionItem>() {

			@Override
			public int compare(ChoiceConversionItem leftItem, ChoiceConversionItem rightItem) {

				int leftItemLevel = getChoiceLevel(leftItem.getSrcName());
				int rightItemLevel = getChoiceLevel(rightItem.getSrcName());


				if (rightItemLevel > leftItemLevel) {
					return 1;
				}

				if (rightItemLevel < leftItemLevel) {
					return -1;
				}

				return 0;
			}
		};

		Collections.sort(sortedChoiceConversionItems, comparator);

		return sortedChoiceConversionItems;
	}

	private int getChoiceLevel(String choiceName) {

		return StringHelper.countOccurencesOfChar(choiceName, ':');
	}

	public void removeItem(ChoiceConversionItem choiceConversionItemToFind) {

		int index = findConversionItem(choiceConversionItemToFind);

		fChoiceConversionItems.remove(index);
	}

	public int findConversionItem(ChoiceConversionItem choiceConversionItemToFind) {

		int index = 0;

		for (ChoiceConversionItem choiceConversionItem : fChoiceConversionItems) {

			if (choiceConversionItem.isMatch(choiceConversionItemToFind)) {
				return index;
			}

			index++;
		}

		return -1;
	}

	public int getSize() {
		
		return fChoiceConversionItems.size();
	}
	
	public ChoiceConversionItem getItem(int index) {
		
		return fChoiceConversionItems.get(index);
	}
}
