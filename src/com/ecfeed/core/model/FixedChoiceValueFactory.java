/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.implementation.ModelClassLoader;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class FixedChoiceValueFactory {

	// TODO - add createValueString method: createValue().toString();
	private ModelClassLoader fLoader;
	boolean fIsExport;

	public FixedChoiceValueFactory(ModelClassLoader loader, boolean isExport){

		fLoader = loader;
		fIsExport = isExport; // TODO - rename
	}

	public Object createValue(ChoiceNode choice, IExtLanguageManager extLanguageManager) {

		if(choice.getParameter() != null) {
			String context = "Model path: " + ModelHelper.getFullPath(choice, extLanguageManager);
			
			return createValue(
					choice.getValueString(), choice.isRandomizedValue(), choice.getParameter().getType(), context);
		}
		
		return null;
	}

	public Object createValue(String valueString, boolean isRandomized, String typeName, String context) {

		if (typeName == null || valueString == null) {
			return null;
		}

		String convertedValueString = valueString;

		if (isRandomized) {

			TypeAdapterProviderForJava typeAdapterProvider = new TypeAdapterProviderForJava();
			ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(typeName);
			convertedValueString = typeAdapter.generateValueAsString(valueString, context);  
		}

		if (JavaLanguageHelper.isJavaType(typeName)) {
			return JavaLanguageHelper.parseJavaValueToObject(convertedValueString, typeName, ERunMode.QUIET);
		}
		
		if (fIsExport) {
			return convertedValueString;
		}

		return parseUserTypeValue(convertedValueString, typeName);
	}
	
	private Object parseUserTypeValue(String valueString, String typeName) {

		Object value = null;
		Class<?> typeClass = null;
		
		try {
			typeClass = fLoader.loadClass(typeName);
		} catch(Exception e) {
			return valueString;
		}
		
		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				if ((((Enum<?>)object).name()).equals(valueString)) {
					value = object;
					break;
				}
			}
		}

		return value;
	}


}
