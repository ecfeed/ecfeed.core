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
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.JavaLanguageHelper;


public class MethodNode extends ParametersParentNode {

	private List<TestCaseNode> fTestCaseNodes;
	private List<TestSuiteNode> fTestSuiteNodes;
	private List<ConstraintNode> fConstraintNodes;

	@Override
	protected String getNonQualifiedName() {
		return getName();
	}

	public MethodNode(String name, IModelChangeRegistrator modelChangeRegistrator){
		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fTestCaseNodes = new ArrayList<>();
		fTestSuiteNodes = new ArrayList<>();
		fConstraintNodes = new ArrayList<>();

		setDefaultPropertyValues();
	}

	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
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
		return new ConstraintsItrImpl(fConstraintNodes.iterator());
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

	public List<String> getParameterTypes() {

		List<String> parameterTypes = new ArrayList<String>();

		List<AbstractParameterNode> parameters = getParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {

			String parameterType = abstractParameterNode.getType();
			parameterTypes.add(parameterType);
		}

		return parameterTypes;
	}

	@Override
	public String toString() {

		return MethodNodeHelper.createSignature(this, true, new ExtLanguageManagerForJava()); 
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		List<AbstractNode> children = new ArrayList<AbstractNode>(super.getChildren());
		children.addAll(fConstraintNodes);
		children.addAll(fTestCaseNodes);
		children.addAll(fTestSuiteNodes);

		return children;
	}

	@Override
	public boolean hasChildren(){
		return(getParameters().size() != 0 || fConstraintNodes.size() != 0 || fTestCaseNodes.size() != 0);
	}

