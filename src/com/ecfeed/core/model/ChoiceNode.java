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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class ChoiceNode extends ChoicesParentNode{

	public static final String ABSTRACT_CHOICE_MARKER = "[ABSTRACT]";

	private ChoicesParentNode fParent;
	private String fValueString;
	private Set<String> fLabels;
	private boolean fIsRandomizedValue;

	public ChoiceNode(String name, IModelChangeRegistrator modelChangeRegistrator, String value) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
		fIsRandomizedValue = false;
	}

	public ChoiceNode(String name, IModelChangeRegistrator modelChangeRegistrator, String value, boolean isRandomized) {
		super(name, modelChangeRegistrator);
		fValueString = value;
		fLabels = new LinkedHashSet<String>();
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
	public String toString(){
		if(isAbstract()){
			return getQualifiedName() + ABSTRACT_CHOICE_MARKER;
		}
		return getQualifiedName() + " [" + getValueString() + "]";
	}

	public String toStringWithParenthesis() {

		if(isAbstract()){
			return getQualifiedName() + ABSTRACT_CHOICE_MARKER;
		}

		return getQualifiedName() + "(" + getValueString() + ")";
	}


	@Override
	public ChoiceNode makeClone(){
		ChoiceNode copy = new ChoiceNode(getFullName(), getModelChangeRegistrator(), fValueString);

		copy.setProperties(getProperties());
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

	public String getQualifiedName(){
		if(parentChoice() != null){
			return parentChoice().getQualifiedName() + ":" + getFullName();
		}
		return getFullName();
	}

	public boolean isCorrectableToBeRandomizedType() {
		return fParent.getParameter().isCorrectableToBeRandomizedType() && !isAbstract();
	}

	public void setRandomizedValue(boolean choice) {
		fIsRandomizedValue = choice;
	}

	public boolean isRandomizedValue() {
		return fIsRandomizedValue;
	}

	public String getRandomizedValueStr() {
		return fIsRandomizedValue ? "YES" : "NO";
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
			names.add(choice.getFullName());
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
	public boolean isMatch(AbstractNode node){
		if(node instanceof ChoiceNode == false){
			return false;
		}

		ChoiceNode compared = (ChoiceNode)node;

		if(getLabels().equals(compared.getLabels()) == false){
			return false;
		}

		if(getValueString().equals(compared.getValueString()) == false){
			return false;
		}

		if(getChoices().size() != compared.getChoices().size()){
			return false;
		}

		for(int i = 0; i < getChoices().size(); i++){
			if(getChoices().get(i).isMatch(compared.getChoices().get(i)) == false){
				return false;
			}
		}

		return super.isMatch(node);
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
		if(fParent != null && fParent != getParameter()){
			return (ChoiceNode)fParent;
		}
		return null;
	}
}
