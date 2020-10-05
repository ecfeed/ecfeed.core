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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;

public abstract class AbstractModelOperation implements IModelOperation {

	private boolean fModelUpdated;
	private String fName;
	private IExtLanguageManager fExtLanguage;

	private List<AbstractNode> fNodesToSelect;

	public AbstractModelOperation(String name, IExtLanguageManager extLanguage){
		fName = name;
		fExtLanguage = extLanguage;
		fNodesToSelect = new ArrayList<AbstractNode>();
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
	public void setNodesToSelect(List<AbstractNode> nodesToSelect) {
		fNodesToSelect = nodesToSelect;
	}

	public IExtLanguageManager getExtLanguageManager() {
		return fExtLanguage;
	}
	
	public void setOneNodeToSelect(AbstractNode nodeToSelect) {
		List<AbstractNode> nodes = new ArrayList<AbstractNode>();
		nodes.add(nodeToSelect);

		setNodesToSelect(nodes);
	}

	public List<AbstractNode> getNodesToSelect() {
		return fNodesToSelect;
	}

	public String convertTextFromExtToIntrLanguage(String text, IExtLanguageManager extLanguage) throws ModelOperationException {

		String result = null;
		
		try {
			result = extLanguage.convertTextFromExtToIntrLanguage(text);
		} catch (Exception e) {
			ModelOperationException.report(e.getMessage());
		}
		
		return result;
	}

}
