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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnBasicParameterOperationConvertToGlobalNew extends AbstractModelOperation {

	private AbstractParameterNode fLocalParameterToConvert;
	private IParametersParentNode fNewParametersParentNode;
	private IExtLanguageManager fExtLanguageManager;

	public OnBasicParameterOperationConvertToGlobalNew(
			AbstractParameterNode localParameterToConvert, 
			IParametersParentNode newParametersParentNode,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REPLACE_PARAMETER_WITH_LINK, extLanguageManager);

		fLocalParameterToConvert = localParameterToConvert;
		fNewParametersParentNode = newParametersParentNode;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

		convertLocalToGlobalParameter(
				fLocalParameterToConvert, fNewParametersParentNode);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation() {
			super("Reverse - " + OperationNames.REPLACE_PARAMETER_WITH_LINK, fExtLanguageManager);
		}

		@Override
		public void execute() {
			undoConvertLocalToGlobalParameter(fLocalParameterToConvert);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

	public static void convertLocalToGlobalParameter( 
			AbstractParameterNode localParameterToConvert, 
			IParametersParentNode newParametersParentNode) {

		NodeMapper nodeMapper = new NodeMapper();
		Optional<NodeMapper> optNodeMapper = Optional.of(nodeMapper);

		AbstractParameterNode newGlobalBasicParameterNode = 
				createGlobalParameter(
						localParameterToConvert, newParametersParentNode, optNodeMapper);

		nodeMapper.removeMappings(localParameterToConvert); // removing because in constraint parameters are local but choices are from global parameter

		newParametersParentNode.addParameter(newGlobalBasicParameterNode, null);

		localParameterToConvert.setLinkToGlobalParameter(newGlobalBasicParameterNode);

		MethodNode methodNode = MethodNodeHelper.findMethodNode(localParameterToConvert);

		replaceRefencesInChildConstraints(
				methodNode, nodeMapper, NodeMapper.MappingDirection.SOURCE_TO_DESTINATION);
	}

	private static AbstractParameterNode createGlobalParameter(
			AbstractParameterNode localParameterToConvert,
			IParametersParentNode newParametersParentNode, 
			Optional<NodeMapper> optNodeMapper) {

		AbstractParameterNode newGlobalBasicParameterNode = 
				makeCloneOfParameter(localParameterToConvert, optNodeMapper);

		newGlobalBasicParameterNode.setLinkToGlobalParameter(null);
		newGlobalBasicParameterNode.setParent(newParametersParentNode);

		if (localParameterToConvert instanceof BasicParameterNode) {

			BasicParameterNode localBasicParameterNode = (BasicParameterNode) localParameterToConvert;
			localBasicParameterNode.clearChoices();
		}

		return newGlobalBasicParameterNode;
	}

	private static AbstractParameterNode makeCloneOfParameter(
			AbstractParameterNode localParameterToConvert,
			Optional<NodeMapper> optNodeMapper) {

		if (localParameterToConvert instanceof BasicParameterNode) {
			return ((BasicParameterNode)localParameterToConvert).makeClone(optNodeMapper);
		}

		return ((CompositeParameterNode)localParameterToConvert).makeClone(optNodeMapper);
	}

	private static void replaceRefencesInChildConstraints(
			MethodNode methodNode,
			NodeMapper nodeMapper,
			NodeMapper.MappingDirection mappingDirection) {

		List<ConstraintNode> constraintsToConvert = 
				ConstraintsParentNodeHelper.findChildConstraints(methodNode);

		for (ConstraintNode constraintNode : constraintsToConvert) {

			constraintNode.replaceReferences(nodeMapper, mappingDirection);
		}

	}

	public static void undoConvertLocalToGlobalParameter(AbstractParameterNode localParameter) {

		BasicParameterNode globalParameter = 
				(BasicParameterNode) localParameter.getLinkToGlobalParameter();

		NodeMapper nodeMapper = new NodeMapper();

		if (localParameter instanceof BasicParameterNode) {
			
			IChoicesParentNode localChoicesParentNode = (IChoicesParentNode) localParameter;
			IChoicesParentNode globalChoicesParentNode = globalParameter;
			
			ChoiceNodeHelper.cloneChoiceNodesRecursively(
					globalChoicesParentNode, localChoicesParentNode, Optional.of(nodeMapper));
		}

		localParameter.setLinkToGlobalParameter(null);

		IParametersParentNode parentOfGlobal = globalParameter.getParent();
		parentOfGlobal.removeParameter(globalParameter);

		MethodNode methodNode = MethodNodeHelper.findMethodNode(localParameter);

		replaceRefencesInChildConstraints(
				methodNode, nodeMapper, NodeMapper.MappingDirection.SOURCE_TO_DESTINATION);
	}


}
