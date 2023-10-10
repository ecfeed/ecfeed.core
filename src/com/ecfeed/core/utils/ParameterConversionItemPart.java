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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;

public abstract class ParameterConversionItemPart implements IParameterConversionItemPart {

	private String fStr;
	private AbstractParameterNode fAbstractParameterNode;
	private CompositeParameterNode fLinkingContext;

	public abstract Integer getTypeSortOrder();

	public ParameterConversionItemPart(
			AbstractParameterNode abstractParameterNode,
			CompositeParameterNode linkingContext,
			String str) {

		if (str == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src name should not be empty.");
		}

		fAbstractParameterNode = abstractParameterNode; 
		fStr = str;
	}

	@Override
	public String toString() {

		if (fLinkingContext == null) {
			return 
					getParameter().getName() + 
					SignatureHelper.SIGNATURE_NAME_SEPARATOR + 
					getStr() + 
					"[" + getTypeDescription() + "]";
		}
		
		return 
				getLinkingContext() + "->" + getParameter().getName() + // XYX define constant for -> 
				SignatureHelper.SIGNATURE_NAME_SEPARATOR + 
				getStr() + 
				"[" + getTypeDescription() + "]";
		
	}

	@Override
	public AbstractParameterNode getParameter() {
		return fAbstractParameterNode;
	}

	@Override
	public CompositeParameterNode getLinkingContext() {
		return fLinkingContext;
	}
	
	@Override
	public String getStr() {
		return fStr;
	}

	@Override
	public void setName(String name) {
		fStr = name;
	}

	@Override
	public Integer getSortOrder() {

		return StringHelper.countOccurencesOfChar(fStr, ':');
	}

	@Override
	public int compareTo(IParameterConversionItemPart other) {

		return getStr().compareTo(other.getStr());
	}

	@Override
	public String getTypeDescription() {

		ItemPartType itemPartType = getType();

		String typeDescription = ItemPartType.convertCodeToDescription(itemPartType.getCode());

		return typeDescription;
	}

	@Override 
	public String getDescription() {

		return fStr + "[" + getTypeDescription() + "]";
	}

	public static boolean isMatch(IParameterConversionItemPart part1, IParameterConversionItemPart part2) {

		if (part1 == null && part2 == null) {
			return true;
		}

		if (part1 == null && part2 != null) {
			return false;
		}

		if (part1 != null && part2 == null) {
			return false;
		}

		if ( part1.getType() != part2.getType()) {
			return false;
		}

		if (!StringHelper.isEqual(part1.getStr(), part2.getStr())) {
			return false;
		}

		return true;
	}
}

