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

public class ParameterConversionDefinition {

	private List<ParameterConversionItem> fParameterConversionItems;

	public ParameterConversionDefinition() {
		fParameterConversionItems = new ArrayList<>();
	}

	public void addItem(
			String sourceChoiceQualifiedName, 
			ChoiceConversionOperation choiceConversionOperation,
			String dstChoiceQualifiedName,
			String constraintsContainingSrcChoice) {

		ParameterConversionItem choiceConversionItem1 = 
				new ParameterConversionItem(
						sourceChoiceQualifiedName,
						choiceConversionOperation,
						dstChoiceQualifiedName,
						constraintsContainingSrcChoice);

		fParameterConversionItems.add(choiceConversionItem1);
	}

	public List<ParameterConversionItem> createSortedCopyOfConversionItems() {

		List<ParameterConversionItem> sortedChoiceConversionItems = new ArrayList<>(fParameterConversionItems);

		Comparator<ParameterConversionItem> comparator = new Comparator<ParameterConversionItem>() {

			@Override
			public int compare(ParameterConversionItem leftItem, ParameterConversionItem rightItem) {

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

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			ParameterConversionItem choiceConversionItem = fParameterConversionItems.get(index);

			String choiceName = choiceConversionItem.getSrcName();

			choiceNames.add(choiceName);
		}

		return choiceNames;
	}

	public List<String> getDstChoiceNames() {

		List<String> choiceNames = new ArrayList<String>();

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			ParameterConversionItem choiceConversionItem = fParameterConversionItems.get(index);

			String choiceName = choiceConversionItem.getDstName();

			choiceNames.add(choiceName);
		}

		return choiceNames;
	}

	private int getChoiceLevel(String choiceName) {

		return StringHelper.countOccurencesOfChar(choiceName, ':');
	}

	public void removeItem(ParameterConversionItem choiceConversionItemToFind) {

		int index = findConversionItem(choiceConversionItemToFind);

		fParameterConversionItems.remove(index);
	}

	public int findConversionItem(ParameterConversionItem choiceConversionItemToFind) {

		int index = 0;

		for (ParameterConversionItem choiceConversionItem : fParameterConversionItems) {

			if (choiceConversionItem.isMatch(choiceConversionItemToFind)) {
				return index;
			}

			index++;
		}

		return -1;
	}

	public int getSize() {

		return fParameterConversionItems.size();
	}

	public ParameterConversionItem getItem(int index) {

		return fParameterConversionItems.get(index);
	}

	public void clear() {
		fParameterConversionItems.clear();
	}
	
	public void setSrcName(String srcName, int index) {
		
		ParameterConversionItem choiceConversionItem = fParameterConversionItems.get(index);
		choiceConversionItem.setSrcName(srcName);
	}
	
	public void setDstName(String dstName, int index) {
		
		ParameterConversionItem choiceConversionItem = fParameterConversionItems.get(index);
		choiceConversionItem.setDstName(dstName);
	}
	
}
