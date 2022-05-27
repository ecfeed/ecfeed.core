/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class StringMatcher {

	public static void createListOfSimilarStringPairs(
			List<String> strings1, List<String> strings2, double minimumIndex) {
		
		// TODO check if strings are unique withing tables

		List<SimilarityData> similarPairs = createListOfSimilarPairs(strings1, strings2);
		
		similarPairs.removeIf(new PredicateGreaterThanMinimumIndex(minimumIndex));

		// extract pairs of strings with highest similarity index then remove 
	}

	private static class PredicateGreaterThanMinimumIndex implements Predicate<Object> {
		
		double fMinimumIndex = 0;

		public PredicateGreaterThanMinimumIndex(double minimumIndex) {
			
			fMinimumIndex = minimumIndex;
		}
		
		@Override
		public boolean test(Object testedObject) {
			
			SimilarityData similarityData = (SimilarityData)testedObject;
			
			if (similarityData.getSimilarityIndex() < fMinimumIndex) {
				return false;
			}
			
			return true;
		}
		
	}
	
	private static List<SimilarityData> createListOfSimilarPairs(List<String> strings1, List<String> strings2) {

		List<SimilarityData> similarityDataList = new ArrayList<>();

		int count1 = strings1.size();
		int count2 = strings2.size();

		for (int index1 = 0; index1 < count1; index1++) {
			for (int index2 = 0; index2 < count2; index2++) {

				String str1 = strings1.get(index1);
				String str2 = strings2.get(index2);

				double similarityIndex = StringSimilarityCalculator.calculateSimilarityIndex(str1, str2);

				similarityDataList.add(new SimilarityData(str1, str2, similarityIndex));
			}
		}

		sortSimilarityData(similarityDataList);

		return similarityDataList;
	}

	private static void sortSimilarityData(List<SimilarityData> similarityDataList) {

		Comparator<SimilarityData> comparatorBySimilarityIndex = new Comparator<StringMatcher.SimilarityData>() {

			@Override
			public int compare(SimilarityData o1, SimilarityData o2) {

				Double result1 = o1.getSimilarityIndex();
				Double result2 = o2.getSimilarityIndex();

				return (-1) * result1.compareTo(result2);
			}
		};

		Collections.sort(similarityDataList, comparatorBySimilarityIndex);
	}

	private static class SimilarityData {

		String fStr1;
		String fStr2;

		double fSimilarityIndex;

		public SimilarityData(String str1, String str2, double correlationResult) {
			fStr1 = str1;
			fStr2 = str2;
			fSimilarityIndex = correlationResult;
		}

		public double getSimilarityIndex() {
			return fSimilarityIndex;
		}

		@Override
		public String toString() {

			return " " + fSimilarityIndex + "  " + fStr1 + "   " + fStr2;
		}

	}

}
