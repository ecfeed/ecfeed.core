/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class NodesByType {

	private Set<ClassNode> fClasses;
	private Set<MethodNode> fMethods;
	private Set<AbstractParameterNode> fLocalParameters;
	private Set<AbstractParameterNode> fGlobalParameters;
	private Set<ChoiceNode> fChoices;
	private Set<ConstraintNode> fConstraints;
	private Set<TestCaseNode> fTestCases;

	public NodesByType() {

		fClasses = new HashSet<>();
		fMethods = new HashSet<>();
		fLocalParameters = new HashSet<>();
		fGlobalParameters = new HashSet<>();
		fChoices = new HashSet<>();
		fConstraints = new HashSet<>();
		fTestCases = new HashSet<>();
	}

	public NodesByType(Collection<IAbstractNode> abstractNodes) {

		this();

		for(IAbstractNode selectedNode : abstractNodes) {
			addNodeByType(selectedNode);
		}	
	}

	private void addNodeByType(IAbstractNode selectedNode) {

		if (selectedNode instanceof ClassNode) {
			fClasses.add((ClassNode)selectedNode);
			return;
		} 

		if (selectedNode instanceof MethodNode) {
			fMethods.add((MethodNode)selectedNode);
			return;
		}

		if ((selectedNode instanceof BasicParameterNode) || (selectedNode instanceof CompositeParameterNode)) {

			AbstractParameterNode abstractParameterNode = (AbstractParameterNode) selectedNode;

			if (abstractParameterNode.isGlobalParameter()) {
				fGlobalParameters.add(abstractParameterNode);
				return;
			} else {
				fLocalParameters.add(abstractParameterNode);
				return;
			}
		} 

		if (selectedNode instanceof ConstraintNode) {
			fConstraints.add((ConstraintNode)selectedNode);
			return;
		} 

		if (selectedNode instanceof TestCaseNode) {
			fTestCases.add((TestCaseNode)selectedNode);
			return;
		} 

		if (selectedNode instanceof ChoiceNode) {
			fChoices.add((ChoiceNode)selectedNode);
			return;
		} 

		ExceptionHelper.reportRuntimeException("Unknown node type.");
		//fOtherNodes.add(selectedNode);
	}

	public Set<ClassNode> getClasses() {
		return fClasses;
	}

	public Set<MethodNode> getMethods() {
		return fMethods;
	}

	public Set<AbstractParameterNode> getLocalParameters() {
		return fLocalParameters;
	}

	public Set<AbstractParameterNode> getGlobalParameters() {
		return fGlobalParameters;
	}

	public Set<ChoiceNode> getChoices() {
		return fChoices;
	}

	public Set<ConstraintNode> getConstraints() {
		return fConstraints;
	}

	public Set<TestCaseNode> getTestCaseNodes() {
		return fTestCases;
	}

}
