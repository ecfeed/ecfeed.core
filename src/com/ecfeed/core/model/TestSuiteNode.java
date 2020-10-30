package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestSuiteNode extends AbstractNode {
	List<TestCaseNode> fTestCase;
	String fSuiteName;

	public TestSuiteNode(String name, IModelChangeRegistrator modelChangeRegistrator, List<TestCaseNode> testData) {
		super(name, modelChangeRegistrator);
		
		fTestCase = testData;
	}
	
	public TestSuiteNode(String name, IModelChangeRegistrator modelChangeRegistrator, Collection<TestCaseNode> testData) {
		super(name, modelChangeRegistrator);
		
		fTestCase = testData.stream().collect(Collectors.toList());
	}

	public TestSuiteNode(List<TestCaseNode> testData) {
		super("", null);
		
		fTestCase = testData;
	}
	
	public TestSuiteNode(Collection<TestCaseNode> testData) {
		super("", null);
		
		fTestCase = testData.stream().collect(Collectors.toList());
	}
	
	public TestSuiteNode() {
		super("", null);
		
		fTestCase = new ArrayList<>();
	}

	public List<TestCaseNode> getTestCaseNodes() { 
		
		return fTestCase;
	}
	
	@Override
	public boolean hasChildren(){
		
		return(fTestCase.size() != 0);
	}
	
	@Override
	public List<? extends AbstractNode> getChildren() {

		return fTestCase;
	}
	

	public void setSuiteName(String suiteName) {
		fSuiteName = suiteName;
	}
	
	public String getSuiteName() {
		return fSuiteName;
	}
	
	public TestSuiteNode getCopy(MethodNode method){
		TestSuiteNode tcase = makeClone();
		if(tcase.updateReferences(method)){
			tcase.setParent(method);
			return tcase;
		}
		else
			return null;
	}
	
	public boolean updateReferences(MethodNode method) {
		
		for (TestCaseNode testCase : getTestCaseNodes()) {
			testCase.updateReferences(method);
		}
		
		return true;
	}
	
	public MethodNode getMethod() {
		
		return (MethodNode)getParent();
	}
	
	@Override
	public TestSuiteNode makeClone() {
		List<TestCaseNode> testdata = new ArrayList<>();
		
		for(TestCaseNode choice : fTestCase) {
			testdata.add(choice);
		}
		
		TestSuiteNode copy = new TestSuiteNode(this.getName(), getModelChangeRegistrator(), testdata);
		copy.setProperties(getProperties());
		return copy;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean isTheSameExtLanguageAndIntrLanguage() {
		return true;
	}

}
