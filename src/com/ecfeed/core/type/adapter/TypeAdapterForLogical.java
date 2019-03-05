/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.type.adapter;

import com.ecfeed.core.utils.SimpleTypeHelper;

public class TypeAdapterForLogical extends TypeAdapterForBoolean {

	@Override
	public String getMyTypeName() {
		return SimpleTypeHelper.TYPE_NAME_LOGICAL;
	}
	
	@Override
	public String getDefaultValue() {
		return SimpleTypeHelper.DEFAULT_EXPECTED_LOGICAL_VALUE;
	}
	
	@Override
	protected void reportRuntimeException(String value) {
		TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, SimpleTypeHelper.TYPE_NAME_LOGICAL);
	}

}
