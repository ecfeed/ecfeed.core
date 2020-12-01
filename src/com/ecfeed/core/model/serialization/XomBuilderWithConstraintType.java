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

import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTY_ATTRIBUTE_TYPE;

import com.ecfeed.core.model.ConstraintType;

import nu.xom.Attribute;
import nu.xom.Element;

public class XomBuilderWithConstraintType extends XomBuilderVersionWithNewNodeNames {

	XomBuilderWithConstraintType(SerializatorParams serializatorParams) {
		super(serializatorParams);
	}

	@Override
	protected int getModelVersion() {
		return 4;
	}

	@Override
	public void addConstraintTypeAttribute(ConstraintType constraintType, Element targetConstraintElement) {

		String typeDescription = constraintType.getDescription();
		Attribute attributeType = new Attribute(PROPERTY_ATTRIBUTE_TYPE, typeDescription);
		targetConstraintElement.addAttribute(attributeType);
	}
}
