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

import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_BYTE;
import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_DOUBLE;
import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_FLOAT;
import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_INT;
import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_LONG;
import static com.ecfeed.core.utils.JavaLanguageHelper.TYPE_NAME_SHORT;
import static com.ecfeed.core.utils.SimpleLanguageHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE;
import static com.ecfeed.core.utils.SimpleLanguageHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.Pair;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class ChoiceNodeHelper {

	private static final double eps = 0.000001;

	public static BasicParameterNode getBasicParameter(ChoiceNode choiceNode) {

		return getParameterRecursive(choiceNode);
	}

	private static BasicParameterNode getParameterRecursive(ChoiceNode choiceNode) {

		IAbstractNode parent = choiceNode.getParent();

		if (parent == null) {
			return null;
		}

		if (parent instanceof BasicParameterNode) {
			return (BasicParameterNode) parent;
		}

		if (parent instanceof ChoiceNode) {

			BasicParameterNode basicParameterNode = getParameterRecursive((ChoiceNode)parent);
			return basicParameterNode;
		}

		if (parent instanceof TestCaseNode) {

			BasicParameterNode basicParameterNode = getParameterForChoiceFromTestCase(choiceNode, parent);
			return basicParameterNode;
		}

		ExceptionHelper.reportRuntimeException("Invalid type of choices parent.");
		return null;
	}

	private static BasicParameterNode getParameterForChoiceFromTestCase(ChoiceNode choiceNode, IAbstractNode parent) {

		TestCaseNode testCaseNode = (TestCaseNode) parent;

		MethodNode methodNode = testCaseNode.getMethod();

		if (methodNode == null) {
			return null;
		}

		int indexOfChoiceNodeInTestCase = choiceNode.getMyIndex();

		AbstractParameterNode abstractParameterNode = methodNode.getParameter(indexOfChoiceNodeInTestCase);

		if (!(abstractParameterNode instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Invalid type of parameter.");
		}

		BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
		return basicParameterNode;
	}

	public static ChoiceNode createChoiceNodeWithDefaultValue(BasicParameterNode parentMethodParameterNode) {

		String defaultValue = parentMethodParameterNode.getDefaultValue();

		ChoiceNode choiceNode = new ChoiceNode(ChoiceNode.ASSIGNMENT_NAME, defaultValue, parentMethodParameterNode.getModelChangeRegistrator());
		choiceNode.setParent(parentMethodParameterNode);

		return choiceNode;
	}

	public static void cloneChoiceNodesRecursively(
			IChoicesParentNode srcParentNode, 
			IChoicesParentNode dstParentNode,
			NodeMapper mapper
			) {

		List<ChoiceNode> childChoiceNodes = srcParentNode.getChoices();

		if (childChoiceNodes.size() == 0) {
			return;
		}

		for (ChoiceNode choiceNode : childChoiceNodes) {

			ChoiceNode clonedChoiceNode = choiceNode.makeClone();

			if (mapper != null) {
				mapper.addMappings(choiceNode, clonedChoiceNode);
			}

			clonedChoiceNode.clearChoices();

			dstParentNode.addChoice(clonedChoiceNode);

			cloneChoiceNodesRecursively(choiceNode, clonedChoiceNode, mapper);
		}
	}

	public static void verifyConversionOfChoices(
			BasicParameterNode abstractParameterNode, 
			String newType, 
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		Set<ChoiceNode> choiceNodes = abstractParameterNode.getAllChoices();

		for (ChoiceNode choiceNode : choiceNodes) {

			boolean isRandomizedValue = choiceNode.isRandomizedValue();

			if (!canConvertChoiceValueFromToType(
					choiceNode.getValueString(), 
					abstractParameterNode.getType(), 
					newType, 
					isRandomizedValue)) {

				addConversionDefinitionItem(choiceNode, isRandomizedValue, inOutParameterConversionDefinition); 
			}
		}
	}

	public static void convertValuesOfChoicesToType(
			BasicParameterNode methodParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition) {

		Set<ChoiceNode> choiceNodes = methodParameterNode.getAllChoices();

		for (ChoiceNode choiceNode : choiceNodes) {

			convertChoiceValueConditionally(choiceNode, parameterConversionDefinition);
		}
	}

	private static void convertChoiceValueConditionally(ChoiceNode choiceNode,
			ParameterConversionDefinition parameterConversionDefinition) {

		String valueString = choiceNode.getValueString();

		int itemCount = parameterConversionDefinition.getItemCount();

		for (int index = 0; index < itemCount; index++) {
			ParameterConversionItem parameterConversionItem = parameterConversionDefinition.getCopyOfItem(index);

			String srcString = parameterConversionItem.getSrcPart().getStr();

			if (StringHelper.isEqual(srcString, valueString)) {
				String dstString = parameterConversionItem.getDstPart().getStr();
				choiceNode.setValueString(dstString);
			}
		}
	}

	private static boolean canConvertChoiceValueFromToType(
			String value, 
			String oldType, 
			String newType, 
			boolean isChoiceRandomized) {

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();

		ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(newType);

		boolean canConvert = typeAdapter.canCovertWithoutLossOfData(oldType, value, isChoiceRandomized);

		return canConvert;
	}

	private static void addConversionDefinitionItem(
			ChoiceNode choiceNode,
			boolean isRandomized,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		IParameterConversionItemPart srcPart = 
				new ParameterConversionItemPartForValue(choiceNode.getValueString());

		String objectsContainingSrcItem = choiceNode.getName() + "(choice)";

		ParameterConversionItem parameterConversionItem = 
				new ParameterConversionItem(srcPart, null, isRandomized, objectsContainingSrcItem);

		inOutParameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);
	}


	public static void moveChildChoices(ChoiceNode srcChoiceNode, ChoiceNode dstChoiceNode) {

		List<ChoiceNode> childChoices = srcChoiceNode.getChoices();
		List<ChoiceNode> childChoicesToRemove = new ArrayList<ChoiceNode>(childChoices);

		for (ChoiceNode childChoice : childChoices) {

			ChoiceNode clonedChoiceNode = childChoice.makeClone();
			dstChoiceNode.addChoice(clonedChoiceNode);
		}

		for (ChoiceNode choiceNodeToRemove : childChoicesToRemove) {
			srcChoiceNode.removeChoice(choiceNodeToRemove);
		}
	}

	public static ChoiceNode addChoiceToChoice(
			ChoiceNode parentChoiceNode, String choiceNodeName, String valueString) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, null);
		parentChoiceNode.addChoice(choiceNode);

		return choiceNode;
	}

	public static String getName(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		String name = choiceNode.getName();
		return name;
	}

	public static String getQualifiedName(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		ChoiceNode parentChoice = getParentChoice(choiceNode);

		if (parentChoice != null) {
			return getQualifiedName(parentChoice, extLanguageManager) + ":" + getName(choiceNode, extLanguageManager);
		}

		return getName(choiceNode, extLanguageManager);
	}

	public static ChoiceNode getParentChoice(ChoiceNode choiceNode){

		IChoicesParentNode choicesParentNode = (IChoicesParentNode)choiceNode.getParent();

		if (choicesParentNode == null) {
			return null;
		}

		BasicParameterNode abstractParameterNode = choicesParentNode.getParameter();

		if(choicesParentNode != null && choicesParentNode != abstractParameterNode){
			return (ChoiceNode)choicesParentNode;
		}

		return null;
	}

	public static String createSignature(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		if (choiceNode == null) {
			return "EMPTY";
		}

		String qualifiedName = getQualifiedName(choiceNode, extLanguageManager);

		if (choiceNode.isAbstract()) {
			return qualifiedName + ChoiceNode.ABSTRACT_CHOICE_MARKER;
		}

		String value;

		if (choiceNode.getParent() == null) {
			value = "N/A";
		} else {
			value = getValueString(choiceNode, extLanguageManager);
		}

		return qualifiedName + " [" + value + "]";
	}

	public static String createShortSignature(ChoiceNode choiceNode) {

		if (choiceNode == null) {
			return "EMPTY";
		}

		return choiceNode.getName();
	}

	public static String createSignatureOfChoiceWithParameter(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		BasicParameterNode basicParameterNode = choiceNode.getParameter();	

		String choiceQualifiedName = ChoiceNodeHelper.getQualifiedName(choiceNode, extLanguageManager);

		if (basicParameterNode == null) {

			return choiceQualifiedName;
		}

		String parameterCompositeName = AbstractParameterNodeHelper.getQualifiedName(basicParameterNode, extLanguageManager);

		if (basicParameterNode.isExpected()) {
			return "[e]" +	ChoiceNodeHelper.getValueString(choiceNode, extLanguageManager);
		}

		return parameterCompositeName + SignatureHelper.SIGNATURE_NAME_SEPARATOR + choiceQualifiedName;
	}

	public static String getValueString(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		if (choiceNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot get value from empty string.");
		}

		BasicParameterNode parameter = choiceNode.getParameter();

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Cannot get value. Empty parameter.");
		}

		String type = parameter.getType();

		String value = choiceNode.getValueString();

		if (JavaLanguageHelper.isJavaType(type)) {
			value = extLanguageManager.conditionallyConvertSpecialValueToExtLanguage(value, type);
		}

		return value;
	}

	public static ChoiceNode createSubstitutePath(ChoiceNode choice, BasicParameterNode parameter) {
		List<ChoiceNode> copies = createListOfCopies(choice);
		setParentsOfChoices(copies, parameter);
		return copies.get(0);
	}

	private static List<ChoiceNode> createListOfCopies(ChoiceNode choice) {
		ChoiceNode orgChoice = choice;
		List<ChoiceNode> copies = new ArrayList<ChoiceNode>();

		for(;;) {

			ChoiceNode copy = orgChoice.makeClone();
			copies.add(copy);

			IAbstractNode orgParent = orgChoice.getParent();
			if (!(orgParent instanceof ChoiceNode)) {
				break;
			}

			orgChoice = (ChoiceNode)orgParent;
		}

		return copies;
	}

	private static void setParentsOfChoices(List<ChoiceNode> copies, BasicParameterNode parameter) {

		int copiesSize = copies.size();
		int lastIndex = copiesSize - 1;

		for (int index = 0; index < lastIndex; index++) {
			ChoiceNode current = copies.get(index);
			ChoiceNode next = copies.get(index + 1);

			current.setParent(next);
		}

		ChoiceNode last = copies.get(copiesSize - 1);
		last.setParent(parameter);
	}

	public static List<String> getChoiceNames(List<ChoiceNode> choiceNodes, IExtLanguageManager extLanguageManager) {

		List<String> choiceNames = new ArrayList<>();

		for (ChoiceNode choiceNode : choiceNodes) {

			String choiceName = ChoiceNodeHelper.getQualifiedName(choiceNode, extLanguageManager);
			choiceNames.add(choiceName);
		}

		return choiceNames;
	}

	public static Pair<ChoiceNode,ChoiceNode> rangeSplit(ChoiceNode choice)	{

		if(!choice.isRandomizedValue()) {
			ExceptionHelper.reportRuntimeException("Attempt do split range on non-randomized choice node.");
		}

		ChoiceNode first = choice.makeClone();
		ChoiceNode second = choice.makeClone();

		first.setRandomizedValue(false);
		second.setRandomizedValue(false);
		String[] parts = choice.getValueString().split(":");

		if (parts.length != 2) {
			ExceptionHelper.reportRuntimeException("Two values are expected during range splitting.");
		}

		first.setValueString(parts[0]);
		second.setValueString(parts[1]);

		return new Pair<>(first,second);
	}

	public static ChoiceNode toRangeFromFirst(ChoiceNode first, ChoiceNode second) {
		assertChoicesNotRandomized(first, second);

		ChoiceNode ret = first.makeClone();

		ret.setRandomizedValue(true);
		ret.setValueString(first.getValueString()+":"+second.getValueString());

		return ret;
	}

	public static ChoiceNode toRangeFromSecond(ChoiceNode first, ChoiceNode second)	{
		assertChoicesNotRandomized(first, second);

		ChoiceNode ret = second.makeClone();

		ret.setRandomizedValue(true);
		ret.setValueString(first.getValueString()+":"+second.getValueString());

		return ret;
	}

	private static void assertChoicesNotRandomized(ChoiceNode first, ChoiceNode second) {

		if (first.isRandomizedValue() || second.isRandomizedValue()) {
			ExceptionHelper.reportRuntimeException("Choices should not be randomized.");
		}
	}

	public static void getPrecedingValue(ChoiceNode choice) {
		assertChoiceNotRandomized(choice);

		String type = choice.getParameter().getType();

		ChoiceNode choiceX = convertValueToNumeric(choice);

		switch(type) {
		case TYPE_NAME_DOUBLE:
		case TYPE_NAME_FLOAT: {
			double val = Double.parseDouble(choiceX.getValueString());
			if(val==0.0)
				val = -eps;
			else if(val<0.0)
				val *= 1+eps;
			else
				val /= 1+eps;
			choice.setValueString(String.valueOf(val));
			return;
		}

		case TYPE_NAME_BYTE:
		case TYPE_NAME_INT:
		case TYPE_NAME_SHORT:
		case TYPE_NAME_LONG: {
			long val = Long.parseLong(choiceX.getValueString());
			if(val!=Long.MIN_VALUE)
				val--;
			choice.setValueString(String.valueOf(val));
			return;
		}

		default: {
			reportExceptionUnhandledType();
		}
		}
	}

	public static void getFollowingVal(ChoiceNode choice) {

		assertChoiceNotRandomized(choice);

		String type = choice.getParameter().getType();

		ChoiceNode choiceX = convertValueToNumeric(choice);

		switch(type) {
		case TYPE_NAME_DOUBLE:
		case TYPE_NAME_FLOAT: {
			double val = Double.parseDouble(choiceX.getValueString());
			if(val==0.0)
				val = eps;
			else if(val<0.0)
				val /= 1+eps;
			else
				val *= 1+eps;
			choice.setValueString(String.valueOf(val));
			return;
		}
		case TYPE_NAME_BYTE:
		case TYPE_NAME_INT:
		case TYPE_NAME_SHORT:
		case TYPE_NAME_LONG: {
			long val = Long.parseLong(choiceX.getValueString());
			if(val != Long.MAX_VALUE)
				val++;
			choice.setValueString(String.valueOf(val));
			return;
		}
		default:
		{
			reportExceptionUnhandledType();
		}
		}
	}

	public static void roundValueDown(ChoiceNode choice)	{
		assertChoiceNotRandomized(choice);

		long val = (long) Math.floor(Double.parseDouble(choice.getValueString()));

		choice.setValueString(String.valueOf(val));
	}

	public static void roundValueUp(ChoiceNode choice) {
		assertChoiceNotRandomized(choice);

		long val = (long) Math.ceil(Double.parseDouble(choice.getValueString()));

		choice.setValueString(String.valueOf(val));
	}

	public static ChoiceNode convertValueToNumeric(ChoiceNode choiceInput) {

		ChoiceNode choice = choiceInput.makeClone();

		assertChoiceNotRandomized(choice);

		String type = choice.getParameter().getType();
		String valueString = choice.getValueString();

		switch(type) {
		case TYPE_NAME_DOUBLE:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Double.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Double.MAX_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MINUS_MIN)) {
				choice.setValueString("-" + Double.MIN_VALUE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MINUS_MAX)) {
				choice.setValueString("-" + Double.MAX_VALUE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
				choice.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_POSITIVE_INF)) {
				choice.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
			}
			break;
		}
		case TYPE_NAME_FLOAT:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Float.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Float.MAX_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MINUS_MIN)) {
				choice.setValueString("-" + Float.MIN_VALUE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MINUS_MAX)) {
				choice.setValueString("-" + Float.MAX_VALUE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
				choice.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_POSITIVE_INF)) {
				choice.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
			}
			break;
		}
		case TYPE_NAME_BYTE:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Byte.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Byte.MAX_VALUE + "");
			}
			break;
		}
		case TYPE_NAME_INT:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Integer.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Integer.MAX_VALUE + "");
			}
			break;
		}
		case TYPE_NAME_SHORT:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Short.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Short.MAX_VALUE + "");
			}
			break;
		}
		case TYPE_NAME_LONG:
		{
			if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MIN)) {
				choice.setValueString(Long.MIN_VALUE + "");
			} else if (valueString.equals(JavaLanguageHelper.SPECIAL_VALUE_MAX)) {
				choice.setValueString(Long.MAX_VALUE + "");
			}
			break;
		}
		default:
		{
			reportExceptionUnhandledType();
		}
		}
		return choice;
	}

	private static void assertChoiceNotRandomized(ChoiceNode choice) {

		if (choice.isRandomizedValue()) {
			ExceptionHelper.reportRuntimeException("Choice should not be randomized.");
		}
	}

	public static List<ChoiceNode> getInterleavedValues(ChoiceNode choice, int N)
	{
		if (! choice.isRandomizedValue()) {
			ExceptionHelper.reportRuntimeException("Choice should be randomized.");
		}

		Pair<ChoiceNode,ChoiceNode> startEnd = rangeSplit(choice);
		ChoiceNode start = convertValueToNumeric(startEnd.getFirst());
		ChoiceNode end = convertValueToNumeric(startEnd.getSecond());

		if (N < 2) {
			N = 2;
		}

		List<ChoiceNode> ret = new ArrayList<>();

		String type = choice.getParameter().getType();

		switch(type) {
		case TYPE_NAME_DOUBLE:
		case TYPE_NAME_FLOAT: {
			double v1 = Double.parseDouble(start.getValueString());
			double v2 = Double.parseDouble(end.getValueString());
			for(int i=0;i<N;i++)
			{
				double v = (v1*(N-1-i)+v2*i)/(N-1);
				ChoiceNode tmp = start.makeClone();
				tmp.setValueString(String.valueOf(v));
				ret.add(tmp);
			}

			if(v2 > Long.MAX_VALUE || v1 < Long.MIN_VALUE)
				return ret;
			long w1 = (long) Math.ceil(v1);
			long w2 = (long) Math.floor(v2);
			if(w1 <= w2)
			{
				for(String val : getInterleavedBigIntegers(String.valueOf(w1),String.valueOf(w2),N))
				{
					ChoiceNode tmp = start.makeClone();
					tmp.setValueString(val);
					ret.add(tmp);
				}
			}
			return ret;
		}

		case TYPE_NAME_BYTE:
		case TYPE_NAME_INT:
		case TYPE_NAME_SHORT:
		case TYPE_NAME_LONG: {
			String v1 = start.getValueString();
			String v2 = end.getValueString();

			for(String val : getInterleavedBigIntegers(v1,v2,N))
			{
				ChoiceNode tmp = start.makeClone();
				tmp.setValueString(val);
				ret.add(tmp);
			}
			return ret;
		}

		default:
		{
			reportExceptionUnhandledType();
			return null;
		}
		}
	}

	private static void reportExceptionUnhandledType() {
		ExceptionHelper.reportRuntimeException("An unnhandled type found.");
	}

	private static List<String> getInterleavedBigIntegers(String start, String end, int N)	{

		BigInteger v1 = new BigInteger(start);
		BigInteger v2 = new BigInteger(end);

		String oldval = "";
		List<String> ret = new ArrayList<>();

		for (int i=0;i<N;i++) {
			BigInteger v = v1.multiply(BigInteger.valueOf(N-1-i)).add(v2.multiply(BigInteger.valueOf(i))).divide(BigInteger.valueOf(N-1));
			String newval = String.valueOf(v);
			if(! oldval.equals(newval)) {
				ret.add(newval);
				oldval= newval;
			}
		}

		return ret;
	}

	public static ChoiceNode makeDerandomizedClone(ChoiceNode choiceNode) {

		ChoiceNode cloneChoiceNode = choiceNode.makeClone();

		cloneChoiceNode.derandomize();

		return cloneChoiceNode;
	}

	public static List<ChoiceNode> removeDuplicates(List<ChoiceNode> choiceNodes) {

		HashSet<ChoiceNode>set = new HashSet<>(choiceNodes);
		List<ChoiceNode> result = new ArrayList<>(set);

		return result;
	}

	public static List<ChoiceNode> getLeafChoices(Collection<ChoiceNode> choices) {

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();

		for (ChoiceNode p : choices) {
			if (p.isAbstract() == false) {
				result.add(p);
			}

			result.addAll(p.getLeafChoices());
		}

		return result;
	}

	public static Set<ChoiceNode> getAllChoices(Collection<ChoiceNode> choices) {

		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();

		for (ChoiceNode p : choices) {
			result.add(p);
			result.addAll(p.getAllChoices());
		}

		return result;
	}

	public static Set<String> getChoiceNames(Collection<ChoiceNode> choiceNodes) {

		Set<String> result = new LinkedHashSet<String>();

		for (ChoiceNode choiceNode : choiceNodes) {
			result.add(choiceNode.getQualifiedName());
		}

		return result;
	}

	public static Set<ChoiceNode> getLabeledChoices(String label, List<ChoiceNode> choices) {

		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();

		for(ChoiceNode p : choices) {

			if(p.getLabels().contains(label)){
				result.add(p);
			}

			result.addAll(p.getLabeledChoices(label));
		}

		return result;
	}

	public static Set<String> getAllLabels(Set<ChoiceNode> choices) {

		Set<String> result = new HashSet<>();

		for (ChoiceNode choiceNode : choices) {
			addLabelsForChoiceNode(choiceNode, result);
		}

		return result;
	}

	private static void addLabelsForChoiceNode(ChoiceNode choiceNode, Set<String> inOutResult) {

		Set<String> labelsOfChoice = choiceNode.getLabels();

		for (String label : labelsOfChoice) {
			inOutResult.add(label);
		}
	}

	public static Set<String> getLeafLabels(List<ChoiceNode> leafChoices) { 

		Set<String> result = new LinkedHashSet<String>();

		for (ChoiceNode choiceNode : leafChoices) {
			result.addAll(choiceNode.getAllLabels());
		}

		return result;
	}

	public static Set<String> getLeafChoiceValues(List<ChoiceNode> leafChoices) {

		Set<String> result = new LinkedHashSet<String>();

		for (ChoiceNode p : leafChoices) {
			result.add(p.getValueString());
		}

		return result;
	}

	public static String generateNewChoiceName(IChoicesParentNode fChoicesParentNode, String startChoiceName) {

		if (!fChoicesParentNode.choiceExistsAsDirectChild(startChoiceName)) {
			return startChoiceName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startChoiceName);

		for (int i = 1;   ; i++) {
			String newParameterName = oldNameCore + String.valueOf(i);

			if (!fChoicesParentNode.choiceExistsAsDirectChild(newParameterName)) {
				return newParameterName;
			}
		}
	}

	public static List<ConstraintNode> getMentioningConstraints(ChoiceNode choiceNode) {

		BasicParameterNode basicParameterNode = getBasicParameter(choiceNode);

		List<ConstraintNode> constraintMentioningParameter = 
				BasicParameterNodeHelper.getMentioningConstraints(basicParameterNode);

		Set<ConstraintNode> result = new HashSet<>();

		for (ConstraintNode constraintNode : constraintMentioningParameter) {

			if (constraintNode.mentions(choiceNode)) {

				result.add(constraintNode);
			}
		}

		return new ArrayList<ConstraintNode>(result);
	}

	public static List<TestCaseNode> getMentioningTestCases(ChoiceNode choiceNodeNotFromTestCase) {

		return new ArrayList<>(getMentioningTestCases2(choiceNodeNotFromTestCase));
	}

	//	private static Set<TestCaseNode> getMentioningTestCases1(ChoiceNode choiceNodeNotFromTestCase) { // TODO MO-RE change result to List
	//
	//		BasicParameterNode basicParameterNode = getBasicParameter(choiceNodeNotFromTestCase);
	//
	//		if (basicParameterNode.isGlobalParameter()) {
	//			return getMentioningTestCasesForGlobalChoice(choiceNodeNotFromTestCase, basicParameterNode);
	//		}
	//
	//		return getMentioningTestCasesForLocalChoice(choiceNodeNotFromTestCase);
	//	}

	//	private static Set<TestCaseNode> getMentioningTestCasesForLocalChoice(ChoiceNode choiceNodeNotFromTestCase) {
	//
	//		Set<TestCaseNode> result = new HashSet<>();
	//
	//		MethodNode methodNode = MethodNodeHelper.findMethodNode(choiceNodeNotFromTestCase);
	//
	//		if (methodNode == null) {
	//			return new HashSet<>();
	//		}
	//
	//		accumulateChoiceMentioningTestCases(choiceNodeNotFromTestCase, methodNode, result);
	//		return result;
	//	}

	//	private static Set<TestCaseNode> getMentioningTestCasesForGlobalChoice(
	//			ChoiceNode choiceNodeNotFromTestCase,
	//			BasicParameterNode basicParameterNode) {
	//
	//		Set<TestCaseNode> result = new HashSet<>();
	//
	//		List<BasicParameterNode> linkedParameterNodes = 
	//				BasicParameterNodeHelper.getLinkedBasicParameters(basicParameterNode);
	//
	//		for (BasicParameterNode linkedParameterNode : linkedParameterNodes) {
	//
	//			MethodNode methodNode = MethodNodeHelper.findMethodNode(linkedParameterNode);
	//			accumulateChoiceMentioningTestCases(choiceNodeNotFromTestCase, methodNode, result);
	//		}
	//
	//		return result;
	//	}

	//	private static void accumulateChoiceMentioningTestCases(
	//			ChoiceNode choiceNodeNotFromTestCase,
	//			MethodNode methodNode,
	//			Set<TestCaseNode> result) {
	//
	//		List<TestCaseNode> testCaseNodes = methodNode.getMentioningTestCases(choiceNodeNotFromTestCase);
	//		result.addAll(testCaseNodes);
	//	}
	//
	private static List<TestCaseNode> getMentioningTestCases2(ChoiceNode choiceNode) { // TODO MO-RE to choice node helper - getMentioningTestCases

		if (choiceNode.isPartOfGlobalParameter()) {
			return calculateTestCasesToDeleteForGlobalChoiceNode(choiceNode);
		}

		return calculateTestCasesToDeleteForLocalNode(choiceNode);
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForGlobalChoiceNode(ChoiceNode globalChoiceNode) {

		CompositeParameterNode compositeParameterNode = AbstractParameterNodeHelper.getTopComposite(globalChoiceNode);

		if (compositeParameterNode != null) {
			return calculateTestCasesForChoiceOfGlobalComposite(compositeParameterNode);
		}

		BasicParameterNode basicParameterNode = BasicParameterNodeHelper.findBasicParameter(globalChoiceNode);

		if (basicParameterNode != null) {
			return calculateTestCasesForChoiceOfGlobalBasicParameter(basicParameterNode);
		}

		return new ArrayList<>();
	}

	public static List<TestCaseNode> calculateTestCasesToDeleteForLocalNode(IAbstractNode abstractNode) { // TODO MO-RE rename

		MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractNode);

		return methodNode.getTestCases();
	}

	private static List<TestCaseNode> calculateTestCasesForChoiceOfGlobalComposite(
			CompositeParameterNode compositeParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(compositeParameterNode);


		for (CompositeParameterNode linkedCompositeParameterNode : linkedCompositeParameterNodes) {

			List<TestCaseNode> testCases = ChoiceNodeHelper.calculateTestCasesToDeleteForLocalNode(linkedCompositeParameterNode);

			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCasesForChoiceOfGlobalBasicParameter(
			BasicParameterNode basicParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<BasicParameterNode> linkedBasicParameterNodes =
				BasicParameterNodeHelper.getLinkedBasicParameters(basicParameterNode);


		for (BasicParameterNode linkedBasicParameterNode : linkedBasicParameterNodes) {

			List<TestCaseNode> testCases = ChoiceNodeHelper.calculateTestCasesToDeleteForLocalNode(linkedBasicParameterNode);

			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

}
