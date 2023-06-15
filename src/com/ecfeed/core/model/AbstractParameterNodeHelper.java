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
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.TypeHelper;

public abstract class AbstractParameterNodeHelper {

	public static String validateParameterName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparatorsInName(nameInExternalLanguage);

		return errorMessage;
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
		
		if (globalParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Global parameter node should not be empty.");
		}

		List<AbstractParameterNode> result = new ArrayList<>();

		IAbstractNode rootNode = RootNodeHelper.findRootNode(globalParameterNode);

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

	public static CompositeParameterNode findTopComposite(IAbstractNode abstractNode) {

		IAbstractNode currentNode = abstractNode;

		CompositeParameterNode topCompositeParameterNode = null;

		if (abstractNode instanceof CompositeParameterNode) {
			topCompositeParameterNode = (CompositeParameterNode) abstractNode;
		}

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

	public static void compareParameterTypes(
			AbstractParameterNode abstractParameter1,
			AbstractParameterNode abstractParameter2) {

		if ((abstractParameter1 instanceof BasicParameterNode) && (abstractParameter2 instanceof CompositeParameterNode)) {

			ExceptionHelper.reportRuntimeException("Types of nodes do not match: basic parameter vs composite parameter.");
		}

		if ((abstractParameter1 instanceof CompositeParameterNode) && (abstractParameter2 instanceof BasicParameterNode)) {

			ExceptionHelper.reportRuntimeException("Types of nodes do not match: composite parameter vs basic parameter.");
		}

	}

	private enum ParameterPathType {
		PATH_CONTAINTS_TOP_NODE,
		PATH_WITHOUT_TOP_NODE
	}

	public static AbstractParameterNode findParameter(
			String path, 
			IParametersParentNode parent) {

		if (path.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {

			IAbstractNode topNode = AbstractNodeHelper.findTopNode(parent);

			if (!(topNode instanceof RootNode)) {
				ExceptionHelper.reportRuntimeException("Cannot find root node.");
			}

			AbstractParameterNode parameter = 
					AbstractParameterNodeHelper.findParameterByRelativePath(
							path, 
							AbstractParameterNodeHelper.ParameterPathType.PATH_CONTAINTS_TOP_NODE, 
							(RootNode)topNode);

			return parameter;
		}

		AbstractParameterNode parameter = 
				AbstractParameterNodeHelper.findParameterByRelativePath(
						path, 
						AbstractParameterNodeHelper.ParameterPathType.PATH_WITHOUT_TOP_NODE, 
						parent);

		return parameter;
	}

	public static AbstractParameterNode findParameterByRelativePath(  // TODO MO-RE old name findParameterByAbsolutePath
			String path, ParameterPathType parameterPathType, IParametersParentNode topNode) {

		if ((parameterPathType == ParameterPathType.PATH_WITHOUT_TOP_NODE) 
				&& (path.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER))) {
			ExceptionHelper.reportRuntimeException("Invalid path. Path with root marker not expected.");
		}

		if ((topNode instanceof RootNode) && (!path.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER))) {
			ExceptionHelper.reportRuntimeException("Invalid path. Path with root marker expected.");
		}

		String formattedPath = formatSearchPath(path, parameterPathType);
		
		IAbstractNode foundAbstractNode = topNode.getChild(formattedPath);
		
		if (!(foundAbstractNode instanceof AbstractParameterNode)) {
			return null;
		}
		
		return (AbstractParameterNode) foundAbstractNode;
	}

	private static String formatSearchPath(String path, ParameterPathType parameterPathType) {
		
		String formattedPath = path;

		if (path.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {
			formattedPath = path.substring(1);
		}
		
		if (parameterPathType == ParameterPathType.PATH_CONTAINTS_TOP_NODE) {
			formattedPath = StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, formattedPath);
		}
		
		return formattedPath;
	}

	public static void compareParameters(
			AbstractParameterNode abstractParameter1, 
			AbstractParameterNode abstractParameter2) {

		if (abstractParameter1 == null && abstractParameter2 == null) {
			return;
		}

		AbstractParameterNodeHelper.compareParameterTypes(abstractParameter1, abstractParameter2);

		NameHelper.compareNames(abstractParameter1.getName(), abstractParameter2.getName());

		if ((abstractParameter1 instanceof BasicParameterNode) && (abstractParameter2 instanceof BasicParameterNode)) {

			BasicParameterNode basicParameterNode1 = (BasicParameterNode) abstractParameter1;
			BasicParameterNode basicParameterNode2 = (BasicParameterNode) abstractParameter2;

			BasicParameterNodeHelper.compareParameters(basicParameterNode1, basicParameterNode2);
			return;
		}

		if ((abstractParameter1 instanceof CompositeParameterNode) && (abstractParameter2 instanceof CompositeParameterNode)) {

			CompositeParameterNode basicParameterNode1 = (CompositeParameterNode) abstractParameter1;
			CompositeParameterNode basicParameterNode2 = (CompositeParameterNode) abstractParameter2;

			CompositeParameterNodeHelper.compareParameters(basicParameterNode1, basicParameterNode2);
			return;
		}

		ExceptionHelper.reportRuntimeException("Unhandled combination of parameter types.");
	}

}
