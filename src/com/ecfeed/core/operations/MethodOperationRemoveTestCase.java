/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package com.ecfeed.core.operations;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationRemoveTestCase extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCase;
	private int fIndex;

	private class DummyAdapterProvider implements ITypeAdapterProvider{

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

	public MethodOperationRemoveTestCase(MethodNode target, TestCaseNode testCase, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_TEST_CASE, extLanguageManager);

		fMethodNode = target;
		fTestCase = testCase;
		fIndex = testCase.getMyIndex();
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fMethodNode);
		fIndex = fTestCase.getMyIndex();
		fMethodNode.removeTestCase(fTestCase);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationAddTestCase(fMethodNode, fTestCase, new DummyAdapterProvider(), fIndex, getExtLanguageManager());
	}

}
