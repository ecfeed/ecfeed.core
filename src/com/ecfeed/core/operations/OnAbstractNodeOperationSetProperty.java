/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.operations;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnAbstractNodeOperationSetProperty extends AbstractModelOperation {

	private NodePropertyDefs.PropertyId fPropertyId;
	private String fNewValue;
	private IAbstractNode fAbstractNode;	

	private String fOriginalValue;	

	public OnAbstractNodeOperationSetProperty(
			NodePropertyDefs.PropertyId propertyId, 
			String value,
			IAbstractNode abstractNode,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_PROPERTY, extLanguageManager);
		
		fPropertyId = propertyId;
		fNewValue = value;
		fAbstractNode = abstractNode;

		fOriginalValue = abstractNode.getPropertyValue(fPropertyId);
	}

	@Override
	public void execute() {
		
		setOneNodeToSelect(fAbstractNode);
		fAbstractNode.setPropertyValue(fPropertyId, fNewValue);
		
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnAbstractNodeOperationSetProperty(fPropertyId, fOriginalValue, fAbstractNode, getExtLanguageManager());
	}

}
