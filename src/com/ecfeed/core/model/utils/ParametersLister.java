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
import com.ecfeed.core.model.AbstractParameterSignatureHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class ParametersLister {

	private ElementLister<AbstractParameterNode> fElementLister;


	public ParametersLister(IModelChangeRegistrator modelChangeRegistrator) {

		fElementLister = new ElementLister<AbstractParameterNode>(modelChangeRegistrator);
	}

	public void addParameter(
			AbstractParameterNode parameter, 
			IAbstractNode parent) {

		parameter.setParent(parent);
		fElementLister.addElement(parameter);
	}

	public void addParameter(
			AbstractParameterNode parameter,
			int index,
			IAbstractNode parent) {

		parameter.setParent(parent);
		fElementLister.addElement(parameter, index);
	}
	
	public void addParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		for (AbstractParameterNode methodParameterNode : parameters) {
			addParameter(methodParameterNode, parent);
		}
	}

	public void setParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		fElementLister.clear();
		addParameters(parameters, parent);
	}

	public List<AbstractParameterNode> getReferenceToParameters() {
		
		return fElementLister.getReferenceToElements();
	}

	public List<BasicParameterNode> getParametersAsBasic() {

		List<BasicParameterNode> result = new ArrayList<>();

		for (AbstractParameterNode abstractParameterNode : getReferenceToParameters()) {

			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				ExceptionHelper.reportRuntimeException("Attempt to get not basic parameter.");
			}

			result.add((BasicParameterNode) abstractParameterNode);
		}

		return result;
	}

	public int getParametersCount(){

		return fElementLister.getElementsCount();
	}	

	public AbstractParameterNode findParameter(String parameterNameToFind) {

		if (parameterNameToFind.contains(SignatureHelper.SIGNATURE_NAME_SEPARATOR)) {
			return findParameterQualified(parameterNameToFind);
		} else {
			return findParameterNonQualified(parameterNameToFind);
		}
	}

	public AbstractParameterNode findParameterNonQualified(String parameterNameToFind) {

		Optional<AbstractParameterNode> result = fElementLister.getReferenceToElements().stream()
				.filter(e -> e.getName().equals(parameterNameToFind))
				.findAny();

		if (result.isPresent()) {
			return result.get();
		}

		return null;
	}

	public AbstractParameterNode findParameterQualified(String parameterNameToFind) {

		for (AbstractParameterNode parameter : fElementLister.getReferenceToElements()) {

			if (AbstractParameterSignatureHelper.getQualifiedName(parameter).equals(parameterNameToFind)) {
				return parameter;
			}

			if (parameter instanceof CompositeParameterNode) {
				return ((CompositeParameterNode) parameter).findParameter(parameterNameToFind);
			}
		}

		return null;
	}

	public AbstractParameterNode getParameter(int parameterIndex) {

		AbstractParameterNode parameterNode = fElementLister.getElement(parameterIndex);

		return parameterNode;
	}	

	public int getParameterIndex(String parameterName) {

		int index = 0;

		for (AbstractParameterNode parameter : fElementLister.getReferenceToElements()) {

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

		for (AbstractParameterNode parameter : fElementLister.getReferenceToElements()) {

			if (parameter instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;
				types.add(basicParameterNode.getType());
			}
		}

		return types;
	}

	public List<String> getParametersNames() {

		List<String> names = new ArrayList<String>();

		for (AbstractParameterNode parameter : fElementLister.getReferenceToElements()) {

			names.add(parameter.getName());
		}

		return names;
	}

	public boolean removeParameter(AbstractParameterNode parameter) {

		parameter.setParent(null);

		// TODO MO-RE rewrite in ParametersLister
		boolean result = fElementLister.getReferenceToElements().removeIf(e -> e.equals(parameter));
		fElementLister.registerChange();

		return result;
	}

	public void removeAllParameters() {

		fElementLister.clear();
	}

	public void replaceParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		// TODO MO-RE rewrite in ParametersLister
		fElementLister.clear();
		addParameters(parameters, parent);

		fElementLister.registerChange();
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

		List<AbstractParameterNode> parameters = fElementLister.getReferenceToElements();
		List<AbstractParameterNode> otherParameters = otherParametersHolder.fElementLister.getReferenceToElements();

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

	public void shiftElements(List<Integer> indicesOfElements, int shift) {

		fElementLister.shiftElements(indicesOfElements, shift);
	}
	
	public void shiftOneElement(int indexOfElement, int shift) {

		fElementLister.shiftOneElement(indexOfElement, shift);
	}
	
}
