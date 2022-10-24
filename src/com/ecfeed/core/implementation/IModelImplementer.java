/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.implementation;

import com.ecfeed.core.model.IAbstractNode;

public interface IModelImplementer {
	public boolean isImplementable(Class<? extends IAbstractNode> type);
	public boolean isImplementable(IAbstractNode node);
	public boolean implement(IAbstractNode node) throws Exception;
	public EImplementationStatus getImplementationStatus(IAbstractNode node);
}
