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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MethodNode extends ParametersParentNode {

	private List<TestCaseNode> fTestCases;
	private List<ConstraintNode> fConstraints;

	public MethodNode(String name, IModelChangeRegistrator modelChangeRegistrator){
		super(name, modelChangeRegistrator);

		fTestCases = new ArrayList<>();
		fConstraints = new ArrayList<>();

		setDefaultPropertyValues();
	}

	private void setDefaultPropertyValues() {

		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM);

		registerChange();
	}

	public static interface ConstraintsItr {

	}

	private static class ConstraintsItrImpl implements ConstraintsItr {

		Iterator<ConstraintNode> fIterator;

		ConstraintsItrImpl(Iterator<ConstraintNode> iterator) {
			fIterator = iterator;
		}

	}

	public ConstraintsItr getIterator() {
		return new ConstraintsItrImpl(fConstraints.iterator());
	}

	public boolean hasNextConstraint(ConstraintsItr contIterator) {

		return ((ConstraintsItrImpl)contIterator).fIterator.hasNext();
	}

	public ConstraintNode getNextConstraint(ConstraintsItr contIterator) {

		return ((ConstraintsItrImpl)contIterator).fIterator.next();
	}

	public void removeConstraint(ConstraintsItr contIterator) {

		((ConstraintsItrImpl)contIterator).fIterator.remove();
		registerChange();
	}	

	@Override
	public String toString(){
		String result = new String(getFullName()) + "(";
		List<String> types = getParameterTypes();
		List<String> names = getParametersNames();
		for(int i = 0; i < types.size(); i++){
			if(getMethodParameters().get(i).isExpected()){
				result += "[e]";
			}
			result += types.get(i);
			result += " ";
			result += names.get(i);
			if(i < types.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}

	public String getShortSignature() {

		List<String> types = getParameterTypes();
		List<String> names = getParametersNames();

		String result = new String(getFullName()) + "(";

		for (int i = 0; i < types.size(); i++) {

			result += types.get(i);
			result += " ";
			result += names.get(i);

			if (i < types.size() - 1) {
				result += ", ";
			}
		}

		result += ")";

		return result;
	}

	public String getLongSignature() {

		return getParent().getFullName() + "." + getShortSignature();
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		List<AbstractNode> children = new ArrayList<AbstractNode>(super.getChildren());
		children.addAll(fConstraints);
		children.addAll(fTestCases);

		return children;
	}

	@Override
	public boolean hasChildren(){
		return(getParameters().size() != 0 || fConstraints.size() != 0 || fTestCases.size() != 0);
	}

	@Override
	public MethodNode makeClone(){
		MethodNode copy = new MethodNode(getFullName(), getModelChangeRegistrator());

		copy.setProperties(getProperties());

		for(MethodParameterNode parameter : getMethodParameters()){
			copy.addParameter(parameter.makeClone());
		}

		for(TestCaseNode testcase : fTestCases){
			TestCaseNode tcase = testcase.getCopy(copy);
			if(tcase != null)
				copy.addTestCase(tcase);
		}

		for(ConstraintNode constraint : fConstraints){
			constraint = constraint.getCopy(copy);
			if(constraint != null)
				copy.addConstraint(constraint);
		}

		copy.setParent(getParent());
//		if(!copy.isMatch(this))
//			assert copy.isMatch(this);
		return copy;
	}

	public int getMyMethodIndex() {

		if (getParent() == null) {
			return -1;
		}

		int index = -1;

		for (AbstractNode abstractNode : getParent().getChildren()) {

			if (abstractNode instanceof MethodNode) {
				index++;
			}

			if (abstractNode.equals(this)) {
				return index;
			}
		}

		return -1;
	}

	public MethodNode getSibling(List<String> argTypes){
		ClassNode parent = getClassNode();
		if(parent == null) return null;
		MethodNode sibling = parent.getMethod(getFullName(), argTypes);
		if(sibling == null || sibling == this){
			return null;
		}
		return sibling;
	}

	public boolean checkDuplicate(int index, String newType){
		List<String> argTypes = getParameterTypes();
		argTypes.set(index, newType);
		return getSibling(argTypes) != null;
	}

	public void addConstraint(ConstraintNode constraint) {
		addConstraint(constraint, fConstraints.size());
	}

	public void addConstraint(ConstraintNode constraint, int index) {
		constraint.setParent(this);
		fConstraints.add(index, constraint);
		registerChange();
	}

	public void addTestCase(TestCaseNode testCase){
		addTestCase(testCase, fTestCases.size());
	}

	public void addTestCase(TestCaseNode testCase, int index){
		fTestCases.add(index, testCase);
		testCase.setParent(this);
		registerChange();
	}

	public ClassNode getClassNode() {
		return (ClassNode)getParent();
	}

	public boolean getRunOnAndroid() {
		ClassNode classNode = getClassNode();
		return classNode.getRunOnAndroid();
	}

	public MethodParameterNode getMethodParameter(String name){
		return (MethodParameterNode)getParameter(name);
	}

	public ArrayList<String> getParametersNames(boolean expected) {
		ArrayList<String> names = new ArrayList<String>();
		for(MethodParameterNode parameter : getMethodParameters()){
			if(parameter.isExpected() == expected){
				names.add(parameter.getFullName());
			}
		}
		return names;
	}

	public MethodParameterNode getMethodParameter(int index) {
		return (MethodParameterNode)(getParameters().get(index));
	}

	public int getMethodParameterCount()
	{
		return getParameters().size();
	}

	public List<ConstraintNode> getConstraintNodes(){
		return fConstraints;
	}

	public List<Constraint> getAllConstraints(){
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(ConstraintNode node : fConstraints){
			constraints.add(node.getConstraint());
		}
		return constraints;
	}

	public List<Constraint> getConstraints(String name) {
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(ConstraintNode node : fConstraints){
			if(node.getFullName().equals(name)){
				constraints.add(node.getConstraint());
			}
		}
		return constraints;
	}

	public Set<String> getConstraintsNames() {
		Set<String> names = new HashSet<String>();
		for(ConstraintNode constraint : fConstraints){
			names.add(constraint.getFullName());
		}
		return names;
	}

	public List<List<ChoiceNode>> getTestDomain() {

		List<List<ChoiceNode>> testDomain = new ArrayList<>();

		int parameterCount = getParametersCount();

		for (int parameterIndex = 0; parameterIndex < parameterCount; parameterIndex++) {
			testDomain.add(getTestDomainProcessAbstractParameterNode(parameterIndex));
		}

		return testDomain;
	}

	private List<ChoiceNode> getTestDomainProcessAbstractParameterNode(int parameterIndex) {

		AbstractParameterNode abstractParameterNode = getParameter(parameterIndex);

		if (abstractParameterNode instanceof MethodParameterNode) {
			return getTestDomainProcessMethodParameterNode(abstractParameterNode);
		} else {
			return abstractParameterNode.getLeafChoicesWithCopies();
		}

	}

	private List<ChoiceNode> getTestDomainProcessMethodParameterNode(AbstractParameterNode abstractParameterNode) {
		MethodParameterNode methodParameterNode = (MethodParameterNode) abstractParameterNode;
		List<ChoiceNode> choicesForParameter;

		if (methodParameterNode.isExpected()) {
			ChoiceNode choiceNode = getTestDomainCreateExpectedChoiceNode(methodParameterNode);

			choicesForParameter = new ArrayList<>();
			choicesForParameter.add(choiceNode);
		} else {
			choicesForParameter = abstractParameterNode.getLeafChoicesWithCopies();
		}

		return choicesForParameter;
	}

	private ChoiceNode getTestDomainCreateExpectedChoiceNode(MethodParameterNode methodParameterNode) {
		String defaultValue = methodParameterNode.getDefaultValue();

		ChoiceNode choiceNode = new ChoiceNode("@expected", methodParameterNode.getModelChangeRegistrator(), defaultValue);
		choiceNode.setParent(methodParameterNode);

		return choiceNode;
	}

	public List<TestCaseNode> getTestCases(){
		return fTestCases;
	}

	public boolean hasParameters() {
		if (getParameters().isEmpty()) {
			return false; 
		}
		return true;
	}

	public boolean hasTestCases() {
		if (fTestCases.isEmpty()) {
			return false;
		}
		return true;
	}

	public Collection<TestCaseNode> getTestCases(String testSuite) {
		ArrayList<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : getTestCases()){
			if(testSuite.equals(testCase.getFullName())){
				testCases.add(testCase);
			}
		}
		return testCases;
	}

	public Set<String> getTestCaseNames() {

		Set<String> names = new HashSet<String>();

		for(TestCaseNode testCase : getTestCases()){
			names.add(testCase.getFullName());
		}

		return names;
	}

	public boolean removeTestCase(TestCaseNode testCase){
		testCase.setParent(null);
		boolean result = fTestCases.remove(testCase);
		registerChange();
		return result;
	}

	public void removeTestCases(){
		fTestCases.clear();
		registerChange();
	}

	public boolean removeConstraint(ConstraintNode constraint) {

		constraint.setParent(null);
		boolean result = fConstraints.remove(constraint);
		registerChange();

		return result;
	}

	public void removeTestSuite(String suiteName) {
		Iterator<TestCaseNode> iterator = getTestCases().iterator();
		while(iterator.hasNext()){
			TestCaseNode testCase = iterator.next();
			if(testCase.getFullName().equals(suiteName)){
				iterator.remove();
			}
		}

		registerChange();
	}

	public boolean isChoiceMentioned(ChoiceNode choice){
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(choice)){
				return true;
			}
		}
		for(TestCaseNode testCase: fTestCases){
			if(testCase.mentions(choice)){
				return true;
			}
		}
		return false;
	}

	public Set<ConstraintNode> getMentioningConstraints(Collection<MethodParameterNode> parameters) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(MethodParameterNode parameter : parameters){
			result.addAll(getMentioningConstraints(parameter));
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(MethodParameterNode parameter) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter)){
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(MethodParameterNode parameter, String label) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter, label)){
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice){

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(choice)){
				result.add(constraint);
			}
		}

		return result;
	}

	public List<TestCaseNode> getMentioningTestCases(ChoiceNode choice){
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : fTestCases){
			if(testCase.getTestData().contains(choice)){
				result.add(testCase);
			}
		}
		return result;
	}

	public boolean isParameterMentioned(MethodParameterNode parameter){
		for(ConstraintNode constraint : fConstraints){
			if(constraint.mentions(parameter)){
				return true;
			}
		}
		if(fTestCases.isEmpty()){
			return false;
		}
		return true;
	}

	public void removeAllTestCases() {

		fTestCases.clear();
		registerChange();
	}

	public void replaceTestCases(List<TestCaseNode> testCases){
		fTestCases.clear();
		fTestCases.addAll(testCases);
		registerChange();
	}

	public void replaceConstraints(List<ConstraintNode> constraints){
		fConstraints.clear();
		fConstraints.addAll(constraints);
		registerChange();
	}

	public void removeAllConstraints() {

		fConstraints.clear();
		registerChange();
	}

	@Override
	public int getMaxChildIndex(AbstractNode potentialChild) {

		if (potentialChild instanceof AbstractParameterNode) { 
			return getParameters().size();
		}

		if (potentialChild instanceof ConstraintNode) {
			return getConstraintNodes().size();
		}

		if (potentialChild instanceof TestCaseNode) { 
			return getTestCases().size();
		}

		return super.getMaxChildIndex(potentialChild);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	@Override
	public boolean isMatch(AbstractNode node){
		if(node instanceof MethodNode == false){
			return false;
		}

		MethodNode comparedMethod = (MethodNode)node;

		int testCasesCount = getTestCases().size();
		int constraintsCount = getConstraintNodes().size();

		if(testCasesCount != comparedMethod.getTestCases().size() ||
				constraintsCount != comparedMethod.getConstraintNodes().size()){
			return false;
		}

		for(int i = 0; i < testCasesCount; i++){
			if(getTestCases().get(i).isMatch(comparedMethod.getTestCases().get(i)) == false){
				return false;
			}
		}

		for(int i = 0; i < constraintsCount; i++){
			if(getConstraintNodes().get(i).isMatch(comparedMethod.getConstraintNodes().get(i)) == false){
				return false;
			}
		}

		return super.isMatch(node);
	}

	@Override
	public List<MethodNode> getMethods(AbstractParameterNode parameter) {
		return Arrays.asList(new MethodNode[]{this});
	}

	public List<MethodParameterNode> getLinkers(GlobalParameterNode globalParameter){
		List<MethodParameterNode> result = new ArrayList<MethodParameterNode>();
		for(MethodParameterNode localParameter : getMethodParameters()){
			if(localParameter.isLinked() && localParameter.getLink() == globalParameter){
				result.add(localParameter);
			}
		}
		return result;
	}

	public final List<MethodParameterNode> getMethodParameters() {
		List<MethodParameterNode> result = new ArrayList<>();
		for(AbstractParameterNode parameter : getParameters()){
			result.add((MethodParameterNode)parameter);
		}
		return result;
	}

	public List<MethodParameterNode> getMethodParameters(boolean expected) {
		List<MethodParameterNode> result = new ArrayList<>();
		for(MethodParameterNode parameter : getMethodParameters()){
			if(parameter.isExpected()){
				result.add(parameter);
			}
		}
		return result;
	}

	public MethodParameterNode getMethodParameter(ChoiceNode choice){
		AbstractParameterNode parameter = choice.getParameter();
		for(MethodParameterNode methodParameter : getMethodParameters()){
			if(methodParameter == parameter || methodParameter.getLink() == parameter){
				return methodParameter;
			}
		}
		return null;
	}

	public List<GlobalParameterNode> getAvailableGlobalParameters() {
		if(getClassNode() != null){
			return getClassNode().getAvailableGlobalParameters();
		}
		return new ArrayList<>();
	}

	public void removeConstraintsWithParameter(MethodParameterNode methodParameter) {

		ArrayList<ConstraintNode> constraintsToDelete = new ArrayList<ConstraintNode>();  

		for(ConstraintNode constraint : fConstraints){
			if (constraint.mentionsParameter(methodParameter)) {
				constraintsToDelete.add(constraint);
			}
		}

		for (ConstraintNode constraint : constraintsToDelete) {
			fConstraints.remove(constraint);
		}

		registerChange();
	}
}
