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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ecfeed.core.utils.ChoiceConversionItem;
import com.ecfeed.core.utils.ChoiceConversionList;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class ParametersParentNode extends AbstractNode {

	private List<AbstractParameterNode> fParameters;
	private List<AbstractParameterNode> fDetachedParameters;

	public ParametersParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);
		fParameters = new ArrayList<AbstractParameterNode>();
		fDetachedParameters = new ArrayList<AbstractParameterNode>();
	}

	public abstract List<MethodNode> getMethods(AbstractParameterNode parameter);	

	public void addParameter(AbstractParameterNode parameter) {

		addParameter(parameter, fParameters.size());
	}

	public void addParameter(AbstractParameterNode parameter, int index) {

		if (parameterExists(parameter)) {
			ExceptionHelper.reportRuntimeException("Parameter: " + parameter.getName() + " already exists.");
		}

		fParameters.add(index, parameter);
		registerChange();
		parameter.setParent(this);
	}

	public List<AbstractParameterNode> getParameters(){
		return fParameters;
	}

	public int getParametersCount(){
		return fParameters.size();
	}	

	public int getDetachedParametersCount() {
		return fDetachedParameters.size();
	}

	public AbstractParameterNode findParameter(String parameterNameToFind) {

		for (AbstractParameterNode parameter : fParameters) {

			final String parameterName = parameter.getName();

			if (parameterName.equals(parameterNameToFind)) {
				return parameter;
			}
		}
		return null;
	}

	public AbstractParameterNode findDetachedParameter(String parameterNameToFind) {

		for (AbstractParameterNode parameter : fDetachedParameters) {

			final String parameterName = parameter.getName();

			if (parameterName.equals(parameterNameToFind)) {
				return parameter;
			}
		}
		return null;
	}

	public AbstractParameterNode getParameter(int parameterIndex) {

		return fParameters.get(parameterIndex);
	}	

	public int getParameterIndex(String parameterName) {

		int index = 0;

		for (AbstractParameterNode parameter : fParameters) {
			if (parameter.getName().equals(parameterName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public boolean parameterExists(String parameterName) {

		if (findParameter(parameterName) == null) {
			return false;
		}

		return true;
	}

	public boolean parameterExists(AbstractParameterNode abstractParameterNode) {

		if (parameterExists(abstractParameterNode.getName())) {
			return true;
		}

		return false;
	}

	public List<String> getParameterTypes() {

		List<String> types = new ArrayList<String>();

		for (AbstractParameterNode parameter : fParameters) {
			types.add(parameter.getType());
		}

		return types;
	}

	public List<String> getParametersNames() {

		List<String> names = new ArrayList<String>();

		for(AbstractParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}

		return names;
	}

	public boolean removeParameter(AbstractParameterNode parameter) {

		parameter.setParent(null);

		boolean result = fParameters.remove(parameter);
		registerChange();

		return result;
	}

	public void replaceParameters(List<AbstractParameterNode> parameters) {

		fParameters.clear();
		fParameters.addAll(parameters);

		registerChange();
	}

	@Override
	public List<? extends AbstractNode> getChildren() {

		return fParameters;
	}

	@Override
	public int getChildrenCount() {

		return fParameters.size();
	}

	@Override
	public boolean isMatch(AbstractNode node) {

		if (node instanceof ParametersParentNode == false) {
			return false;
		}

		ParametersParentNode comparedParent = (ParametersParentNode)node;

		if(getParameters().size() != comparedParent.getParameters().size()) {
			return false;
		}

		for (int i = 0; i < getParameters().size(); ++i) {

			if (getParameters().get(i).isMatch(comparedParent.getParameters().get(i)) == false) {
				return false;
			}
		}

		return super.isMatch(node);
	}

	public static String generateNewParameterName(ParametersParentNode fParametersParentNode, String startParameterName) {

		if (!fParametersParentNode.parameterExists(startParameterName)) {
			return startParameterName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startParameterName);

		for (int i = 1;   ; i++) {

			String newParameterName = oldNameCore + String.valueOf(i);

			if (!fParametersParentNode.parameterExists(newParameterName)) {
				return newParameterName;
			}
		}
	}

	public void addParameterToDetached(AbstractParameterNode abstractParameterNode) {

		abstractParameterNode.setDetached(true);
		fDetachedParameters.add(abstractParameterNode);
	}

	public void detachParameterNode(String name) {

		AbstractParameterNode abstractParameterNode = findParameter(name);

		if (abstractParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot find parameter " + name + ".");
		}

		changeChoicesToDetached(abstractParameterNode);

		MethodNode methodNode = (MethodNode)abstractParameterNode.getParent();

		if (false == removeParameter(abstractParameterNode)) {
			ExceptionHelper.reportRuntimeException("Cannot remove parameter.");
		}

		abstractParameterNode.setParent(methodNode);
		addParameterToDetached(abstractParameterNode);
	}

	private void changeChoicesToDetached(AbstractParameterNode abstractParameterNode) {

		List<ChoiceNode> choiceNodes = abstractParameterNode.getChoices();

		for (ChoiceNode choiceNode : choiceNodes) {
			ChoiceNodeHelper.setDetachedWithChildren(choiceNode, true);
		}

	}

	public void attachParameterNode(
			String detachedParameterName, 
			String destinationParameterName,
			ChoiceConversionList choiceConversionList) {

		MethodParameterNode detachedParameterNode = (MethodParameterNode)findDetachedParameter(detachedParameterName);
		MethodParameterNode destinationParameterNode = (MethodParameterNode)findParameter(destinationParameterName);

		if (detachedParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot find detached parameter " + detachedParameterName + ".");
		}

		MethodNode methodNode = (MethodNode)this;

		if (choiceConversionList != null) {
			moveChoicesByConversionList(
					choiceConversionList, 
					detachedParameterNode, 
					destinationParameterNode,
					methodNode);
		}

		moveRemainingTopChoices(detachedParameterNode, destinationParameterNode);

		methodNode.updateParameterReferencesInConstraints(
				(MethodParameterNode)detachedParameterNode, 
				(MethodParameterNode)destinationParameterNode);


		fDetachedParameters.remove(detachedParameterNode);
	}

	private void moveChoicesByConversionList(
			ChoiceConversionList choiceConversionItems,
			MethodParameterNode srcParameterNode, 
			MethodParameterNode dstParameterNode,
			MethodNode methodNode) {
		
		List<ChoiceConversionItem> sortedChoiceConversionItems = 
				choiceConversionItems.createSortedCopyOfConversionItems();
		
		for (ChoiceConversionItem choiceConversionItem : sortedChoiceConversionItems) {

			ChoiceNode srcChoiceNode = srcParameterNode.getChoice(choiceConversionItem.getSrcName());

			if (srcChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find source choice.");
			}

			ChoiceNode dstChoiceNode = dstParameterNode.getChoice(choiceConversionItem.getDstName());

			if (dstChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find destination choice.");
			}

			moveChildChoices(srcChoiceNode, dstChoiceNode);

			methodNode.updateChoiceReferencesInTestCases(srcChoiceNode, dstChoiceNode);
			methodNode.updateChoiceReferencesInConstraints(srcChoiceNode, dstChoiceNode);

			// remove source choice

			ChoicesParentNode choicesParentNode = srcChoiceNode.getParent();
			choicesParentNode.removeChoice(srcChoiceNode);
		}
	}

	private void moveRemainingTopChoices(MethodParameterNode detachedParameterNode,
			MethodParameterNode destinationParameterNode) {
		List<ChoiceNode> choiceNodes = detachedParameterNode.getChoices();

		for (ChoiceNode choiceNode : choiceNodes) {
			addChoiceWithUniqueName(choiceNode, destinationParameterNode);
		}
	}

	private void moveChildChoices(ChoiceNode srcChoiceNode, ChoiceNode dstChoiceNode) {

		List<ChoiceNode> childChoices = srcChoiceNode.getChoices();

		for (ChoiceNode childChoice : childChoices) {

			dstChoiceNode.addChoice(childChoice);
		}
	}

	private void addChoiceWithUniqueName(ChoiceNode choiceNode, MethodParameterNode methodParameterNode) {


		String orginalChoiceName = choiceNode.getName();

		if (!choiceNameExistsAmongChildren(orginalChoiceName, methodParameterNode)) {

			methodParameterNode.addChoice(choiceNode);
			return;
		}

		for (int postfixCounter = 1; postfixCounter < 999; postfixCounter++) {

			String tmpName = orginalChoiceName + "-" + postfixCounter;

			if (!choiceNameExistsAmongChildren(tmpName, methodParameterNode)) {

				choiceNode.setName(tmpName);
				methodParameterNode.addChoice(choiceNode);
				return;
			}
		}

		ExceptionHelper.reportRuntimeException("Cannot add choice to method parameter.");
	}

	// TODO DE-NO - move to choices parent node
	private boolean choiceNameExistsAmongChildren(String choiceName, MethodParameterNode methodParameterNode) {

		List<ChoiceNode> choiceNodes = methodParameterNode.getChoices();

		for (ChoiceNode choiceNode : choiceNodes) {

			String currentChoiceName = choiceNode.getName();

			if (currentChoiceName.equals(choiceName)) {
				return true;
			}
		}

		return false;
	}

}
