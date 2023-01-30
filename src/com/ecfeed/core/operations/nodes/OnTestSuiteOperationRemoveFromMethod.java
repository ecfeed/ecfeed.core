package com.ecfeed.core.operations.nodes;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnTestSuiteOperationRemoveFromMethod extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestSuiteNode fTestSuite;
	private List<TestCaseNode> fTestCaseNodes;

	public OnTestSuiteOperationRemoveFromMethod(
			MethodNode target, TestSuiteNode testSuite, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_TEST_SUITE, extLanguageManager);
		fMethodNode = target;
		fTestSuite = testSuite;
		fTestCaseNodes = new ArrayList<>(target.getTestCases(testSuite.getName()));
	}

	@Override
	public String toString() {

		return createDescription(fTestSuite.getName()); 
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fMethodNode);
		fMethodNode.removeTestSuite(fTestSuite);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new OnTestSuiteOperationAddToMethod(
				fMethodNode, 
				fTestCaseNodes, 
				new TypeAdapterProviderForJava(), 
				getExtLanguageManager());
	}

}
