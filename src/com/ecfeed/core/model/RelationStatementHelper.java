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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class RelationStatementHelper {

	public static boolean isRightParameterTypeAllowed(String rightParameterType, String leftParameterType) {

		if (JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& !JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& !JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& !JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		return true;
	}

	public static void compareRelationStatements(RelationStatement statement1, RelationStatement statement2) {
		
		AbstractParameterNodeHelper.compareParameters(statement1.getLeftParameter(), statement2.getLeftParameter());
		if((statement1.getRelation() != statement2.getRelation())){
			ExceptionHelper.reportRuntimeException("Compared statements have different relations: " +
					statement1.getRelation() + " and " + statement2.getRelation());
		}
		
		compareConditions(statement1.getConditionValue(), statement2.getConditionValue());
	}
	
	public static void compareConditions(Object condition1, Object condition2) { // XYX move ?

		if (condition1 instanceof String && condition2 instanceof String) {
			if(condition1.equals(condition2) == false){
				ExceptionHelper.reportRuntimeException("Compared labels are different: " + condition1 + "!=" + condition2);
				return;
			}
		}

		if (condition1 instanceof ChoiceNode && condition2 instanceof ChoiceNode) {
			ChoiceNodeHelper.compareChoices((ChoiceNode)condition1, (ChoiceNode)condition2);
			return;
		}

		if (condition1 instanceof BasicParameterNode && condition2 instanceof BasicParameterNode) {
			MethodNodeHelper.compareMethodParameters((BasicParameterNode)condition1, (BasicParameterNode)condition2); // XYX
			return;

		}

		if (condition1 instanceof java.lang.String && condition2 instanceof java.lang.String) {
			StringHelper.compareStrings((String)condition1, (String) condition2, "Condition strings do not match.");
			return;
		}

		String type1 = condition1.getClass().getTypeName();
		String type2 = condition2.getClass().getTypeName();

		ExceptionHelper.reportRuntimeException("Unknown or not same types of compared conditions of types: " + type1 + ", " + type2 + ".");
	}
	
}

