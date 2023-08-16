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

import nu.xom.Element;

public class XomElementHelper {
	
	public static String elementToString(nu.xom.Element element) {
		return element.getLocalName() + "   " + printAttributes(element);
	}

	private static String printAttributes(Element element) {
		
		String result = "";
		
		int attributeCount = element.getAttributeCount();
		
		for (int index = 0; index < attributeCount; index ++) {
			
			nu.xom.Attribute attribute = element.getAttribute(index);
			
			result += attribute.getLocalName() + "=" + attribute.getValue() + ",";
		}
		
		return result;
	}

}
