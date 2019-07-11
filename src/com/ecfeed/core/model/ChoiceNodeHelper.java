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
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.Pair;
import com.ecfeed.core.utils.SimpleTypeHelper;

import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ecfeed.core.utils.JavaTypeHelper.*;
import static com.ecfeed.core.utils.SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE;
import static com.ecfeed.core.utils.SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE;

public class ChoiceNodeHelper {

	public static ChoiceNode createSubstitutePath(ChoiceNode choice, MethodParameterNode parameter) {
		List<ChoiceNode> copies = createListOfCopies(choice);
		setParentsOfChoices(copies, parameter);
		return copies.get(0);
	}

	private static List<ChoiceNode> createListOfCopies(ChoiceNode choice) {
		ChoiceNode orgChoice = choice;
		List<ChoiceNode> copies = new ArrayList<ChoiceNode>();

		for(;;) {
			ChoiceNode copy = 
					new ChoiceNode(
							orgChoice.getFullName(), orgChoice.getModelChangeRegistrator(), orgChoice.getValueString());
			
			copies.add(copy);

			AbstractNode orgParent = orgChoice.getParent();
			if (!(orgParent instanceof ChoiceNode)) {
				break;
			}

			orgChoice = (ChoiceNode)orgParent;
		}

		return copies;
	}

