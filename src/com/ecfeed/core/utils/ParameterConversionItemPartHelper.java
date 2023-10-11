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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.CompositeParameterNode;

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

	public static String getLabel(IParameterConversionItemPart part) {

		if (!(part instanceof ParameterConversionItemPartForLabel)) {
			return null;
		}

		ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
				(ParameterConversionItemPartForLabel) part;

		String label = parameterConversionItemPartForLabel.getLabel();

		return label;
	}

	public static IParameterConversionItemPart convertRawItemPartToTyped(
			IParameterConversionItemPart itemPart) {

		if (!(itemPart instanceof ParameterConversionItemPartForRaw)) {
			ExceptionHelper.reportRuntimeException("Invalid type of item part.");
		}

		ParameterConversionItemPartForRaw parameterConversionItemPartForRaw = 
				(ParameterConversionItemPartForRaw)itemPart;

		BasicParameterNode basicParameterNode = (BasicParameterNode) itemPart.getParameter();
		CompositeParameterNode linkingContext = itemPart.getLinkingContext();

		String code = parameterConversionItemPartForRaw.getCode();

		if (code.equals(IParameterConversionItemPart.ItemPartType.LABEL.getCode())) {

			ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
					new ParameterConversionItemPartForLabel(
							basicParameterNode, linkingContext, parameterConversionItemPartForRaw.getStr());

			return parameterConversionItemPartForLabel;
		}

		ChoiceNode choiceNode = basicParameterNode.getChoice(itemPart.getStr());

		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice =
				new ParameterConversionItemPartForChoice(basicParameterNode, linkingContext, choiceNode);

		return parameterConversionItemPartForChoice;
	}

}

