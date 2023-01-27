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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.TypeHelper;

public abstract class AbstractParameterNodeHelper {

	public static String getName(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = extLanguageManager.convertTextFromIntrToExtLanguage(abstractParameterNode.getName());
		return name;
	}

	public static String getCompositeName( // TODO MO-RE merge overloads into one function ? 
			AbstractParameterNode abstractParameterNode) {

//		AbstractParameterNode currentParameterNode = abstractParameterNode;
//		String compositeName = "";
//		
//		for (;;) {
//
//			String currentParameterNodeName = currentParameterNode.getName();
//			
//			if (StringHelper.isNullOrEmpty(compositeName)) {
//				compositeName = currentParameterNodeName; 
//			} else {
//				compositeName = currentParameterNodeName + SignatureHelper.SIGNATURE_NAME_SEPARATOR + compositeName;
//			}
//			
//			IParametersParentNode parametersParentNode = currentParameterNode.getParent();
//			
//			if (parametersParentNode == null || parametersParentNode instanceof MethodNode) {
//				return compositeName;
//			}
//			
//			if (parametersParentNode instanceof CompositeParameterNode) {
//				currentParameterNode = (AbstractParameterNode) parametersParentNode;
//				continue;
//			}
//			
//			ExceptionHelper.reportRuntimeException("Invalid type of parameters parent.");
//		}
		
		return getCompositeName(abstractParameterNode, null);
	}
	
	public static String getCompositeName(
			AbstractParameterNode abstractParameterNode, 
			IExtLanguageManager extLanguageManager) {

		AbstractParameterNode currentParameterNode = abstractParameterNode;
		String compositeName = "";
		
		for (;;) {

			String currentParameterNodeNameInExtLanguage = getParameterName(currentParameterNode, extLanguageManager);
			
			if (StringHelper.isNullOrEmpty(compositeName)) {
				compositeName = currentParameterNodeNameInExtLanguage; 
			} else {
				compositeName = currentParameterNodeNameInExtLanguage + SignatureHelper.SIGNATURE_NAME_SEPARATOR + compositeName;
			}
			
			IParametersParentNode parametersParentNode = currentParameterNode.getParent();
			
			if (parametersParentNode == null || 
					parametersParentNode instanceof MethodNode || 
					parametersParentNode instanceof ClassNode || 
					parametersParentNode instanceof RootNode) {
				
				return compositeName;
			}
			
			if (parametersParentNode instanceof CompositeParameterNode) {
				currentParameterNode = (AbstractParameterNode) parametersParentNode;
				continue;
			}
			
			ExceptionHelper.reportRuntimeException("Invalid type of parameters parent.");
		}
	}

	private static String getParameterName(AbstractParameterNode currentParameterNode,
			IExtLanguageManager extLanguageManager) {
		
		if (extLanguageManager == null) {
			return currentParameterNode.getName();
		}
		
		return getParameterNameInExtLanguage(currentParameterNode, extLanguageManager);
	}

	private static String getParameterNameInExtLanguage(
			AbstractParameterNode currentParameterNode,
			IExtLanguageManager extLanguageManager) {
		
		String currentParameterNodeNameInIntrLanguage = currentParameterNode.getName();
		
		String currentParameterNodeNameInExtLanguage = 
				extLanguageManager.convertTextFromIntrToExtLanguage(currentParameterNodeNameInIntrLanguage);
		
		return currentParameterNodeNameInExtLanguage;
	}

	public static String validateParameterName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparatorsInName(nameInExternalLanguage);

