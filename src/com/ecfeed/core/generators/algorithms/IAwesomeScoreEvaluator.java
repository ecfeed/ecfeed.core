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

public interface IAwesomeScoreEvaluator<E> {

	public void initialize(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException;
	public int getScoreForTestCase(SortedMap<Integer, E> testCase);
	public int getScore(SortedMap<Integer, E> tuple);
	public void update(SortedMap<Integer, E> testCase);
	public int getCountOfInitialNTuples();
	public int getCountOfRemainingNTuples();
	
	public int getCountOfTuples(SortedMap<Integer, E> tuple);
	public boolean contains(SortedMap<Integer, E> tuple);
}