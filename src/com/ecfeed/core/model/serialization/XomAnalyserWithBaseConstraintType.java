/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.util.List;

import com.ecfeed.core.model.ConstraintType;

import nu.xom.Element;

public abstract class XomAnalyserWithBaseConstraintType extends XomAnalyser {

	public XomAnalyserWithBaseConstraintType() {
		super();
	}

	@Override
	protected ConstraintType getConstraintType(Element element, List<String> errorList) throws ParserException {

		return ConstraintType.EXTENDED_FILTER;
	}

}
