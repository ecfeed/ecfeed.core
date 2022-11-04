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

	public List<MethodNode> getMethods(AbstractParameterNode parameter); 	// TODO MO-RE - MOVE FROM HERE
	
	public void addParameter(AbstractParameterNode parameter);	
	public void addParameter(AbstractParameterNode parameter, int index);
	public void addParameters(List<BasicParameterNode> parameters);
	
	public boolean removeParameter(AbstractParameterNode parameter);
	public void replaceParameters(List<AbstractParameterNode> parameters);

	public int getParametersCount();
	public List<AbstractParameterNode> getParameters();
	public AbstractParameterNode getParameter(int parameterIndex);

	public AbstractParameterNode findParameter(String parameterNameToFind);
//	public BasicParameterNode findMethodParameter(String name); // TODO MO-RE do we need 2 similar functions ?
	public int getParameterIndex(String parameterName);

	public boolean parameterExists(String parameterName);
	public boolean parameterExists(AbstractParameterNode abstractParameterNode);

	public List<String> getParameterTypes();
	public List<String> getParametersNames();

	public String generateNewParameterName(String startParameterName);

}
