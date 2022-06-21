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

public class ParameterConversionItemForChoice extends ParameterConversionItem {

	public ParameterConversionItemForChoice(
			ChoiceNode srcChoice, 
			ChoiceNode dstChoice,
			String constraintsContainingSrcItem) {

		super(srcChoice.getQualifiedName(), dstChoice.getQualifiedName(), constraintsContainingSrcItem);	
	}

}

