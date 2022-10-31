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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class BasicParameterNode extends AbstractParameterNode {

	private boolean fExpected;
	private String fDefaultValue;
	private boolean fLinked;
	private BasicParameterNode fLinkToGlobalParameter;
	private MethodNode fLinkToMethod;
	private List<ChoiceNode> fChoicesCopy;

	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			boolean linked,
			BasicParameterNode link,
			IModelChangeRegistrator modelChangeRegistrator) {

		super(name, type, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fExpected = expected;
		fDefaultValue = defaultValue;
		fLinked = linked;
		fLinkToGlobalParameter = link;
	}

	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, type, defaultValue, expected, false, null, modelChangeRegistrator);
	}

	public BasicParameterNode(
			String name,
			String type,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, type, null, false, false, null, modelChangeRegistrator);
	}
	
	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected) {
		
		this(name, type, defaultValue, expected, null);
	}
	
	public BasicParameterNode(
			AbstractParameterNode source,
			String defaultValue, 
			boolean expected, 
			boolean linked,
			BasicParameterNode link) {

		this(
				source.getName(),
				source.getType(), defaultValue, expected, linked, link, source.getModelChangeRegistrator()
				);

		addChoices(source.getChoices());
	}

	public BasicParameterNode(AbstractParameterNode source,
			String defaultValue, boolean expected) {
		this(source, defaultValue, expected, false, null);
	}

	public BasicParameterNode(BasicParameterNode source) {

		this(
				source.getName(),
				source.getType(),
				source.getDefaultValue(),
				source.fExpected,
				source.fLinked,
				source.fLinkToGlobalParameter,
				source.getModelChangeRegistrator());
		
		for(ChoiceNode choice : source.getChoices()){
			addChoice(choice.makeClone());
		}
	}
	
	@Override
	public boolean isGlobalParameter() {
		IAbstractNode parent = getParent();
		
		if (parent instanceof MethodNode) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String getNonQualifiedName() {
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
	public BasicParameterNode makeClone() {
		BasicParameterNode copy = 
				new BasicParameterNode(getName(), getType(), getDefaultValue(), isExpected(), getModelChangeRegistrator()
						);

		copy.fLinked = fLinked;
		copy.fLinkToGlobalParameter = fLinkToGlobalParameter;

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
		
		if (fLinkToMethod != null) {
			return null;
		}

		if (isLinked() && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getType();
		}
		return super.getType();
	}

	@Override
	public String getTypeComments() {

		if (isLinked() && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getTypeComments();
		}
		return super.getTypeComments();
	}

	public String getRealType() {
		return super.getType();
	}

	@Override
	public List<ChoiceNode> getChoices(){

		boolean linked = isLinked();
		
		if (linked && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getChoices();
		}
		
		return super.getChoices();
	}

	@Override
	public List<ChoiceNode> getChoicesWithCopies() {

		if (isLinked() && fLinkToGlobalParameter != null) {
			if (fChoicesCopy == null) {
				fChoicesCopy = fLinkToGlobalParameter.getChoicesCopy();
				return fChoicesCopy;
			}
			List<ChoiceNode> temp = fLinkToGlobalParameter.getChoicesCopy();
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

	@Override
	public List<IAbstractNode> getChildren() {
		
		if (isLinked()) {
			
			List<IAbstractNode> result = new ArrayList<>();
			result.addAll(getChoices());
			
			return result;
		}
		
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

	public BasicParameterNode getLinkToGlobalParameter() {

		return fLinkToGlobalParameter;
	}

	public MethodNode getLinkToMethod() {

		return fLinkToMethod;
	}

	public void setLinkToGlobalParameter(BasicParameterNode link) {

		fLinkToGlobalParameter = link;
		fLinkToMethod = null;
	}

	public void setLinkToMethod(MethodNode link) {

		fLinkToMethod = link;
		fLinkToGlobalParameter = null;
	}

	@Override
	public boolean isMatch(IAbstractNode other) {
		
		if (other instanceof BasicParameterNode == false) {
			return false;
		}
		
		BasicParameterNode otherBasicParameter = (BasicParameterNode) other;

		if (getType().equals(otherBasicParameter.getType()) == false) {
			return false;
		}

		if (isExpected() != otherBasicParameter.isExpected()) {
			return false;
		}

		String defaultValue = getDefaultValue();
		String otherDefaultValue = otherBasicParameter.getDefaultValue();
		
		if (!StringHelper.isEqual(defaultValue, otherDefaultValue)) {
			return false;
		}

		int choicesCount = getChoiceCount();
		int otherChoicesCount = otherBasicParameter.getChoiceCount();
		
		if (choicesCount != otherChoicesCount) {
			return false;
		}

		for (int i = 0; i < choicesCount; i++) {
			if (getChoices().get(i).isMatch(otherBasicParameter.getChoices().get(i)) == false) {
				return false;
			}
		}

		return super.isMatch(other);
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

	public List<ChoiceNode> getChoicesCopy() {
		List<ChoiceNode> copy = new ArrayList<>();
		for(ChoiceNode choice : getChoices()){
			copy.add(choice.makeClone());
		}
		return copy;
	}

	public List<BasicParameterNode> getLinkedMethodParameters(){

		List<BasicParameterNode> result = new ArrayList<>();
		List<MethodNode> methods = getMethods();

		if (methods == null) {
			return new ArrayList<>();

		}

		for(MethodNode method : methods) {
			result.addAll(method.getLinkers(this));
		}

		return result;
	}

	public String getQualifiedName() {

		if (isGlobalParameter()) {
			if(getParent() == getRoot() || getParent() == null){
				return getName();
			}
			return getParent().getName() + ":" + getName();
		} else {
			return getNonQualifiedName();
		}
	}
	
}
