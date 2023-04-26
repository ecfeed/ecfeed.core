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

public class ParametersWithContextLister {

	private ElementLister<ParameterWithLinkingContext> fElementLister;

	public ParametersWithContextLister(IModelChangeRegistrator modelChangeRegistrator) {

		fElementLister = new ElementLister<ParameterWithLinkingContext>(modelChangeRegistrator);
	}

	public void addParameter(
			AbstractParameterNode parameter, 
			IAbstractNode parent) {

		parameter.setParent(parent);

		ParameterWithLinkingContext parameterWithLinkingContext = 
				new ParameterWithLinkingContext(parameter, null);

		fElementLister.addElement(parameterWithLinkingContext);

	}

	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext, 
			IAbstractNode parent) {

		parameter.setParent(parent);

		ParameterWithLinkingContext parameterWithLinkingContext = 
				new ParameterWithLinkingContext(parameter, linkingContext);

		fElementLister.addElement(parameterWithLinkingContext);
	}

	public void addParameter(
			AbstractParameterNode parameter,
			AbstractParameterNode linkingContext,			
			int index, 
			IAbstractNode parent) {

		parameter.setParent(parent);

		ParameterWithLinkingContext parameterWithLinkingContext = 
				new ParameterWithLinkingContext(parameter, linkingContext);

		fElementLister.addElement(parameterWithLinkingContext, index);
	}

	public void addParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		for (AbstractParameterNode methodParameterNode : parameters) {
			addParameter(methodParameterNode, parent);
		}
	}

	public void setBasicParameters(List<BasicParameterNode> parameters, IAbstractNode parent) {

		fElementLister.clear();

		for (BasicParameterNode basicParameterNode : parameters) {

			ParameterWithLinkingContext parameterWithLinkingContext = 
					new ParameterWithLinkingContext(basicParameterNode, null);

			fElementLister.addElement(parameterWithLinkingContext);
		}
	}

	public void setParametersWithLinkingContexts(List<ParameterWithLinkingContext> parametersWithContexts) {

		fElementLister.clear();

		for (ParameterWithLinkingContext parameterWithContexts : parametersWithContexts) {

			fElementLister.addElement(parameterWithContexts);
		}
	}

	public List<AbstractParameterNode> getParameters() {

		List<AbstractParameterNode> result = new ArrayList<>();

		for (ParameterWithLinkingContext parameterWithLinkingContext : fElementLister.getReferenceToElements()) {

			result.add(parameterWithLinkingContext.getParameter());
		}

		return result;
	}

	public List<ParameterWithLinkingContext> getParametersWithLinkingContexts() {

		return fElementLister.getReferenceToElements();
	}

	public ParameterWithLinkingContext getParameterWithLinkingContexts(int index) {

		ParameterWithLinkingContext copy = 
				new ParameterWithLinkingContext(fElementLister.getElement(index));

		return copy;
	}

	public List<BasicParameterNode> getParametersAsBasic() {

		List<BasicParameterNode> result = new ArrayList<>();

		for (ParameterWithLinkingContext parameterWithLinkingContext : fElementLister.getReferenceToElements()) {

			AbstractParameterNode abstractParameterNode = parameterWithLinkingContext.getParameter();

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

		ParameterWithLinkingContext parameterWithLinkingContext = fElementLister.getElement(parameterIndex);

		return parameterWithLinkingContext.getParameter();
	}	

	public int getParameterIndex(String parameterName) {

		int index = 0;

		for (ParameterWithLinkingContext parameterWithLinkingContext : fElementLister.getReferenceToElements()) {

			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();

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

		for (ParameterWithLinkingContext parameterWithLinkingContext : fElementLister.getReferenceToElements()) {

			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();

			if (parameter instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;
				types.add(basicParameterNode.getType());
			}
		}

		return types;
	}

	public List<String> getParametersNames() {

		List<String> names = new ArrayList<String>();

		for (ParameterWithLinkingContext parameterWithLinkingContext : fElementLister.getReferenceToElements()) {

			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();

			names.add(parameter.getName());
		}

		return names;
	}

	public boolean removeParameter(AbstractParameterNode parameter) {

		parameter.setParent(null);

		// XYX rewrite in ParametersLister
		boolean result = fElementLister.getReferenceToElements().removeIf(e -> e.getParameter().equals(parameter));
		fElementLister.registerChange();

		return result;

	}

	public void removeAllParameters() {

		fElementLister.clear();
	}

	public void replaceParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {

		fElementLister.clear();
		addParameters(parameters, parent);
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

	public boolean isMatch(ParametersWithContextLister otherParametersHolder) {

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

}
