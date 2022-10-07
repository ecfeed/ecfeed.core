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

import java.util.HashMap;
import java.util.Map;

public class DeploymentMapper {

	private Map<ChoiceNode, ChoiceNode> fMappingsOfSourceToDevelopedChoice;
	private Map<MethodParameterNode, MethodParameterNode> fMappingsOfSourceToDevelopedParameter;
	private Map<RelationStatement, RelationStatement> fMappingsOfSourceToDevelopedRelationStatement;

	public DeploymentMapper() {

		fMappingsOfSourceToDevelopedChoice = new HashMap<>();
		fMappingsOfSourceToDevelopedParameter = new HashMap<>();
	}

	public void addParameterMappings(
			MethodParameterNode sourceMethodParameterNode,
			MethodParameterNode developedMethodParameterNode) {

		fMappingsOfSourceToDevelopedParameter.put(sourceMethodParameterNode, developedMethodParameterNode);
	}

	public ChoiceNode getDeployedChoiceNode(ChoiceNode sourceChoiceNode) {

		return fMappingsOfSourceToDevelopedChoice.get(sourceChoiceNode);
	}

	public void addChoiceMappings(
			ChoiceNode sourceChoiceNode,
			ChoiceNode developedChoiceNode) {

		fMappingsOfSourceToDevelopedChoice.put(sourceChoiceNode, developedChoiceNode);
	}

	public MethodParameterNode getDeployedParameterNode(MethodParameterNode sourceMethodParameterNode) {

		return fMappingsOfSourceToDevelopedParameter.get(sourceMethodParameterNode);
	}

	public void addRelationStatementMappings(
			RelationStatement sourceRelationStatement,
			RelationStatement developedRelationStatement) {

		fMappingsOfSourceToDevelopedRelationStatement.put(sourceRelationStatement, developedRelationStatement);
	}
	
	public RelationStatement getDeployedRelationStatement(RelationStatement sourceParentRelationStatement) {
		return fMappingsOfSourceToDevelopedRelationStatement.get(sourceParentRelationStatement);
	}

}
