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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ShifterOfListElements;

public class ElementLister<TypeOfElement> {

	private List<TypeOfElement> fElements;
	private IModelChangeRegistrator fModelChangeRegistrator;

	public ElementLister(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
		fElements = new ArrayList<TypeOfElement>();
	}

	public void addElement(TypeOfElement element) {

		if (elementExists(element)) {
			reportErrorElementExists(element);
		}

		fElements.add(element);
		registerChange();
	}

	public void addElement(TypeOfElement element, int index) {

		if (elementExists(element)) {
			reportErrorElementExists(element);
		}

		fElements.add(index, element);

		registerChange();
	}

	public void addElements(List<TypeOfElement> elements, IAbstractNode parent) {

		for (TypeOfElement methodParameterNode : elements) {
			
			addElement(methodParameterNode);
		}
	}
	
	public boolean removeElement(AbstractParameterNode parameter) {
		
		boolean result = fElements.removeIf(e -> e.equals(parameter));
		registerChange();
		return result;
	}
	
	public void replaceElements(List<TypeOfElement> parameters, IAbstractNode parent) {

		fElements.clear();
		addElements(parameters, parent);
	}

	private boolean elementExists(TypeOfElement elementToCheck) {

		for (TypeOfElement currentElement : fElements) {

			if (elementToCheck.equals(currentElement)) {
				return true;
			}
		}

		return false;
	}

	private void reportErrorElementExists(TypeOfElement parameter) {

		ExceptionHelper.reportRuntimeException("Element: " + parameter.toString() + " already exists.");
	}

	public void clear() {

		fElements.clear();
	}

	public List<TypeOfElement> getReferenceToElements() {

		return fElements;
	}

