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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.*;
import com.ecfeed.core.generators.api.IGeneratorArgument;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.AbstractNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.FastNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.OptimalNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.IConstraint;

import static org.junit.Assert.*;

public class CompareNWiseAlgsTest {

	private List<List<Integer>> generateInput(int cnt){
		List<List<Integer>> input = new ArrayList<>();

		for (int i = 0; i < cnt; i++) {
			List<Integer> in1 = new ArrayList<>();
			in1.add(i*2);
			in1.add(i*2+1);
			input.add(in1);
		}

		return input;
	}

	//@Test
	public void compareFastAndOptimalWithGen() {

		List<List<Integer>> input1 = generateInput(12);
		List<List<Integer>> input2 = generateInput(12);

		NWiseGenerator<Integer> nWiseGen = null;
		FastNWiseAlgorithm<Integer> fast = new FastNWiseAlgorithm<>(3, 100);
		try {
			nWiseGen = new NWiseGenerator<>();
			Map<String, IGeneratorArgument> params = new HashMap<>();

			GeneratorArgumentN generatorArgumentN = new GeneratorArgumentN(3);
			params.put(generatorArgumentN.getName(), generatorArgumentN);

			GeneratorArgumentCoverage generatorArgumentCoverage = new GeneratorArgumentCoverage(100);
			params.put(generatorArgumentCoverage.getName(), generatorArgumentCoverage);
			nWiseGen.initialize(input1, new HashSet<IConstraint<Integer>>(), params, null);

			fast.initialize(input2, new HashSet<IConstraint<Integer>>(), null);
		} catch (GeneratorException e) {
			fail("Unexpected exception when initializing.");
			e.printStackTrace();
		}

		try {
			List<List<Integer>> resFast = getAll(fast);
			List<List<Integer>> resOptimal = getAll(nWiseGen);

			assertTrue("Size F: " + resFast.size() + ", O: " + resOptimal.size(), resFast.size() >= resOptimal.size());
			System.out.println(
					"Size F: " + resFast.size() + ", O: " + resOptimal.size());
		} catch (GeneratorException e) {
			fail("Unexpected exception.");
			e.printStackTrace();
		}


	}

	@Test
	public void compareFastAndOptimal() {
		List<List<Integer>> input1 = new ArrayList<>();
		List<List<Integer>> input2 = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			List<Integer> in1 = new ArrayList<>();
			in1.add(i*2);
			in1.add(i*2+1);
			input1.add(in1);

			List<Integer> in2 = new ArrayList<>();
			in2.add(i*2);
			in2.add(i*2+1);
			input2.add(in2);
		}

		FastNWiseAlgorithm<Integer> fast = new FastNWiseAlgorithm<>(3, 100);
		OptimalNWiseAlgorithm<Integer> optimal = new OptimalNWiseAlgorithm<>(3,
				100);

		try {
			fast.initialize(input1, new HashSet<IConstraint<Integer>>(), null);
			optimal.initialize(input2, new HashSet<IConstraint<Integer>>(), null);
		} catch (GeneratorException e) {
			fail("Unexpected exception when initializing the algorithms.");
			e.printStackTrace();
		}

		try {
			long start = System.currentTimeMillis();
			List<List<Integer>> resFast = getAll(fast);
			long mid = System.currentTimeMillis();
			List<List<Integer>> resOptimal = getAll(optimal);
			long end = System.currentTimeMillis();

			assertTrue("Size F: " + resFast.size() + ", O: " + resOptimal.size(), resFast.size() >= resOptimal.size());
			System.out.println(
					"Size F: " + resFast.size() + ", O: " + resOptimal.size());
			System.out.println(
					"Time F: " + (mid - start) + ", O: " + (end - mid));

			System.out.println("Fast: ");
			write(resFast);
			System.out.println("Optimal: ");
			write(resOptimal);

			System.out.println(fast.totalProgress() + " / " + fast.totalWork());
			System.out.println(optimal.totalProgress() + " / " + optimal.totalWork());


			// assertTrue("Time F: " + (mid - start) + ", O: " + (end - mid), (mid - start) <= (end - mid));

		} catch (GeneratorException e) {
			fail("Unexpected exception when initializing algorithms.");
			e.printStackTrace();
		}

	}

	private void write(List<List<Integer>> list) {
		for(List<Integer> item: list) {
			for(Integer val: item)
				System.out.print(val + " ");
			System.out.println();
		}
	}

	private List<List<Integer>> getAll(AbstractNWiseAlgorithm<Integer> alg)
			throws GeneratorException {
		List<List<Integer>> res = new ArrayList<>();

		List<Integer> next = null;
		while ((next = alg.getNext()) != null) {
			res.add(next);
		}

		return res;
	}


	private List<List<Integer>> getAll(AbstractGenerator<Integer> gen)
			throws GeneratorException {
		List<List<Integer>> res = new ArrayList<>();

		List<Integer> next = null;
		while ((next = gen.next()) != null) {
			res.add(next);
		}
		return res;
	}

}
