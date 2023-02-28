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
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class BasicParameterNode extends AbstractParameterNode implements IChoicesParentNode {

	private String fType;
	private String fTypeComments = "";
	private boolean fExpected;
	private String fDefaultValue;
	private List<ChoiceNode> fChoicesCopy;
	private BasicParameterNode fDeploymentParameterNode;

	private ChoicesListHolder fChoicesListHolder;

	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			AbstractParameterNode link,
			IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fType = type;
		fExpected = expected;
		fDefaultValue = defaultValue;
		fChoicesListHolder = new ChoicesListHolder(modelChangeRegistrator);

		setLinkToGlobalParameter(link);

		createDefaultProperties();
	}

	public BasicParameterNode(
			String name,
			String type,
			String defaultValue,
			boolean expected,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, type, defaultValue, expected, null, modelChangeRegistrator);
	}

	public BasicParameterNode(
			String name,
			String type,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, type, null, false, null, modelChangeRegistrator);
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
			AbstractParameterNode link) {

		this(source.getName(), source.getType(), defaultValue, expected, link, source.getModelChangeRegistrator());

		addChoices(source.getChoices());
	}

	public BasicParameterNode(BasicParameterNode source,
			String defaultValue, boolean expected) {
		this(source, defaultValue, expected, null);
	}

	public BasicParameterNode(BasicParameterNode source) {

		this(
				source.getName(),
				source.getType(),
				source.getDefaultValue(),
				source.fExpected,
				source.getLinkToGlobalParameter(),
				source.getModelChangeRegistrator());

		for(ChoiceNode choice : source.getChoices()){
			addChoice(choice.makeClone());
		}
	}

	@Override
	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
	}

	public void setNameUnsafe(String name) {

		super.setName(name);
	}

	@Override
	public void setCompositeName(String name) {

		String simplifiedName = name.replace(SignatureHelper.SIGNATURE_NAME_SEPARATOR, "_"); 
		JavaLanguageHelper.verifyIsValidJavaIdentifier(simplifiedName);

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
		return getName() + ": " + getType();
	}

	@Override
	public BasicParameterNode makeClone() {
		BasicParameterNode parameter = makeClone(null);

		parameter.setParent(getParent());

		return parameter;
	}
	
