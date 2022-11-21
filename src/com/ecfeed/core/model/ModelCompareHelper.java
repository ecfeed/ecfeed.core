/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.Collection;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;

public class ModelCompareHelper {

	public static void compareModels(RootNode model1, RootNode model2) {
	}

	public static void compareSizes(Collection<? extends Object> collection1, Collection<? extends Object> collection2) {
		if(collection1.size() != collection2.size()){
			ExceptionHelper.reportRuntimeException("Different sizes of collections");
		}
	}

	public static void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			ExceptionHelper.reportRuntimeException("Different names: " + name + ", " + name2);
		}
	}

	public static void compareTypes(String type1, String type2) {

		if(type1.equals(type2) == false){
			ExceptionHelper.reportRuntimeException("Different types: " + type1 + ", " + type2);
		}
	}

	public static void compareChoices(ChoiceNode choice1, ChoiceNode choice2) {

		ModelCompareHelper.compareNames(choice1.getName(), choice2.getName());
		compareValues(choice1.getValueString(),choice2.getValueString());
		compareLabels(choice1.getLabels(), choice2.getLabels());
		assertIntegersEqual(choice1.getChoices().size(), choice2.getChoices().size(), "Length of choices list differs.");
		for(int i = 0; i < choice1.getChoices().size(); i++){
			compareChoices(choice1.getChoices().get(i), choice2.getChoices().get(i));
		}
	}
	
	public static void compareValues(Object value1, Object value2) {
		
		boolean result = true;
		if(value1 == null){
			result = (value2 == null);
		}
		else{
			result = value1.equals(value2);
		}
		if(!result){
			ExceptionHelper.reportRuntimeException("Value " + value1 + " differ from " + value2);
		}
	}

	public static void compareLabels(Set<String> labels, Set<String> labels2) {
		assertIsTrue(labels.size() == labels2.size(), "Sizes of labels should be equal.");
		for(String label : labels){
			assertIsTrue(labels2.contains(label), "Label2 should contain label1");
		}
	}
	
	private static void assertIsTrue(boolean b, String message) {

		if (b == true) {
			return;
		}

		ExceptionHelper.reportRuntimeException("True boolean value expected." + " " + message);
	}
	

	public static void assertIntegersEqual(int size, int size2, String message) {

		if (size == size2) {
			return;
		}

		ExceptionHelper.reportRuntimeException("Integers do not match." + " " + message);
	}
	
}
