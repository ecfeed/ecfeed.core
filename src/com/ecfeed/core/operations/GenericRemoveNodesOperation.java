/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;

public class GenericRemoveNodesOperation extends BulkOperation {

	private final Set<AbstractNode> fSelectedNodes;

	private final Set<AbstractNode> fAffectedNodes = new HashSet<>();
	private final Set<TestCaseNode> fAffectedTestCases = new HashSet<>();
	private final Set<ConstraintNode> fAffectedConstraints = new HashSet<>();

	public GenericRemoveNodesOperation(
			Collection<? extends AbstractNode> nodes, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			AbstractNode nodeToSelect,
			AbstractNode nodeToSelectAfterReverseOperation) {

		super(OperationNames.REMOVE_NODES, 
				false,
				nodeToSelect,
				nodeToSelectAfterReverseOperation);

		fSelectedNodes = new HashSet<>(nodes);

		Iterator<AbstractNode> iterator = fSelectedNodes.iterator();
		while(iterator.hasNext()){
			AbstractNode node = iterator.next();
			for(AbstractNode ancestor : node.getAncestors()) {
				if(fSelectedNodes.contains(ancestor)) {
					iterator.remove();
					break;
				}
			}
		}

		prepareOperations(adapterProvider, validate);
		return;
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedConstraints;
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedTestCases;
	}