// TODO LATEST [REFACTOR]	
	public BasicParameterNode createCopy(NodeMapper mapper) {
		BasicParameterNode parameter = makeClone(mapper);

		parameter.setDeploymentParameter(this);
		parameter.setParent(null);

		mapper.addMappings(this, parameter);

		parameter.setNameUnsafe(AbstractParameterNodeHelper.getQualifiedName(this));

		return parameter;
	}

	public BasicParameterNode getDeploymentParameter() {

		return fDeploymentParameterNode;
	}

	public void setDeploymentParameter(BasicParameterNode parameterNode) {
		fDeploymentParameterNode = parameterNode;
	}

	private BasicParameterNode makeClone(NodeMapper mapper) {

		BasicParameterNode copyOfBasicParameterNode =
				new BasicParameterNode(
						getName(), getType(), getDefaultValue(), isExpected(), getModelChangeRegistrator());

		copyProperties(copyOfBasicParameterNode);

		if (!this.isLinked()) {
			ChoiceNodeHelper.cloneChoiceNodesRecursively(this, copyOfBasicParameterNode, mapper);
		}

		return copyOfBasicParameterNode;
	}

	private void copyProperties(BasicParameterNode copyOfBasicParameterNode) {

		copyOfBasicParameterNode.setLinkToGlobalParameter(getLinkToGlobalParameter());
		copyOfBasicParameterNode.setProperties(getProperties());

		if (getDefaultValue() != null) {
			copyOfBasicParameterNode.setDefaultValueString(getDefaultValue());
		}
	}

	public String getType() {

		if (isLinked() && getLinkToGlobalParameter() != null) {

			if (getLinkToGlobalParameter() instanceof BasicParameterNode) {

				BasicParameterNode link = (BasicParameterNode) getLinkToGlobalParameter();

				return link.getType();
			}

			return null;
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

		if (isLinked() && getLinkToGlobalParameter() != null) {

			if (getLinkToGlobalParameter() instanceof BasicParameterNode) {

				BasicParameterNode link = (BasicParameterNode) getLinkToGlobalParameter();

				return link.getTypeComments();
			}

			return null;
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

		if (!linked) {
			return fChoicesListHolder.getChoices();
		}

		AbstractParameterNode abstractLinkToGlobalParameter = getLinkToGlobalParameter();

		if (abstractLinkToGlobalParameter == null) {
			ExceptionHelper.reportRuntimeException("Invalid configuration of linked parameter.");
		}

		if (!(abstractLinkToGlobalParameter instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Invalid link type.");
		}

		BasicParameterNode basicParameterNodeLink = (BasicParameterNode) abstractLinkToGlobalParameter;

		return basicParameterNodeLink.getChoices();
	}

	public List<ChoiceNode> getChoicesWithCopies() {

		if (isLinked() && getLinkToGlobalParameter() != null) {

			if (getLinkToGlobalParameter() instanceof BasicParameterNode) {

				BasicParameterNode link = (BasicParameterNode) getLinkToGlobalParameter();

				if (fChoicesCopy == null) {
					fChoicesCopy = link.getChoices();
					return fChoicesCopy;
				}

				List<ChoiceNode> temp = link.getChoices();

				if (!choiceListsMatch(fChoicesCopy, temp)) {
					fChoicesCopy = temp;
				}

				return fChoicesCopy;
			}

			return null;
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

		IAbstractNode parent = getParent();

		if (parent instanceof MethodNode) {

			MethodNode method = (MethodNode) parent;

			return Arrays.asList(new MethodNode[] { method });
		}

		if (parent instanceof CompositeParameterNode) {
			return new ArrayList<>();
		}

		if (parent instanceof RootNode) {
			return new ArrayList<>();
		}

		if (parent instanceof ClassNode) {
			return new ArrayList<>();
		}

		ExceptionHelper.reportRuntimeException("Unexpected parent node type.");
		return null;
	}

	public List<ChoiceNode> getOwnChoices() {
		return getChoices();
	}

	public String getDefaultValue() {
		return fDefaultValue;
	}

	public String getDefaultValueForSerialization() {
		if (fDefaultValue == null) {
			return "";
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

	@Override
	public boolean isMatch(IAbstractNode other) {

		if (other instanceof BasicParameterNode == false) {
			return false;
		}

		BasicParameterNode otherBasicParameter = (BasicParameterNode) other;

		if (!propertiesMatch(otherBasicParameter)) {
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

	public boolean propertiesMatch(BasicParameterNode otherBasicParameter) {

		if (!StringHelper.isEqual(getName(), otherBasicParameter.getName())) {
			return false;
		}

		if (!StringHelper.isEqual(getType(), otherBasicParameter.getType())) {
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

		return true;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public Object accept(IBasicParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public Set<ConstraintNode> getMentioningConstraints() {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) getParent();
		return constraintsParentNode.getMentioningConstraints(this);
	}

	public Set<ConstraintNode> getMentioningConstraints(String label) {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) getParent();
		return constraintsParentNode.getMentioningConstraints(this, label);
	}

	public List<ChoiceNode> getChoicesCopy() {
		List<ChoiceNode> copy = new ArrayList<>();
		for(ChoiceNode choice : getChoices()){
			copy.add(choice.makeClone());
		}
		return copy;
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
	public boolean hasChoices() {

		if (getChoiceCount() == 0) {
			return false;
		}

		return true;
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
	public BasicParameterNode getParameter() {

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

	@Override
	public BasicParameterNode getLinkDestination() {

		return (BasicParameterNode) super.getLinkDestination();
	}
}
