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

import java.util.ArrayList;
import java.util.Collection;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class NodesByType {

	private ArrayList<ClassNode> fClasses = new ArrayList<>();
	private ArrayList<MethodNode> fMethods = new ArrayList<>();
	private ArrayList<BasicParameterNode> fLocalParameters = new ArrayList<>();  // TODO MO-RE merge with global ?
	private ArrayList<BasicParameterNode> fGlobalParameters = new ArrayList<>();
	private ArrayList<ChoiceNode> fChoices = new ArrayList<>();
	private ArrayList<ConstraintNode> fConstraints = new ArrayList<>();
	private ArrayList<TestCaseNode> fTestCases = new ArrayList<>();
	private ArrayList<IAbstractNode> fOtherNodes = new ArrayList<>();

	public NodesByType() {

		fClasses = new ArrayList<>();
		fMethods = new ArrayList<>();
		fLocalParameters = new ArrayList<>();
		fGlobalParameters = new ArrayList<>();
		fChoices = new ArrayList<>();
		fConstraints = new ArrayList<>();
		fTestCases = new ArrayList<>();
		fOtherNodes = new ArrayList<>();
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

		if (selectedNode instanceof BasicParameterNode) {

			if (((BasicParameterNode) selectedNode).isGlobalParameter()) {
				fGlobalParameters.add((BasicParameterNode)selectedNode);
			} else {
				fLocalParameters.add((BasicParameterNode)selectedNode);
			}

			return;
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

		fOtherNodes.add(selectedNode);
	}

	public ArrayList<ClassNode> getClasses() {
		return fClasses;
	}

	public ArrayList<MethodNode> getMethods() {
		return fMethods;
	}

	public ArrayList<BasicParameterNode> getLocalParameters() {
		return fLocalParameters;
	}

	public ArrayList<BasicParameterNode> getGlobalParameters() {
		return fGlobalParameters;
	}

	public ArrayList<ChoiceNode> getChoices() {
		return fChoices;
	}

	public ArrayList<ConstraintNode> getConstraints() {
		return fConstraints;
	}

	public ArrayList<TestCaseNode> getTestCases() {
		return fTestCases;
	}

	public ArrayList<IAbstractNode> getOtherNodes() {
		return fOtherNodes;
	}

}
