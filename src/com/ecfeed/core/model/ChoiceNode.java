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
import java.util.Set;

import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class ChoiceNode extends ChoicesParentNode {

	public static final String ABSTRACT_CHOICE_MARKER = "[ABSTRACT]";
	public static final String ASSIGNMENT_NAME = "@assignment";

	private ChoicesParentNode fParent;
	private String fValueString;
	private Set<String> fLabels;
	private boolean fIsRandomizedValue;
	private boolean fIsDetached;

	private ChoiceNode fOrigChoiceNode = null;

	public ChoiceNode(String name, String value, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
		fIsRandomizedValue = false;
		fIsDetached = false;
	}

	public ChoiceNode(String name, String value, boolean isRandomized, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<>();
		fIsRandomizedValue = isRandomized;
	}

	@Override
	public AbstractParameterNode getParameter() {
		if(fParent != null){
			return fParent.getParameter();
		}
		return null;
	}

	@Override
	public ChoicesParentNode getParent(){
		return fParent;
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		return getChoices();
	}

	@Override
	protected String getNonQualifiedName() {
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

		AbstractParameterNode parameter = getParameter();

		if (parameter == null) {
			ExceptionHelper.reportRuntimeException("Method parameter unknown.");
		}
		String typeName = parameter.getType();

		if (!JavaLanguageHelper.isJavaType(typeName)) {
			return getValueString();
		}

		TypeAdapterProviderForJava typeAdapterProvider = new TypeAdapterProviderForJava();
		ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(typeName);

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

	@Override
	public ChoiceNode makeClone(){
		ChoiceNode copy = makeCloneUnlink();
		if(isClone())
			copy.setOrigChoiceNode(getOrigChoiceNode());
		else
			copy.setOrigChoiceNode(this);
		return copy;
	}

	public ChoiceNode makeCloneUnlink() {
		ChoiceNode copy = new ChoiceNode(getName(), fValueString, getModelChangeRegistrator());

		copy.setProperties(getProperties());
		copy.setDetached(isDetached());
		copy.setParent(fParent);

		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.makeClone());
		}
		for(String label : fLabels){
			copy.addLabel(label);
		}

		copy.setRandomizedValue(fIsRandomizedValue);
		return copy;
	}

	public ChoiceNode getQualifiedCopy(MethodParameterNode parameter) {
		return ChoiceNodeHelper.createSubstitutePath(this, parameter);
	}

	public String getQualifiedName() {

		return getQualifiedName(":");
	}
	
	public String getQualifiedName(String separatorForChoiceNames) {

		if (parentChoice() != null) {
			return parentChoice().getQualifiedName(separatorForChoiceNames) + separatorForChoiceNames + getName();
		}

		return getName();
	}

	public boolean isCorrectableToBeRandomizedType() {
		return fParent.getParameter().isCorrectableToBeRandomizedType() && !isAbstract();
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

	public void setParent(ChoicesParentNode parent){
		super.setParent(parent);
		fParent = parent;
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

	public Set<String> getLabels(){
		return fLabels;
	}

	@Override
	public Set<String> getLeafLabels() {
		if(isAbstract() == false){
			return getAllLabels();
		}
		return super.getLeafLabels();
	}

	public Set<String> getAllLabels(){
		Set<String> allLabels = getInheritedLabels();
		allLabels.addAll(fLabels);
		return allLabels;
	}

	public Set<String> getInheritedLabels() {
		if(parentChoice() != null){
			return parentChoice().getAllLabels();
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

		if (parentChoice() != null) {
			return parentChoice().isMatchIncludingParents(choice); 
		}

		return false;
	}

	private boolean isParameterAndNameMatch(ChoiceNode choice) {

		AbstractParameterNode param = getParameter();
		AbstractParameterNode otherParam = choice.getParameter();

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
		if(parentChoice() == null){
			return 0;
		}
		return parentChoice().level() + 1;
	}

	@Override
	public boolean isMatch(AbstractNode choiceNode){

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

	private ChoiceNode parentChoice(){
		AbstractParameterNode parameter = getParameter();
		if(fParent != null && fParent != parameter){
			return (ChoiceNode)fParent;
		}
		return null;
	}

	public boolean isDetached() {
		return fIsDetached;
	}

	public void setDetached(boolean isDetached) {
		fIsDetached = isDetached;
	}

	public MethodNode getMethodNode() {

		MethodParameterNode methodParameterNode = (MethodParameterNode)getParameter();

		if (methodParameterNode == null) {
			return null;
		}

		MethodNode methodNode = methodParameterNode.getMethod();

		return methodNode;
	}

}
