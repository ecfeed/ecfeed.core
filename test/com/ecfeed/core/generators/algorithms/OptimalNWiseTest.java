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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.algorithms.OptimalNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.Tuples;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;
import com.google.common.collect.Sets;

public class OptimalNWiseTest extends NWiseAlgorithmTest {

	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	private final Collection<IConstraint<String>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<String>>();

	@Test
	public void testCorrectness() {
		testCorrectness(OptimalNWiseAlgorithm.class);
	}

	@Test
	public void testConstraints() {
		testConstraints(OptimalNWiseAlgorithm.class);
	}

	@Test
	public void testSize() {
		try {
			for (int variables : new int[] { 1, 2, 5 }) {
				for (int choices : new int[] { 1, 2, 5 }) {
					for (int n = 1; n <= variables; n++) {
						List<List<String>> input = GeneratorTestUtils.prepareInput(variables, choices);
						Collection<IConstraint<String>> constraints = EMPTY_CONSTRAINTS;
						IAlgorithm<String> algorithm = new OptimalNWiseAlgorithm<String>(n, 100);

						algorithm.initialize(input, new HomebrewConstraintEvaluator<>(constraints), new SimpleProgressMonitor());
						int generatedDataSize = GeneratorTestUtils.algorithmResult(algorithm).size();
						int referenceDataSize = referenceResult(input, n).size();
						assertTrue(Math.abs(generatedDataSize - referenceDataSize) <= referenceDataSize / 30);
					}
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

	private Set<List<String>> referenceResult(List<List<String>> input, int n) throws GeneratorException {
		List<Set<String>> referenceInput = GeneratorTestUtils.referenceInput(input);
		Set<List<String>> cartesianProduct = Sets.cartesianProduct(referenceInput);
		Set<List<String>> referenceResult = new HashSet<List<String>>();
		Set<List<String>> remainingTuples = getAllTuples(input, n);
		for (int k = maxTuples(input, n); k > 0; k--) {
			for (List<String> vector : cartesianProduct) {
				Set<List<String>> originalTuples = getTuples(vector, n);
				originalTuples.retainAll(remainingTuples);
				if (originalTuples.size() == k) {
					referenceResult.add(vector);
					remainingTuples.removeAll(originalTuples);
				}
			}
		}
		return referenceResult;
	}

	protected int maxTuples(List<List<String>> input, int n) {
		return (new Tuples<List<String>>(input, n)).getAll().size();
	}

	protected Set<List<String>> getTuples(List<String> vector, int n) {
		return (new Tuples<String>(vector, n)).getAll();
	}

}
