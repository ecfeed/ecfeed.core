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

import com.ecfeed.core.model.AbstractNode;


public interface IModelOperation {
	public void execute();
	public boolean modelUpdated();
	public IModelOperation getReverseOperation();
	public String getName();
	public void setNodesToSelect(List<AbstractNode> nodes);
	public List<AbstractNode> getNodesToSelect();

}
