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

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForLabel;
import com.ecfeed.core.utils.StringHelper;

public class StatementConditionHelper {

	private static final String TYPE_INFO_CHOICE = "choice";
	private static final String TYPE_INFO_PARAMETER = "parameter";
	private static final String TYPE_INFO_LABEL = "label";

	public static ChoiceNode getChoiceForMethodParameter(List<ChoiceNode> choices, BasicParameterNode methodParameterNode) {

		if (choices == null) {
			return null;
		}

		IParametersParentNode methodNode = (IParametersParentNode) methodParameterNode.getParent();

		if (methodNode == null) {
			return null;
		}

		int index = methodNode.getParameters().indexOf(methodParameterNode);		
		if (index == -1) {
			return null;
		}

		if(choices.size() < index + 1) {
			return null;
		}

		return choices.get(index);
	}

	public static String createChoiceDescription(String choiceName) {
		return choiceName + "[" + TYPE_INFO_CHOICE + "]";
	}

	public static String createParameterDescription(String parameterName) {
		return parameterName + "[" + TYPE_INFO_PARAMETER + "]";
	}

	public static String createLabelDescription(String parameterName) {
		return parameterName + "[" + TYPE_INFO_LABEL + "]";
	}	

	public static boolean containsNoTypeInfo(String string) {

		if (string.contains("[") &&  string.contains("]")) {
			return false;
		}

		return true;
	}

	public static boolean containsLabelTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_LABEL)) {
			return true;
		}

		return false;
	}

	public static String removeLabelTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_LABEL);
	}

	public static boolean containsParameterTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_PARAMETER)) {
			return true;
		}

		return false;
	}

	public static String removeParameterTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_PARAMETER);
	}

	public static boolean containsChoiceTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_CHOICE)) {
			return true;
		}

		return false;
	}	

	public static String removeChoiceTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_CHOICE);
	}

	public static boolean getChoiceRandomized(List<ChoiceNode> choices, BasicParameterNode methodParameterNode) {
		ChoiceNode choiceNode = getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return false;
		}

		return choiceNode.isRandomizedValue();
	}

	public static boolean getChoiceRandomized(ChoiceNode choice, BasicParameterNode methodParameterNode) {
		return getChoiceRandomized(Arrays.asList(choice), methodParameterNode);
	}

	private static boolean containsTypeInfo(String string, String typeDescription) {

		if (string.contains("[" + typeDescription + "]")) {
			return true;
		}

		return false;
	}

	private static String removeTypeInfo(String string, String typeDescription) {
		return StringHelper.removeFromPostfix("[" + typeDescription + "]", string);
	}

	public static void convertRightCondition(
			ParameterConversionItem parameterConversionItem,
			RelationStatement relationStatement,
			IStatementCondition inOutRightCondition) {

		IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();
		IParameterConversionItemPart dstPart = parameterConversionItem.getDstPart();

		IParameterConversionItemPart.ItemPartType srcType = srcPart.getType();
		IParameterConversionItemPart.ItemPartType dstType = dstPart.getType();

		if (srcType == dstType) {
			inOutRightCondition.convert(parameterConversionItem);
			return;
		}

		if (srcType == IParameterConversionItemPart.ItemPartType.LABEL && 
				inOutRightCondition instanceof LabelCondition) {

			convertLabelPartToChoicePart(srcPart, dstPart, relationStatement, inOutRightCondition);
			return;
		}

		if (srcType == IParameterConversionItemPart.ItemPartType.CHOICE && 
				inOutRightCondition instanceof ChoiceCondition) {

			convertChoicePartToLabelPart(srcPart, dstPart, relationStatement, inOutRightCondition);
			return;
		}
	}

	private static void convertLabelPartToChoicePart(
			IParameterConversionItemPart srcPart,
			IParameterConversionItemPart dstPart,
			RelationStatement relationStatement,
			IStatementCondition inOutRightCondition) {

		LabelCondition labelCondition = (LabelCondition) inOutRightCondition;
		ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
				(ParameterConversionItemPartForLabel) srcPart;

		String labelOfCondition = labelCondition.getRightLabel();
		String labelOfItemPart = parameterConversionItemPartForLabel.getLabel();


		if (!StringHelper.isEqual(labelOfCondition, labelOfItemPart)) {
			return;
		}

		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
				(ParameterConversionItemPartForChoice) dstPart;

		ChoiceNode choiceNode = parameterConversionItemPartForChoice.getChoiceNode();

		ChoiceCondition choiceCondition = new ChoiceCondition(choiceNode, relationStatement);

		inOutRightCondition = choiceCondition;
	}

	private static void convertChoicePartToLabelPart(
			IParameterConversionItemPart srcPart,
			IParameterConversionItemPart dstPart,
			RelationStatement relationStatement,
			IStatementCondition inOutRightCondition) {

		ChoiceCondition choiceCondition = (ChoiceCondition) inOutRightCondition;

		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
				(ParameterConversionItemPartForChoice) srcPart;

		ChoiceNode choiceOfCondition = choiceCondition.getRightChoice();
		ChoiceNode choiceOfItemPart = parameterConversionItemPartForChoice.getChoiceNode();


		if (!choiceOfCondition.equals(choiceOfItemPart)) {
			return;
		}

		ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
				(ParameterConversionItemPartForLabel) dstPart;

		String label = parameterConversionItemPartForLabel.getLabel();

		LabelCondition labelCondition = new LabelCondition(label, relationStatement);

		inOutRightCondition = labelCondition;
	}

}

