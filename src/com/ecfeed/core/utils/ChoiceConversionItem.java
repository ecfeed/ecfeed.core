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

public class ChoiceConversionItem {

	private String fChoiceSrcName;
	private String fChoiceDstName;
	private ChoiceConversionOperation fChoiceConversionOperation;
	private String fConstraintsContainingSrcChoice;

	public ChoiceConversionItem(
			String choiceSrcName, 
			ChoiceConversionOperation choiceConversionOperation, 
			String choiceDstName,
			String constraintsContainingSrcChoice) {

		fChoiceSrcName = choiceSrcName;
		fChoiceConversionOperation = choiceConversionOperation;
		fChoiceDstName = choiceDstName;

		fConstraintsContainingSrcChoice = constraintsContainingSrcChoice;
	}


	public ChoiceConversionOperation getOperation() {
		return fChoiceConversionOperation;
	}

	public String getSrcName() {
		return fChoiceSrcName;
	}

	public String getDstName() {
		return fChoiceDstName;
	}

	public String getConstraintsContainingSrcChoice() {
		return fConstraintsContainingSrcChoice;
	}

	public void setSrcName(String srcName) {
		fChoiceSrcName = srcName;
	}

	public void setDstName(String dstName) {
		fChoiceDstName = dstName;
	}

	public boolean isMatch(ChoiceConversionItem otherItem) {

		if (!fChoiceSrcName.equals(otherItem.getSrcName())) {
			return false;
		}

		if (fChoiceConversionOperation != otherItem.getOperation()) {
			return false;
		}

		if (!fChoiceDstName.equals(otherItem.getDstName())) {
			return false;
		}

		return true;
	}

}
