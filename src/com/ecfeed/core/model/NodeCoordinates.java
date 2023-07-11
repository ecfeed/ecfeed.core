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

public class NodeCoordinates {

	private IAbstractNode fNode;
	private int fIndex;

	public NodeCoordinates(IAbstractNode dropNode, int dropIndex) {

		fNode = dropNode;
		fIndex = dropIndex;
	}

	public IAbstractNode getNode() {
		return fNode;
	}

	public int getIndex() {
		return fIndex;
	}

	public boolean isMatch(NodeCoordinates otherCoordinates) {

		if (!fNode.equals(otherCoordinates.fNode)) {
			return false;
		}

		if (fIndex != otherCoordinates.fIndex) {
			return false;
		}

		return true;
	}

}
