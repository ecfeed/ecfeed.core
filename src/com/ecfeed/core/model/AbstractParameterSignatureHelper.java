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

	private static final String EXPECTED_DECORATION = "[EXP]";	
	private static final String SHORTENED_LINK_TEXT = ":";
	private static final String LINK_SPECIFIER_TEXT = "->";

	public enum ExtendedName {
		IRRELEVANT,
		EMPTY,
		NAME_ONLY,
		PATH_TO_TOP_CONTAINTER,
		PATH_TO_TOP_CONTAINTER_WITHOUT_TOP_LINKED_ITEM // example: for LS1->GS1:GP1 this path of parameter (only) should be GP1, usage in compressed signature: LS1:GP1
	}

	public enum TypeOfLink {
		IRRELEVANT,
		NORMAL,
		SHORTENED
	}

	public enum Decorations {
		NO,
		YES
	}

	public enum TypeIncluded {
		NO,
		YES
	}

	public static String createSignatureOldStandard(
			AbstractParameterNode parameter,
			IExtLanguageManager extLanguageManager) {

		String signature = createSignatureOfParameterWithLinkNewStandard(
				parameter,
				ExtendedName.NAME_ONLY,
				TypeOfLink.IRRELEVANT,
				null,
				ExtendedName.IRRELEVANT,
				Decorations.NO,
				TypeIncluded.YES,
				extLanguageManager);

		signature = signature.replaceAll(" ", "");
		String[] elements = signature.split(":");

		return elements[1] + " " + elements[0];
	}

	public static String createSignatureOfParameterNewStandard(
			AbstractParameterNode parameter,
			ExtendedName extendedNameTypeOfParameter,
			Decorations decorations,
			TypeIncluded typeIncluded,
			IExtLanguageManager extLanguageManager) {

		String signature = createSignatureOfParameterWithLinkNewStandard(
				parameter,
				extendedNameTypeOfParameter,
				TypeOfLink.IRRELEVANT,
				null,
				ExtendedName.IRRELEVANT,
				decorations,
				typeIncluded,
				extLanguageManager);

		return signature;
	}

	public static String createSignatureOfParameterWithLinkNewStandard( 
			AbstractParameterNode parameterWhichHasLink, // may be parameter with link or linking context
			ExtendedName extendedNameTypeOfParameter,
			TypeOfLink typeOfLink,
			AbstractParameterNode link,
			ExtendedName extendedNameTypeOfLink,
			Decorations decorations,
			TypeIncluded typeIncluded,
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignatureOfParameterNameNewStandard(
						parameterWhichHasLink, 
						extendedNameTypeOfParameter,
						typeOfLink,
						link,
						extendedNameTypeOfLink,
						extLanguageManager);

		if (decorations == Decorations.YES && parameterWhichHasLink instanceof BasicParameterNode) {

			BasicParameterNode basicParameterNode = (BasicParameterNode) parameterWhichHasLink;

			if (basicParameterNode.isExpected()) {
				signature += EXPECTED_DECORATION;
			}
		}

		if (typeIncluded == TypeIncluded.YES) {

			if (extendedNameTypeOfParameter != ExtendedName.EMPTY) {
				signature += SignatureHelper.SIGNATURE_TYPE_SEPARATOR;
			}

			String type = createSignatureOfParameterTypeNewStandard(parameterWhichHasLink, extLanguageManager);
			signature += type;
		}

		return signature;
	}

	public static String createSignatureOfParameterWithContextOrLinkNewStandard(
			AbstractParameterNode parameter,
			AbstractParameterNode context,
			IExtLanguageManager extLanguageManager) {

		if (context == null) {

			String signatureOfParameterWithLink = 
					AbstractParameterSignatureHelper.createSignatureOfParameterWithLinkNewStandard(
							parameter,
							ExtendedName.PATH_TO_TOP_CONTAINTER,
							TypeOfLink.NORMAL,
							parameter.getLinkToGlobalParameter(),
							ExtendedName.PATH_TO_TOP_CONTAINTER,
							Decorations.NO,
							TypeIncluded.NO,
							extLanguageManager);

			return signatureOfParameterWithLink;
		}

		String signatureOfParameterWithContext = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithLinkNewStandard(
						context,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						TypeOfLink.NORMAL,
						parameter,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						Decorations.NO,
						TypeIncluded.NO,
						extLanguageManager);

		return signatureOfParameterWithContext;
	}

	public static String createPathToTopContainerNewStandard( // former getQualifiedName
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = createSignatureToTopContainerNewStandard(abstractParameterNode);

		if (extLanguageManager != null) {
			qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);
		}

		return qualifiedName;
	}

	public static String createPathToRootNewStandard(
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = createSignatureToRootNewStandard(abstractParameterNode);

		if (extLanguageManager != null) {
			qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);
		}

		return qualifiedName;
	}

	public static String createSignatureOfLocalOrGlobalParameterNewStandard(AbstractParameterNode parameter) {

		String signature;

		if (parameter.isGlobalParameter()) {

			signature = createPathToRootNewStandard(parameter, new ExtLanguageManagerForJava());

		} else {

			signature = createSignatureOfParameterNewStandard(
					parameter, ExtendedName.PATH_TO_TOP_CONTAINTER, Decorations.NO, TypeIncluded.NO, 
					new ExtLanguageManagerForJava());

		}

		return signature;
	}

	private static String createSignatureOfParameterNameNewStandard(
			AbstractParameterNode parameterWhichHasLink,
			ExtendedName extendedNameTypeOfParameter,
			TypeOfLink typeOfLink,
			AbstractParameterNode link,
			ExtendedName extendedNameTypeOfLink,
			IExtLanguageManager extLanguageManager) {

		if (parameterWhichHasLink == null && link == null) {
			ExceptionHelper.reportRuntimeException("Attempt to create signature of empty parameter and link.");
		}

		if (link == null) {

			String signatureOfParameterOnly = 
					createSignatureOfSingleParameterNameNewStandard(
							parameterWhichHasLink, 
							extendedNameTypeOfParameter,
							extLanguageManager);

			return signatureOfParameterOnly;
		}

		if (parameterWhichHasLink == null) {

			String signatureOfLinkOnly =
					createSignatureOfSingleParameterNameNewStandard(
							link, 
							extendedNameTypeOfLink,
							extLanguageManager);

			return signatureOfLinkOnly;
		}

		String signatureOfParameter = 
				createSignatureOfSingleParameterNameNewStandard(
						parameterWhichHasLink, 
						extendedNameTypeOfParameter,
						extLanguageManager);

		String signatureOfLink = 
				createSignatureOfSingleParameterNameNewStandard(
						link, 
						extendedNameTypeOfLink,
						extLanguageManager);

		String signature = signatureOfParameter + getLinkSpecifier(typeOfLink)  + signatureOfLink;

		return signature;
	}

	private static String getLinkSpecifier(TypeOfLink typeOfLink) {

		if (typeOfLink == TypeOfLink.SHORTENED) {
			return SHORTENED_LINK_TEXT;
		}

		return LINK_SPECIFIER_TEXT;
	}

	private static String createSignatureOfSingleParameterNameNewStandard(
			AbstractParameterNode abstractParameterNode,
			ExtendedName extendedNameType,
			IExtLanguageManager extLanguageManager) {

		if (extendedNameType == ExtendedName.NAME_ONLY) {
			return abstractParameterNode.getName();
		}

		if (extendedNameType == ExtendedName.PATH_TO_TOP_CONTAINTER) {

			String signature = createPathToTopContainerNewStandard(abstractParameterNode, extLanguageManager);
			return signature;
		}

		if (extendedNameType == ExtendedName.PATH_TO_TOP_CONTAINTER_WITHOUT_TOP_LINKED_ITEM) {

			String signature = createPathToTopContainerNewStandard(abstractParameterNode, extLanguageManager);
			signature = StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, signature);

			return signature;
		}

		return "";
	}

	private static String createSignatureToTopContainerNewStandard(AbstractParameterNode abstractParameterNode) { 

		LinkedList<String> segments = new LinkedList<>();

		IAbstractNode parent = abstractParameterNode;

		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (!(parent == null || parent instanceof RootNode || parent instanceof MethodNode));

		String signature = String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, segments);
		return signature;
	}

	private static String createSignatureToRootNewStandard(
			AbstractParameterNode abstractParameterNode) { 

		LinkedList<String> segments = new LinkedList<>();

		IAbstractNode parent = abstractParameterNode;

		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (parent != null);

		String signature = String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, segments);
		String signatureWithRootMarker = SignatureHelper.SIGNATURE_ROOT_MARKER + signature;

		return signatureWithRootMarker;
	}


	public static String createSignatureOfParameterTypeNewStandard( // former createSignatureOfType
			AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

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

	/////////////////////////////////////////////////////////////////////////////////////////////

	// OBSOLETE
	private static String createSignatureOfSingleParameterName(
			AbstractParameterNode abstractParameterNode) {

		printObsoleteInfo();

		String signature = getQualifiedName(abstractParameterNode, new ExtLanguageManagerForJava());

		return signature;
	}

	// OBSOLETE
	public static String createSignatureOfParameterWithContext(
			AbstractParameterNode parameter,
			AbstractParameterNode context) {

		printObsoleteInfo();

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Attempt to create signature of empty parameter.");
		}

		if (context == null) {

			String signatureOfParameter = 
					createSignature(parameter, new ExtLanguageManagerForJava());

			return signatureOfParameter;
		}

		String signatureOfContext = 
				createSignatureWithPathToTopParametersParent(
						context, new ExtLanguageManagerForJava());

		String signatureOfParameter = 
				createSignatureWithPathToTopParametersParent(
						parameter, new ExtLanguageManagerForJava());

		return signatureOfContext + LINK_SPECIFIER_TEXT + signatureOfParameter;
	}

	// OBSOLETE
	public static String createSignature(
			AbstractParameterNode globalParameterNode, 
			SignatureHelper.SignatureType signatureType,
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

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

	// OBSOLETE
	public static String createSignatureOfType(BasicParameterNode globalParameterNode, IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

		String type = globalParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

	// OBSOLETE
	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(abstractParameterNode);

		if (extLanguageManager != null) {
			qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);
		}

		return qualifiedName;
	}

	// OBSOLETE
	public static String getQualifiedName(AbstractParameterNode abstractParameterNode) { // TODO MO-RE remove and use createSignatureWithPathToTopParametersParent instead

		printObsoleteInfo();

		LinkedList<String> segments = new LinkedList<>();

		IAbstractNode parent = abstractParameterNode;

		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (!(parent == null || parent instanceof RootNode || parent instanceof MethodNode));

		return String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, segments);
	}

	// OBSOLETE
	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext,
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

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

	// OBSOLETE
	public static String getQualifiedName(  // TODO MO-RE convert to create signature
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext) {

		printObsoleteInfo();

		String qualifiedName = getQualifiedName(abstractParameterNode, linkingContext, null);
		return qualifiedName;
	}

	// OBSOLETE
	public static String getCompositeName(AbstractParameterNode abstractParameterNode) {

		//		return getCompositeName(abstractParameterNode, null);

		printObsoleteInfo();

		AbstractParameterNode currentParameterNode = abstractParameterNode;
		String compositeName = "";

		for (;;) {

			String currentParameterNodeName = createSignatureOfSingleParameterName(currentParameterNode);

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

	// OBSOLETE
	public static String createSignatureOfOneParameterByIntrLanguage(
			String parameterTypeInIntrLanguage,
			String parameterNameInIntrLanguage,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

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

	// OBSOLETE
	public static String createSignature(
			CompositeParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

		String signatureOfName = createSignatureOfParameterNewStandard(
				parameter,
				ExtendedName.NAME_ONLY,	Decorations.NO, TypeIncluded.NO,
				extLanguageManager);

		String signature = 
				createSignature(
						createSignatureOfParameterTypeNewStandard(parameter, extLanguageManager),
						signatureOfName,
						false,
						extLanguageManager);

		return signature;
	}	

	// OBSOLETE
	public static String createSignature(
			String parameterType,
			String parameterName,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

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

	// OBSOLETE
	public static String createSignature(
			BasicParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

		//		String signature = 
		//				createSignature(
		//						createSignatureOfType(parameter, extLanguageManager),
		//						createSignatureOfParameterName(parameter, extLanguageManager),
		//						parameter.isExpected(),
		//						extLanguageManager);

		String signatureOfParameterName = 
				createSignatureOfParameterNewStandard(
						parameter,
						ExtendedName.NAME_ONLY,	Decorations.NO, TypeIncluded.NO,
						extLanguageManager);

		String signature = 
				createSignature(
						createSignatureOfType(parameter, extLanguageManager),
						signatureOfParameterName,
						parameter.isExpected(),
						extLanguageManager);

		return signature;
	}

	// OBSOLETE
	public static String createReverseSignature(
			String type, 
			String name, 
			Boolean expected) {

		printObsoleteInfo();

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

	// OBSOLETE
	private static String createExpectedDecoration(Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			if (expectedFlag == true) {
				signature += "[EXP]";
			}
		}

		return signature;
	}

	// OBSOLETE
	public static String createParameterSignature(BasicParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		printObsoleteInfo();

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);


		String type = createSignatureOfType(abstractParameterNode, extLanguageManager);

		String label = type + " " + name;
		return label;
	}

	// OBSOLETE
	public static String createSignature(AbstractParameterNode parameter,  // TODO MO-RE remove this method ?
			ExtLanguageManagerForJava extLanguageManagerForJava) {

		return getQualifiedName(parameter);
	}

	// OBSOLETE
	public static String createSignatureWithPathToTopParametersParent(
			AbstractParameterNode abstractParameterNode,
			IExtLanguageManager extLanguageManager) {

		return getQualifiedName(abstractParameterNode, extLanguageManager);
	}

	private static void printObsoleteInfo() {
		//		System.out.println("OBSOLETE FUNCTION");
	}

}
