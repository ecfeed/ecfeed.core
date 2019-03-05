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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;

public class IterableTestSubinput<E> implements Iterable<List<E>>{

	List<List<E>> fTestInput;	
	List<Integer> fDimensionsToIterate;

	public IterableTestSubinput(List<List<E>> testInput, List<Integer> dimensionsToIterate) throws GeneratorException {

		final String EMPTY_DIM_LIST = "Empty list of dimensions.";

		if (dimensionsToIterate == null) {
			GeneratorException.report(EMPTY_DIM_LIST);
		}

		if (dimensionsToIterate.size() == 0) {
			GeneratorException.report(EMPTY_DIM_LIST);
		}

		fTestInput = testInput;
		fDimensionsToIterate = dimensionsToIterate;
	}

	@Override
	public Iterator<List<E>> iterator() {
		Iterator<List<E>> it = new Iterator<List<E>>() {

			private List<Integer> fCurrentIndexes = createInitialIndexes();
			private List<Integer> fMaxIndexes = createMaxIndexes();

			@Override
			public boolean hasNext() {

				int last = fCurrentIndexes.size() - 1;

				int currentIndex = fCurrentIndexes.get(last);
				int maxIndex = fMaxIndexes.get(last);

				if (currentIndex <= maxIndex) {
					return true;
				}

				return false;
			}

			@Override
			public List<E> next() {
				List<E> result = getCurrentValues();
				incrementIndexes();
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private List<Integer> createInitialIndexes() {

				List<Integer> indexes = new ArrayList<Integer>();

				for (int cnt = 0; cnt < fDimensionsToIterate.size(); cnt++) {
					indexes.add(0);
				}

				return indexes;
			}

			private List<Integer> createMaxIndexes() {

				List<Integer> indexes = new ArrayList<Integer>();

				for (Integer dimension : fDimensionsToIterate) {
					List<E> values = fTestInput.get(dimension);

					indexes.add(values.size() -1);
				}

				return indexes;
			}

			private List<E> getCurrentValues() {

				List<E> result = new ArrayList<E>();

				int dimensionsCount = fDimensionsToIterate.size();

				for (int cnt = 0; cnt < dimensionsCount; cnt++) {

					int dimension = fDimensionsToIterate.get(cnt);
					List<E> values = fTestInput.get(dimension);

					int currentIndex = fCurrentIndexes.get(cnt);
					E currentValue = values.get(currentIndex);

					result.add(currentValue);
				}

				return result;
			}

			private void incrementIndexes() {

				int inxSize = fCurrentIndexes.size();

				for (int inx = 0; inx < inxSize; inx++) {

					boolean isLastIndex = (inx == inxSize -1);
					boolean isCarryOver = incrementIndex(inx, isLastIndex);

					if (!isCarryOver) {
						return;
					}
				}
			}

			private boolean incrementIndex(int position, boolean isLastIndex) {

				boolean isCarryOver;

				int currentIndex  = fCurrentIndexes.get(position);
				int maxIndex  = fMaxIndexes.get(position);

				if (currentIndex < maxIndex) {
					currentIndex++;
					isCarryOver = false;

				} else {

					if (isLastIndex) {
						currentIndex++; // increment above the limit
					} else {
						currentIndex = 0;
					}

					isCarryOver = true;
				}

				fCurrentIndexes.set(position, currentIndex);
				return isCarryOver;
			}

		};

		return it;
	}

}
