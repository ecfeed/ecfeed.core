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

	private List<IParameterConversionItem> fParameterConversionItems;

	public ParameterConversionDefinition() {
		fParameterConversionItems = new ArrayList<>();
	}

	public void addItem(
			String sourceItemQualifiedName, 
			String dstItemQualifiedName,
			String constraintsContainingSrcItem) {

		ParameterConversionItemForChoice parameterConversionItem1 = 
				new ParameterConversionItemForChoice(
						sourceItemQualifiedName,
						dstItemQualifiedName,
						constraintsContainingSrcItem);

		fParameterConversionItems.add(parameterConversionItem1);
	}

	public List<IParameterConversionItem> createSortedCopyOfConversionItems() {

		List<IParameterConversionItem> sortedConversionItems = new ArrayList<>(fParameterConversionItems);

		Comparator<IParameterConversionItem> comparator = new Comparator<IParameterConversionItem>() {

			@Override
			public int compare(IParameterConversionItem leftItem, IParameterConversionItem rightItem) {

				int leftItemTypeLevel = leftItem.getItemTypeLevel();
				int rightItemTypeLevel = rightItem.getItemTypeLevel();
				
				if (rightItemTypeLevel > leftItemTypeLevel) {
					return 1;
				}

				if (rightItemTypeLevel < leftItemTypeLevel) {
					return -1;
				}
				
				int leftItemLevel = leftItem.getItemLevel();
				int rightItemLevel = rightItem.getItemLevel();


				if (rightItemLevel > leftItemLevel) {
					return 1;
				}

				if (rightItemLevel < leftItemLevel) {
					return -1;
				}

				return 0;
			}
		};

		Collections.sort(sortedConversionItems, comparator);

		return sortedConversionItems;
	}

	public List<String> getNamesOfSrcItems() {

		List<String> itemNames = new ArrayList<String>();

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			IParameterConversionItem conversionItem = fParameterConversionItems.get(index);

			String itemName = conversionItem.getSrcName();

			itemNames.add(itemName);
		}

		return itemNames;
	}

	public List<String> getNamesOfDstItems() {

		List<String> itemNames = new ArrayList<String>();

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			IParameterConversionItem conversionItem = fParameterConversionItems.get(index);

			String itemName = conversionItem.getDstName();

			itemNames.add(itemName);
		}

		return itemNames;
	}

	public void removeItem(ParameterConversionItemForChoice conversionItemToFind) {

		int index = findConversionItem(conversionItemToFind);

		fParameterConversionItems.remove(index);
	}

	public int findConversionItem(IParameterConversionItem conversionItemToFind) {

		int index = 0;

		for (IParameterConversionItem conversionItem : fParameterConversionItems) {

			if (conversionItem.isMatch(conversionItemToFind)) {
				return index;
			}

			index++;
		}

		return -1;
	}

	public int getSize() {

		return fParameterConversionItems.size();
	}

	public IParameterConversionItem getItem(int index) {

		return fParameterConversionItems.get(index);
	}

	public void clear() {
		fParameterConversionItems.clear();
	}

	public void setSrcName(String srcName, int index) {

		IParameterConversionItem conversionItem = fParameterConversionItems.get(index);
		conversionItem.setSrcName(srcName);
	}

	public void setDstName(String dstName, int index) {

		IParameterConversionItem conversionItem = fParameterConversionItems.get(index);
		conversionItem.setDstName(dstName);
	}

}
