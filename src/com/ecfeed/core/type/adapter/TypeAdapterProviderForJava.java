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

import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;

public class TypeAdapterProviderForJava implements ITypeAdapterProvider{

	@SuppressWarnings("rawtypes")
	protected ITypeAdapter<?> getTypeAdapterBaseForUserType(String type) {
		return new TypeAdapterBaseForUserType(type);
	}

	@Override
	public ITypeAdapter<?> getAdapter(String type){
		if(!JavaLanguageHelper.isJavaType(type) && !SimpleLanguageHelper.isSimpleType(type)){
			type = TypeAdapterHelper.USER_TYPE;
		}
		switch(type){
		case JavaLanguageHelper.TYPE_NAME_BOOLEAN:
			return new TypeAdapterForBoolean();
		case JavaLanguageHelper.TYPE_NAME_BYTE:
			return new TypeAdapterForByte();
		case JavaLanguageHelper.TYPE_NAME_CHAR:
			return new TypeAdapterForChar();
		case JavaLanguageHelper.TYPE_NAME_DOUBLE:
			return new TypeAdapterForDouble();
		case JavaLanguageHelper.TYPE_NAME_FLOAT:
			return new TypeAdapterForFloat();
		case JavaLanguageHelper.TYPE_NAME_INT:
			return new TypeAdapterForInt();
		case JavaLanguageHelper.TYPE_NAME_LONG:
			return new TypeAdapterForLong();
		case JavaLanguageHelper.TYPE_NAME_SHORT:
			return new TypeAdapterForShort();
		case JavaLanguageHelper.TYPE_NAME_STRING:
			return new TypeAdapterForString();
		case SimpleLanguageHelper.TYPE_NAME_TEXT:
			return new TypeAdapterForText();
		case SimpleLanguageHelper.TYPE_NAME_NUMBER:
			return new TypeAdapterForNumber();
		case SimpleLanguageHelper.TYPE_NAME_LOGICAL:
			return new TypeAdapterForLogical();
		default:
			return getTypeAdapterBaseForUserType(type);
		}
	}
}
