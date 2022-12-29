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

import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.link.MethodParameterOperationSetLink;
import com.ecfeed.core.operations.link.MethodParameterOperationSetLinkedAndMakeMethodConsistent;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ReplaceMethodParametersWithGlobalOperation extends BulkOperation{

	public ReplaceMethodParametersWithGlobalOperation(
			IParametersParentNode parent, 
			List<BasicParameterNode> originals, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REPLACE_PARAMETERS, false, parent, parent, extLanguageManager);
		
		for(BasicParameterNode parameter : originals){
			addOperation(new ReplaceParameterWithLink(parameter, parent, adapterProvider, extLanguageManager));
		}
	}
	
	private class ReplaceParameterWithLink extends BulkOperation{

		public ReplaceParameterWithLink(
				BasicParameterNode target, 
				IParametersParentNode parent, 
				ITypeAdapterProvider adapterProvider,
				IExtLanguageManager extLanguageManager) {
			super(OperationNames.REPLACE_PARAMETER_WITH_LINK, true, target, target, extLanguageManager);
			MethodNode method = (MethodNode) target.getParent();
			BasicParameterNode global = new BasicParameterNode(target);
			addOperation(new GenericOperationAddParameter(parent, global, true, extLanguageManager));
			addOperation(new MethodParameterOperationSetLink(target, global, extLanguageManager));
			addOperation(new MethodParameterOperationSetLinkedAndMakeMethodConsistent(target, true, extLanguageManager));
			for(ConstraintNode constraint : method.getConstraintNodes()){
				if(constraint.mentions(target)){
					ConstraintNode copy = constraint.makeClone();
					addOperation(new MethodOperationAddConstraint(method, copy, constraint.getMyIndex(), extLanguageManager));
				}
			}
			for(TestCaseNode tc : method.getTestCases()){
				TestCaseNode copy = tc.makeClone();
				addOperation(new MethodOperationAddTestCase(method, copy, adapterProvider, tc.getMyIndex(), extLanguageManager));
			}
		}

		@Override
		public void execute() {
			try {
				super.execute();
			} catch (Exception e) {
				throw e;
			}
		}

	}

}
