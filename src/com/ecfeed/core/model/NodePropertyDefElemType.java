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


public class NodePropertyDefElemType {

	private static final String WB_ELEMENT_TYPE = "wbElementType";
	private static final String PAGE_ELEMENT = "Page element";
	private static final String BUTTON = "Button";
	private static final String TEXT = "Text";
	private static final String SELECT = "Select";
	private static final String RADIO = "Radio";
	private static final String PAGE_URL = "Page URL";
	private static final String DELAY = "Delay[s]";
	private static final String BROWSER = "Browser";
	private static final String EMPTY_STR = "";
	private static final String UNMAPPED = "Unmapped";
	private static final String CHECKBOX = "Checkbox";

	public static final NodePropertyDef elementType = 
			new NodePropertyDef(
					WB_ELEMENT_TYPE, JavaTypeHelper.TYPE_NAME_STRING, EMPTY_STR,
					new String[]{EMPTY_STR, TEXT, CHECKBOX, PAGE_ELEMENT, PAGE_URL, DELAY, BROWSER });


	private static final NodePropertyValueSet VALUE_SET_FOR_STRING = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, SELECT, RADIO, PAGE_ELEMENT, PAGE_URL, BROWSER});

	private static final NodePropertyValueSet VALUE_SET_FOR_CHAR = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, PAGE_ELEMENT});

	private static final NodePropertyValueSet VALUE_SET_FOR_BOOLEAN = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, CHECKBOX, BUTTON, PAGE_ELEMENT});

	private static final NodePropertyValueSet VALUE_SET_FOR_NUMERIC_TYPES = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, DELAY, PAGE_ELEMENT});

	private static final NodePropertyValueSet VALUE_SET_FOR_EXPECTED_PARAMETER = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, PAGE_URL});	


	public static String[] getPossibleValues(String parameterType) {
		return getValueSet(parameterType).getPossibleValues();
	}

	public static NodePropertyValueSet getValueSet(String parameterType, boolean isExpectedParameter) {

		NodePropertyValueSet valueSet = getValueSet(parameterType);

		if (!isExpectedParameter) {
			return valueSet;
		}

		return NodePropertyValueSet.intersect(valueSet, VALUE_SET_FOR_EXPECTED_PARAMETER);
	}	


	public static String getDefaultValue(String parameterType) {
		if (parameterType == null) {
			return null;
		}
		return getValueSet(parameterType).getDefaultValue();
	}

	public static NodePropertyValueSet getValueSet(String parameterType) {
		if (JavaTypeHelper.isStringTypeName(parameterType)) {
			return VALUE_SET_FOR_STRING;
		}
		if (JavaTypeHelper.isCharTypeName(parameterType)) {
			return VALUE_SET_FOR_CHAR;
		}
		if (JavaTypeHelper.isBooleanTypeName(parameterType)) {
			return VALUE_SET_FOR_BOOLEAN;
		}		
		if (JavaTypeHelper.isNumericTypeName(parameterType)) {
			return VALUE_SET_FOR_NUMERIC_TYPES;
		}		
		return null;
	}

	public static boolean isUnmapped(String value) {
		if (StringHelper.isEqual(value, UNMAPPED)) {
			return true;
		}
		return false;
	}	

	public static boolean isText(String value) {
		if ( StringHelper.isEqual(value, TEXT)) {
			return true;
		}
		return false;
	}

	public static boolean isCheckbox(String value) {
		if (StringHelper.isEqual(value, CHECKBOX)) {
			return true;
		}
		return false;
	}	

	public static boolean isSelect(String value) {
		if (StringHelper.isEqual(value, SELECT)) {
			return true;
		}
		return false;
	}	

	public static boolean isRadio(String value) {
		if (StringHelper.isEqual(value, RADIO)) {
			return true;
		}
		return false;
	}	

	public static boolean isButton(String value) {
		if (StringHelper.isEqual(value, BUTTON)) {
			return true;
		}
		return false;
	}

	public static boolean isPageElement(String value) {
		if (value.equals(PAGE_ELEMENT)) {
			return true;
		}
		return false;
	}

	public static boolean isPageUrl(String value) {
		if (value.equals(PAGE_URL)) {
			return true;
		}
		return false;
	}	

	public static boolean isDelay(String value) {
		if (value.equals(DELAY)) {
			return true;
		}
		return false;
	}

	public static boolean isBrowser(String value) {
		if (value.equals(BROWSER)) {
			return true;
		}
		return false;
	}

	public static boolean isChildElementAvailable(String value) {

		if (StringHelper.isNullOrEmpty(value)) {
			return false;
		}

		if (isPageElement(value) || isText(value) || isButton(value) || isCheckbox(value) || isSelect(value) || isRadio(value)) {
			return true;
		}

		return false;
	}

	public static boolean isTypeForSimpleFindByValue(String value) {

		if (isText(value)) {
			return true;
		}

		if (isCheckbox(value)) {
			return true;
		}	

		if (isSelect(value)) {
			return true;
		}	

		if (isRadio(value)) {
			return true;
		}	

		if (isButton(value)) {
			return true;
		}

		if (isPageElement(value)) {
			return true;
		}

		return false;
	}	


	public static boolean isOptionalAvailable(String value) {

		if (isText(value)) {
			return true;
		}
		if (isSelect(value)) {
			return true;
		}
		if (isRadio(value)) {
			return true;
		}		
		if (isCheckbox(value)) {
			return true;
		}		
		if (isButton(value)) {
			return true;
		}		
		if (isPageElement(value)) {
			return true;
		}		
		return false;
	}	

	public static boolean isActionAvailable(String value) {
		if (isPageElement(value)) {
			return true;
		}
		return false;
	}	

}
