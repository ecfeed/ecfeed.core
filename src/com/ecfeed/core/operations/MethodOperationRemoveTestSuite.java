package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationRemoveTestSuite extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestSuiteNode fTestSuite;
	private List<TestCaseNode> fTestCaseNodes;

	private class DummyAdapterProvider implements ITypeAdapterProvider {

		@Override
		public ITypeAdapter<?> getAdapter(String type) {

			return new ITypeAdapter<Object>() {
				@Override
				public boolean isNullAllowed() {
					return false;
				}
				@Override
				public String getDefaultValue() {
					return null;
				}
				@Override
				public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
					return value;
				}
				@Override
				public Object generateValue(String range, String context) {
					return null;
				}
				@Override
				public String generateValueAsString(String range, String context) {
					return null;
				}
				@Override
				public boolean isRandomizable() {
					return false;
				}
				@Override
				public String getMyTypeName() {
					return null;
				}
				@Override
				public boolean isConvertibleTo(String type) {
					return false;
				}
				@Override
				public boolean canCovertWithoutLossOfData(String oldType, String value, boolean isRandomized) {
					return false;
				}
			};
		}

	}

	public MethodOperationRemoveTestSuite(MethodNode target, TestSuiteNode testSuite, IExtLanguageManager extLanguageManager) {
		super(OperationNames.REMOVE_TEST_SUITE, extLanguageManager);
		fMethodNode = target;
		fTestSuite = testSuite;
		fTestCaseNodes = new ArrayList<>(target.getTestCases(testSuite.getName()));
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fMethodNode);
		fMethodNode.removeTestSuite(fTestSuite);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new MethodOperationAddTestSuite(
				fMethodNode, 
				fTestCaseNodes, 
				new DummyAdapterProvider(), 
				getExtLanguageManager());
	}

}
