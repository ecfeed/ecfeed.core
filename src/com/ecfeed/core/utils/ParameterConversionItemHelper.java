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

public class ParameterConversionItemHelper {

	public static ParameterConversionItem convertRawItemToTypedItem(
			ParameterConversionItem rawItem) {

		IParameterConversionItemPart convertedSrcPart = 
				ParameterConversionItemPartHelper.convertRawItemPartToTyped(rawItem.getSrcPart());

		IParameterConversionItemPart convertedDstPart = 
				ParameterConversionItemPartHelper.convertRawItemPartToTyped(rawItem.getDstPart());

		ParameterConversionItem result = 
				new ParameterConversionItem(
						convertedSrcPart, 
						convertedDstPart, 
						rawItem.isRandomized(), 
						rawItem.getConstraintsContainingSrcItem());

		return result;
	}

}

