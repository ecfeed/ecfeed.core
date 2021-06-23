package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestSuiteNode extends AbstractNode {
	List<TestCaseNode> fTestCaseNodes;
	String fSuiteName;
	boolean fDisplayLimitExceeded;

	public TestSuiteNode(String name, IModelChangeRegistrator modelChangeRegistrator, List<TestCaseNode> testData) {
		super(name, modelChangeRegistrator);

		fTestCaseNodes = testData;
	}

	public TestSuiteNode(String name, IModelChangeRegistrator modelChangeRegistrator, Collection<TestCaseNode> testData) {
		super(name, modelChangeRegistrator);

		fTestCaseNodes = testData.stream().collect(Collectors.toList());
	}

	public TestSuiteNode(List<TestCaseNode> testData) {
		super("", null);

		fTestCaseNodes = testData;
	}

	public TestSuiteNode(Collection<TestCaseNode> testData) {
		super("", null);

		fTestCaseNodes = testData.stream().collect(Collectors.toList());
	}

	public TestSuiteNode() {
		super("", null);

		fTestCaseNodes = new ArrayList<>();
	}

	public void setDisplayLimitExceededFlag(boolean displayLimitExceeded) {
		fDisplayLimitExceeded  = displayLimitExceeded;
	}
	
	public boolean getDisplayLimitExceededFlag() {
	
		return fDisplayLimitExceeded;
	}
		
	public List<TestCaseNode> getTestCaseNodes() { 

		return fTestCaseNodes;
	}

	@Override
	public boolean hasChildren(){

		return(fTestCaseNodes.size() != 0);
	}

	@Override
	protected String getNonQualifiedName() {
		return getName();
	}

	@Override
	public List<TestCaseNode> getChildren() {

		return fTestCaseNodes;
	}
	
	@Override
	public int getChildrenCount() {
		
		return fTestCaseNodes.size();
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

		for(TestCaseNode choice : fTestCaseNodes) {
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

}
