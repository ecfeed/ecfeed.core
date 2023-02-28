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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;

public class NodesByType {

	private Set<ClassNode> fClasses;
	private Set<MethodNode> fMethods;
	private Set<BasicParameterNode> fBasicParameters;
	private Set<CompositeParameterNode> fCompositeParameters;
	private Set<ChoiceNode> fChoices;
	private Set<ConstraintNode> fConstraints;
	private Set<TestSuiteNode> fTestSuites;
	private Set<TestCaseNode> fTestCases;


	public NodesByType() {

		fClasses = new HashSet<>();
		fMethods = new HashSet<>();
		fBasicParameters = new HashSet<>();
		fCompositeParameters = new HashSet<>();
		fChoices = new HashSet<>();
		fConstraints = new HashSet<>();
		fTestSuites = new HashSet<>();
		fTestCases = new HashSet<>();
	}

	public NodesByType(NodesByType other) {

		fClasses = other.getClasses();
		fMethods = other.getMethods();
		fBasicParameters = other.getBasicParameters();
		fCompositeParameters = other.getCompositeParameters();
		fChoices = other.getChoices();
		fConstraints = other.getConstraints();
		fTestSuites = other.getTestSuites();
		fTestCases = other.getTestCases();
	}



	public NodesByType(Collection<IAbstractNode> abstractNodes) {

		this();

		for(IAbstractNode selectedNode : abstractNodes) {
			addNode(selectedNode);
		}	
	}

	@Override
	public String toString() {

		if (isEmpty()) {
			return "Empty";
		} 

		String str = 
				"Cls:" + fClasses.size() +
				" Met:" + fMethods.size() + 
				" BasPar:" + fBasicParameters.size() +
				" ComPar:" + fCompositeParameters.size() + 
				" Cho:" + fChoices.size() + 
				" Cnst:" + fConstraints.size() +
				" TSui:" + fTestSuites.size() +
				" TCas:" + fTestCases.size();

		return str;
	}

	public boolean isEmpty() {

		if (fClasses.size() > 0) {
			return false;
		}

		if (fMethods.size() > 0) {
			return false;
		}

		if (fBasicParameters.size() > 0) {
			return false;
		}

		if (fCompositeParameters.size() > 0) {
			return false;
		}

		if (fChoices.size() > 0) {
			return false;
		}

		if (fConstraints.size() > 0) {
			return false;
		}

		if (fTestSuites.size() > 0) {
			return false;
		}

		if (fTestCases.size() > 0) {
			return false;
		}

		return true;
	}

	public String createContentMessage() {

		String message = "";

		message += createContentMessageForNodeType(fClasses.size(), "class", "classes");
		message += createContentMessageForNodeType(fMethods.size(), "method", "methods");
		message += createContentMessageForNodeType(fBasicParameters.size(), "parameter", "parameters");
		message += createContentMessageForNodeType(fCompositeParameters.size(), "structure", "structures");
		message += createContentMessageForNodeType(fChoices.size(), "choice", "choices");
		message += createContentMessageForNodeType(fConstraints.size(), "constraint", "constraints");
		message += createContentMessageForNodeType(fTestSuites.size(), "test suite", "test suites");
		message += createContentMessageForNodeType(fTestCases.size(), "test case", "test cases");

		message = StringHelper.removeFromPostfix(", ", message);

		return message;
	}

	private String createContentMessageForNodeType(int size, String nameInSingular, String nameInPlural) {

		if (size == 0) {
			return "";
		}

		if (size == 1) {
			return "1 " + nameInSingular + ", ";
		}

		return size + " " + nameInPlural + ", ";
	}

	public void addNode(IAbstractNode abstractNode) {

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
			return;
		} 

		if (abstractNode instanceof ConstraintNode) {
			fConstraints.add((ConstraintNode)abstractNode);
			return;
		} 

		if (abstractNode instanceof TestCaseNode) {
			fTestCases.add((TestCaseNode)abstractNode);
			return;
		} 

		if (abstractNode instanceof TestSuiteNode) {
			fTestSuites.add((TestSuiteNode)abstractNode);
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

	public List<BasicParameterNode> getListOfBasicParameters() {

		List<BasicParameterNode> list =	new ArrayList<>(fBasicParameters);
		return list;
	}

	public Set<CompositeParameterNode> getCompositeParameters() {
		return fCompositeParameters;
	}

	public List<CompositeParameterNode> getListOfCompositeParameters() {

		List<CompositeParameterNode> list =	new ArrayList<>(fCompositeParameters);

		return list;
	}

	public Set<ChoiceNode> getChoices() {
		return fChoices;
	}

	public Set<ConstraintNode> getConstraints() {
		return fConstraints;
	}

	public void addConstraints(Collection<ConstraintNode> constraintNodes) {
		fConstraints.addAll(constraintNodes);
	}

	public Set<TestCaseNode> getTestCases() {
		return fTestCases;
	}

	public Set<TestSuiteNode> getTestSuites() {
		return fTestSuites;
	}

	public void addMethods(Set<MethodNode> methods) {
		fMethods.addAll(methods);
	}

	public void addTestCases(Collection<TestCaseNode> testCaseNodes) {
		fTestCases.addAll(testCaseNodes);
	}

	public void addBasicParameters(List<BasicParameterNode> basicParameterNodes) {
		fBasicParameters.addAll(basicParameterNodes);
	}

	public void addTestCases(List<ConstraintNode> constraintsToDelete) {
		fConstraints.addAll(constraintsToDelete);
	}

	public void addCompositeParameters(List<CompositeParameterNode> compositeParameters) {
		fCompositeParameters.addAll(compositeParameters);
	}

	public void addBasicParameters(Set<BasicParameterNode> basicParameters) {
		fBasicParameters.addAll(basicParameters);
	}

	public void addCompositeParameters(Set<CompositeParameterNode> compositeParameters) {
		fCompositeParameters.addAll(compositeParameters);
	}

	public void addChoices(Set<ChoiceNode> choices) {
		fChoices.addAll(choices);
	}

	public void addTestSuites(Set<TestSuiteNode> testSuiteNodes) {
		fTestSuites.addAll(testSuiteNodes);
	}

}
