package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class TestSuiteNode extends AbstractNode {
	private Set<TestCaseNode> fTestCaseNodes;
	private boolean fDisplayLimitExceeded; // TODO MO-RE remove this flag (display does not belong here)

	public TestSuiteNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);

		if (StringHelper.isNullOrEmpty(name)) {
			ExceptionHelper.reportRuntimeException("Empty test suite name.");
		}

		fTestCaseNodes = new HashSet<>();
		fDisplayLimitExceeded = false;
	}

	public TestSuiteNode(
			String name, 
			Collection<TestCaseNode> testCaseNodes,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, modelChangeRegistrator);

		for (TestCaseNode testCaseNode : testCaseNodes) {

			String currentName = testCaseNode.getName();

			if (!StringHelper.isEqual(name, currentName)) {
				ExceptionHelper.reportRuntimeException("Inconsistent test case names.");
			}
		}

		fTestCaseNodes = new HashSet<>(testCaseNodes);
	}

	//	public TestSuiteNode(
	//			String name, IModelChangeRegistrator modelChangeRegistrator, Collection<TestCaseNode> testCaseNodes) {
	//
	//		super(name, modelChangeRegistrator);
	//
	//		fTestCaseNodes = new HashSet<>(testCaseNodes);
	//	}

	//	public TestSuiteNode(List<TestCaseNode> testData) {
	//		super("", null);
	//
	//		fTestCaseNodes = testData;
	//	}

	//	public TestSuiteNode(Collection<TestCaseNode> testData) {
	//		super("", null);
	//
	//		// fTestCaseNodes = testData.stream().collect(Collectors.toList()); TODO MO-RE use constructor
	//		fTestCaseNodes = new ArrayList<>(testData);
	//	}

	//	public TestSuiteNode() {
	//		super("", null);
	//
	//		fTestCaseNodes = new ArrayList<>();
	//	}

	public String toString() {
		return getName();
	}

	public void setDisplayLimitExceededFlag(boolean displayLimitExceeded) {
		fDisplayLimitExceeded  = displayLimitExceeded;
	}

	public boolean getDisplayLimitExceededFlag() {

		return fDisplayLimitExceeded;
	}

	void addTestCase(TestCaseNode testCaseNode) {

		fTestCaseNodes.add(testCaseNode);
	}
	
	void removeTestCase(TestCaseNode testCaseNode) {

		fTestCaseNodes.remove(testCaseNode);
	}

	public List<TestCaseNode> getTestCaseNodes() { 

		return new ArrayList<>(fTestCaseNodes);
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

		return new ArrayList<>(fTestCaseNodes);
	}

	@Override
	public int getChildrenCount() {

		return fTestCaseNodes.size();
	}

	public void setSuiteName(String suiteName) {
		super.setName(suiteName);
	}

	public String getSuiteName() {
		return getName();
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

		TestSuiteNode copy = new TestSuiteNode(this.getName(), fTestCaseNodes, getModelChangeRegistrator());
		copy.setProperties(getProperties());
		return copy;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

}
