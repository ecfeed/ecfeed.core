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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.JavaLanguageHelper;

public class MethodParameterNode extends AbstractParameterNode {

	private boolean fExpected;
	private String fDefaultValue;
	private boolean fLinked;
	private GlobalParameterNode fLink;
	private List<ChoiceNode> fChoicesCopy;

	public MethodParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			boolean linked,
			GlobalParameterNode link,
			IModelChangeRegistrator modelChangeRegistrator) {

		super(name, type, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fExpected = expected;
		fDefaultValue = defaultValue;
		fLinked = linked;
		fLink = link;
	}

	public MethodParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, type, defaultValue, expected, false, null, modelChangeRegistrator);
	}

	public MethodParameterNode(AbstractParameterNode source,
			String defaultValue, boolean expected, boolean linked,
			GlobalParameterNode link) {

		this(
				source.getName(),
				source.getType(), defaultValue, expected, linked, link, source.getModelChangeRegistrator()
				);

		addChoices(source.getChoices());
	}

	public MethodParameterNode(AbstractParameterNode source,
			String defaultValue, boolean expected) {
		this(source, defaultValue, expected, false, null);
	}

	@Override
	protected String getNonQualifiedName() {
		return getName();
	}

	@Override
	public String toString() {
		if (fExpected) {
			return super.toString() + "(" + getDefaultValue() + "): "
					+ getType();
		}
		return new String(getName() + ": " + getType());
	}

	@Override
	public MethodParameterNode makeClone() {
		MethodParameterNode copy = 
				new MethodParameterNode(getName(), getType(), getDefaultValue(), isExpected(), getModelChangeRegistrator()
						);

		copy.fLinked = fLinked;
		copy.fLink = fLink;

		copy.setProperties(getProperties());
		copy.setParent(this.getParent());

		if (getDefaultValue() != null)
			copy.setDefaultValueString(getDefaultValue());

		for (ChoiceNode choice : getChoices()) {
			copy.addChoice(choice.makeClone());
		}

		copy.setParent(getParent());
		return copy;
	}

	@Override
	public String getType() {
		if (isLinked() && fLink != null) {
			return fLink.getType();
		}
		return super.getType();
	}

	@Override
	public String getTypeComments() {
		if (isLinked() && fLink != null) {
			return fLink.getTypeComments();
		}
		return super.getTypeComments();
	}

	public String getRealType() {
		return super.getType();
	}

	@Override
	public List<ChoiceNode> getChoices(){
		if(isLinked() && fLink != null){
			return fLink.getChoices();
		}
		return super.getChoices();
	}

	@Override
	public List<ChoiceNode> getChoicesWithCopies() {
		if (isLinked() && fLink != null) {
			if (fChoicesCopy == null) {
				fChoicesCopy = fLink.getChoicesCopy();
				return fChoicesCopy;
			}
			List<ChoiceNode> temp = fLink.getChoicesCopy();
			if(!choiceListsMatch(fChoicesCopy, temp))
				fChoicesCopy = temp;
			return fChoicesCopy;
		}
		return super.getChoices();
	}

	public ChoiceNode findChoice(String choiceQualifiedName) {

		Set<ChoiceNode> choiceNodes = getAllChoices();

		Iterator<ChoiceNode> it = choiceNodes.iterator();

		while(it.hasNext()) {
			ChoiceNode choiceNode = it.next();

			if (choiceNode.getQualifiedName().equals(choiceQualifiedName)) {
				return choiceNode;
			}
		}

		return null;
	}

	public ChoiceNode findFirstChoiceWithValue(String choiceValueString) {

		Set<ChoiceNode> choiceNodes = getAllChoices();

		Iterator<ChoiceNode> it = choiceNodes.iterator();

		while(it.hasNext()) {
			ChoiceNode choiceNode = it.next();

			if (choiceNode.getValueString().equals(choiceValueString)) {
				return choiceNode;
			}
		}

		return null;
	}

	private boolean choiceListsMatch(List<ChoiceNode> list1,
			List<ChoiceNode> list2) {
		if(list1.size() != list2.size())
			return false;
		for(int i=0; i< list1.size(); i++)
			if(list1.get(i).getName() != list2.get(i).getName() || list1.get(i).getValueString() != list2.get(i).getValueString())
				return false;
		return true;
	}

	//	@Override
	//	public ChoiceNode getChoice(String qualifiedName) {
	//		if (isLinked()) {
	//			return getLink().getChoice(qualifiedName);
	//		}
	//		return super.getChoice(qualifiedName);
	//	}

	@Override
	public List<? extends AbstractNode> getChildren() {
		if(isLinked())
			return getChoices();
		return super.getChildren();
	}

	public List<ChoiceNode> getRealChoices() {
		return super.getChoices();
	}

	@Override
	public List<MethodNode> getMethods() {
		return Arrays.asList(new MethodNode[] { getMethod() });
	}

	public List<ChoiceNode> getOwnChoices() {
		return super.getChoices();
	}

	public MethodNode getMethod() {
		return (MethodNode) getParent();
	}

	public String getDefaultValue() {
		return fDefaultValue;
	}

	public String getDefaultValueForSerialization() {
		if (fDefaultValue == null) {
			return new String();
		}
		return fDefaultValue;
	}	

	public void setDefaultValueString(String value) {
		fDefaultValue = value;
		registerChange();
	}

	public boolean isExpected() {
		return fExpected;
	}

	public void setExpected(boolean isexpected) {
		fExpected = isexpected;
		registerChange();
	}

	public boolean isLinked() {
		return fLinked;
	}

	public void setLinked(boolean linked) {

		fLinked = linked;
		registerChange();
	}

	public GlobalParameterNode getLink() {
		return fLink;
	}

	public void setLink(GlobalParameterNode link) {
		this.fLink = link;
		registerChange();
	}

	@Override
	public boolean isMatch(AbstractNode node) {
		if (node instanceof MethodParameterNode == false) {
			return false;
		}
		MethodParameterNode comparedParameter = (MethodParameterNode) node;

		if (getType().equals(comparedParameter.getType()) == false) {
			return false;
		}

		if (isExpected() != comparedParameter.isExpected()) {
			return false;
		}

		if (fDefaultValue
				.equals(comparedParameter.getDefaultValue()) == false) {
			return false;
		}

		int choicesCount = getChoiceCount();
		if (choicesCount != comparedParameter.getChoiceCount()) {
			return false;
		}

		for (int i = 0; i < choicesCount; i++) {
			if (getChoices().get(i)
					.isMatch(comparedParameter.getChoices().get(i)) == false) {
				return false;
			}
		}

		return super.isMatch(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints() {
		return getMethod().getMentioningConstraints(this);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(String label) {
		return getMethod().getMentioningConstraints(this, label);
	}

	//	public List<String> detachChoiceNode(
	//			String choiceQualifiedName,
	//			ListOfModelOperations inOutReverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		ChoiceNode choiceNode = findChoice(choiceQualifiedName);
	//
	//		if (choiceNode == null) {
	//			ExceptionHelper.reportRuntimeException("Cannot find choice node using qualified name.");
	//		}
	//
	//		MethodNode methodNode = choiceNode.getMethodNode();
	//
	//		MethodParameterNode methodParameterNode = (MethodParameterNode)choiceNode.getParameter();
	//
	//		if (methodNode == null) {
	//			ExceptionHelper.reportRuntimeException("Attempt to detach choice without method.");
	//		}
	//
	//		List<String> detachedChoiceNames = new ArrayList<String>();
	//
	//		detachChoiceNodeWithChildren(
	//				choiceNode, 
	//				methodParameterNode, 
	//				methodNode, 
	//				detachedChoiceNames,
	//				inOutReverseOperations,
	//				extLanguageManager);
	//
	//		return detachedChoiceNames;
	//	}

	//	private void detachChoiceNodeWithChildren(
	//			ChoiceNode parentChoiceNode, 
	//			MethodParameterNode methodParameterNode,
	//			MethodNode methodNode, 
	//			List<String> inOutDetachedChoiceNames,
	//			ListOfModelOperations reverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		List<ChoiceNode> choiceNodes = parentChoiceNode.getChoices();
	//
	//		int countOfChoices = choiceNodes.size();
	//
	//		for (int index = countOfChoices - 1; index >= 0; index--) {
	//
	//			ChoiceNode currentChoiceNode = choiceNodes.get(index);
	//
	//			detachChoiceNodeWithChildren(
	//					currentChoiceNode, 
	//					methodParameterNode, 
	//					methodNode, 
	//					inOutDetachedChoiceNames,
	//					reverseOperations,
	//					extLanguageManager);
	//		}
	//
	//		detachSingleChoiceNode(
	//				parentChoiceNode, 
	//				methodParameterNode, 
	//				methodNode, 
	//				inOutDetachedChoiceNames,
	//				reverseOperations,
	//				extLanguageManager);
	//	}

	//	public void detachSingleChoiceNode(
	//			ChoiceNode choiceNode, 
	//			MethodParameterNode methodParameterNode,
	//			MethodNode methodNode,
	//			List<String> inOutDetachedChoiceNames,
	//			ListOfModelOperations reverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		ChoiceNode clonedChoiceNode = choiceNode.makeClone();
	//
	//		String qualifiedNameForDetachedNodes = clonedChoiceNode.getQualifiedName("-");
	//		clonedChoiceNode.setName(qualifiedNameForDetachedNodes);
	//		clonedChoiceNode.setParent(methodParameterNode);
	//
	//		String detachedChoiceNewName = addChoiceToDetachedWithUniqueName(clonedChoiceNode);
	//		inOutDetachedChoiceNames.add(detachedChoiceNewName);
	//
	//		MethodNodeHelper.updateChoiceReferencesInTestCases(
	//				choiceNode, clonedChoiceNode, methodNode.getTestCases(), reverseOperations, extLanguageManager);
	//
	//		MethodNodeHelper.updateChoiceReferencesInConstraints(
	//				choiceNode, clonedChoiceNode,
	//				methodNode.getConstraintNodes(),
	//				extLanguageManager);
	//
	//		ChoicesParentNode choicesParentNode = choiceNode.getParent();
	//		choicesParentNode.removeChoice(choiceNode);
	//	}

	//	public void attachChoiceNode(
	//			String detachedChoiceName, 
	//			String actualChoiceName,
	//			ListOfModelOperations reverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		ChoiceNode actualChoiceNode = getChoice(actualChoiceName);
	//		ChoiceNode detachedChoiceNode = getDetachedChoice(detachedChoiceName);
	//
	//		MethodNode methodNode = actualChoiceNode.getMethodNode();
	//
	//		if (methodNode == null) {
	//			ExceptionHelper.reportRuntimeException("Attempt to detach choice without method.");
	//		}
	//
	//		MethodNodeHelper.updateChoiceReferencesInTestCases(
	//				detachedChoiceNode, actualChoiceNode, methodNode.getTestCases(), 
	//				reverseOperations, extLanguageManager);
	//
	//		MethodNodeHelper.updateChoiceReferencesInConstraints(
	//				detachedChoiceNode, actualChoiceNode,
	//				methodNode.getConstraintNodes(),
	//				extLanguageManager);
	//
	//		int detachedIndex = getDetachedChoiceIndex(detachedChoiceName);
	//		removeDetachedChoiceByIndex(detachedIndex);
	//	}

}
