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

import java.util.List;

public interface IParametersParentNode extends IAbstractNode {

	public void addParameter(BasicParameterNode parameter);	
	public void addParameter(BasicParameterNode parameter, int index);
	public void addParameters(List<BasicParameterNode> parameters);
	
	public boolean removeParameter(BasicParameterNode parameter);
	public void replaceParameters(List<BasicParameterNode> parameters);

	public int getParametersCount();
	public List<BasicParameterNode> getParameters();
	public BasicParameterNode getParameter(int parameterIndex);

	public BasicParameterNode findParameter(String parameterNameToFind);
	public int getParameterIndex(String parameterName);

	public boolean parameterExists(String parameterName);
	public boolean parameterExists(BasicParameterNode abstractParameterNode);

	public List<String> getParameterTypes();
	public List<String> getParametersNames();

	public String generateNewParameterName(String startParameterName);

}
