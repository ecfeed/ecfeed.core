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

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;

public class TypeAdapterProvider implements ITypeAdapterProvider{

	@SuppressWarnings("rawtypes")
	protected ITypeAdapter<?> getTypeAdapterBaseForUserType(String type) {
		return new TypeAdapterBaseForUserType(type);
	}

	public ITypeAdapter<?> getAdapter(String type){
		if(!JavaTypeHelper.isJavaType(type) && !SimpleTypeHelper.isSimpleType(type)){
			type = TypeAdapterHelper.USER_TYPE;
		}
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return new TypeAdapterForBoolean();
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return new TypeAdapterForByte();
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return new TypeAdapterForChar();
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return new TypeAdapterForDouble();
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return new TypeAdapterForFloat();
		case JavaTypeHelper.TYPE_NAME_INT:
			return new TypeAdapterForInt();
		case JavaTypeHelper.TYPE_NAME_LONG:
			return new TypeAdapterForLong();
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return new TypeAdapterForShort();
		case JavaTypeHelper.TYPE_NAME_STRING:
			return new TypeAdapterForString();
		case SimpleTypeHelper.TYPE_NAME_TEXT:
			return new TypeAdapterForText();
		case SimpleTypeHelper.TYPE_NAME_NUMBER:
			return new TypeAdapterForNumber();
		case SimpleTypeHelper.TYPE_NAME_LOGICAL:
			return new TypeAdapterForLogical();
		default:
			return getTypeAdapterBaseForUserType(type);
		}
	}
}
