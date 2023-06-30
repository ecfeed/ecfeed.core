/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.operations.link.MethodParameterOperationSetLink;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnBasicParameterOperationConvertToGlobal extends CompositeOperation{

	public OnBasicParameterOperationConvertToGlobal(
			BasicParameterNode localParameterToConvert, 
			IParametersParentNode newParametersParentNode,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REPLACE_PARAMETER_WITH_LINK, true, localParameterToConvert, localParameterToConvert, extLanguageManager);

		//MethodNode methodNode = MethodNodeHelper.findMethodNode(localParameterToConvert);
		
		NodeMapper nodeMapper = new NodeMapper();
		Optional<NodeMapper> optNodeMapper = Optional.of(nodeMapper);
		
		BasicParameterNode newGlobalBasicParameterNode = 
				localParameterToConvert.makeClone(optNodeMapper);
		
		nodeMapper.removeMappings(localParameterToConvert); // removing because in constraint parameters are local but choices are from global parameter

		addOperationWhichAddsNewGlobalParameter(
				newGlobalBasicParameterNode, newParametersParentNode, extLanguageManager);

		addOperationWhichSetsLinkOnOldParameter(
				localParameterToConvert, newGlobalBasicParameterNode, extLanguageManager);
		
		addOperationsWhichReplaceReferencesInConstraint(localParameterToConvert, nodeMapper, extLanguageManager);
		
//		String anyNotNullLinkSignature = " ";
//		String newType = BasicParameterNodeHelper.calculateNewParameterType(parameterToConvert, anyNotNullLinkSignature);
//		addOperation(new HostMethodOperationPrepareParameterChange(parameterToConvert, newType, extLanguageManager));
//
//		for(ConstraintNode constraint : methodNode.getConstraintNodes()){
//			if(constraint.mentions(parameterToConvert)){
//				ConstraintNode copy = constraint.makeClone(optNodeMapper);
//				addOperation(new OnConstraintOperationAdd(methodNode, copy, constraint.getMyIndex(), extLanguageManager));
//			}
//		}
//
//		for(TestCaseNode tc : methodNode.getTestCases()){
//			TestCaseNode copy = tc.makeClone(optNodeMapper);
//			addOperation(
//					new OnTestCaseOperationAddToMethod(
//							methodNode, copy, tc.getMyIndex(), Optional.empty(), extLanguageManager));
//		}
	}

	private void addOperationsWhichReplaceReferencesInConstraint(
			BasicParameterNode localParameterToConvert,
			NodeMapper nodeMapper,
			IExtLanguageManager extLanguageManager) {
		
		MethodNode methodNode = MethodNodeHelper.findMethodNode(localParameterToConvert);
		
		List<ConstraintNode> constraintsToConvert = 
				ConstraintsParentNodeHelper.findChildConstraints(methodNode);
		
		for (ConstraintNode constraintNode : constraintsToConvert) {
			
			OnConstraintOperationReplaceReferences operation = 
					new OnConstraintOperationReplaceReferences(
							constraintNode, 
							nodeMapper,
							NodeMapper.MappingDirection.SOURCE_TO_DESTINATION,
							extLanguageManager);
			
			addOperation(operation);
		}
	}

	private void addOperationWhichSetsLinkOnOldParameter(
			BasicParameterNode parameterToConvert,
			BasicParameterNode linkToGlobalParameter, 
			IExtLanguageManager extLanguageManager) {
		
		MethodParameterOperationSetLink operationSetLink = 
				new MethodParameterOperationSetLink(
						parameterToConvert, linkToGlobalParameter, extLanguageManager);
		
		addOperation(operationSetLink);
	}

	private void addOperationWhichAddsNewGlobalParameter(
			BasicParameterNode newGlobalBasicParameterNode,
			IParametersParentNode newParametersParentNode, 
			IExtLanguageManager extLanguageManager) {
		
		GenericOperationAddParameter operationAddParameter = 
				new GenericOperationAddParameter(
						newParametersParentNode, newGlobalBasicParameterNode, true, extLanguageManager);
		
		addOperation(operationAddParameter);
	}

}
