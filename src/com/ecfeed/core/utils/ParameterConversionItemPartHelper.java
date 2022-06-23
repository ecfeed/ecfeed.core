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

public abstract class ParameterConversionItemPartHelper {

	public static ChoiceNode getChoice(IParameterConversionItemPart part) {
		
		if (!(part instanceof ParameterConversionItemPartForChoice)) {
			return null;
		}
		
		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
				(ParameterConversionItemPartForChoice) part;
		
		ChoiceNode srcChoiceNode = parameterConversionItemPartForChoice.getChoiceNode();
		
		return srcChoiceNode;
	}
	
}

