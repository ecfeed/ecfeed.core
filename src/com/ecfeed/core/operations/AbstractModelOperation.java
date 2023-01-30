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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public abstract class AbstractModelOperation implements IModelOperation {

	private boolean fModelUpdated;
	private String fName;
	private IExtLanguageManager fExtLanguageManager;

	private List<IAbstractNode> fNodesToSelect;

	public AbstractModelOperation(String name, IExtLanguageManager extLanguageManager){
		fName = name;
		fExtLanguageManager = extLanguageManager;
		fNodesToSelect = new ArrayList<>();
	}

	@Override
	public boolean modelUpdated() {
		return fModelUpdated;
	}

	protected void markModelUpdated(){
		fModelUpdated = true;
	}

	@Override
	public String getName(){
		return fName;
	}

	@Override
	public String toString(){
		return getName();
	}

	@Override
	public void setNodesToSelect(List<IAbstractNode> nodesToSelect) {
		fNodesToSelect = nodesToSelect;
	}

	public String createDescription(String... operationParameters) {

		String parametersString = "";
		String separator = ", ";

		for (String operationParameter : operationParameters) {

			parametersString = parametersString + operationParameter + separator;
		}

		parametersString = StringHelper.getAllBeforeLastToken(parametersString, separator);

		return "OP:" + fName + "(" + parametersString + ")";
	}

	public IExtLanguageManager getExtLanguageManager() {
		return fExtLanguageManager;
	}

	public void setOneNodeToSelect(IAbstractNode nodeToSelect) {

		List<IAbstractNode> nodes = new ArrayList<>();
		nodes.add(nodeToSelect);

		setNodesToSelect(nodes);
	}

	public List<IAbstractNode> getNodesToSelect() {
		return fNodesToSelect;
	}

}