	//	public void addParameter(
	//			AbstractParameterNode parameter, 
	//			AbstractParameterNode linkingContext, 
	//			IAbstractNode parent) {
	//
	//		addParameter(parameter, linkingContext, fElements.size(), parent);
	//	}
	//
	//	public void addParameter(
	//			AbstractParameterNode parameter,
	//			AbstractParameterNode linkingContext,			
	//			int index, 
	//			IAbstractNode parent) {
	//
	//		if (parameterWithContextExists(parameter, linkingContext)) {
	//			reportErrorParameterExists(parameter, linkingContext);
	//		}
	//
	//		fParametersWithContexts.add(index, new TypeOfElement(parameter, linkingContext));
	//		parameter.setParent(parent);
	//
	//		registerChange();
	//	}
	//
	//	private void reportErrorParameterExists(
	//			AbstractParameterNode parameter,
	//			AbstractParameterNode linkingContext) {
	//
	//		if (linkingContext == null) {
	//			ExceptionHelper.reportRuntimeException("Parameter: " + parameter.getName() + " already exists.");
	//		}
	//
	//		ExceptionHelper.reportRuntimeException(
	//				"Parameter: " + parameter.getName() 
	//				+ " with linking context" + linkingContext.getName() 
	//				+ " already exists.");
	//
	//	}
	//
	//	public void addItems(List<Ty> parameters, IAbstractNode parent) {
	//
	//		for (AbstractParameterNode methodParameterNode : parameters) {
	//			addParameter(methodParameterNode, parent);
	//		}
	//	}
	//
	//	public void setBasicParameters(List<BasicParameterNode> parameters, IAbstractNode parent) {
	//
	//		fElements.clear();
	//
	//		for (BasicParameterNode basicParameterNode : parameters) {
	//
	//			fElements.add(new TypeOfElement(basicParameterNode, null));
	//		}
	//	}
	//
	//	public void setParametersWithLinkingContexts(List<TypeOfElement> parametersWithContexts) {
	//
	//		fElements.clear();
	//		fElements.addAll(parametersWithContexts);
	//	}
	//
	//
	//	private boolean parameterWithContextExists(
	//			AbstractParameterNode parameter,
	//			AbstractParameterNode linkingContext) {
	//
	//		TypeOfElement parameterWithLinkingContextToFind = 
	//				new TypeOfElement(parameter, linkingContext);
	//
	//		for (TypeOfElement currentParameterWithLinkingContext : fElements) {
	//
	//			if (parameterWithLinkingContextToFind.isMatch(currentParameterWithLinkingContext)) {
	//				return true;
	//			}
	//		}
	//
	//		return false;
	//	}
	//
	//	public List<TypeOfElement> getParametersWithLinkingContexts() {
	//
	//		List<TypeOfElement> copy = new ArrayList<>(fElements);
	//
	//		return copy;
	//	}
	//
	//	public TypeOfElement getParameterWithLinkingContexts(int index) {
	//
	//		TypeOfElement copy = 
	//				new TypeOfElement(fElements.get(index));
	//
	//		return copy;
	//	}
	//
	//	public List<BasicParameterNode> getParametersAsBasic() {
	//
	//		List<BasicParameterNode> result = new ArrayList<>();
	//
	//		for (TypeOfElement parameterWithLinkingContext : fElements) {
	//
	//			AbstractParameterNode abstractParameterNode = parameterWithLinkingContext.getParameter();
	//
	//			if (!(abstractParameterNode instanceof BasicParameterNode)) {
	//				ExceptionHelper.reportRuntimeException("Attempt to get not basic parameter.");
	//			}
	//
	//			result.add((BasicParameterNode) abstractParameterNode);
	//		}
	//
	//		return result;
	//	}
	//
	//	public int getParametersCount(){
	//
	//		return fElements.size();
	//	}	
	//
	//	public AbstractParameterNode findParameter(String parameterNameToFind) {
	//
	//		if (parameterNameToFind.contains(SignatureHelper.SIGNATURE_NAME_SEPARATOR)) {
	//			return findParameterQualified(parameterNameToFind);
	//		} else {
	//			return findParameterNonQualified(parameterNameToFind);
	//		}
	//	}
	//
	//	public AbstractParameterNode findParameterNonQualified(String parameterNameToFind) {
	//
	//		Optional<AbstractParameterNode> result = getParameters().stream()
	//				.filter(e -> e.getName().equals(parameterNameToFind))
	//				.findAny();
	//
	//		if (result.isPresent()) {
	//			return result.get();
	//		}
	//
	//		return null;
	//	}
	//
	//	public AbstractParameterNode findParameterQualified(String parameterNameToFind) {
	//
	//		for (AbstractParameterNode parameter : getParameters()) {
	//
	//			if (AbstractParameterSignatureHelper.getQualifiedName(parameter).equals(parameterNameToFind)) {
	//				return parameter;
	//			}
	//
	//			if (parameter instanceof CompositeParameterNode) {
	//				return ((CompositeParameterNode) parameter).findParameter(parameterNameToFind);
	//			}
	//		}
	//
	//		return null;
	//	}
	//
	//	public AbstractParameterNode getParameter(int parameterIndex) {
	//
	//		TypeOfElement parameterWithLinkingContext = fElements.get(parameterIndex);
	//
	//		return parameterWithLinkingContext.getParameter();
	//	}	
	//
	//	public int getParameterIndex(String parameterName) {
	//
	//		int index = 0;
	//
	//		for (TypeOfElement parameterWithLinkingContext : fElements) {
	//
	//			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();
	//
	//			if (parameter.getName().equals(parameterName)) {
	//				return index;
	//			}
	//			index++;
	//		}
	//		return -1;
	//	}
	//
	//	public boolean parameterExists(String parameterName) {
	//
	//		if (findParameter(parameterName) == null) {
	//			return false;
	//		}
	//
	//		return true;
	//	}
	//
	//	public boolean parameterExists(AbstractParameterNode abstractParameterNode) {
	//
	//		if (parameterExists(abstractParameterNode.getName())) {
	//			return true;
	//		}
	//
	//		return false;
	//	}
	//
	//	public List<String> getParameterTypes() {
	//
	//		List<String> types = new ArrayList<String>();
	//
	//		for (TypeOfElement parameterWithLinkingContext : fElements) {
	//
	//			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();
	//
	//			if (parameter instanceof BasicParameterNode) {
	//
	//				BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;
	//				types.add(basicParameterNode.getType());
	//			}
	//		}
	//
	//		return types;
	//	}
	//
	//	public List<String> getParametersNames() {
	//
	//		List<String> names = new ArrayList<String>();
	//
	//		for (TypeOfElement parameterWithLinkingContext : fElements) {
	//
	//			AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();
	//
	//			names.add(parameter.getName());
	//		}
	//
	//		return names;
	//	}
	//
	//	public boolean removeParameter(AbstractParameterNode parameter) {
	//
	//		parameter.setParent(null);
	//
	//		boolean result = fElements.removeIf(e -> e.getParameter().equals(parameter));
	//		registerChange();
	//
	//		return result;
	//	}
	//
	//	public void removeAllParameters() {
	//
	//		fElements.clear();
	//	}
	//
	//	public void replaceParameters(List<AbstractParameterNode> parameters, IAbstractNode parent) {
	//
	//		fElements.clear();
	//		addParameters(parameters, parent);
	//
	//		registerChange();
	//	}
	//
	//	public String generateNewParameterName(String startParameterName) {
	//
	//		if (!parameterExists(startParameterName)) {
	//			return startParameterName;
	//		}
	//
	//		String oldNameCore = StringHelper.removeFromNumericPostfix(startParameterName);
	//
	//		for (int i = 1;   ; i++) {
	//
	//			String newParameterName = oldNameCore + String.valueOf(i);
	//
	//			if (!parameterExists(newParameterName)) {
	//				return newParameterName;
	//			}
	//		}
	//	}
	//
	//	public boolean isMatch(ObjectLister otherParametersHolder) {
	//
	//		List<AbstractParameterNode> parameters = getParameters();
	//		List<AbstractParameterNode> otherParameters = otherParametersHolder.getParameters();
	//
	//		int parametersSize = parameters.size();
	//
	//		if (parametersSize != otherParameters.size()) {
	//			return false;
	//		}
	//
	//		for (int i = 0; i < parametersSize; ++i) {
	//
	//			AbstractParameterNode abstractParameterNode = parameters.get(i);
	//			AbstractParameterNode otherParameter = otherParameters.get(i);
	//
	//			if (!abstractParameterNode.isMatch(otherParameter)) {
	//				return false;
	//			}
	//		}
	//
	//		return true;
	//	}

	public int getElementsCount() {

		return fElements.size();
	}

	private void registerChange() { // TODO MO-RE make private

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}

	public TypeOfElement getElement(int index) {

		return fElements.get(index);
	}

	public void shiftElements(List<Integer> indicesOfElements, int shift) {

		ShifterOfListElements.shiftElements(fElements, indicesOfElements, shift);
	}

	public void shiftOneElement(int indexOfElement, int shift) {

		ShifterOfListElements.shiftOneElement(fElements, indexOfElement, shift);
	}

}
