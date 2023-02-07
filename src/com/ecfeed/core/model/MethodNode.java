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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class MethodNode extends AbstractNode implements IParametersAndConstraintsParentNode, ITestCasesParentNode {

	ParametersHolder fParametersHolder;
	ParametersHolder fDeployedParametersHolder;
	private List<TestCaseNode> fTestCaseNodes;
	private List<TestSuiteNode> fTestSuiteNodes;
	private ConstraintNodeListHolder fConstraintNodeListHolder;

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	public MethodNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fParametersHolder = new ParametersHolder(modelChangeRegistrator);
		fDeployedParametersHolder = null;
		fTestCaseNodes = new ArrayList<>();
		fTestSuiteNodes = new ArrayList<>();
		fConstraintNodeListHolder = new ConstraintNodeListHolder(modelChangeRegistrator);

		setDefaultPropertyValues();
	}

	public MethodNode(String name){
		this(name, null);
	}

	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		ClassNode classNode = getClassNode();

		if (classNode != null) {

			MethodNode otherMethodNode = classNode.findMethodWithTheSameName(name);

			if (otherMethodNode != null) {
				ExceptionHelper.reportRuntimeException("Method with the same name already exists.");
			}
		}

		super.setName(name);
	}

	private void setDefaultPropertyValues() {

		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM);

		registerChange();
	}

	@Override
	public ConstraintNodeListHolder.ConstraintsItr getIterator() {
		return fConstraintNodeListHolder.getIterator();
	}

	@Override
	public boolean hasNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		return fConstraintNodeListHolder.hasNextConstraint(contIterator);
	}

	@Override
	public ConstraintNode getNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		return fConstraintNodeListHolder.getNextConstraint(contIterator);
	}

	@Override
	public void removeConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		fConstraintNodeListHolder.removeConstraint(contIterator);
	}	

	public void removeAllDeployedParameters() {

		if (fDeployedParametersHolder != null) {
			fDeployedParametersHolder.removeAllParameters();
		}
	}

	public List<String> getParameterTypes() { // TODO MO-RE move to IParametersParant

		List<String> parameterTypes = new ArrayList<String>();

		List<AbstractParameterNode> parameters = getParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {

			if (abstractParameterNode instanceof BasicParameterNode) {

				BasicParameterNode methodParameterNode = (BasicParameterNode)abstractParameterNode;

				String parameterType = methodParameterNode.getType();
				parameterTypes.add(parameterType);

			} else {

				parameterTypes.add(CompositeParameterNode.COMPOSITE_PARAMETER_TYPE);
			}
		}

		return parameterTypes;
	}

	@Override
	public String toString() {

		return MethodNodeHelper.createSignature(this, true, new ExtLanguageManagerForJava()); 
	}

	@Override
	public List<IAbstractNode> getChildren(){

		List<IAbstractNode> children = new ArrayList<>(super.getChildren());
		children.addAll(fParametersHolder.getParameters());
		children.addAll(fConstraintNodeListHolder.getConstraintNodes());
		children.addAll(fTestCaseNodes);
		children.addAll(fTestSuiteNodes);

		return children;
	}

	@Override
	public boolean hasChildren(){
		return (getParameters().size() != 0 || fConstraintNodeListHolder.getConstraintListSize() != 0 || fTestCaseNodes.size() != 0);
	}

	@Override
	public MethodNode makeClone() {

		MethodNode clonedMethodNode = new MethodNode(getName(), getModelChangeRegistrator());

		clonedMethodNode.setProperties(getProperties());

		for (AbstractParameterNode parameter : getParameters()) {

			if (!(parameter instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;

			clonedMethodNode.addParameter(basicParameterNode.makeClone());
		}

		for (TestCaseNode testcase : fTestCaseNodes) {

			TestCaseNode tcase = testcase.getCopy(clonedMethodNode);

			if (tcase != null) {
				clonedMethodNode.addTestCase(tcase);
			}
		}

		clonedMethodNode.fConstraintNodeListHolder = fConstraintNodeListHolder.makeClone(clonedMethodNode);

		//		for(ConstraintNode constraint : fConstraintNodes){
		//			constraint = constraint.getCopy(copy);
		//			if(constraint != null)
		//				copy.addConstraint(constraint);
		//		}

		clonedMethodNode.setParent(getParent());
		//		if(!copy.isMatch(this))
		//			assert copy.isMatch(this);
		return clonedMethodNode;
	}

	public int getMyMethodIndex() {

		if (getParent() == null) {
			return -1;
		}

		int index = -1;

		for (IAbstractNode abstractNode : getParent().getChildren()) {

			if (abstractNode instanceof MethodNode) {
				index++;
			}

			if (abstractNode.equals(this)) {
				return index;
			}
		}

		return -1;
	}

	public MethodNode getSibling(){

		ClassNode classNode = getClassNode();

		if (classNode == null) 
			return null;

		MethodNode sibling = classNode.findMethodWithTheSameName(getName());

		if (sibling == null || sibling == this) {
			return null;
		}

		return sibling;
	}

	public boolean checkDuplicate() { // TODO MO-RE remove ? no check of duplicates (when unique function name) ??
		return getSibling() != null;
	}

	@Override
	public void addConstraint(ConstraintNode constraint) {

		fConstraintNodeListHolder.addConstraint(constraint, this);
	}

	@Override
	public void addConstraint(ConstraintNode constraint, int index) {

		fConstraintNodeListHolder.addConstraint(constraint, index, this);
	}

	@Override
	public boolean removeConstraint(ConstraintNode constraint) {

		return fConstraintNodeListHolder.removeConstraint(constraint);
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

	//	@Override
	//	public BasicParameterNode findMethodParameter(String name){
	//		return (BasicParameterNode)findParameter(name);
	//	}

	public ArrayList<String> getParametersNames(boolean expected) {

		ArrayList<String> names = new ArrayList<String>();

		for (AbstractParameterNode parameter : getParameters()) {

			if (!(parameter instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;

			if (basicParameterNode.isExpected() == expected) {
				names.add(parameter.getName());
			}
		}
		return names;
	}

	public AbstractParameterNode getMethodParameter(int index) {

		List<AbstractParameterNode> parameters = getParameters();

		return parameters.get(index);
	}

	public int getMethodParameterCount()
	{
		return getParameters().size();
	}

	@Override
	public List<ConstraintNode> getConstraintNodes() {
		return fConstraintNodeListHolder.getConstraintNodes();
	}

	@Override
	public List<Constraint> getConstraints() {

		return fConstraintNodeListHolder.getConstraints();
	}

	@Override
	public void setConstraints(List<ConstraintNode> constraints) {

		fConstraintNodeListHolder.setConstraints(constraints);
	}

	@Override
	public List<Constraint> getConstraints(String name) {

		return fConstraintNodeListHolder.getConstraints(name);
	}

	@Override
	public List<ConstraintNode> getConstraintNodes(String name) {

		return fConstraintNodeListHolder.getConstraintNodes(name);
	}

	@Override
	public Set<String> getNamesOfConstraints() {

		return fConstraintNodeListHolder.getConstraintsNames();
	}

	public List<List<ChoiceNode>> getTestDomain() {

		List<List<ChoiceNode>> testDomain = new ArrayList<>();

		int parameterCount = getParametersCount();

		for (int parameterIndex = 0; parameterIndex < parameterCount; parameterIndex++) {
			testDomain.add(getTestDomainForParameterIndex(parameterIndex));
		}

		return testDomain;
	}

	private List<ChoiceNode> getTestDomainForParameterIndex(int parameterIndex) {

		BasicParameterNode basicParameterNode = (BasicParameterNode) getParameter(parameterIndex);

		return getTestDomainForParameterNode(basicParameterNode);
	}

	private static List<ChoiceNode> getTestDomainForParameterNode(BasicParameterNode basicParameterNode) {

		List<ChoiceNode> choicesForParameter = new ArrayList<>();

		if (basicParameterNode.isExpected()) {
			ChoiceNode choiceNode = ChoiceNodeHelper.createChoiceNodeWithDefaultValue(basicParameterNode);

			choicesForParameter.add(choiceNode);

			return choicesForParameter;
		}

		return basicParameterNode.getLeafChoicesWithCopies();
	}

	@Override
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

	public void removeTestSuite(TestSuiteNode testSuite) {

		String testSuiteName = testSuite.getName();
		fTestCaseNodes.removeIf(e -> testSuiteName.equals(e.getName()));

		fTestSuiteNodes.remove(testSuite);
		registerChange();
	}

	@Override
	public boolean isChoiceMentionedInConstraints(ChoiceNode choice) {

		return fConstraintNodeListHolder.isChoiceMentioned(choice);
	}

	public boolean isChoiceMentioned(ChoiceNode choice){

		if (isChoiceMentionedInConstraints(choice)) {
			return true;
		}

		for(TestCaseNode testCase: fTestCaseNodes){
			if(testCase.mentions(choice)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter) {

		return fConstraintNodeListHolder.getMentioningConstraints(parameter);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, String label) {

		return fConstraintNodeListHolder.getMentioningConstraints(parameter, label);
	}

	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice) {

		return fConstraintNodeListHolder.getMentioningConstraints(choice);
	}

	public Set<ConstraintNode> getMentioningConstraints(Collection<BasicParameterNode> parameters) {

		Set<ConstraintNode> result = new HashSet<>();

		for (BasicParameterNode basicParameterNode : parameters) {
			Set<ConstraintNode> constraintsForOneParameter = getMentioningConstraints(basicParameterNode);

			result.addAll(constraintsForOneParameter);
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

	@Override
	public boolean isParameterMentionedInConstraints(BasicParameterNode parameter) {

		return fConstraintNodeListHolder.isParameterMentioned(parameter);
	}

	public boolean isParameterMentioned(BasicParameterNode parameter) {

		if (isParameterMentionedInConstraints(parameter)) {
			return true;
		}

		if (fTestCaseNodes.isEmpty()) {
			return false;
		}

		return true;
	}

	public void removeAllTestCases() {

		fTestCaseNodes.clear();
		registerChange();
	}

	@Override
	public void replaceTestCases(List<TestCaseNode> testCases){
		fTestCaseNodes.clear();
		fTestCaseNodes.addAll(testCases);
		registerChange();
	}

	@Override
	public void replaceConstraints(List<ConstraintNode> constraints){

		fConstraintNodeListHolder.replaceConstraints(constraints);
	}

	@Override
	public void removeAllConstraints() {

		fConstraintNodeListHolder.removeAllConstraints();
	}

	@Override
	public int getMaxChildIndex(IAbstractNode potentialChild) {

		if (potentialChild instanceof BasicParameterNode) {
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
	public boolean isMatch(IAbstractNode other){

		if(other instanceof MethodNode == false){
			return false;
		}

		MethodNode otherMethodNode = (MethodNode)other;

		if (!fParametersHolder.isMatch(otherMethodNode.fParametersHolder)) {
			return false;
		}

		List<TestCaseNode> testCases = getTestCases();

		int testCasesCount = testCases.size();
		int constraintsCount = getConstraintNodes().size();

		List<TestCaseNode//	public List<MethodNode> getChildMethods(BasicParameterNode parameter) {
		//		return Arrays.asList(new MethodNode[]{this});
		//	}
		> testCasesToCompare = otherMethodNode.getTestCases();

		if(testCasesCount != testCasesToCompare.size() ||
				constraintsCount != otherMethodNode.getConstraintNodes().size()){
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
			if(getConstraintNodes().get(i).isMatch(otherMethodNode.getConstraintNodes().get(i)) == false){
				return false;
			}
		}

		boolean isMatch = super.isMatch(other);

		if (!isMatch) {
			return false;
		}

		return true;
	}

	public List<BasicParameterNode> getLinkers(BasicParameterNode globalParameter) {

		List<BasicParameterNode> result = new ArrayList<BasicParameterNode>();

		for (AbstractParameterNode localParameter : getParameters()) {

			if (!(localParameter instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode) localParameter;

			if (basicParameterNode.isLinked() && basicParameterNode.getLinkToGlobalParameter() == globalParameter) {
				result.add(basicParameterNode);
			}
		}
		return result;
	}

	public final boolean isDeployed() {

		return fDeployedParametersHolder != null && fDeployedParametersHolder.getParametersCount() > 0;
	}

	public final List<BasicParameterNode> getDeployedMethodParameters() {

		if (isDeployed()) {
			return fDeployedParametersHolder.getParametersAsBasic();
		}

		return new ArrayList<>();
	}

	public BasicParameterNode getMethodParameter(ChoiceNode choice) {

		BasicParameterNode parameter = choice.getParameter();

		for (AbstractParameterNode methodParameter : getParameters()) {

			if (!(methodParameter instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode methodBasicParameterNode = (BasicParameterNode) methodParameter;

			if (methodBasicParameterNode == parameter 
					|| methodBasicParameterNode.getLinkToGlobalParameter() == parameter) {

				return methodBasicParameterNode;
			}
		}

		return null;
	}

	public List<BasicParameterNode> getAllGlobalParametersAvailableForLinking() {

		if(getClassNode() != null){
			return getClassNode().getAllGlobalParametersAvailableForLinking();
		}
		return new ArrayList<>();
	}

	@Override
	public void removeMentioningConstraints(BasicParameterNode methodParameter) {

		fConstraintNodeListHolder.removeMentioningConstraints(methodParameter);
	}

	@Override
	public int getChildrenCount() {

		int parametetersSize = fParametersHolder.getParametersCount(); 
		int testCasesSize = fTestCaseNodes.size();
		int constraintsSize = fConstraintNodeListHolder.getConstraintListSize();

		return parametetersSize + testCasesSize + constraintsSize;
	}

	@Override
	public void addParameter(AbstractParameterNode parameter) {

		fParametersHolder.addParameter(parameter, this);
	}

	@Override
	public void addParameter(AbstractParameterNode parameter, int index) {

		fParametersHolder.addParameter(parameter, index, this);
	}

	@Override
	public void addParameters(List<AbstractParameterNode> parameters) {

		fParametersHolder.addParameters(parameters, this);
	}

	public void setDeployedParameters(List<BasicParameterNode> parameters) {

		if (fDeployedParametersHolder == null) {
			fDeployedParametersHolder = new ParametersHolder(getModelChangeRegistrator());
		} 

		fDeployedParametersHolder.setBasicParameters(parameters, this);
	}

	@Override
	public boolean removeParameter(AbstractParameterNode parameter) {

		return fParametersHolder.removeParameter(parameter);
	}

	@Override
	public void replaceParameters(List<AbstractParameterNode> parameters) {

		fParametersHolder.replaceParameters(parameters);
	}

	@Override
	public int getParametersCount() {

		return fParametersHolder.getParametersCount();
	}

	@Override
	public List<AbstractParameterNode> getParameters() {

		return fParametersHolder.getParameters();
	}

	@Override
	public AbstractParameterNode getParameter(int parameterIndex) {

		return fParametersHolder.getParameter(parameterIndex);
	}

	@Override
	public AbstractParameterNode findParameter(String parameterNameToFind) {

		return fParametersHolder.findParameter(parameterNameToFind);
	}

	@Override
	public int getParameterIndex(String parameterName) {

		return fParametersHolder.getParameterIndex(parameterName);
	}

	@Override
	public boolean parameterExists(String parameterName) {

		return fParametersHolder.parameterExists(parameterName);
	}

	@Override
	public boolean parameterExists(BasicParameterNode abstractParameterNode) {

		return fParametersHolder.parameterExists(abstractParameterNode);
	}

	@Override
	public List<String> getParametersNames() {

		return fParametersHolder.getParametersNames();
	}

	@Override
	public String generateNewParameterName(String startParameterName) {

		return fParametersHolder.generateNewParameterName(startParameterName);
	}

	public List<BasicParameterNode> getParametersAsBasic() {

		return fParametersHolder.getParametersAsBasic();
	}

	public void setTestCases(List<TestCaseNode> testCases) {

		fTestCaseNodes.clear();
		fTestCaseNodes.addAll(testCases);
	}

	@Override
	public List<AbstractParameterNode> getNestedAbstractParameters(boolean follow) {
		List<AbstractParameterNode> nodes = new ArrayList<>();
		
		for (AbstractParameterNode node : getParameters()) {

			if (node instanceof BasicParameterNode) {
				nodes.add(node);
			} else if (node instanceof CompositeParameterNode) {
				nodes.add((CompositeParameterNode) node);
				nodes.addAll(((CompositeParameterNode) node).getNestedAbstractParameters(follow));
			}
		}
		
		return nodes;
	}
	
	@Override
	public List<BasicParameterNode> getNestedBasicParameters(boolean follow) {
		List<BasicParameterNode> nodes = new ArrayList<>();

		for (AbstractParameterNode node : getParameters()) {

			if (node instanceof BasicParameterNode) {
				nodes.add((BasicParameterNode) node);
			} else if (node instanceof CompositeParameterNode) {
				nodes.addAll(((CompositeParameterNode) node).getNestedBasicParameters(follow));
			}
		}

		return nodes;
	}

	@Override
	public List<CompositeParameterNode> getNestedCompositeParameters(boolean follow) {
		List<CompositeParameterNode> nodes = new ArrayList<>();

		for (AbstractParameterNode node : getParameters()) {

			if (node instanceof CompositeParameterNode) {
				nodes.addAll(((CompositeParameterNode) node).getNestedCompositeParameters(follow));
			}
		}

		return nodes;
	}

}
