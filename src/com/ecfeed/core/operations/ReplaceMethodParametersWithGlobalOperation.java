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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExtLanguage;

public class ReplaceMethodParametersWithGlobalOperation extends BulkOperation{

	private class ReplaceParameterWithLink extends BulkOperation{

		public ReplaceParameterWithLink(
				MethodParameterNode target, 
				GlobalParametersParentNode parent, 
				ITypeAdapterProvider adapterProvider,
				ExtLanguage viewMode) {
			super(OperationNames.REPLACE_PARAMETER_WITH_LINK, true, target, target, viewMode);
			MethodNode method = target.getMethod();
			GlobalParameterNode global = new GlobalParameterNode(target);
			addOperation(new GenericOperationAddParameter(parent, global, true, viewMode));
			addOperation(new MethodParameterOperationSetLink(target, global, viewMode));
			addOperation(new MethodParameterOperationSetLinked(target, true, viewMode));
			for(ConstraintNode constraint : method.getConstraintNodes()){
				if(constraint.mentions(target)){
					ConstraintNode copy = constraint.makeClone();
					addOperation(new MethodOperationAddConstraint(method, copy, constraint.getMyIndex(), viewMode));
				}
			}
			for(TestCaseNode tc : method.getTestCases()){
				TestCaseNode copy = tc.makeClone();
				addOperation(new MethodOperationAddTestCase(method, copy, adapterProvider, tc.getMyIndex(), viewMode));
			}
		}

		@Override
		public void execute() throws ModelOperationException{
			try {
				super.execute();
			} catch (ModelOperationException e) {
				throw e;
			}
		}

	}

	public ReplaceMethodParametersWithGlobalOperation(
			GlobalParametersParentNode parent, 
			List<MethodParameterNode> originals, 
			ITypeAdapterProvider adapterProvider,
			ExtLanguage viewMode) {
		
		super(OperationNames.REPLACE_PARAMETERS, false, parent, parent, viewMode);
		
		for(MethodParameterNode parameter : originals){
			addOperation(new ReplaceParameterWithLink(parameter, parent, adapterProvider, viewMode));
		}
	}

}
