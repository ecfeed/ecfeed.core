package com.ecfeed.core.operations;

import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationRemoveTestSuite extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestSuiteNode fTestSuite;

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
				public String convert(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
					return value;
				}
				@Override
				public boolean isCompatible(String type) {
					return true;
				}
				@Override
				public Object generateValue(String range) {
					return null;
				}
				@Override
				public String generateValueAsString(String range) {
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
			};
		}

	}

	public MethodOperationRemoveTestSuite(MethodNode target, TestSuiteNode testSuite, IExtLanguageManager extLanguageManager) {
		super(OperationNames.REMOVE_TEST_SUITE, extLanguageManager);
		fMethodNode = target;
		fTestSuite = testSuite;
	}

	@Override
	public void execute() throws ModelOperationException {
		setOneNodeToSelect(fMethodNode);
		fMethodNode.removeTestSuite(fTestSuite);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		List<TestCaseNode> testCaseNodes = fTestSuite.getTestCaseNodes();

		return new MethodOperationAddTestSuite(
				fMethodNode, 
				testCaseNodes, 
				new DummyAdapterProvider(), 
				getExtLanguageManager());
	}

}
