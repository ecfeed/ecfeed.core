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

import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SignatureHelper;

public class MethodDeploymentConsistencyUpdater {

	public static void makeModelConsistent(RootNode root) {

		for (ClassNode classNode : root.getClasses()) {
			for (MethodNode methodNode : classNode.getMethods()) {
				if (validateDeploymentSizeConsistency(methodNode)) {
					updateDeploymentNameConsistency(methodNode);
				}
			}
		}
	}

	public static boolean validateDeploymentSizeConsistency(MethodNode method) {

		if (!method.isDeployed()) {
			return false;
		}

		List<BasicParameterNode> deployment = method.getDeployedMethodParameters();

		if (deployment == null) {
			return false;
		}

		return getNestedSize(method) == deployment.size();
	}

	private static void updateDeploymentNameConsistency(MethodNode method) {

		if (!method.isDeployed()) {
			return;
		}

		List<BasicParameterNode> deployment = method.getDeployedMethodParameters();

		if (deployment == null) {
			return;
		}

		for (BasicParameterNode parameter : deployment) {
			AbstractParameterNode parameterReference = parameter.getDeploymentParameter();
			parameter.setNameUnsafe(getQualifiedDeploymentName(parameterReference));
		}
	}

	public static BasicParameterNode getNestedBasicParameter(AbstractParameterNode parameter, String[] path, int index) {

		if (parameter instanceof BasicParameterNode) {
			return (BasicParameterNode) parameter;
		}

		try {
			CompositeParameterNode element = (CompositeParameterNode) parameter;
			AbstractParameterNode elementNested = element.getParameter(element.getParameterIndex(path[index]));

			return getNestedBasicParameter(elementNested, path, index + 1);
		}
		catch (Exception e) {
			ExceptionHelper.reportRuntimeException("The parameter '" + parameter.getName() + "'could not be parsed.");
		}

		return null;
	}

	private static int getNestedSize(MethodNode method) {
		int size = 0;

		for (AbstractParameterNode parameter : method.getParameters()) {
			size = getNestedSize(parameter, size);
		}

		return size;
	}

	private static int getNestedSize(AbstractParameterNode parameter, int size) {
		
		if (parameter instanceof BasicParameterNode) {
			size++;
		} else if (parameter instanceof CompositeParameterNode) {
			
			if (parameter.isLinked() && (parameter.getLinkToGlobalParameter() != null)) {
				parameter = parameter.getLinkToGlobalParameter();
			}
			
			List<AbstractParameterNode> parameters = ((CompositeParameterNode) parameter).getParameters();

			for (AbstractParameterNode parameterNested : parameters) {
				size = getNestedSize(parameterNested, size);
			}
		}

		return size;
	}

	private static String getQualifiedDeploymentName(AbstractParameterNode parameter) {

		return getQualifiedDeploymentName(parameter, "");
	}

	private static String getQualifiedDeploymentName(AbstractParameterNode parameter, String prefix) {
		String prefixParsed = parameter.getName() + prefix;

		IAbstractNode parent = parameter.getParent();

		if (parent instanceof AbstractParameterNode) {
			return getQualifiedDeploymentName((AbstractParameterNode) parent, SignatureHelper.SIGNATURE_NAME_SEPARATOR + prefixParsed);
		}

		return prefixParsed;
	}

}
