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
			String dstChoiceQualifiedName,
			String constraintsContainingSrcChoice) {

		ChoiceConversionItem choiceConversionItem1 = 
				new ChoiceConversionItem(
						sourceChoiceQualifiedName,
						choiceConversionOperation,
						dstChoiceQualifiedName,
						constraintsContainingSrcChoice);

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

	public List<String> getSrcChoiceNames() {

		List<String> choiceNames = new ArrayList<String>();

		int size = fChoiceConversionItems.size();

		for (int index = 0; index < size; index++) {

			ChoiceConversionItem choiceConversionItem = fChoiceConversionItems.get(index);

			String choiceName = choiceConversionItem.getSrcName();

			choiceNames.add(choiceName);
		}

		return choiceNames;
	}

	public List<String> getDstChoiceNames() {

		List<String> choiceNames = new ArrayList<String>();

		int size = fChoiceConversionItems.size();

		for (int index = 0; index < size; index++) {

			ChoiceConversionItem choiceConversionItem = fChoiceConversionItems.get(index);

			String choiceName = choiceConversionItem.getDstName();

			choiceNames.add(choiceName);
		}

		return choiceNames;
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

	public void clear() {
		fChoiceConversionItems.clear();
	}
	
	public void setSrcName(String srcName, int index) {
		
		ChoiceConversionItem choiceConversionItem = fChoiceConversionItems.get(index);
		choiceConversionItem.setSrcName(srcName);
	}
	
	public void setDstName(String dstName, int index) {
		
		ChoiceConversionItem choiceConversionItem = fChoiceConversionItems.get(index);
		choiceConversionItem.setDstName(dstName);
	}
	
}
