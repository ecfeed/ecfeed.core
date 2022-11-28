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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericRemoveNodesOperationsCreator {

	private final Set<IAbstractNode> fSelectedNodes;

	private final Set<IAbstractNode> fAffectedNodes = new HashSet<>(); // TODO MO-RE initialize in constructor
	private final Set<TestCaseNode> fAffectedTestCases = new HashSet<>();
	private final Set<ConstraintNode> fAffectedConstraints = new HashSet<>();
	
	List<IModelOperation> fOperations;

	public GenericRemoveNodesOperationsCreator(
			Set<IAbstractNode> selectedNodes, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fSelectedNodes = selectedNodes;
		removeNodesWithAncestorsOnList();

		fOperations = new ArrayList<>(); 

		createModelOperationsDeletingNodes(
				fSelectedNodes,
				getAllConstraintNodes(),
				getAllTestCaseNodes(),

				fAffectedNodes,
				fAffectedConstraints,
				fAffectedTestCases,
				fOperations,

				extLanguageManager,
				typeAdapterProvider, 
				validate);
	}
	
	public List<IModelOperation> getOperations() {
		return fOperations;
	}

	private void removeNodesWithAncestorsOnList() {

		Iterator<IAbstractNode> iterator = fSelectedNodes.iterator();

		while (iterator.hasNext()) {

			IAbstractNode currentNode = iterator.next();

			List<IAbstractNode> ancestors = currentNode.getAncestors();

			for (IAbstractNode ancestor : ancestors) {

				if (fSelectedNodes.contains(ancestor)) {

					// node is deleted because ancestor will be remove with the whole sub-tree which includes current node 
					iterator.remove(); 
					break;
				}
			}
		}
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedConstraints;
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedTestCases;
	}

	private static void createModelOperationsDeletingNodes(
			Set<IAbstractNode> selectedNodes,
			Set<ConstraintNode> allConstraintNodes,
			Set<TestCaseNode> allTestCaseNodes,

			Set<IAbstractNode> outAffectedNodes,
			Set<ConstraintNode> outAffectedConstraints,
			Set<TestCaseNode> outAffectedTestCases,

			List<IModelOperation> outOperations,

			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate){

		HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap = new HashMap<>();
		HashMap<MethodNode, List<BasicParameterNode>> parameterMap = new HashMap<>();
		ArrayList<ClassNode> classes = new ArrayList<>();
		ArrayList<MethodNode> methods = new ArrayList<>();
		ArrayList<BasicParameterNode> params = new ArrayList<>();
		ArrayList<BasicParameterNode> globals = new ArrayList<>();
		ArrayList<ChoiceNode> choices = new ArrayList<>();
		ArrayList<IAbstractNode> others = new ArrayList<>();
		HashSet<ConstraintNode> constraints = new HashSet<>();
		ArrayList<TestCaseNode> testcases = new ArrayList<>();

		for(IAbstractNode node : selectedNodes) {
			if(node instanceof ClassNode){
				classes.add((ClassNode)node);
			} else if(node instanceof MethodNode){
				methods.add((MethodNode)node);
			} else if(node instanceof BasicParameterNode){

				if (((BasicParameterNode) node).isGlobalParameter()) {
					globals.add((BasicParameterNode)node);
				} else {
					params.add((BasicParameterNode)node);
				}

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

		//		Set<ConstraintNode> allConstraintNodes = getAllConstraintNodes();
		//		Set<TestCaseNode> allTestCaseNodes = getAllTestCaseNodes();

		// removing classes, they are independent from anything
		for (ClassNode clazz : classes) {
			outAffectedNodes.add(clazz);
		}
		// removing choices and deleting connected constraints/test cases from their respective to-remove lists beforehand
		for (ChoiceNode choice : choices) {
			createAffectedConstraints(choice, allConstraintNodes, outAffectedConstraints);
			createAffectedTestCases(choice, allTestCaseNodes, outAffectedTestCases);
			outAffectedNodes.add(choice);
		}
		// removing test cases
		for (TestCaseNode tcase : testcases) {
			outAffectedNodes.add(tcase);
		}
		// leaving this in case of any further nodes being added
		for (IAbstractNode node : others) {
			outAffectedNodes.add(node);
		}
		/*
		 * Iterate through global params. Do the same checks as for method
		 * parameters with every linker. If no linker is in potentially
		 * duplicate method - just proceed to remove global and all linkers and
		 * remove it from the lists.
		 */
		Iterator<BasicParameterNode> globalItr = globals.iterator();
		while (globalItr.hasNext()) {
			BasicParameterNode global = globalItr.next();
			List<BasicParameterNode> linkers = global.getLinkedMethodParameters();
			boolean isDependent = false;
			for (BasicParameterNode param : linkers) {
				MethodNode method = param.getMethod();
				if (addMethodToMap(method, duplicatesMap, methods)) {
					duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getMyIndex(), null);
					isDependent = true;
					if (!parameterMap.containsKey(method)) {
						parameterMap.put(method, new ArrayList<BasicParameterNode>());
					}
					parameterMap.get(method).add(global);
				}
			}
			if (!isDependent) {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(global, allConstraintNodes, outAffectedConstraints);
				outAffectedNodes.add(global);
				globalItr.remove();
				/*
				 * in case linkers contain parameters assigned to removal -
				 * remove them from list; Global param removal will handle them.
				 */
				for (BasicParameterNode param : linkers) {
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
		Iterator<BasicParameterNode> paramItr = params.iterator();
		while (paramItr.hasNext()) {
			BasicParameterNode param = paramItr.next();
			MethodNode method = param.getMethod();

			if (addMethodToMap(method, duplicatesMap, methods)) {
				duplicatesMap.get(method.getClassNode()).get(method.getName()).get(method).set(param.getMyIndex(), null);
				if (!parameterMap.containsKey(method)) {
					parameterMap.put(method, new ArrayList<BasicParameterNode>());
				}
				parameterMap.get(method).add(param);
			} else {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(param, allConstraintNodes, outAffectedConstraints);
				outAffectedNodes.add(param);
				paramItr.remove();
			}
		}
		//Removing methods - information for model map has been already taken
		Iterator<MethodNode> methodItr = methods.iterator();
		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			outAffectedNodes.add(method);
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
							for (BasicParameterNode node : parameterMap.get(method)) {
								//remove mentioning constraints from the list to avoid duplicates
								createAffectedConstraints(node, allConstraintNodes, outAffectedConstraints);
								outAffectedNodes.add(node);
							}
						}
					}
				}
				// Else remove with duplicate check off;
				else {
					for (MethodNode method : methodSet) {
						if (parameterMap.containsKey(method)) {
							for (BasicParameterNode node : parameterMap.get(method)) {
								//remove mentioning constraints from the list to avoid duplicates
								createAffectedConstraints(node, allConstraintNodes, outAffectedConstraints);
								if (node instanceof BasicParameterNode && ((BasicParameterNode)node).isGlobalParameter()) {
								
									GenericOperationRemoveGlobalParameter operation = new GenericOperationRemoveGlobalParameter(
											((BasicParameterNode)node).getParametersParent(), 
											(BasicParameterNode)node, 
											true,
											extLanguageManager);

									addOper(operation, outOperations);	


								} else if ((node instanceof BasicParameterNode) && !((BasicParameterNode)node).isGlobalParameter()) {

									MethodOperationRemoveParameter operation = new MethodOperationRemoveParameter(
											method, (BasicParameterNode)node, validate, false, extLanguageManager);

									addOper(operation, outOperations);

								}
							}
						}
					}
				}
			}
		}

		for (ConstraintNode constraint : constraints) {
			outAffectedNodes.add(constraint);
		}

		outAffectedConstraints.stream().forEach(
				e-> addOper(
						FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));

		outAffectedTestCases.stream().forEach(
				e-> addOper(
						FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));

		outAffectedNodes.stream().forEach(
				e-> addOper(FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));
	}

	private static void addOper(IModelOperation operation, List<IModelOperation> fOperations) {

		if (operation == null) {
			ExceptionHelper.reportRuntimeException("Attempt to add empty operation.");
		}

		fOperations.add(operation);
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

	private static boolean addMethodToMap(MethodNode method, HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap, List<MethodNode> removedMethods){
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

	private static void createAffectedConstraints(
			IAbstractNode node, 
			Set<ConstraintNode> allConstraintNodes,
			Set<ConstraintNode> outAffectedConstraints) {

		if (node instanceof ChoiceNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraintNode = itr.next();
				if (constraintMentionsChoiceNodeOrAnyChild((ChoiceNode)node, constraintNode)) {
					outAffectedConstraints.add(constraintNode);
				}
			}
		} else if (node instanceof BasicParameterNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraint = itr.next();
				if (constraint.mentions((BasicParameterNode)node)) {
					outAffectedConstraints.add(constraint);
				}
			}
		}
	}

	private static boolean constraintMentionsChoiceNodeOrAnyChild(ChoiceNode choiceNode, ConstraintNode constraintNode) {

		if (constraintNode.mentions(choiceNode)) {
			return true;
		}

		List<ChoiceNode> childChoiceNodes = choiceNode.getChoices();

		for (ChoiceNode childChoiceNode : childChoiceNodes) {

			if (constraintMentionsChoiceNodeOrAnyChild(childChoiceNode, constraintNode)) {
				return true;
			}
		}

		return false;
	}

	private static void createAffectedTestCases(
			IAbstractNode node, 
			Set<TestCaseNode> allTestCaseNodes,
			Set<TestCaseNode> outAffectedTestCases) {

		Iterator<TestCaseNode> itr = allTestCaseNodes.iterator();
		while (itr.hasNext()) {
			TestCaseNode testCase = itr.next();
			if (testCase.mentions((ChoiceNode)node)) {
				outAffectedTestCases.add(testCase);
			}
		}

	}

}
