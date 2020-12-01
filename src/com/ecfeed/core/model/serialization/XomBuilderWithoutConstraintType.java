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

import com.ecfeed.core.model.ConstraintType;

import nu.xom.Element;

public abstract class XomBuilderWithoutConstraintType extends XomBuilder {

	XomBuilderWithoutConstraintType(SerializatorParams serializatorParams) {

		super(serializatorParams);
	}

	@Override
	public void addConstraintTypeAttribute(ConstraintType constraintType, Element targetConstraintElement) {
	}

}
