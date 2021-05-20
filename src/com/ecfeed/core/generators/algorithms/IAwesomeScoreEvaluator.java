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
import java.util.SortedMap;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.IntegerHolder;

public interface IAwesomeScoreEvaluator<E> {

	public void initialize(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException;
	//
	//	public int getInitialNTupleCount();
		public int getCurrentNTupleCount();
	//
	//	public int getScore(List<E> tuple);
	//	public void updateScores(List<E> test);

	public int calculateScoreForNTuple(SortedMap<Integer, E> nTuple);

	public boolean contains(SortedMap<Integer, E> tmpTuple);

	public int getCountOfTuple(SortedMap<Integer, E> tuple);

	public void update(SortedMap<Integer, E> affectingTuple, IntegerHolder outRemainingTuplesCount);


}