	private void prepareOperations(ITypeAdapterProvider adapterProvider, boolean validate){
		HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap = new HashMap<>();
		HashMap<MethodNode, List<AbstractParameterNode>> parameterMap = new HashMap<>();
		ArrayList<ClassNode> classes = new ArrayList<>();
		ArrayList<MethodNode> methods = new ArrayList<>();
		ArrayList<MethodParameterNode> params = new ArrayList<>();
		ArrayList<GlobalParameterNode> globals = new ArrayList<>();
		ArrayList<ChoiceNode> choices = new ArrayList<>();
		ArrayList<AbstractNode> others = new ArrayList<>();
		HashSet<ConstraintNode> constraints = new HashSet<>();
		ArrayList<TestCaseNode> testcases = new ArrayList<>();

		for(AbstractNode node : fSelectedNodes) {
			if(node instanceof ClassNode){
				classes.add((ClassNode)node);
			} else if(node instanceof MethodNode){
				methods.add((MethodNode)node);
			} else if(node instanceof MethodParameterNode){
				params.add((MethodParameterNode)node);
			} else if(node instanceof GlobalParameterNode){
				globals.add((GlobalParameterNode)node);
			} else if(node instanceof ConstraintNode){
				constraints.add((ConstraintNode)node);
			} else if(node instanceof TestCaseNode){
				testcases.add((TestCaseNode)node);
			} else if(node instanceof ChoiceNode){
				choices.add((ChoiceNode)node);
			} else{
				others.add(node);
			}		
		}	

		Set<ConstraintNode> allConstraintNodes = getAllConstraintNodes();
		Set<TestCaseNode> allTestCaseNodes = getAllTestCaseNodes();

		// removing classes, they are independent from anything
		for (ClassNode clazz : classes) {
			fAffectedNodes.add(clazz);
		}
		// removing choices and deleting connected constraints/test cases from their respective to-remove lists beforehand
		for (ChoiceNode choice : choices) {
			createAffectedConstraints(choice, allConstraintNodes);
			createAffectedTestCases(choice, allTestCaseNodes);
			fAffectedNodes.add(choice);
		}
		// removing test cases
		for (TestCaseNode tcase : testcases) {
			fAffectedNodes.add(tcase);
		}
		// leaving this in case of any further nodes being added
		for (AbstractNode node : others) {
			fAffectedNodes.add(node);
		}
		/*
		 * Iterate through global params. Do the same checks as for method
		 * parameters with every linker. If no linker is in potentially
		 * duplicate method - just proceed to remove global and all linkers and
		 * remove it from the lists.
		 */
		Iterator<GlobalParameterNode> globalItr = globals.iterator();
		while (globalItr.hasNext()) {
			GlobalParameterNode global = globalItr.next();
			List<MethodParameterNode> linkers = global.getLinkers();
			boolean isDependent = false;
			for (MethodParameterNode param : linkers) {
				MethodNode method = param.getMethod();
				if (addMethodToMap(method, duplicatesMap, methods)) {
					duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getMyIndex(), null);
					isDependent = true;
					if (!parameterMap.containsKey(method)) {
						parameterMap.put(method, new ArrayList<AbstractParameterNode>());
					}
					parameterMap.get(method).add(global);
				}
			}
			if (!isDependent) {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(global, allConstraintNodes);
				fAffectedNodes.add(global);
				globalItr.remove();
				/*
				 * in case linkers contain parameters assigned to removal -
				 * remove them from list; Global param removal will handle them.
				 */
				for (MethodParameterNode param : linkers) {
					params.remove(param);
				}
			}
		}
		/*
		 * Iterate through parameters. If parent method is potential duplicate -
		 * add it to map for further validation. Replace values of to-be-deleted
		 * param with NULL to remove them later without disturbing parameters
		 * order. If parameters method is not potential duplicate - simply
		 * forward it for removal and remove it from to-remove list.
		 */
		Iterator<MethodParameterNode> paramItr = params.iterator();
		while (paramItr.hasNext()) {
			MethodParameterNode param = paramItr.next();
			MethodNode method = param.getMethod();

			if (addMethodToMap(method, duplicatesMap, methods)) {
				duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getMyIndex(), null);
				if (!parameterMap.containsKey(method)) {
					parameterMap.put(method, new ArrayList<AbstractParameterNode>());
				}
				parameterMap.get(method).add(param);
			} else {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(param, allConstraintNodes);
				fAffectedNodes.add(param);
				paramItr.remove();
			}
		}
		//Removing methods - information for model map has been already taken
		Iterator<MethodNode> methodItr = methods.iterator();
		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			fAffectedNodes.add(method);
			methodItr.remove();
		}
		// Detect duplicates
		Iterator<ClassNode> classItr = duplicatesMap.keySet().iterator();
		while (classItr.hasNext()) {
			ClassNode classNext = classItr.next();
			Iterator<String> nameItr = duplicatesMap.get(classNext).keySet().iterator();
			while (nameItr.hasNext()) {		
				// delete removed parameters marked with null (set?)
				// remember that we are validating both param and method removal at once. Need to store params somewhere else.
				HashSet<List<String>> paramSet = new HashSet<>();
				String strNext = nameItr.next();
				methodItr = duplicatesMap.get(classNext).get(strNext).keySet().iterator();
				while (methodItr.hasNext()) {
					MethodNode methodNext = methodItr.next();
					List<String> paramList = duplicatesMap.get(classNext).get(strNext).get(methodNext);
					Iterator<String> parameterItr = paramList.iterator();
					//removing parameters from model image
					while (parameterItr.hasNext()) {
						if (parameterItr.next() == null) {
							parameterItr.remove();
						}
					}
					paramSet.add(paramList);
				}
				//	There is more methods than method signatures, ergo duplicates present. Proceeding to remove with duplicate check on.
				Set<MethodNode> methodSet = duplicatesMap.get(classNext).get(strNext).keySet();
				if (paramSet.size() < methodSet.size()) {
					for (MethodNode method : methodSet) {
						if (parameterMap.containsKey(method)) {
							for (AbstractParameterNode node : parameterMap.get(method)) {
								//remove mentioning constraints from the list to avoid duplicates
								createAffectedConstraints(node, allConstraintNodes);
								fAffectedNodes.add(node);
							}
						}
					}
				}
				// Else remove with duplicate check off;
				else {
					for (MethodNode method : methodSet) {
						if (parameterMap.containsKey(method)) {
							for (AbstractParameterNode node : parameterMap.get(method)) {
								//remove mentioning constraints from the list to avoid duplicates
								createAffectedConstraints(node, allConstraintNodes);
								if (node instanceof MethodParameterNode) {
									
									addOperation(
											new MethodOperationRemoveParameter(
													method, (MethodParameterNode)node, validate, true));
									
								} else if (node instanceof GlobalParameterNode) {
									
									addOperation(
											new GenericOperationRemoveGlobalParameter(
													((GlobalParameterNode)node).getParametersParent(), 
													(GlobalParameterNode)node, 
													true));	
								}
							}
						}
					}
				}
			}
		}

		for (ConstraintNode constraint : constraints) {
			fAffectedNodes.add(constraint);
		}

		fAffectedConstraints.stream().forEach(
				e-> addOperation(FactoryRemoveOperation.getRemoveOperation(e, adapterProvider, validate)));

		fAffectedTestCases.stream().forEach(
				e-> addOperation(FactoryRemoveOperation.getRemoveOperation(e, adapterProvider, validate)));

		fAffectedNodes.stream().forEach(
				e-> addOperation(FactoryRemoveOperation.getRemoveOperation(e, adapterProvider, validate)));
	}

	private Set<ConstraintNode> getAllConstraintNodes() {
		return fSelectedNodes.iterator().next()
				.getRoot()
				.getChildren()
				.stream()
				.filter(e -> (e instanceof ClassNode))
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof MethodNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof ConstraintNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(e -> (ConstraintNode) e)
				.collect(Collectors.toSet());
	}

	private Set<TestCaseNode> getAllTestCaseNodes() {
		return fSelectedNodes.iterator().next()
				.getRoot()
				.getChildren()
				.stream()
				.filter(e -> (e instanceof ClassNode))
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof MethodNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof TestCaseNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(e -> (TestCaseNode) e)
				.collect(Collectors.toSet());
	}

	private boolean addMethodToMap(MethodNode method, HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap, List<MethodNode> removedMethods){
		ClassNode clazz = method.getClassNode();
		boolean hasDuplicate = false;
		for(MethodNode classMethod : clazz.getMethods()){
			if(classMethod != method && classMethod.getName().equals(method.getName()) && !removedMethods.contains(classMethod)){
				if(duplicatesMap.get(clazz) == null){
					duplicatesMap.put(clazz, new HashMap<String, HashMap<MethodNode, List<String>>>());
				}
				if(!(duplicatesMap.get(clazz).containsKey(classMethod.getName()))){
					duplicatesMap.get(clazz).put(classMethod.getName(), new HashMap<MethodNode, List<String>>());
				}
				if(!duplicatesMap.get(clazz).get(classMethod.getName()).containsKey(classMethod)){
					duplicatesMap.get(clazz).get(classMethod.getName())
					.put(classMethod, new ArrayList<String>(classMethod.getParameterTypes()));
				}
				if(!duplicatesMap.get(clazz).get(classMethod.getName()).containsKey(method)){
					duplicatesMap.get(clazz).get(classMethod.getName()).put(method, new ArrayList<String>(method.getParameterTypes()));
				}
				hasDuplicate = true;
			}
		}
		return hasDuplicate;
	}

	private void createAffectedConstraints(AbstractNode node, Set<ConstraintNode> allConstraintNodes) {

		if (node instanceof ChoiceNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraint = itr.next();
				if (constraint.mentions((ChoiceNode)node)) {
					fAffectedConstraints.add(constraint);
				}
			}
		} else if (node instanceof AbstractParameterNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraint = itr.next();
				if (constraint.mentions((AbstractParameterNode)node)) {
					fAffectedConstraints.add(constraint);
				}
			}
		}

	}

	private void createAffectedTestCases(AbstractNode node, Set<TestCaseNode> allTestCaseNodes) {

		Iterator<TestCaseNode> itr = allTestCaseNodes.iterator();
		while (itr.hasNext()) {
			TestCaseNode testCase = itr.next();
			if (testCase.mentions((ChoiceNode)node)) {
				fAffectedTestCases.add(testCase);
			}
		}

	}

}
