package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class TestSuiteNode extends AbstractNode {
	private String fSuiteName;
	private List<TestCaseNode> fTestCaseNodes;
	private boolean fDisplayLimitExceeded;

	public TestSuiteNode(
			String name, IModelChangeRegistrator modelChangeRegistrator, List<TestCaseNode> testCaseNodes) {

		super(name, modelChangeRegistrator);

		String firstName = testCaseNodes.get(0).getName();

		if (firstName == null) {
			ExceptionHelper.reportRuntimeException("Empty test case name.");
		}

		for (TestCaseNode testCaseNode : testCaseNodes) {

			if (!StringHelper.isEqual(firstName, testCaseNode.getName())) {
				ExceptionHelper.reportRuntimeException("Inconsistent test case names.");
			}
		}

		fTestCaseNodes = testCaseNodes;
	}

	public TestSuiteNode(
			String name, IModelChangeRegistrator modelChangeRegistrator, Collection<TestCaseNode> testCaseNodes) {

		super(name, modelChangeRegistrator);

		//fTestCaseNodes = testData.stream().collect(Collectors.toList()); TODO MO-RE use constructor
		fTestCaseNodes = new ArrayList<>(testCaseNodes);
	}

	public TestSuiteNode(List<TestCaseNode> testData) {
		super("", null);

		fTestCaseNodes = testData;
	}

	public TestSuiteNode(Collection<TestCaseNode> testData) {
		super("", null);

		// fTestCaseNodes = testData.stream().collect(Collectors.toList()); TODO MO-RE use constructor
		fTestCaseNodes = new ArrayList<>(testData);
	}

	public TestSuiteNode() {
		super("", null);

		fTestCaseNodes = new ArrayList<>();
	}

	public String toString() {
		return fSuiteName;
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
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public List<IAbstractNode> getChildren() {

		List<IAbstractNode> result = new ArrayList<>();
		result.addAll(fTestCaseNodes);

		return result;
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
			testCase.correctTestCase(method);
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