	private static void setParentsOfChoices(List<ChoiceNode> copies, MethodParameterNode parameter) {
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

	public static Map<String, List<String>> convertToParamAndChoiceNames(MethodNode methodNode, List<List<ChoiceNode>> algorithmInput) {
		
		Map<String, List<String>> paramAndChoiceNames = new HashMap<String, List<String>>();
		
		int parametersCount = methodNode.getParametersCount();
		
		for (int parameterIndex = 0;  parameterIndex < parametersCount;  parameterIndex++) {
			
			String parameterName = methodNode.getParameter(parameterIndex).getFullName();
			List<ChoiceNode> choicesForParameter = algorithmInput.get(parameterIndex);
			
			paramAndChoiceNames.put(parameterName, getChoiceNames(choicesForParameter));
		}
		
		return paramAndChoiceNames;
	}
	
	public static List<String> getChoiceNames(List<ChoiceNode> choiceNodes) {
		
		List<String> choiceNames = new ArrayList<>();
		
		for (ChoiceNode choiceNode : choiceNodes) {
			choiceNames.add(choiceNode.getFullName());
		}
		
		return choiceNames;
	}

	public static Pair<ChoiceNode,ChoiceNode> rangeSplit(ChoiceNode choice)
	{
		if(!choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("rangeStart on non-random choice node");
		ChoiceNode first = choice.makeClone();
		ChoiceNode second = choice.makeClone();

		first.setRandomizedValue(false);
		second.setRandomizedValue(false);
		String[] parts = choice.getValueString().split(":");
		if(parts.length != 2)
			ExceptionHelper.reportRuntimeException("expected two values here");
		first.setValueString(parts[0]);
		second.setValueString(parts[1]);
		return new Pair<>(first,second);
	}

	public static ChoiceNode toRangeFromFirst(ChoiceNode first, ChoiceNode second)
	{
		if(first.isRandomizedValue() || second.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("gluing already randomized values");
		ChoiceNode ret = first.makeClone();
		ret.setRandomizedValue(true);
		ret.setValueString(first.getValueString()+":"+second.getValueString());
		return ret;
	}

	public static ChoiceNode toRangeFromSecond(ChoiceNode first, ChoiceNode second)
	{
		if(first.isRandomizedValue() || second.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("gluing already randomized values");
		ChoiceNode ret = second.makeClone();
		ret.setRandomizedValue(true);
		ret.setValueString(first.getValueString()+":"+second.getValueString());
		return ret;
	}

	private static final double eps = 0.000001;
	public static ChoiceNode precedingVal(ChoiceNode choice)
	{
		if(choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("is randomized value");
		String type = choice.getParameter().getType();
		ChoiceNode clone = choice.makeClone();
		choice = convertValueToNumeric(choice);
		switch(type)
		{
			case TYPE_NAME_DOUBLE:
			case TYPE_NAME_FLOAT: {
				double val = Double.parseDouble(choice.getValueString());
				if(val==0.0)
					val = -eps;
				else if(val<0.0)
					val *= 1+eps;
				else
					val /= 1+eps;
				clone.setValueString(String.valueOf(val));
				return clone;
			}
			case TYPE_NAME_BYTE:
			case TYPE_NAME_INT:
			case TYPE_NAME_SHORT:
			case TYPE_NAME_LONG: {
				long val = Long.parseLong(choice.getValueString());
				if(val!=Long.MIN_VALUE)
					val--;
				clone.setValueString(String.valueOf(val));
				return clone;
			}
			default:
			{
				ExceptionHelper.reportRuntimeException("unhandled type");
				return null;
			}
		}
	}
	public static ChoiceNode followingVal(ChoiceNode choice)
	{
		if(choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("is randomized value");
		String type = choice.getParameter().getType();
		ChoiceNode clone = choice.makeClone();
		choice = convertValueToNumeric(choice);
		switch(type)
		{
			case TYPE_NAME_DOUBLE:
			case TYPE_NAME_FLOAT: {
				double val = Double.parseDouble(choice.getValueString());
				if(val==0.0)
					val = eps;
				else if(val<0.0)
					val /= 1+eps;
				else
					val *= 1+eps;
				clone.setValueString(String.valueOf(val));
				return clone;
			}
			case TYPE_NAME_BYTE:
			case TYPE_NAME_INT:
			case TYPE_NAME_SHORT:
			case TYPE_NAME_LONG: {
				long val = Long.parseLong(choice.getValueString());
				if(val != Long.MAX_VALUE)
					val++;
				clone.setValueString(String.valueOf(val));
				return clone;
			}
			default:
			{
				ExceptionHelper.reportRuntimeException("unhandled type");
				return null;
			}
		}
	}

	public static ChoiceNode roundValueDown(ChoiceNode choiceInput)
	{
		ChoiceNode choice = choiceInput.makeClone();
		if(choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("is randomized value");

		double v = Double.parseDouble(choice.getValueString());
		long w = (long) Math.floor(v);
		choice.setValueString(String.valueOf(w));
		return choice;
	}

	public static ChoiceNode roundValueUp(ChoiceNode choiceInput)
	{
		ChoiceNode choice = choiceInput.makeClone();
		if(choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("is randomized value");

		double v = Double.parseDouble(choice.getValueString());
		long w = (long) Math.ceil(v);
		choice.setValueString(String.valueOf(w));
		return choice;
	}


	public static ChoiceNode convertValueToNumeric(ChoiceNode choiceInput)
	{
		ChoiceNode choice = choiceInput.makeClone();
		if(choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("is randomized value");
		String type = choice.getParameter().getType();
		String valueString = choice.getValueString();
		switch(type) {
			case TYPE_NAME_DOUBLE:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Double.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Double.MAX_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MIN)) {
					choice.setValueString("-" + Double.MIN_VALUE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MAX)) {
					choice.setValueString("-" + Double.MAX_VALUE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
					choice.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_POSITIVE_INF)) {
					choice.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
				}
				break;
			}
			case TYPE_NAME_FLOAT:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Float.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Float.MAX_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MIN)) {
					choice.setValueString("-" + Float.MIN_VALUE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MAX)) {
					choice.setValueString("-" + Float.MAX_VALUE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
					choice.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_POSITIVE_INF)) {
					choice.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
				}
				break;
			}
			case TYPE_NAME_BYTE:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Byte.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Byte.MAX_VALUE + "");
				}
				break;
			}
			case TYPE_NAME_INT:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Integer.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Integer.MAX_VALUE + "");
				}
				break;
			}
			case TYPE_NAME_SHORT:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Short.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Short.MAX_VALUE + "");
				}
				break;
			}
			case TYPE_NAME_LONG:
			{
				if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
					choice.setValueString(Long.MIN_VALUE + "");
				} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
					choice.setValueString(Long.MAX_VALUE + "");
				}
				break;
			}
			default:
			{
				ExceptionHelper.reportRuntimeException("unhandled type");
			}
		}
		return choice;
	}

	public static List<ChoiceNode> interleavedValues(ChoiceNode choice, int N)
	{
		if(! choice.isRandomizedValue())
			ExceptionHelper.reportRuntimeException("should be randomized");
		Pair<ChoiceNode,ChoiceNode> startEnd = rangeSplit(choice);
		ChoiceNode start = convertValueToNumeric(startEnd.getFirst());
		ChoiceNode end = convertValueToNumeric(startEnd.getSecond());
		if(N<2)
			N=2;
		List<ChoiceNode> ret = new ArrayList<>();

		String type = choice.getParameter().getType();
		switch(type)
		{
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

				long w1 = (long) Math.ceil(v1);
				long w2 = (long) Math.floor(v2);
				if(w1 <= w2)
				{
					for(String val : interleavedBigIntegers(String.valueOf(w1),String.valueOf(w2),N))
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

				for(String val : interleavedBigIntegers(v1,v2,N))
				{
					ChoiceNode tmp = start.makeClone();
					tmp.setValueString(val);
					ret.add(tmp);
				}
				return ret;
			}
			default:
			{
				ExceptionHelper.reportRuntimeException("unhandled type");
				return null;
			}
		}
	}

	private static List<String> interleavedBigIntegers(String start, String end, int N)
	{
		BigInteger v1 = new BigInteger(start);
		BigInteger v2 = new BigInteger(end);
		String oldval = "";
		List<String> ret = new ArrayList<>();
		for(int i=0;i<N;i++)
		{
			BigInteger v = v1.multiply(BigInteger.valueOf(N-1-i)).add(v2.multiply(BigInteger.valueOf(i))).divide(BigInteger.valueOf(N-1));
			String newval = String.valueOf(v);
			if(! oldval.equals(newval)) {
				ret.add(newval);
				oldval= newval;
			}
		}
		return ret;
	}

}
