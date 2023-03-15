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

import java.util.LinkedList;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class AbstractParameterSignatureHelper {

	private static final String LINK_SPECIFIER = "->";

	public static String createSignatureOfParameterWithContext(
			AbstractParameterNode parameter,
			AbstractParameterNode context) {

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Attempt to create signature of empty parameter.");
		}

		if (context == null) {

			String signatureOfParameter = 
					createSignature(parameter, new ExtLanguageManagerForJava());  // TODO MO-RE

			return signatureOfParameter;
		}

		String signatureOfContext = 
				createSignatureWithPathToTopParametersParent(
						context, new ExtLanguageManagerForJava());  // TODO MO-RE

		String signatureOfParameter = 
				createSignatureWithPathToTopParametersParent(
						parameter, new ExtLanguageManagerForJava());  // TODO MO-RE

		return signatureOfContext + LINK_SPECIFIER + signatureOfParameter;
	}

	public static String createSignatureOfParameterWithLink(
			AbstractParameterNode parameter,
			AbstractParameterNode link) {

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Attempt to create signature of empty parameter.");
		}

		if (link == null) {

			String signatureOfParameter = 
					createSignature(parameter, new ExtLanguageManagerForJava());  // TODO MO-RE

			return signatureOfParameter;
		}

		String signatureOfLink = 
				createSignatureWithPathToTopParametersParent(
						link, new ExtLanguageManagerForJava());  // TODO MO-RE

		String signatureOfParameter = 
				createSignatureWithPathToTopParametersParent(
						parameter, new ExtLanguageManagerForJava());  // TODO MO-RE

		return signatureOfParameter + LINK_SPECIFIER  + signatureOfLink;
	}


	public static String createSignature( // XYX in use
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
			type = createSignatureOfType(basicParameterNode, extLanguageManager);
		}

		return (type + " " + qualifiedName).trim();
	}
	
	public static String createSignatureOfType(BasicParameterNode globalParameterNode, IExtLanguageManager extLanguageManager) {

		String type = globalParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(abstractParameterNode);

		if (extLanguageManager != null) {
			qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);
		}

		return qualifiedName;
	}

	public static String getQualifiedName(AbstractParameterNode abstractParameterNode) { // TODO MO-RE remove and use createSignatureWithPathToTopParametersParent instead 

		LinkedList<String> segments = new LinkedList<>();

		IAbstractNode parent = abstractParameterNode;

		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (!(parent == null || parent instanceof RootNode || parent instanceof MethodNode));

		return String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, segments);
	}

	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext,
			IExtLanguageManager extLanguageManager) {

		if (linkingContext == null || !abstractParameterNode.isGlobalParameter()) {
			return getQualifiedName(abstractParameterNode, extLanguageManager);
		}

		String ownQualifiedName = getQualifiedName(abstractParameterNode, extLanguageManager);

		String ownQualifiedNameWithoutPrefix = StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, ownQualifiedName);

		if (abstractParameterNode.isClassParameter()) {
			ownQualifiedNameWithoutPrefix = StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, ownQualifiedNameWithoutPrefix);
		}

		CompositeParameterNode candidate = null;

		parameterLoop:
			for (CompositeParameterNode candidateComposite : linkingContext.getNestedCompositeParameters(false)) {
				for (AbstractParameterNode candidateParametr : candidateComposite.getLinkDestination().getParameters()) {
					if (candidateParametr == abstractParameterNode) {
						candidate = candidateComposite;
						break parameterLoop;
					}
				}
			}

		String linkingSignature = getQualifiedName(candidate != null ? candidate : linkingContext, extLanguageManager);

		return linkingSignature + SignatureHelper.SIGNATURE_NAME_SEPARATOR + ownQualifiedNameWithoutPrefix;
	}

	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext) {

		String qualifiedName = getQualifiedName(abstractParameterNode, linkingContext, null);
		return qualifiedName;
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

		if (parameterNameInIntrLanguage != null) {

//			signature += extLanguageManager.getTypeSeparator();
//
//			if (parameterTypeInIntrLanguage != null) {
//				signature += " ";
//			}

			parameterNameInIntrLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(parameterNameInIntrLanguage);

			signature += parameterNameInIntrLanguage;
		}
		
		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		if (parameterTypeInIntrLanguage != null) {
			
			signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
			
			String parameterTypeInExtLanguage = extLanguageManager.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage);
			signature += parameterTypeInExtLanguage;
		}

		return signature;
	}

	public static String createSignature(
			CompositeParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						getType(parameter, extLanguageManager),
						createSignatureOfParameterName(parameter, extLanguageManager),
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

	public static String createReverseSignatureWithOptionalLink(
			BasicParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String type = createSignatureOfType(parameter, extLanguageManager);

		String signature = createReverseSignatureOfAbstractParameter(parameter, type);

		return signature;
	}

	public static String createReverseSignatureWithOptionalLink(
			CompositeParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String type = CompositeParameterNode.COMPOSITE_PARAMETER_TYPE;

		String signature = createReverseSignatureOfAbstractParameter(parameter, type);

		return signature;

	}

	public static String createSignature( // TODO MO-RE remove ?
			BasicParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						createSignatureOfType(parameter, extLanguageManager),
						createSignatureOfParameterName(parameter, extLanguageManager),
						parameter.isExpected(),
						extLanguageManager);

		return signature;
	}

	private static String createReverseSignatureOfAbstractParameter(AbstractParameterNode parameter, String type) {

		String signature = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithLink(
						parameter, parameter.getLinkToGlobalParameter());

		signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
		signature += type;

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

	private static String createExpectedDecoration(Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			if (expectedFlag == true) {
				signature += "[EXP]";
			}
		}

		return signature;
	}


	public static String createParameterSignature(BasicParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);


		String type = createSignatureOfType(abstractParameterNode, extLanguageManager);

		String label = type + " " + name;
		return label;
	}

	public static String createSignature(AbstractParameterNode parameter,  // TODO MO-RE remove this method ?
			ExtLanguageManagerForJava extLanguageManagerForJava) {

		return getQualifiedName(parameter);
	}

	public static String createSignatureWithPathToTopParametersParent(
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		return getQualifiedName(abstractParameterNode, extLanguageManager);
	}

	public static String createSignatureOfParameterName(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);
		return name;
	}
	
}
