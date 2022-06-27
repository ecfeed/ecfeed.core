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

	public void addItem(ParameterConversionItem parameterConversionItem) {

		fParameterConversionItems.add(parameterConversionItem);
	}

	public List<ParameterConversionItem> createSortedCopyOfConversionItems() {

		List<ParameterConversionItem> sortedConversionItems = new ArrayList<>(fParameterConversionItems);

		Comparator<ParameterConversionItem> comparator = new Comparator<ParameterConversionItem>() {

			@Override
			public int compare(ParameterConversionItem leftItem, ParameterConversionItem rightItem) {

				int leftItemTypeSortOrder = leftItem.getSrcPart().getTypeSortOrder();
				int rightItemTypeSortOrder = rightItem.getSrcPart().getTypeSortOrder();

				if (rightItemTypeSortOrder > leftItemTypeSortOrder) {
					return 1;
				}

				if (rightItemTypeSortOrder < leftItemTypeSortOrder) {
					return -1;
				}

				int leftItemSortOrder = leftItem.getSrcPart().getSortOrder();
				int rightItemSortOrder = rightItem.getSrcPart().getSortOrder();

				if (rightItemSortOrder > leftItemSortOrder) {
					return 1;
				}

				if (rightItemSortOrder < leftItemSortOrder) {
					return -1;
				}

				return rightItem.getSrcPart().getName().compareTo(leftItem.getSrcPart().getName());
			}
		};

		Collections.sort(sortedConversionItems, comparator);

		return sortedConversionItems;
	}

	public List<String> getNamesOfSrcItems() {

		List<String> itemNames = new ArrayList<String>();

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			ParameterConversionItem conversionItem = fParameterConversionItems.get(index);

			String itemName = conversionItem.getSrcPart().getName();

			itemNames.add(itemName);
		}

		return itemNames;
	}

	public List<String> getNamesOfDstItems() {

		List<String> itemNames = new ArrayList<String>();

		int size = fParameterConversionItems.size();

		for (int index = 0; index < size; index++) {

			ParameterConversionItem conversionItem = fParameterConversionItems.get(index);

			String itemName = conversionItem.getDstPart().getName();

			itemNames.add(itemName);
		}

		return itemNames;
	}

	//	public void removeItem(ParameterConversionItemForChoice conversionItemToFind) {
	//
	//		int index = findConversionItem(conversionItemToFind);
	//
	//		fParameterConversionItems.remove(index);
	//	}

	public int findConversionItem(ParameterConversionItem conversionItemToFind) {

		int index = 0;

		for (ParameterConversionItem conversionItem : fParameterConversionItems) {

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

	public ParameterConversionItem getItem(int index) {

		return fParameterConversionItems.get(index);
	}

	public void clear() {
		fParameterConversionItems.clear();
	}

//	public void setSrcName(String srcName, int index) {
//
//		ParameterConversionItem conversionItem = fParameterConversionItems.get(index);
//		conversionItem.setSrcName(srcName);
//	}
//
//	public void setDstName(String dstName, int index) {
//
//		ParameterConversionItem conversionItem = fParameterConversionItems.get(index);
//		conversionItem.setDstName(dstName);
//	}

}
