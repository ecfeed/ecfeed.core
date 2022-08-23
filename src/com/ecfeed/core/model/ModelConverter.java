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

import java.util.List;

public class ModelConverter {

	private static final String INVALID_MODEL_VERSION = "Invalid model version.";

	public static RootNode convertToCurrentVersion(RootNode model) {

		int softwareVersion = ModelVersionDistributor.getCurrentSoftwareVersion();
		int modelVersion = model.getModelVersion();

		for (int version = modelVersion; version < softwareVersion; version++) {
			model = convertToNextVersion(model, version);
		}
		return model;
	}

	private static RootNode convertToNextVersion(RootNode model, int fromVersion) {
		switch (fromVersion) {
		case 0:
			model = convertFrom0To1(model);
			break;
		case 1:
			model = convertFrom1To2(model);
			break;
		case 2:
			model = convertFrom2To3(model);
			break;
		case 3:
			model = convertFrom3To4(model);
			break;
			
		default:
			ExceptionHelper.reportRuntimeException(INVALID_MODEL_VERSION); 
			break;
		}

		model.setVersion(fromVersion+1);
		return model;
	}

	private static RootNode convertFrom0To1(RootNode model) {
		// no changes in model internal structure, just serialization and parsing differs
		model.setVersion(1);
		return model;
	}

	private static RootNode convertFrom1To2(RootNode model) {
		// Flag of the ClassNode: RunOnAndroid moved to properties in ClassNode constructor.
		// Serializer in version 2 writes the RunOnAndroid flag to NodeProperties of the ClassNode.
		model.setVersion(2);
		return model;
	}

	private static RootNode convertFrom2To3(RootNode model) {
		// Relation types changed from symbols to text. The model is compatible backwards.
		model.setVersion(3);
		return model;
	}

	public static RootNode convertFrom3To4(RootNode rootNode) {

		List<ClassNode> classNodes = rootNode.getClasses();

		for (ClassNode classNode:  classNodes) {
			convertClassFrom3To4(classNode);
		}


		rootNode.setVersion(4);
		return rootNode;
	}

	private static void convertClassFrom3To4(ClassNode classNode) {

		List<MethodNode> methodNodes = classNode.getMethods();

		for (MethodNode methodNode : methodNodes) {
			convertMethodFrom3To4(methodNode);
		}
	}

	private static void convertMethodFrom3To4(MethodNode methodNode) {

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {
			convertConstraintFrom3To4(constraintNode);
		}
	}

	private static void convertConstraintFrom3To4(ConstraintNode constraintNode) {

		Constraint constraint = constraintNode.getConstraint();

		AbstractStatement oldAbstractStatement = constraint.getPostcondition();

		if (!(oldAbstractStatement instanceof ExpectedValueStatement)) {
			return;
		}

		constraint.setType(ConstraintType.ASSIGNMENT);

		ExpectedValueStatement oldExpectedValueStatement = (ExpectedValueStatement)oldAbstractStatement;

		MethodParameterNode methodParameterNode = oldExpectedValueStatement.getLeftParameter();
		String value = oldExpectedValueStatement.getChoice().getValueString();

		AbstractStatement newPostcondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode, value);

		constraint.setPostcondition(newPostcondition);
	}

}
