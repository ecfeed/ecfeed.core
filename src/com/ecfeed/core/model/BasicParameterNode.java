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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class BasicParameterNode extends AbstractParameterNode implements IChoicesParentNode {

	private String fType;
	private String fTypeComments;
	private boolean fExpected;
	private String fDefaultValue;
	private boolean fLinked;
	private BasicParameterNode fLinkToGlobalParameter;
	private MethodNode fLinkToMethod;
	private List<ChoiceNode> fChoicesCopy;
	
	private ChoicesListHolder fChoicesListHolder;

	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			boolean linked,
			BasicParameterNode link,
			IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fType = type;
		fExpected = expected;
		fDefaultValue = defaultValue;
		fLinked = linked;
		fLinkToGlobalParameter = link;
		
		fChoicesListHolder = new ChoicesListHolder(modelChangeRegistrator);
		
		createDefaultProperties();
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
			BasicParameterNode source,
			String defaultValue, 
			boolean expected, 
			boolean linked,
			BasicParameterNode link) {

		this(source.getName(), source.getType(), defaultValue, expected, linked, link, source.getModelChangeRegistrator());

		addChoices(source.getChoices());
	}

	public BasicParameterNode(BasicParameterNode source,
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
	
	public boolean isGlobalParameter() {
		IAbstractNode parent = getParent();
		
		if (parent instanceof MethodNode) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
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

	public String getType() {
		
		if (fLinkToMethod != null) {
			return null;
		}

		if (isLinked() && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getType();
		}
		
		return fType;
	}

	public void setType(String type) {

		if (SimpleLanguageHelper.isSimpleType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to set invalid parameter type: " + type);
		}

		fType = type;
		registerChange();
	}
	
	public String getTypeComments() {

		if (isLinked() && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getTypeComments();
		}
		return fTypeComments;
	}

	public void setTypeComments(String comments){
		fTypeComments = comments;
		registerChange();
	}
	
	public String getRealType() { 
		return fType;
	}

	@Override
	public List<ChoiceNode> getChoices(){

		boolean linked = isLinked();
		
		if (linked && fLinkToGlobalParameter != null) {
			return fLinkToGlobalParameter.getChoices();
		}
		
		return fChoicesListHolder.getChoices();
	}

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
		return getChoices();
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

		List<IAbstractNode> result = new ArrayList<>();
		result.addAll(fChoicesListHolder.getChoices());
		
		return result;
	}

	public List<ChoiceNode> getRealChoices() {
		return fChoicesListHolder.getChoices();
	}

	public List<MethodNode> getMethods() {
		
		MethodNode method = getMethod();
		
		if (method == null) {
			return new ArrayList<>();
		}
		
		return Arrays.asList(new MethodNode[] { method });
	}

	public List<ChoiceNode> getOwnChoices() {
		return getChoices();
	}

	public MethodNode getMethod() {
		
		IAbstractNode parent = getParent();
		
		if (parent instanceof RootNode) {
			return null;
		}

		if (parent instanceof ClassNode) {
			return null;
		}
		
		return (MethodNode) parent;
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

	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public Set<ConstraintNode> getMentioningConstraints() {
		return getMethod().getMentioningConstraints(this);
	}

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
		
		if (isGlobalParameter()) {
			return new ArrayList<>();
		}

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

	@Override
	public int getChildrenCount() {

		return getChoiceCount();
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd) {

		fChoicesListHolder.addChoice(choiceToAdd, this);
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd, int index) {

		fChoicesListHolder.addChoice(choiceToAdd, index, this);
	}

	@Override
	public void addChoices(List<ChoiceNode> choicesToAdd) {

		fChoicesListHolder.addChoices(choicesToAdd, this);
	}

	@Override
	public int getChoiceCount() {

		return getChoices().size();
	}

	@Override
	public ChoiceNode getChoice(String qualifiedName) {

		return (ChoiceNode)getChild(qualifiedName);
	}

	@Override
	public int getChoiceIndex(String choiceNameToFind) {

		return fChoicesListHolder.getChoiceIndex(choiceNameToFind);
	}

	@Override
	public boolean choiceExistsAsDirectChild(String choiceNameToFind) {

		return fChoicesListHolder.choiceExists(choiceNameToFind);
	}

	@Override
	public List<ChoiceNode> getLeafChoices() {
		
		return ChoiceNodeHelper.getLeafChoices(getChoices());	}

	@Override
	public List<ChoiceNode> getLeafChoicesWithCopies() {

		return ChoiceNodeHelper.getLeafChoices(getChoicesWithCopies());
	}

	@Override
	public Set<String> getAllChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getAllChoices());
	}

	@Override
	public Set<String> getAllLabels() {

		return ChoiceNodeHelper.getAllLabels(getAllChoices());
	}

	@Override
	public Set<String> getLeafChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getLeafChoices());
	}

	@Override
	public Set<ChoiceNode> getAllChoices() {

		return ChoiceNodeHelper.getAllChoices(getChoices());
	}

	@Override
	public Set<String> getChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getChoices());
	}

	@Override
	public Set<ChoiceNode> getLabeledChoices(String label) {

		return ChoiceNodeHelper.getLabeledChoices(label, getChoices());
	}

	@Override
	public Set<String> getLeafLabels() {

		return ChoiceNodeHelper.getLeafLabels(getLeafChoices());
	}

	@Override
	public Set<String> getLeafChoiceValues() {

		return ChoiceNodeHelper.getLeafChoiceValues(getLeafChoices());
	}

	@Override
	public boolean removeChoice(ChoiceNode choice) {
		
		return fChoicesListHolder.removeChoice(choice);	
	}

	@Override
	public void replaceChoices(List<ChoiceNode> newChoices) {
		
		fChoicesListHolder.replaceChoices(newChoices, this);
	}

	@Override
	public void clearChoices() {
		fChoicesListHolder.clearChoices();
		
	}

	@Override
	public BasicParameterNode getParameter() { // TODO MO-RE remove ?
		
		return this;
	}
	
	public IParametersParentNode getParametersParent() {

		return (IParametersParentNode)getParent();
	}
	
	public boolean isCorrectableToBeRandomizedType() {
		return JavaLanguageHelper.isNumericTypeName(fType) || JavaLanguageHelper.isStringTypeName(fType);
	}

	private void createDefaultProperties() {

		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL);
	}
	
}
