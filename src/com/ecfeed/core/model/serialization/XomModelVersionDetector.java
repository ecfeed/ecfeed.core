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

import static com.ecfeed.core.model.serialization.SerializationConstants.ROOT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.VERSION_ATTRIBUTE;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class XomModelVersionDetector {

	public static int getVersion(Element element, ListOfStrings outErrorList) {
		String qualifiedName = element.getQualifiedName(); 

		if(qualifiedName.equals(ROOT_NODE_NAME) == false){
			outErrorList.add("Unexpected root node name: " + qualifiedName);
			return 0;
		}

		String versionStr = element.getAttributeValue(VERSION_ATTRIBUTE);
		if(versionStr == null){
			return 0;
		}			

		return convertVersion(versionStr);
	}

	private static int convertVersion(String versionStr) {
		int version = 0;
		try {
			version = Integer.parseInt(versionStr);
		} catch (NumberFormatException e) {
			ExceptionHelper.reportRuntimeException("Invalid version");
		}

		if (version < 0) {
			ExceptionHelper.reportRuntimeException("Invalid version");
		}
		return version;
	}

}
