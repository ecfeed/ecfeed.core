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

	public static List<Pair<String,String>> createListOfSimilarStringPairs(
			List<String> strings1, 
			List<String> strings2, 
			double minimumSimilarityIndex) {

		checkForAbsenceOfDuplicates(strings1);
		checkForAbsenceOfDuplicates(strings2);

		List<SimilarityItem> similarityItems = 
				createSortedSimilarityList(strings1, strings2, minimumSimilarityIndex);

		List<SimilarityItem> compressedSimilarityItems = 
				createListOfSimilarItemsWithoutDuplicates(similarityItems);

		List<Pair<String,String>> resultListOfPairs = 
				createListOfStringPairs(compressedSimilarityItems);

		return resultListOfPairs;
	}

	private static List<Pair<String, String>> createListOfStringPairs(
			List<SimilarityItem> similarityItems) {

		List<Pair<String, String>> pairs = new ArrayList<>();

		for (SimilarityItem similarityItem : similarityItems) {

			String string1 = similarityItem.getString1();
			String string2 = similarityItem.getString2();

			Pair<String,String> pair = new Pair<>(string1, string2);

			pairs.add(pair);
		}

		return pairs;
	}

	private static List<SimilarityItem> createListOfSimilarItemsWithoutDuplicates(
			List<SimilarityItem> similarityItems) {

		List<SimilarityItem> resultSimilarityItems = new ArrayList<>();

		while(similarityItems.size() > 0) {

			SimilarityItem topSimilarityItem = similarityItems.get(0);

			resultSimilarityItems.add(topSimilarityItem);

			removeDuplicatedStrings(
					topSimilarityItem.getString1(), 
					topSimilarityItem.getString2(), 
					similarityItems);
		}

		return resultSimilarityItems;
	}

	private static void removeDuplicatedStrings(
			String string1, 
			String string2,
			List<SimilarityItem> similarItems) {

		similarItems.removeIf(new PredicateFirstStringUsed(string1));
		similarItems.removeIf(new PredicateSecondStringUsed(string2));
	}

	private static void removePairsWithTooLowSimilarity(double minimumIndex, List<SimilarityItem> similarItems) {

		similarItems.removeIf(new PredicateSimilarityIndexSmallerThanMinimum(minimumIndex));

	}

	private static void checkForAbsenceOfDuplicates(List<String> strings) {

		sortStringsAlphabetically(strings);

		for (int index = 0; index < strings.size() - 1; index++) {

			String currentStr = strings.get(index);
			String nextStr = strings.get(index + 1);

			if (StringHelper.isEqual(currentStr, nextStr)) {
				ExceptionHelper.reportRuntimeException("Non unique strings in string list.");
			}
		}

	}

	private static void sortStringsAlphabetically(List<String> strings) {
		Comparator<String> comparatorByAlphaOrder = new Comparator<String>() {

			@Override
			public int compare(String string1, String string2) {

				return string1.compareTo(string2);
			}
		};

		Collections.sort(strings, comparatorByAlphaOrder);
	}


	private static class PredicateFirstStringUsed implements Predicate<Object> {

		private String fString1ToRemove;

		public PredicateFirstStringUsed(String string1ToRemove) {

			fString1ToRemove = string1ToRemove;
		}

		@Override
		public boolean test(Object testedObject) {

			SimilarityItem similarityItem = (SimilarityItem)testedObject;

			String firstString = similarityItem.getString1();

			if (StringHelper.isEqual(firstString, fString1ToRemove)) {
				return true;
			}

			return false;
		}

	}

	private static class PredicateSecondStringUsed implements Predicate<Object> {

		private String fString1ToRemove;

		public PredicateSecondStringUsed(String string1ToRemove) {

			fString1ToRemove = string1ToRemove;
		}

		@Override
		public boolean test(Object testedObject) {

			SimilarityItem similarityItem = (SimilarityItem)testedObject;

			String firstString = similarityItem.getString2();

			if (StringHelper.isEqual(firstString, fString1ToRemove)) {
				return true;
			}

			return false;
		}

	}

	private static class PredicateSimilarityIndexSmallerThanMinimum implements Predicate<Object> {

		double fMinimumIndex = 0;

		public PredicateSimilarityIndexSmallerThanMinimum(double minimumIndex) {

			fMinimumIndex = minimumIndex;
		}

		@Override
		public boolean test(Object testedObject) {

			SimilarityItem similarityItems = (SimilarityItem)testedObject;

			if (similarityItems.getSimilarityIndex() < fMinimumIndex) {
				return true;
			}

			return false;
		}

	}

	private static List<SimilarityItem> createSortedSimilarityList(
			List<String> strings1, 
			List<String> strings2,
			double minimumSimilarityIndex) {

		List<SimilarityItem> similarityItems = createListOfSimilarityItems(strings1, strings2);

		removePairsWithTooLowSimilarity(minimumSimilarityIndex, similarityItems);

		sortSimilarityItemsByGreatestSimilarityAndAlphabeticallyByFirstString(similarityItems);

		return similarityItems;
	}

	private static List<SimilarityItem> createListOfSimilarityItems(List<String> strings1, List<String> strings2) {
		List<SimilarityItem> similarityItems = new ArrayList<>();

		int count1 = strings1.size();
		int count2 = strings2.size();

		for (int index1 = 0; index1 < count1; index1++) {
			for (int index2 = 0; index2 < count2; index2++) {

				String str1 = strings1.get(index1);
				String str2 = strings2.get(index2);

				double similarityIndex = StringSimilarityCalculator.calculateSimilarityIndex(str1, str2);

				similarityItems.add(new SimilarityItem(str1, str2, similarityIndex));
			}
		}
		return similarityItems;
	}

	private static void sortSimilarityItemsByGreatestSimilarityAndAlphabeticallyByFirstString(List<SimilarityItem> similarityDataList) {

		Comparator<SimilarityItem> comparatorBySimilarityIndex = new Comparator<StringMatcher.SimilarityItem>() {

			@Override
			public int compare(SimilarityItem o1, SimilarityItem o2) {

				Double result1 = o1.getSimilarityIndex();
				Double result2 = o2.getSimilarityIndex();

				if (result1 == result2) {

					String str1 = o1.getString1();
					String str2 = o1.getString2();

					return str1.compareTo(str2);
				}

				return (-1) * result1.compareTo(result2);
			}
		};

		Collections.sort(similarityDataList, comparatorBySimilarityIndex);
	}

	private static class SimilarityItem {

		private String fStr1;
		private String fStr2;

		double fSimilarityIndex;

		public SimilarityItem(String str1, String str2, double correlationResult) {
			fStr1 = str1;
			fStr2 = str2;
			fSimilarityIndex = correlationResult;
		}

		public double getSimilarityIndex() {
			return fSimilarityIndex;
		}

		public String getString1() {
			return fStr1;
		}

		public String getString2() {
			return fStr2;
		}

		@Override
		public String toString() {

			return " " + fSimilarityIndex + "  " + fStr1 + "   " + fStr2;
		}

	}

}
