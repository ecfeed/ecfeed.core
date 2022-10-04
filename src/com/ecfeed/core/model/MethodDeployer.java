/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.ExceptionHelper;

public abstract class MethodDeployer {

	public static MethodNode deploy(MethodNode methodNode) {
		
		if (methodNode == null) {
			ExceptionHelper.reportRuntimeException("No method.");
		}
		
		MethodNode clonedMethodNode = methodNode.makeClone();
		
		return clonedMethodNode;
	}
	
}
