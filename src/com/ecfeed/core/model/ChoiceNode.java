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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class ChoiceNode extends AbstractNode implements IChoicesParentNode {

	private static final String NO_PARENT = "No parent.";
	public static final String ABSTRACT_CHOICE_MARKER = "[ABSTRACT]";
	public static final String ASSIGNMENT_NAME = "@assignment";

	private String fValueString;
	private Set<String> fLabels;
	private boolean fIsRandomizedValue;
	private ChoicesListHolder fChoicesListHolder;

	private ChoiceNode fOrigChoiceNode = null; // used in Sat Solver

	public ChoiceNode(String name, String value, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
		fIsRandomizedValue = false;

		fChoicesListHolder = new ChoicesListHolder(modelChangeRegistrator);
	}

	public ChoiceNode(String name, String value) {
		this(name, value, null);
	}

	public ChoiceNode(String name, String value, boolean isRandomized, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<>();
		fIsRandomizedValue = isRandomized;

		fChoicesListHolder = new ChoicesListHolder(modelChangeRegistrator);
	}

	@Override
	public BasicParameterNode getParameter() {

		BasicParameterNode basicParameterNode = ChoiceNodeHelper.getBasicParameter(this);
		return basicParameterNode;
	}

	@Override
	public List<IAbstractNode> getChildren(){
		return new ArrayList<>(getChoices());
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public String toString(){

		if(isAbstract()){
			return getQualifiedName() + ABSTRACT_CHOICE_MARKER; 
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}

	public void derandomize() {

		if (!isRandomizedValue()) {
			return;
		}

		setValueString(getDerandomizedValue());
		setRandomizedValue(false);
	}

	public String getDerandomizedValue() {

		if (!isRandomizedValue()) {
			return getValueString();
		}

		BasicParameterNode parameter = getParameter();

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Method parameter unknown.");
		}
		String typeName = parameter.getType();

		if (!JavaLanguageHelper.isJavaType(typeName)) {
			return getValueString();
		}

		ITypeAdapter<?> typeAdapter = JavaLanguageHelper.getTypeAdapter(typeName);

		String valueString = getValueString();

		String derandomizedValueString = typeAdapter.generateValueAsString(valueString, "Derandomizing.");

		return derandomizedValueString;
	}

	public String toStringWithParenthesis() {

		if(isAbstract()){
			return getQualifiedName() + ABSTRACT_CHOICE_MARKER;
		}

		return getQualifiedName() + "(" + getValueString() + ")";
	}

	public boolean isClone()
	{
		return fOrigChoiceNode==null;
	}

	public void setOrigChoiceNode(ChoiceNode node)
	{
		fOrigChoiceNode = node;
	}

	public ChoiceNode getOrigChoiceNode()
	{
		if(fOrigChoiceNode==null)
			return this;
		return fOrigChoiceNode;
	}

	public ChoiceNode makeClone() { // TODO MO-RE remove ?

		ChoiceNode copy = makeCloneUnlink();

		if(isClone())
			copy.setOrigChoiceNode(getOrigChoiceNode());
		else
			copy.setOrigChoiceNode(this);

		return copy;
	}

	public ChoiceNode makeCloneUnlink() { // TODO MO-RE rename ? private ?

		ChoiceNode copy = new ChoiceNode(getName(), fValueString, getModelChangeRegistrator());

		copy.setProperties(getProperties());
		copy.setParent(getParent());

		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.makeClone());
		}
		for(String label : fLabels){
			copy.addLabel(label);
		}

		copy.setRandomizedValue(fIsRandomizedValue);
		return copy;
	}

	@Override
	public ChoiceNode makeClone(Optional<NodeMapper> nodeMapper) {

		ChoiceNode copy = new ChoiceNode(getName(), fValueString, getModelChangeRegistrator());

		copy.setProperties(getProperties());
		copy.setParent(getParent());
		copy.setRandomizedValue(fIsRandomizedValue);

		for (ChoiceNode choice : getChoices()) {
			copy.addChoice(choice.makeClone(nodeMapper));
		}

		for (String label : fLabels) {
			copy.addLabel(label);
		}

		if (nodeMapper.isPresent()) {
			nodeMapper.get().addMappings(this, copy);
		}

		return copy;
	}

	public ChoiceNode getQualifiedCopy(BasicParameterNode parameter) {
		return ChoiceNodeHelper.createSubstitutePath(this, parameter);
	}

	public String getQualifiedName() {

		return getQualifiedName(":");
	}

	public String getQualifiedName(String separatorForChoiceNames) {

		if (getParentChoice() != null) {
			return getParentChoice().getQualifiedName(separatorForChoiceNames) + separatorForChoiceNames + getName();
		}

		return getName();
	}

	public boolean isCorrectableToBeRandomizedType() {

		IAbstractNode parent = getParent();

		if (parent == null) {
			ExceptionHelper.reportRuntimeException(NO_PARENT);
			return false;
		}

		if (!(parent instanceof IChoicesParentNode)) {
			ExceptionHelper.reportRuntimeException(NO_PARENT);
			return false;
		}

		IChoicesParentNode choicesParentNode = (IChoicesParentNode)parent;

		return choicesParentNode.getParameter().isCorrectableToBeRandomizedType() && !isAbstract();
	}

	public void setRandomizedValue(boolean choice) {
		fIsRandomizedValue = choice;
	}

	public boolean isRandomizedValue() {

		if (isAbstract()) {
			return false;
		}

		return fIsRandomizedValue;
	}

	public String getRandomizedValueStr() {
		return isRandomizedValue() ? "YES" : "NO";
	}

	public void setParent(IChoicesParentNode parent){
		super.setParent(parent);
	}

	public String getValueString() {
		return fValueString;
	}

	public void setValueString(String value) {
		fValueString = value;
	}

	public boolean addLabel(String label){
		return fLabels.add(label);
	}

	public boolean removeLabel(String label){
		return fLabels.remove(label);
	}

	public void renameLabel(String oldValue, String newValue) {

		if (fLabels.contains(oldValue)) {
			fLabels.remove(oldValue);
			fLabels.add(newValue);
		}
	}

	public Set<String> getLabels(){
		return fLabels;
	}

	@Override
	public Set<String> getLeafLabels() {

		if (isAbstract() == false) {
			return getAllLabels();
		}

		return ChoiceNodeHelper.getLeafLabels(getLeafChoices());
	}

	public Set<String> getAllLabels(){
		Set<String> allLabels = getInheritedLabels();
		allLabels.addAll(fLabels);
		return allLabels;
	}

	public Set<String> getInheritedLabels() {
		if(getParentChoice() != null){
			return getParentChoice().getAllLabels();
		}
		return new LinkedHashSet<String>();
	}

	public List<String> getListOfChildrenChoiceNames() {

		List<ChoiceNode> existingChoices = getChoices();

		List<String> names = new ArrayList<String>();

		for (ChoiceNode choice : existingChoices) {
			names.add(choice.getName());
		}

		return names;
	}

	public boolean isAbstract(){

		if (getChoices().size() == 0) {
			return false;
		}

		return true;

	}

	public boolean isMatchIncludingParents(ChoiceNode choice) {

		if (this == choice) {
			return true;
		}

		if (isParameterAndNameMatch(choice)) {
			return true;
		}

		if (getParentChoice() != null) {
			return getParentChoice().isMatchIncludingParents(choice); 
		}

		return false;
	}

	private boolean isParameterAndNameMatch(ChoiceNode choice) {

		BasicParameterNode param = getParameter();
		BasicParameterNode otherParam = choice.getParameter();

		if (param != otherParam) {
			return false;
		}

		String name = getQualifiedName();
		String otherName = choice.getQualifiedName();

		if (!StringHelper.isEqual(name, otherName)) {
			return false;
		}

		return true;
	}

	public int level(){
		if(getParentChoice() == null){
			return 0;
		}
		return getParentChoice().level() + 1;
	}

	@Override
	public boolean isMatch(IAbstractNode choiceNode){

		if(choiceNode instanceof ChoiceNode == false){
			return false;
		}

		ChoiceNode choiceNodeToCompare = (ChoiceNode)choiceNode;

		if (getLabels().equals(choiceNodeToCompare.getLabels()) == false){
			return false;
		}

		if(getValueString().equals(choiceNodeToCompare.getValueString()) == false){
			return false;
		}

		if(getChoices().size() != choiceNodeToCompare.getChoices().size()){
			return false;
		}

		for(int i = 0; i < getChoices().size(); i++){
			if(getChoices().get(i).isMatch(choiceNodeToCompare.getChoices().get(i)) == false){
				return false;
			}
		}

		boolean isMatch = super.isMatch(choiceNode);

		if (!isMatch) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public ChoiceNode getParentChoice() {

		IAbstractNode parent = getParent();

		if (parent == null) {
			return null;
		}

		if (parent instanceof ChoiceNode) {
			return (ChoiceNode)parent;
		}

		return null;
	}

	//	public MethodNode getMethodNode() {
	//
	//		BasicParameterNode methodParameterNode = (BasicParameterNode)getParameter();
	//
	//		if (methodParameterNode == null) {
	//			return null;
	//		}
	//
	//		MethodNode methodNode = methodParameterNode.getMethod();
	//
	//		return methodNode;
	//	}

	@Override
	public int getChildrenCount() {

		return getChoiceCount();
	}

	@Override
	public boolean hasChoices() {

		if (getChoiceCount() == 0) {
			return false;
		}

		return true;
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd) {

		fChoicesListHolder.addChoice(choiceToAdd, this);
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd, int index) {

		fChoicesListHolder.addChoice(choiceToAdd, index, this);
		registerChange();
	}

	@Override
	public void addChoices(List<ChoiceNode> choicesToAdd) {

		fChoicesListHolder.addChoices(choicesToAdd, this);
		registerChange();
	}

	@Override
	public int getChoiceCount() {

		return getChoices().size();
	}

	@Override
	public List<ChoiceNode> getChoices() {

		return fChoicesListHolder.getChoices();
	}

	@Override
	public ChoiceNode getChoice(String qualifiedName) {

		return (ChoiceNode)findChild(qualifiedName);
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

		return ChoiceNodeHelper.getLeafChoices(getChoices());
	}

	@Override
	public List<ChoiceNode> getLeafChoicesWithCopies() {

		return ChoiceNodeHelper.getLeafChoices(getChoices());
	}

	@Override
	public Set<String> getAllChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getAllChoices());
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
	public Set<String> getLeafChoiceValues() {

		return ChoiceNodeHelper.getLeafChoiceValues(getLeafChoices());
	}

	@Override
	public boolean removeChoice(ChoiceNode choice) {

		boolean result = fChoicesListHolder.removeChoice(choice);
		registerChange();
		return result;
	}

	@Override
	public void replaceChoices(List<ChoiceNode> newChoices) {

		fChoicesListHolder.replaceChoices(newChoices, this);
		registerChange();
	}

	@Override
	public void clearChoices() {

		fChoicesListHolder.clearChoices();
	}

	@Override
	public List<ChoiceNode> getChoicesWithCopies() {
		return new ArrayList<>();
	}

	public boolean isPartOfGlobalParameter() {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(this);

		if (methodNode == null) {
			return true;
		}

		return false;
	}

	@Override
	public List<IAbstractNode> getDirectChildren() {
		return getChildren();
	}

	@Override
	public boolean canAddChild(IAbstractNode child) {

		if (child instanceof ChoiceNode) {
			return true;
		}

		return false;
	}

}
