/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.algorithms;

import java.util.List;

import com.ecfeed.core.model.ChoiceNode;


public class ComparableChoiceTuple implements Comparable<ComparableChoiceTuple> {

	private List<ChoiceNode> fTuple;

	ComparableChoiceTuple(List<ChoiceNode> tuple) {
		fTuple = tuple;
	}

	@Override
	public int compareTo(ComparableChoiceTuple otherTuple) {

		if (fTuple.size() < otherTuple.fTuple.size()) {
			return -1;
		}

		if (fTuple.size() > otherTuple.fTuple.size()) {
			return 1;
		}

		return compareContentTo(otherTuple);
	}

	private int compareContentTo(ComparableChoiceTuple otherTuple) {

		int tupleSize = fTuple.size();

		for (int index = 0; index < tupleSize; index++ ) {

			ChoiceNode thisChoice = this.fTuple.get(index);
			ChoiceNode otherChoice = otherTuple.fTuple.get(index);

			int result = thisChoice.getName().compareTo(otherChoice.getName());

			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	@Override
	public String toString() {
		return fTuple.toString();
	}

}
