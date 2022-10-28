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
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;


public class TestCaseNode extends AbstractNode {
	List<ChoiceNode> fTestData;

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public void verifyName(String nameInIntrLanguage) {
	}

	@Override
	public int getMyIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getTestCases().indexOf(this);
	}

	@Override
	public String toString() {

		return TestCaseNodeHelper.createSignature(this, true, new ExtLanguageManagerForJava());
	}

	@Override
	public int getChildrenCount() {
		return 0;
	}

	@Override
	public TestCaseNode makeClone(){
		List<ChoiceNode> testdata = new ArrayList<>();
		for(ChoiceNode choice : fTestData){
			testdata.add(choice);
		}
		TestCaseNode copy = new TestCaseNode(this.getName(), getModelChangeRegistrator(), testdata);
		copy.setProperties(getProperties());
		return copy;
	}

	public TestCaseNode(String name, IModelChangeRegistrator modelChangeRegistrator, List<ChoiceNode> testData) {

		super(name, modelChangeRegistrator);
		fTestData = testData;
	}

	public TestCaseNode(List<ChoiceNode> testData) {

		super("", null);
		fTestData = testData;
	}

	public MethodNode getMethod() {

		return (MethodNode)getParent();
	}

	public TestSuiteNode getTestSuite() {

		Optional<TestSuiteNode> testSuite = getMethod().getTestSuites().stream().filter(e -> e.getTestCaseNodes().contains(this)).findAny(); 

		if (testSuite.isPresent()) {
			return testSuite.get();
		}

		ExceptionHelper.reportRuntimeException("The selected TestCaseNode does not belong to any test suite");
		return null;
	}

	public BasicParameterNode getMethodParameter(ChoiceNode choice){
		if(getTestData().contains(choice)){
			int index = getTestData().indexOf(choice);
			return getMethod().getMethodParameters().get(index);
		}
		return null;
	}

	public List<ChoiceNode> getTestData(){
		return fTestData;
	}

	public void replaceValue(int index, ChoiceNode newValue) {
		fTestData.set(index, newValue);
	}

	public boolean mentions(ChoiceNode choice) {
		for(ChoiceNode p : fTestData){
			if(p.isMatchIncludingParents(choice)){
				return true;
			}
		}
		return false;
	}

	public static boolean validateTestSuiteName(String newName) {

		if (newName.length() < 1 || newName.length() > 64) {
			return false;
		}

		if(newName.matches("[ ]+.*")) { 
			return false;
		}

		return true;
	}

	public TestCaseNode getCopy(MethodNode method){
		TestCaseNode tcase = makeClone();
		if(tcase.updateReferences(method)){
			tcase.setParent(method);
			return tcase;
		}
		else
			return null;
	}

	public boolean updateReferences(MethodNode method){
		List<BasicParameterNode> parameters = method.getMethodParameters();
		if(parameters.size() != getTestData().size())
			return false;

		for(int i = 0; i < parameters.size(); i++){
			BasicParameterNode parameter = parameters.get(i);
			if(parameter.isExpected()){
				String name = getTestData().get(i).getName();
				String value = getTestData().get(i).getValueString();
				ChoiceNode newChoice = new ChoiceNode(name, value, parameter.getModelChangeRegistrator());
				newChoice.setParent(parameter);
				getTestData().set(i, newChoice);
			} else{
				ChoiceNode original = getTestData().get(i);
				ChoiceNode newReference = parameter.getChoice(original.getQualifiedName());
				if(newReference == null){
					return false;
				}
				getTestData().set(i, newReference);
			}
		}
		return true;
	}

	@Override
	public boolean isMatch(IAbstractNode testCaseNode){

		if(testCaseNode instanceof TestCaseNode == false){
			return false;
		}

		TestCaseNode testCaseNodeToCompare = (TestCaseNode)testCaseNode;

		List<ChoiceNode> testData = getTestData();

		List<ChoiceNode> testDataToCompare = testCaseNodeToCompare.getTestData();
		if(testData.size() != testDataToCompare.size()){
			return false;
		}

		for (int i = 0; i < testData.size(); i++) {

			ChoiceNode choiceNode = testData.get(i);
			ChoiceNode choiceNodeToCompare = testDataToCompare.get(i);

			if (choiceNode.isMatch(choiceNodeToCompare) == false){
				return false;
			}
		}

		boolean isMatch = super.isMatch(testCaseNode);

		if (!isMatch) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public boolean isConsistent() {
		for(ChoiceNode choice : getTestData()){
			BasicParameterNode parameter = getMethodParameter(choice);
			if(parameter == null || (parameter.isExpected() == false && parameter.getChoice(choice.getQualifiedName()) == null)){
				return false;
			}
			if(choice.isAbstract()){
				return false;
			}
		}
		return true;
	}

	@Override
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getTestCases().size();
		}
		return -1;
	}

	public TestCase getTestCase() {
		return new TestCase(fTestData);
	}

	public void updateChoiceReferences(
			ChoiceNode oldChoiceNode, ChoiceNode newChoiceNode) {

		int index = 0;

		for (ChoiceNode choiceNode : fTestData) {

			if (choiceNode.equals(oldChoiceNode)) {
				fTestData.set(index, newChoiceNode);
			}

			index++;
		}
	}

}
