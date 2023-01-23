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
	private Set<BasicParameterNode> fBasicParameters;
	private Set<CompositeParameterNode> fCompositeParameters;
	private Set<ChoiceNode> fChoices;
	private Set<ConstraintNode> fConstraints;
	private Set<TestCaseNode> fTestCases;

	public NodesByType() {

		fClasses = new HashSet<>();
		fMethods = new HashSet<>();
		fBasicParameters = new HashSet<>();
		fCompositeParameters = new HashSet<>();
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

	public void addNodeByType(IAbstractNode abstractNode) { // TODO MO-RE rename to addNode

		if (abstractNode instanceof ClassNode) {
			fClasses.add((ClassNode)abstractNode);
			return;
		} 

		if (abstractNode instanceof MethodNode) {
			fMethods.add((MethodNode)abstractNode);
			return;
		}

		if (abstractNode instanceof BasicParameterNode) {
			fBasicParameters.add((BasicParameterNode) abstractNode);
			return;
		}

		if (abstractNode instanceof CompositeParameterNode) {
			fCompositeParameters.add((CompositeParameterNode) abstractNode);
		} 

		if (abstractNode instanceof ConstraintNode) {
			fConstraints.add((ConstraintNode)abstractNode);
			return;
		} 

		if (abstractNode instanceof TestCaseNode) {
			fTestCases.add((TestCaseNode)abstractNode);
			return;
		} 

		if (abstractNode instanceof ChoiceNode) {
			fChoices.add((ChoiceNode)abstractNode);
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

	public Set<BasicParameterNode> getBasicParameters() {
		return fBasicParameters;
	}

	public Set<CompositeParameterNode> getCompositeParameters() {
		return fCompositeParameters;
	}

	//	public void addBasicParameters(Collection<BasicParameterNode> basicChildParaBasicParameterNodes) {
	//		
	//		for (BasicParameterNode basicParameterNode : basicChildParaBasicParameterNodes) {
	//			
	//			if (basicParameterNode.isGlobalParameter()) {
	//				fGlobalParameters.add(basicParameterNode);
	//			} else {
	//				fLocalParameters.add(basicParameterNode);
	//			}
	//		}
	//	}
	//	
	public Set<ChoiceNode> getChoices() {
		return fChoices;
	}

	public Set<ConstraintNode> getConstraints() {
		return fConstraints;
	}

	public void addConstraints(Collection<ConstraintNode> constraintNodes) {
		fConstraints.addAll(constraintNodes);
	}

	public Set<TestCaseNode> getTestCaseNodes() {
		return fTestCases;
	}

	public void addTestCases(Collection<TestCaseNode> testCaseNodes) {
		fTestCases.addAll(testCaseNodes);
	}

}
