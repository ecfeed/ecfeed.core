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
import java.util.function.Predicate;

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

	public boolean removeElement(TypeOfElement parameter) {

		boolean result = fElements.removeIf(e -> e.equals(parameter));
		registerChange();
		return result;
	}

	public boolean removeIf(Predicate<TypeOfElement> filter) {

		boolean result = fElements.removeIf(filter);

		if (result) {
			registerChange();
		}

		return result;
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

	public int getElementsCount() {

		return fElements.size();
	}

	private void registerChange() {

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
