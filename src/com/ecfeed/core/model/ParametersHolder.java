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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class ParametersHolder {

	private List<BasicParameterNode> fParameters;
	private IModelChangeRegistrator fModelChangeRegistrator;

	public ParametersHolder(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
		fParameters = new ArrayList<BasicParameterNode>();
	}

	public void addParameter(BasicParameterNode parameter, IAbstractNode parent) {

		addParameter(parameter, fParameters.size(), parent);
	}

	public void addParameters(List<BasicParameterNode> parameters, IAbstractNode parent) {

		for (BasicParameterNode methodParameterNode : parameters) {
			addParameter(methodParameterNode, parent);
		}
	}

	public void addParameter(BasicParameterNode parameter, int index, IAbstractNode parent) {

		if (parameterExists(parameter)) {
			ExceptionHelper.reportRuntimeException("Parameter: " + parameter.getName() + " already exists.");
		}

		fParameters.add(index, parameter);
		parameter.setParent(parent);

		registerChange();
	}

	public List<BasicParameterNode> getParameters() {

		return fParameters;
	}

	public int getParametersCount(){

		return fParameters.size();
	}	

	public BasicParameterNode findParameter(String parameterNameToFind) {

		for (BasicParameterNode parameter : fParameters) {

			final String parameterName = parameter.getName();

			if (parameterName.equals(parameterNameToFind)) {
				return parameter;
			}
		}
		return null;
	}

	public BasicParameterNode getParameter(int parameterIndex) {

		return fParameters.get(parameterIndex);
	}	

	public int getParameterIndex(String parameterName) {

		int index = 0;

		for (BasicParameterNode parameter : fParameters) {
			if (parameter.getName().equals(parameterName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public boolean parameterExists(String parameterName) {

		if (findParameter(parameterName) == null) {
			return false;
		}

		return true;
	}

	public boolean parameterExists(BasicParameterNode abstractParameterNode) {

		if (parameterExists(abstractParameterNode.getName())) {
			return true;
		}

		return false;
	}

	public List<String> getParameterTypes() {

		List<String> types = new ArrayList<String>();

		for (BasicParameterNode parameter : fParameters) {
			types.add(parameter.getType());
		}

		return types;
	}

	public List<String> getParametersNames() {

		List<String> names = new ArrayList<String>();

		for(BasicParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}

		return names;
	}

	public boolean removeParameter(BasicParameterNode parameter) {

		parameter.setParent(null);

		boolean result = fParameters.remove(parameter);
		registerChange();

		return result;
	}

	public void replaceParameters(List<BasicParameterNode> parameters) {

		fParameters.clear();
		fParameters.addAll(parameters);

		registerChange();
	}

	public String generateNewParameterName(String startParameterName) {

		if (!parameterExists(startParameterName)) {
			return startParameterName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startParameterName);

		for (int i = 1;   ; i++) {

			String newParameterName = oldNameCore + String.valueOf(i);

			if (!parameterExists(newParameterName)) {
				return newParameterName;
			}
		}
	}

	public boolean isMatch(ParametersHolder otherParametersHolder) {
		
		List<BasicParameterNode> parameters = getParameters();
		List<BasicParameterNode> otherParameters = otherParametersHolder.getParameters();
		
		int parametersSize = parameters.size();
		
		if (parametersSize != otherParameters.size()) {
			return false;
		}

		for (int i = 0; i < parametersSize; ++i) {

			BasicParameterNode abstractParameterNode = parameters.get(i);
			BasicParameterNode otherParameter = otherParameters.get(i);
			
			if (!abstractParameterNode.isMatch(otherParameter)) {
				return false;
			}
		}
		
		return true;
	}

	private void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}

}
