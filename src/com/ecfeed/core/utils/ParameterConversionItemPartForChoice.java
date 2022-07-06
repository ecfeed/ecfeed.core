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

import com.ecfeed.core.model.ChoiceNode;

public class ParameterConversionItemPartForChoice extends ParameterConversionItemPart {

	private ChoiceNode fChoiceNode;

	public ParameterConversionItemPartForChoice(ChoiceNode choiceNode) {
		super(choiceNode.getName());

		fChoiceNode = choiceNode;
	}

	@Override
	public Integer getTypeSortOrder() {
		return 0;
	}

	@Override
	public boolean isMatch(IParameterConversionItemPart otherPart) {

		if (!(otherPart instanceof ParameterConversionItemPartForChoice)) {
			return false;
		}

		ChoiceNode choiceNode = getChoiceNode();
		ChoiceNode otherChoiceNode = ((ParameterConversionItemPartForChoice)otherPart).getChoiceNode();

		if (choiceNode.equals(otherChoiceNode)) {
			return true;
		}

		return false;
	}

	public ChoiceNode getChoiceNode() {
		return fChoiceNode;
	}

	@Override
	public ItemPartType getType() {
		return IParameterConversionItemPart.ItemPartType.CHOICE;
	}

	@Override
	public String getDescription() {
		return super.getDescription(ItemPartType.CHOICE.getCode());
	}

	@Override
	public IParameterConversionItemPart makeClone() {
		
		ParameterConversionItemPartForChoice clone = 
				new ParameterConversionItemPartForChoice(fChoiceNode);
		
		return clone;
	}

}