	@Override
	public MethodNode makeClone(){
		MethodNode copy = new MethodNode(getName(), getModelChangeRegistrator());

		copy.setProperties(getProperties());

		for(MethodParameterNode parameter : getMethodParameters()){
			copy.addParameter(parameter.makeClone());
		}

		for(TestCaseNode testcase : fTestCaseNodes){
			TestCaseNode tcase = testcase.getCopy(copy);
			if(tcase != null)
				copy.addTestCase(tcase);
		}

		for(ConstraintNode constraint : fConstraintNodes){
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

		ClassNode classNode = getClassNode();

		if (classNode == null) 
			return null;

		MethodNode sibling = classNode.findMethodWithTheSameSignature(getName(), argTypes);

		if (sibling == null || sibling == this) {
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
		addConstraint(constraint, fConstraintNodes.size());
	}

	public void addConstraint(ConstraintNode constraint, int index) {
		constraint.setParent(this);
		fConstraintNodes.add(index, constraint);
		registerChange();
	}

	public void addTestSuite(TestSuiteNode testSuite) {
		addTestSuite(testSuite, fTestSuiteNodes.size());
	}

	public void addTestSuite(TestSuiteNode testCase, int index) {
		fTestSuiteNodes.add(index, testCase);
		registerChange();
	}

	public void addTestCase(TestCaseNode testCase){
		addTestCase(testCase, fTestCaseNodes.size());
	}

	public void addTestCase(TestCaseNode testCase, int index) {
		fTestCaseNodes.add(index, testCase);
		testCase.setParent(this);
		registerChange();
	}

	public ClassNode getClassNode() {
		return (ClassNode)getParent();
	}

	public MethodParameterNode findMethodParameter(String name){
		return (MethodParameterNode)findParameter(name);
	}

	public ArrayList<String> getParametersNames(boolean expected) {
		ArrayList<String> names = new ArrayList<String>();
		for(MethodParameterNode parameter : getMethodParameters()){
			if(parameter.isExpected() == expected){
				names.add(parameter.getName());
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
		return fConstraintNodes;
	}

	public List<Constraint> getAllConstraints(){
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(ConstraintNode node : fConstraintNodes){
			constraints.add(node.getConstraint());
		}
		return constraints;
	}

	public List<Constraint> getConstraints(String name) {

		List<Constraint> constraints = new ArrayList<Constraint>();

		for(ConstraintNode node : fConstraintNodes){
			if(node.getName().equals(name)){
				constraints.add(node.getConstraint());
			}
		}

		return constraints;
	}

	public List<ConstraintNode> getConstraintNodes(String name) {

		List<ConstraintNode> constraintNodes = new ArrayList<ConstraintNode>();

		for(ConstraintNode constraintNode : fConstraintNodes){
			if(constraintNode.getName().equals(name)){
				constraintNodes.add(constraintNode);
			}
		}

		return constraintNodes;
	}

	public Set<String> getConstraintsNames() {
		Set<String> names = new HashSet<String>();
		for(ConstraintNode constraint : fConstraintNodes){
			names.add(constraint.getName());
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

		ChoiceNode choiceNode = new ChoiceNode(ChoiceNode.ASSIGNMENT_NAME, defaultValue, methodParameterNode.getModelChangeRegistrator());
		choiceNode.setParent(methodParameterNode);

		return choiceNode;
	}

	public List<TestCaseNode> getTestCases() {

		return fTestCaseNodes;
	}

	public List<TestSuiteNode> getTestSuites() {

		return fTestSuiteNodes;
	}

	public Optional<TestSuiteNode> getTestSuite(String testSuiteName) {

		for (TestSuiteNode testSuite : fTestSuiteNodes) {
			if (testSuite.getSuiteName().equalsIgnoreCase(testSuiteName)) {
				return Optional.of(testSuite);
			}
		}

		return Optional.empty();
	}

	public boolean hasParameters() {
		if (getParameters().isEmpty()) {
			return false; 
		}
		return true;
	}

	public boolean hasTestSuites() {
		if (fTestSuiteNodes.isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean hasTestCases() {
		if (fTestCaseNodes.isEmpty()) {
			return false;
		}
		return true;
	}

	public Collection<TestCaseNode> getTestCases(String testSuiteName) {
		ArrayList<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : getTestCases()){
			if(testSuiteName.equals(testCase.getName())){
				testCases.add(testCase);
			}
		}
		return testCases;
	}

	public Set<String> getTestCaseNames() {

		Set<String> names = new HashSet<String>();

		for(TestCaseNode testCase : getTestCases()){
			names.add(testCase.getName());
		}

		return names;
	}

	public boolean removeTestCase(TestCaseNode testCase) {
		testCase.setParent(null);
		boolean result = fTestCaseNodes.remove(testCase);
		registerChange();
		return result;
	}

	public void removeTestCases(){
		fTestCaseNodes.clear();
		registerChange();
	}

	public boolean removeConstraint(ConstraintNode constraint) {

		constraint.setParent(null);
		boolean result = fConstraintNodes.remove(constraint);
		registerChange();

		return result;
	}

	public void removeTestSuite(TestSuiteNode testSuite) {

		String testSuiteName = testSuite.getName();
		fTestCaseNodes.removeIf(e -> testSuiteName.equals(e.getName()));

		fTestSuiteNodes.remove(testSuite);
		registerChange();
	}

	public boolean isChoiceMentioned(ChoiceNode choice){
		for(ConstraintNode constraint : fConstraintNodes){
			if(constraint.mentions(choice)){
				return true;
			}
		}
		for(TestCaseNode testCase: fTestCaseNodes){
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

		for(ConstraintNode constraint : fConstraintNodes){
			if(constraint.mentions(parameter)){
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(MethodParameterNode parameter, String label) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(ConstraintNode constraint : fConstraintNodes){
			if(constraint.mentions(parameter, label)){
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice){

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(ConstraintNode constraint : fConstraintNodes){
			if(constraint.mentions(choice)){
				result.add(constraint);
			}
		}

		return result;
	}

	public List<TestCaseNode> getMentioningTestCases(ChoiceNode choice){
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		for(TestCaseNode testCase : fTestCaseNodes){
			if(testCase.getTestData().contains(choice)){
				result.add(testCase);
			}
		}
		return result;
	}

	public boolean isParameterMentioned(MethodParameterNode parameter){
		for(ConstraintNode constraint : fConstraintNodes){
			if(constraint.mentions(parameter)){
				return true;
			}
		}
		if(fTestCaseNodes.isEmpty()){
			return false;
		}
		return true;
	}

	public void removeAllTestCases() {

		fTestCaseNodes.clear();
		registerChange();
	}

	public void replaceTestCases(List<TestCaseNode> testCases){
		fTestCaseNodes.clear();
		fTestCaseNodes.addAll(testCases);
		registerChange();
	}

	public void replaceConstraints(List<ConstraintNode> constraints){
		fConstraintNodes.clear();
		fConstraintNodes.addAll(constraints);
		registerChange();
	}

	public void removeAllConstraints() {

		fConstraintNodes.clear();
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

		MethodNode methodToCompare = (MethodNode)node;

		List<TestCaseNode> testCases = getTestCases();

		int testCasesCount = testCases.size();
		int constraintsCount = getConstraintNodes().size();

		List<TestCaseNode> testCasesToCompare = methodToCompare.getTestCases();

		if(testCasesCount != testCasesToCompare.size() ||
				constraintsCount != methodToCompare.getConstraintNodes().size()){
			return false;
		}

		for (int i = 0; i < testCasesCount; i++){

			TestCaseNode testCase = testCases.get(i);
			TestCaseNode testCaseToCompare = testCasesToCompare.get(i);

			if (testCase.isMatch(testCaseToCompare) == false){
				return false;
			}
		}

		for(int i = 0; i < constraintsCount; i++){
			if(getConstraintNodes().get(i).isMatch(methodToCompare.getConstraintNodes().get(i)) == false){
				return false;
			}
		}

		boolean isMatch = super.isMatch(node);

		if (!isMatch) {
			return false;
		}

		return true;
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

		for(ConstraintNode constraint : fConstraintNodes){
			if (constraint.mentionsParameter(methodParameter)) {
				constraintsToDelete.add(constraint);
			}
		}

		for (ConstraintNode constraint : constraintsToDelete) {
			fConstraintNodes.remove(constraint);
		}

		registerChange();
	}

	public void updateChoiceReferencesInTestCases(ChoiceNode oldChoiceNode, ChoiceNode newChoiceNode) {

		for (TestCaseNode testCaseNode : fTestCaseNodes)  {
			testCaseNode.updateChoiceReferences(oldChoiceNode, newChoiceNode);
		}
	}

	public void updateChoiceReferencesInConstraints(ChoiceNode oldChoiceNode, ChoiceNode newChoiceNode) {

		for (ConstraintNode constraintNode : fConstraintNodes) {
			constraintNode.updateChoiceReferences(oldChoiceNode, newChoiceNode);
		}
	}

}
