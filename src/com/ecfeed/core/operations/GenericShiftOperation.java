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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericShiftOperation extends AbstractModelOperation {

	private List<AbstractNode> fToBeShifted;
	private int fShiftSize;
	private List<? extends AbstractNode> fCollection;

	public GenericShiftOperation(List<? extends AbstractNode> collection, AbstractNode shifted, boolean up, IExtLanguageManager extLanguageManager){
		
		this(collection, Arrays.asList(new AbstractNode[]{shifted}), up, extLanguageManager);
	}

	public GenericShiftOperation(
			List<? extends AbstractNode> collection, List<? extends AbstractNode> shifted, boolean up, IExtLanguageManager extLanguageManager){
		
		this(collection, shifted, 0, extLanguageManager);
		
		fShiftSize = minAllowedShift(shifted, up);
	}

	public GenericShiftOperation(List<? extends AbstractNode> collection, List<? extends AbstractNode> shifted, int shift, IExtLanguageManager extLanguageManager){
		super(OperationNames.MOVE, extLanguageManager);
		shift = shiftAllowed(shifted, shift) ? shift : 0;
		fToBeShifted = new ArrayList<>(shifted);
		fCollection = collection;
		fShiftSize = shift;
	}

	@Override
	public void execute() {

		setNodesToSelect();
		shiftElements(fCollection, indices(fCollection, fToBeShifted), fShiftSize);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericShiftOperation(fCollection, fToBeShifted, -fShiftSize, getExtLanguageManager());
	}

	public int getShift(){
		return fShiftSize;
	}

	protected List<? extends AbstractNode> getCollection(){
		return fCollection;
	}

	protected List<? extends AbstractNode> getShiftedElements(){
		return fToBeShifted;
	}

	protected void setShift(int shift){
		fShiftSize = shift;
	}

	protected int minAllowedShift(List<? extends AbstractNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		return shiftAllowed(shifted, shift) ? shift : 0; 
	}

	protected boolean haveSameParent(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0)return true;
		IAbstractNode parent = shifted.get(0).getParent();
		for(IAbstractNode node : shifted){
			if(node.getParent() != parent){
				return false;
			}
		}
		return true;
	}

	protected boolean areInstancesOfSameClass(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0) return true;
		Class<?> _class = shifted.get(0).getClass();
		for(AbstractNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}

	protected AbstractNode borderNode(List<? extends AbstractNode> nodes, int shift){
		return shift < 0 ? minIndexNode(nodes) : maxIndexNode(nodes);
	}

	protected List<Integer> indices(List<?> collection, List<?> elements){
		List<Integer> indices = new ArrayList<>();
		for(Object element : elements){
			indices.add(collection.indexOf(element));
		}
		return indices;
	}

	protected void shiftElements(List<?> list, List<Integer> indices, int shift){
		Collections.sort(indices);
		if(shift > 0){
			Collections.reverse(indices);
		}

		for(int i = 0; i < indices.size(); i++){
			shiftElement(list, indices.get(i), shift);
		}
	}
	protected void shiftElement(List<?> list, int index, int shift) {

		int minIndex = Math.min(index, index+shift);
		int maxIndex = Math.max(index, index+shift) + ((shift < 0) ? 1:0);
		List<?> rotated = list.subList(minIndex, (shift > 0) ? maxIndex + 1 : maxIndex);
		int rotation = (shift>0) ? -1 : 1;
		Collections.rotate(rotated, rotation);
	}

	protected boolean shiftAllowed(List<? extends AbstractNode> shifted, int shift){
		if(areInstancesOfSameClass(shifted) == false){
			return false;
		}
		if(haveSameParent(shifted) == false){
			return false;
		}
		if(shift == 0){
			return false;
		}
		int newIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getMyIndex() + shift : -1;
		return newIndex >= 0 && newIndex < shifted.get(0).getMaxIndex();
	}

	private AbstractNode minIndexNode(List<? extends AbstractNode> nodes){
		if(nodes.size() == 0) return null;
		AbstractNode minIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			minIndexNode = node.getMyIndex() < minIndexNode.getMyIndex() ? node : minIndexNode; 
		}
		return minIndexNode;
	}

	private AbstractNode maxIndexNode(List<? extends AbstractNode> nodes){
		if(nodes.size() == 0) return null;
		AbstractNode maxIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			maxIndexNode = node.getMyIndex() > maxIndexNode.getMyIndex() ? node : maxIndexNode; 
		}
		return maxIndexNode;
	}

	private void setNodesToSelect() {
		

		setNodesToSelect(fToBeShifted);
	}

}
