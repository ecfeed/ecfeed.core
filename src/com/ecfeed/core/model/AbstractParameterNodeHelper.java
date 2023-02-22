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
import java.util.LinkedList;
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

	public static String createSignature(
			AbstractParameterNode globalParameterNode, 
			SignatureHelper.SignatureType signatureType,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(globalParameterNode, extLanguageManager);

		if (signatureType == SignatureHelper.SignatureType.WITHOUT_TYPE) {
			return qualifiedName;
		}

		String type = "";

		if (globalParameterNode instanceof BasicParameterNode) {

			BasicParameterNode basicParameterNode = (BasicParameterNode)globalParameterNode;
			type = getType(basicParameterNode, extLanguageManager);
		}

		return (type + " " + qualifiedName).trim();
	}

	public static String getQualifiedName(
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(abstractParameterNode);
		
		if (extLanguageManager != null) {
			qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);
		}

		return qualifiedName;
	}

	public static String getQualifiedName(AbstractParameterNode abstractParameterNode) {

		LinkedList<String> segments = new LinkedList<>();

		IAbstractNode parent = abstractParameterNode;

		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (!(parent == null || parent instanceof RootNode || parent instanceof MethodNode));

		return String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, segments);
	}

	public static String getQualifiedName(
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext,
			IExtLanguageManager extLanguageManager) {

		if (linkingContext == null) {
			return getQualifiedName(abstractParameterNode, extLanguageManager);
		}

		String ownQualifiedName = getQualifiedName(abstractParameterNode, extLanguageManager);

		String ownQualifiedNameWithoutPrefix = 
				StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, ownQualifiedName);

		String linkingSignature = getQualifiedName(linkingContext, extLanguageManager);

		return linkingSignature + SignatureHelper.SIGNATURE_NAME_SEPARATOR + ownQualifiedNameWithoutPrefix;
	}

	public static String getQualifiedName(
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext) {

		String qualifiedName = getQualifiedName(abstractParameterNode, linkingContext, null);
		return qualifiedName;
	}

	public static String getQualifiedName(String linkingContext, String parameterName) {

		if (linkingContext == null) {
			return parameterName;
		}

		if (parameterName.startsWith("[G]:")) {
			return parameterName.substring(4);
		}

		return linkingContext + SignatureHelper.SIGNATURE_NAME_SEPARATOR + parameterName;
	}

	public static String getType(BasicParameterNode globalParameterNode, IExtLanguageManager extLanguageManager) {

		String type = globalParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

	public static String getName(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = extLanguageManager.convertTextFromIntrToExtLanguage(abstractParameterNode.getName());
		return name;
	}

	public static String getCompositeName(AbstractParameterNode abstractParameterNode) {

		//		return getCompositeName(abstractParameterNode, null);

		AbstractParameterNode currentParameterNode = abstractParameterNode;
		String compositeName = "";

		for (;;) {

			String currentParameterNodeName = currentParameterNode.getName();

			if (StringHelper.isNullOrEmpty(compositeName)) {
				compositeName = currentParameterNodeName; 
			} else {
				compositeName = currentParameterNodeName + SignatureHelper.SIGNATURE_NAME_SEPARATOR + compositeName;
			}

			IParametersParentNode parametersParentNode = currentParameterNode.getParent();

			if (parametersParentNode instanceof CompositeParameterNode) {
				currentParameterNode = (AbstractParameterNode) parametersParentNode;
				continue;
			}

			return compositeName;
		}
	}

	public static String getCompositeName( // TODO MO-RE there are methods getQualifiedName in this module
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

	//--------------------------------------------------------------------------------------------------		

	public static String createSignature(
			BasicParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						getType(parameter, extLanguageManager),
						createNameSignature(parameter, extLanguageManager),
						parameter.isExpected(),
						extLanguageManager);

		return signature;
	}

	public static String createSignature(
			CompositeParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						getType(parameter, extLanguageManager),
						createNameSignature(parameter, extLanguageManager),
						false,
						extLanguageManager);

		return signature;
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

		if (parameterType != null) {
			signature += parameterType;
		}

		if (parameterName != null) {

			signature += extLanguageManager.getTypeSeparator();
			signature += " ";
			signature += parameterName;
		}

		return signature;
	}

	public static String createReverseSignature(
			BasicParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		//		IAbstractNode parent = parameter.getParent();
		String parentCompositeSignature = "";

		//		if (parent instanceof CompositeParameterNode) {
		//			
		//			CompositeParameterNode compositeParameterNode = (CompositeParameterNode) parent;
		//			
		//			parentCompositeSignature = compositeParameterNode.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;
		//		}

		String name = createNameSignature(parameter, extLanguageManager);
		String type = getType(parameter, extLanguageManager);

		String signature = parentCompositeSignature + createReverseSignature(type, name, parameter.isExpected());

		if (parameter.isLinked()) {

			signature += addLinkedPrefix(parameter, extLanguageManager);
		}

		return signature;
	}

	public static String createReverseSignature(
			CompositeParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String name = createNameSignature(parameter, extLanguageManager);

		String signature = name;
		signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
		signature += CompositeParameterNode.COMPOSITE_PARAMETER_TYPE;

		if (parameter.isLinked()) {
			signature += addLinkedPrefix(parameter, extLanguageManager);
		}

		return signature;
	}

	public static String createReverseSignature(
			String type, 
			String name, 
			Boolean expected) {

		String signature = "";

		if (expected != null) {
			String expectedDecoration = createExpectedDecoration(expected);
			signature += expectedDecoration;
		}

		if (name != null) {
			signature += name;
		}

		if (type != null) {
			signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
			signature += type;
		}

		return signature;
	}

	private static String addLinkedPrefix(
			AbstractParameterNode parameter, 
			IExtLanguageManager languageManager) {

		AbstractParameterNode parameterLinked = parameter.getLinkToGlobalParameter();

		if (parameterLinked != null) {
			return " [LINKED]->" + getQualifiedName(parameterLinked, languageManager);
		}

		return "";
	}

	//--------------------------------------------------------------------------------------------------	

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

	public static String createNameSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

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

	public static List<AbstractParameterNode> getLinkedParameters(AbstractParameterNode globalParameterNode) {

		List<AbstractParameterNode> result = new ArrayList<>();

		IAbstractNode rootNode = AbstractNodeHelper.findRoot(globalParameterNode);

		getParametersLinkedToGlobalParameterRecursive(globalParameterNode, rootNode, result);

		return result;
	}

	private static void getParametersLinkedToGlobalParameterRecursive(
			AbstractParameterNode globBasicParameterNode,
			IAbstractNode currentNode,
			List<AbstractParameterNode> inOutLinkedParameters) {

		if ((currentNode instanceof AbstractParameterNode) &&
				isParameterLinkedToGlobal((AbstractParameterNode) currentNode, globBasicParameterNode)) {

			inOutLinkedParameters.add((AbstractParameterNode) currentNode);
			return;
		}

		if ((currentNode instanceof ChoiceNode)) {
			return;
		}

		List<IAbstractNode> children = currentNode.getChildren();

		for (IAbstractNode childNode : children) {
			getParametersLinkedToGlobalParameterRecursive(globBasicParameterNode, childNode, inOutLinkedParameters);
		}
	}

	private static boolean isParameterLinkedToGlobal(
			AbstractParameterNode currentParameter,
			AbstractParameterNode globalBasicParameterNode) {

		AbstractParameterNode linkToGlobalParameter = currentParameter.getLinkToGlobalParameter();

		if (linkToGlobalParameter == null) {
			return false;
		}

		if (linkToGlobalParameter == globalBasicParameterNode) {
			return true;
		}

		return false;
	}

	public static boolean parameterMentionsBasicParameter(
			AbstractParameterNode abstractParameterNode,
			BasicParameterNode basicParameterNode) {

		if (abstractParameterNode instanceof BasicParameterNode) {
			return BasicParameterNodeHelper.parameterMentionsBasicParameter(
					(BasicParameterNode)abstractParameterNode, basicParameterNode);
		}

		if (abstractParameterNode instanceof CompositeParameterNode) {
			return CompositeParameterNodeHelper.parameterMentionsBasicParameter(
					(CompositeParameterNode)abstractParameterNode, basicParameterNode);
		}

		return false;
	}

	public static CompositeParameterNode getTopComposite(IAbstractNode abstractNode) {

		IAbstractNode currentNode = abstractNode;

		CompositeParameterNode topCompositeParameterNode = null;

		for (;;) {

			IAbstractNode parent = currentNode.getParent();

			if (parent == null || parent instanceof ClassNode || parent instanceof RootNode) {
				return topCompositeParameterNode;
			}

			if (parent instanceof CompositeParameterNode) {
				topCompositeParameterNode = (CompositeParameterNode) parent;
			}

			currentNode = parent;
		}
	}

}
