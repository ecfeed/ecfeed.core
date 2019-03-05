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

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;


public class NodePropertyDefFindByType {

	public static final String UNMAPPED = "Unmapped";
	private static final String ID = "Id";
	private static final String CLASS_NAME = "Class name";
	private static final String TAG_NAME = "Tag name";
	public  static final String NAME = "Name";
	private static final String LINK_TEXT = "Link text";
	private static final String PARTIAL_LINK_TEXT = "Partial link text";
	private static final String CSS_SELECTOR = "CSS selector";
	private static final String XPATH = "Xpath";

	private static final String[] valuesForStandardSet = new String[] { UNMAPPED, ID, CLASS_NAME, TAG_NAME, NAME, LINK_TEXT, PARTIAL_LINK_TEXT, CSS_SELECTOR, XPATH }; 

	private static final NodePropertyValueSet STANDARD_VALUE_SET_WITH_DEFAULT_UNMAPPED = 
			new NodePropertyValueSet(UNMAPPED, valuesForStandardSet);

	private static final NodePropertyValueSet STANDARD_VALUE_SET_WITH_DEFAULT_ID = 
			new NodePropertyValueSet(ID, valuesForStandardSet);

	private static final NodePropertyValueSet VALUE_SET_FOR_RADIO = 
			new NodePropertyValueSet(NAME, new String[] {NAME});

	public static final NodePropertyDef findByTypeOfElement = 
			new NodePropertyDef(
					"wbFindByType", JavaTypeHelper.TYPE_NAME_STRING, STANDARD_VALUE_SET_WITH_DEFAULT_UNMAPPED);

	public static String[] getPossibleValues(String webElementType) {
		return getValueSet(webElementType).getPossibleValues();
	}

	public static NodePropertyValueSet getValueSet(String webElementType) {

		if (NodePropertyDefElemType.isRadio(webElementType)) {
			return VALUE_SET_FOR_RADIO;
		}

		if (NodePropertyDefElemType.isUnmapped(webElementType)) {
			return STANDARD_VALUE_SET_WITH_DEFAULT_UNMAPPED;
		}

		return STANDARD_VALUE_SET_WITH_DEFAULT_ID;
	}

	public static boolean isUnmapped(String value) {
		if (StringHelper.isEqual(value, UNMAPPED)) {
			return true;
		}
		return false;
	}	

	public static boolean isId(String value) {
		if (StringHelper.isEqual(value, ID)) {
			return true;
		}
		return false;
	}	

	public static boolean isClassName(String value) {
		if (value.equals(CLASS_NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isTagName(String value) {
		if (StringHelper.isEqual(value, TAG_NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isName(String value) {
		if (StringHelper.isEqual(value, NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isLinkText(String value) {
		if (StringHelper.isEqual(value, LINK_TEXT)) {
			return true;
		}
		return false;
	}	

	public static boolean isPartialLinkText(String value) {
		if (StringHelper.isEqual(value, PARTIAL_LINK_TEXT)) {
			return true;
		}
		return false;
	}	

	public static boolean isCssSelector(String value) {
		if (StringHelper.isEqual(value, CSS_SELECTOR)) {
			return true;
		}
		return false;
	}	

	public static boolean isXPath(String value) {
		if (StringHelper.isEqual(value, XPATH)) {
			return true;
		}
		return false;
	}

	public static boolean isTypeForSimpleFindByValue(String value) {

		if (isId(value)) {
			return true;
		}	

		if (isClassName(value)) {
			return true;
		}	

		if (isTagName(value)) {
			return true;
		}	

		if (isName(value)) {
			return true;
		}	

		return false;
	}	

}
