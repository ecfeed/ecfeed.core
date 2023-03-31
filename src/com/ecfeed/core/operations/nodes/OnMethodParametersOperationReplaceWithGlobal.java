/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.operations.link.HostMethodOperationPrepareParameterChange;
import com.ecfeed.core.operations.link.MethodParameterOperationSetLink;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnMethodParametersOperationReplaceWithGlobal extends CompositeOperation{

	public OnMethodParametersOperationReplaceWithGlobal(
			IParametersParentNode parent, 
			List<BasicParameterNode> originals, 
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REPLACE_PARAMETERS, false, parent, parent, extLanguageManager);
		
		for(BasicParameterNode parameter : originals){
			addOperation(new ReplaceParameterWithLink(parameter, parent, extLanguageManager));
		}
	}
	
	private class ReplaceParameterWithLink extends CompositeOperation{

		public ReplaceParameterWithLink(
				BasicParameterNode target, 
				IParametersParentNode parent, 
				IExtLanguageManager extLanguageManager) {
			super(OperationNames.REPLACE_PARAMETER_WITH_LINK, true, target, target, extLanguageManager);
			MethodNode method = (MethodNode) target.getParent();
			BasicParameterNode global =
					target.makeClone();
					// new BasicParameterNode(target);
			addOperation(new GenericOperationAddParameter(parent, global, true, extLanguageManager));
			addOperation(new MethodParameterOperationSetLink(target, global, extLanguageManager));
			
			String anyNotNullLinkSignature = " ";
			String newType = BasicParameterNodeHelper.calculateNewParameterType(target, anyNotNullLinkSignature);
			addOperation(new HostMethodOperationPrepareParameterChange(target, newType, extLanguageManager));
			
			for(ConstraintNode constraint : method.getConstraintNodes()){
				if(constraint.mentions(target)){
					ConstraintNode copy = constraint.makeClone();
					addOperation(new OnConstraintOperationAdd(method, copy, constraint.getMyIndex(), extLanguageManager));
				}
			}
			for(TestCaseNode tc : method.getTestCases()){
				TestCaseNode copy = tc.makeClone();
				addOperation(
						new OnTestCaseOperationAddToMethod(
								method, copy, tc.getMyIndex(), Optional.empty(), extLanguageManager));
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