		return errorMessage;
	}

	public static String getType(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {
		
		if (abstractParameterNode instanceof CompositeParameterNode) {
			return CompositeParameterNode.COMPOSITE_PARAMETER_TYPE;
		}
		
		BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;

		String type = basicParameterNode.getType();
		
		if (type == null) {
			return null;
		}
		
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);
		return type;
	}

	public static String createSignatureOfOneParameterByIntrLanguage(
			String parameterTypeInIntrLanguage,
			String parameterNameInIntrLanguage,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		if (parameterTypeInIntrLanguage != null) {
			String parameterTypeInExtLanguage = extLanguageManager.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage);
			signature += parameterTypeInExtLanguage;
		}

		if (parameterNameInIntrLanguage != null) {

			signature += extLanguageManager.getTypeSeparator();

			if (parameterTypeInIntrLanguage != null) {
				signature += " ";
			}
			
			parameterNameInIntrLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(parameterNameInIntrLanguage);

			signature += parameterNameInIntrLanguage;
		}

		return signature;
	}

	public static String createSignature(
			BasicParameterNode abstractParameterNode, 
			boolean isExpected,
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						getType(abstractParameterNode, extLanguageManager),
						createNameSignature(abstractParameterNode, extLanguageManager),
						isExpected,
						extLanguageManager);

		return signature;
	}

	public static String createSignature(
			CompositeParameterNode compositeParameterNode) {

		return CompositeParameterNode.COMPOSITE_PARAMETER_TYPE + " " + compositeParameterNode.getName();
	}

	public static String createReverseSignature(
			CompositeParameterNode compositeParameterNode) {

		return compositeParameterNode.getName() 
				+ SignatureHelper.SIGNATURE_TYPE_SEPARATOR 
				+ CompositeParameterNode.COMPOSITE_PARAMETER_TYPE;
	}
	
	public static String createSignature(
			String parameterType,
			String parameterName,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		signature += parameterType;

		if (parameterName != null) {

			signature += extLanguageManager.getTypeSeparator();
			signature += " ";
			signature += parameterName;
		}

		return signature;
	}

	public static String createReverseSignature(
			String parameterType,
			String parameterName,
			Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		if (parameterName != null) {
			signature += parameterName;
		}

		if (parameterType != null) {
			signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
			signature += parameterType;
		}

		return signature;
	}

	private static String createExpectedDecoration(Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			if (expectedFlag == true) {
				signature += "[e]";
			}
		}

		return signature;
	}

	public static String createParameterSignature(BasicParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);


		String type = getType(abstractParameterNode, extLanguageManager);

		String label = type + " " + name;
		return label;
	}

	public static String createNameSignature(BasicParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);
		return name;
	}

	public static List<String> convertParameterTypesToExtLanguage(
			List<String> parameterTypes,
			IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (String parameterType : parameterTypes) {

			parameterType = extLanguageManager.convertTypeFromIntrToExtLanguage(parameterType);
			result.add(parameterType);
		}

		return result;
	}

//	public static BasicParameterNode getReferencedParameter(IParametersAndConstraintsParentNode method, BasicParameterNode reference) {
//
//		for (AbstractParameterNode parameter : method.getParameters()) {
//
//			if (parameter instanceof BasicParameterNode) {
//				Optional<BasicParameterNode> parameterParsed = getReferenceParameterBasic((BasicParameterNode) parameter, reference);
//
//				if (parameterParsed.isPresent()) {
//					return parameterParsed.get();
//				}
//			}
//		}
//
//		ExceptionHelper.reportRuntimeException("The referenced method does not contain the required parameter");
//
//		return null;
//	}

//	private static Optional<BasicParameterNode> getReferenceParameterBasic(BasicParameterNode parameter, BasicParameterNode reference) {
//
//		if (parameter.getDeploymentParameter().isLinked()) {
//			if (parameter.getDeploymentParameter().getLinkToGlobalParameter() == reference) {
//				return Optional.of(parameter);
//			}
//		}
//
//		if (parameter.getDeploymentParameter() == reference) {
//			return Optional.of(parameter);
//		}
//
//		if (parameter.getDeploymentParameter() == null) {
//			if (parameter.getName().equals(reference.getName())) {
//				return Optional.of(parameter);
//			}
//		}
//
//		return Optional.empty();
//	}

	public static boolean hasRandomizedChoices(BasicParameterNode abstractParameterNode) {

		Set<ChoiceNode> choices = abstractParameterNode.getAllChoices();

		for (ChoiceNode choice : choices) {

			if (choice.isRandomizedValue()) {
				return true;
			}
		}

		return false;
	}
	
	public static String getMaxJavaTypeFromConversionDefinition( 
			TypeHelper.TypeCathegory javaTypeCathegory,
			ParameterConversionDefinition parameterConversionDefinition) {

		if (parameterConversionDefinition == null) {
			return null;
		}

		int itemCount = parameterConversionDefinition.getItemCount();

		if (itemCount <= 0) {
			return null;
		}

		String resultTypeInIntrLanguage = 
				JavaLanguageHelper.getSmallestTypeForCathegory(javaTypeCathegory);

		for (int index = 0; index < itemCount; index++) {

			String currentValue = parameterConversionDefinition.getCopyOfItem(index).getDstPart().getStr();

			String typeForCurrentValue = 
					JavaLanguageHelper.getMaxTypeForValue(
							currentValue, resultTypeInIntrLanguage, false);

			resultTypeInIntrLanguage = 
					JavaLanguageHelper.getLargerType(resultTypeInIntrLanguage, typeForCurrentValue);
		}

		return resultTypeInIntrLanguage;
	}

}
