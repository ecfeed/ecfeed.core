/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class ParametersLister {

	private List<AbstractParameterNode> fParameters;
	private IModelChangeRegistrator fModelChangeRegistrator;

	public ParametersLister(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
		fParameters = new ArrayList<AbstractParameterNode>();
	}

	public void addParameter(AbstractParameterNode parameter, IAbstractNode parent) {

		addParameter(parameter, fParameters.size(), parent);
	}

	public void addParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		for (AbstractParameterNode methodParameterNode : parameters) {
			addParameter(methodParameterNode, parent);
		}
	}

	public void setBasicParameters(List<BasicParameterNode> parameters, IAbstractNode parent) {

		fParameters.clear();
		fParameters.addAll(parameters);
	}

	public void addParameter(AbstractParameterNode parameter, int index, IAbstractNode parent) {

		if (parameterExists(parameter)) {
			ExceptionHelper.reportRuntimeException("Parameter: " + parameter.getName() + " already exists.");
		}

		fParameters.add(index, parameter);
		parameter.setParent(parent);

		registerChange();
	}

	public List<AbstractParameterNode> getParameters() {

		return fParameters;
	}

	public List<BasicParameterNode> getParametersAsBasic() {

		List<BasicParameterNode> result = new ArrayList<>();

		for (AbstractParameterNode abstractParameterNode : fParameters) {

			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				ExceptionHelper.reportRuntimeException("Attempt to get not basic parameter.");
			}

			result.add((BasicParameterNode) abstractParameterNode);
		}

		return result;
	}

	public int getParametersCount(){

		return fParameters.size();
	}	

	public AbstractParameterNode findParameter(String parameterNameToFind) {

		if (parameterNameToFind.contains(SignatureHelper.SIGNATURE_NAME_SEPARATOR)) {
			return findParameterQualified(parameterNameToFind);
		} else {
			return findParameterNonQualified(parameterNameToFind);
		}
	}

	public AbstractParameterNode findParameterNonQualified(String parameterNameToFind) {

		Optional<AbstractParameterNode> result = getParameters().stream()
				.filter(e -> e.getName().equals(parameterNameToFind))
				.findAny();

		if (result.isPresent()) {
			return result.get();
		}

		return null;
	}

	public AbstractParameterNode findParameterQualified(String parameterNameToFind) {

		for (AbstractParameterNode parameter : getParameters()) {

			if (AbstractParameterNodeHelper.getQualifiedName(parameter).equals(parameterNameToFind)) {
				return parameter;
			}

			if (parameter instanceof CompositeParameterNode) {
				return ((CompositeParameterNode) parameter).findParameter(parameterNameToFind);
			}
		}

		return null;
	}

	public AbstractParameterNode getParameter(int parameterIndex) {

		return fParameters.get(parameterIndex);
	}	

	public int getParameterIndex(String parameterName) {

		int index = 0;

		for (AbstractParameterNode parameter : fParameters) {
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

	public boolean parameterExists(AbstractParameterNode abstractParameterNode) {

		if (parameterExists(abstractParameterNode.getName())) {
			return true;
		}

		return false;
	}

	public List<String> getParameterTypes() {

		List<String> types = new ArrayList<String>();

		for (AbstractParameterNode parameter : fParameters) {

			if (parameter instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;
				types.add(basicParameterNode.getType());
			}
		}

		return types;
	}

	public List<String> getParametersNames() {

		List<String> names = new ArrayList<String>();

		for(AbstractParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}

		return names;
	}

	public boolean removeParameter(AbstractParameterNode parameter) {

		parameter.setParent(null);

		boolean result = fParameters.remove(parameter);
		registerChange();

		return result;
	}

	public void removeAllParameters() {

		fParameters.clear();
	}

	public void replaceParameters(List<AbstractParameterNode> parameters) {

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

	public boolean isMatch(ParametersLister otherParametersHolder) {

		List<AbstractParameterNode> parameters = getParameters();
		List<AbstractParameterNode> otherParameters = otherParametersHolder.getParameters();

		int parametersSize = parameters.size();

		if (parametersSize != otherParameters.size()) {
			return false;
		}

		for (int i = 0; i < parametersSize; ++i) {

			AbstractParameterNode abstractParameterNode = parameters.get(i);
			AbstractParameterNode otherParameter = otherParameters.get(i);

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
