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

public class ConversionPartMatcher {

	public static List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> createListOfSimilarStringPairs(
			List<ParameterConversionItemPartForRaw> items1, 
			List<ParameterConversionItemPartForRaw> items2, 
			double minimumSimilarityIndex) {

		checkForAbsenceOfDuplicates(items1);
		checkForAbsenceOfDuplicates(items2);

		List<SimilarityItem> similarityItems = 
				createSortedSimilarityList(items1, items2, minimumSimilarityIndex);

		List<SimilarityItem> compressedSimilarityItems = 
				createListOfSimilarItemsWithoutDuplicates(similarityItems);

		List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> resultListOfPairs = 
				createListOfStringPairs(compressedSimilarityItems);

		return resultListOfPairs;
	}

	private static List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> createListOfStringPairs(
			List<SimilarityItem> similarityItems) {

		List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> pairs = new ArrayList<>();

		for (SimilarityItem similarityItem : similarityItems) {

			ParameterConversionItemPartForRaw item1 = similarityItem.getItem1();
			ParameterConversionItemPartForRaw item2 = similarityItem.getItem2();

			Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw> pair = new Pair<>(item1, item2);

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
					topSimilarityItem.getItem1(), 
					topSimilarityItem.getItem2(), 
					similarityItems);
		}

		return resultSimilarityItems;
	}

	private static void removeDuplicatedStrings(
			ParameterConversionItemPartForRaw string1, 
			ParameterConversionItemPartForRaw string2,
			List<SimilarityItem> similarItems) {

		similarItems.removeIf(new PredicateIsMatchForFirstItem(string1));
		similarItems.removeIf(new PredicateIsMatchForSecondItem(string2));
	}

	private static void removePairsWithTooLowSimilarity(double minimumIndex, List<SimilarityItem> similarItems) {

		similarItems.removeIf(new PredicateSimilarityIndexSmallerThanMinimum(minimumIndex));

	}

	private static void checkForAbsenceOfDuplicates(List<ParameterConversionItemPartForRaw> items) {

		sortStringsAlphabetically(items);

		for (int index = 0; index < items.size() - 1; index++) {

			ParameterConversionItemPartForRaw currentItem = items.get(index);
			ParameterConversionItemPartForRaw nextItem = items.get(index + 1);

			if (currentItem.isMatch(nextItem)) {
				ExceptionHelper.reportRuntimeException("Non unique items in list.");
			}
		}
	}

	private static void sortStringsAlphabetically(List<ParameterConversionItemPartForRaw> strings) {

		Comparator<ParameterConversionItemPartForRaw> comparatorByAlphaOrder = new Comparator<ParameterConversionItemPartForRaw>() {

			@Override
			public int compare(ParameterConversionItemPartForRaw string1, ParameterConversionItemPartForRaw string2) {

				return string1.compareTo(string2);
			}
		};

		Collections.sort(strings, comparatorByAlphaOrder);
	}


	private static class PredicateIsMatchForFirstItem implements Predicate<Object> {

		private ParameterConversionItemPartForRaw fItem;

		public PredicateIsMatchForFirstItem(ParameterConversionItemPartForRaw item) {

			fItem = item;
		}

		@Override
		public boolean test(Object testedObject) {

			SimilarityItem similarityItem = (SimilarityItem)testedObject;

			ParameterConversionItemPartForRaw item1 = similarityItem.getItem1();

			return item1.isMatch(fItem);
		}

	}

	private static class PredicateIsMatchForSecondItem implements Predicate<Object> {

		private ParameterConversionItemPartForRaw fItem;

		public PredicateIsMatchForSecondItem(ParameterConversionItemPartForRaw item) {

			fItem = item;
		}

		@Override
		public boolean test(Object testedObject) {

			SimilarityItem similarityItem = (SimilarityItem)testedObject;

			ParameterConversionItemPartForRaw item2 = similarityItem.getItem2();

			return item2.isMatch(fItem);
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
			List<ParameterConversionItemPartForRaw> items1, 
			List<ParameterConversionItemPartForRaw> items2,
			double minimumSimilarityIndex) {

		List<SimilarityItem> similarityItems = createListOfSimilarityItems(items1, items2);

		removePairsWithTooLowSimilarity(minimumSimilarityIndex, similarityItems);

		sortSimilarityItemsByGreatestSimilarityAndAlphabeticallyByFirstString(similarityItems);

		return similarityItems;
	}

	private static List<SimilarityItem> createListOfSimilarityItems(
			List<ParameterConversionItemPartForRaw> items1, 
			List<ParameterConversionItemPartForRaw> items2) {

		List<SimilarityItem> similarityItems = new ArrayList<>();

		int count1 = items1.size();
		int count2 = items2.size();

		for (int index1 = 0; index1 < count1; index1++) {
			for (int index2 = 0; index2 < count2; index2++) {

				ParameterConversionItemPartForRaw item1 = items1.get(index1);
				ParameterConversionItemPartForRaw item2 = items2.get(index2);

				double similarityIndex = StringSimilarityCalculator.calculateSimilarityIndex(item1, item2);

				similarityItems.add(new SimilarityItem(item1, item2, similarityIndex));
			}
		}
		return similarityItems;
	}

	private static void sortSimilarityItemsByGreatestSimilarityAndAlphabeticallyByFirstString(List<SimilarityItem> similarityDataList) {

		Comparator<SimilarityItem> comparatorBySimilarityIndex = new Comparator<ConversionPartMatcher.SimilarityItem>() {

			@Override
			public int compare(SimilarityItem o1, SimilarityItem o2) {

				Double index1 = o1.getSimilarityIndex();
				Double index2 = o2.getSimilarityIndex();

				if (index1 == index2) {

					ParameterConversionItemPartForRaw item1 = o1.getItem1();
					ParameterConversionItemPartForRaw item2 = o1.getItem2();

					return item1.compareTo(item2);
				}

				return (-1) * index1.compareTo(index2);
			}
		};

		Collections.sort(similarityDataList, comparatorBySimilarityIndex);
	}

	private static class SimilarityItem { // TODO DE-NO template class ?

		private ParameterConversionItemPartForRaw fItem1;
		private ParameterConversionItemPartForRaw fItem2;

		double fSimilarityIndex;

		public SimilarityItem(ParameterConversionItemPartForRaw item1, ParameterConversionItemPartForRaw item2, double correlationResult) {
			fItem1 = item1;
			fItem2 = item2;
			fSimilarityIndex = correlationResult;
		}

		public double getSimilarityIndex() {
			return fSimilarityIndex;
		}

		public ParameterConversionItemPartForRaw getItem1() {
			return fItem1;
		}

		public ParameterConversionItemPartForRaw getItem2() {
			return fItem2;
		}

		@Override
		public String toString() {

			return " " + fSimilarityIndex + "  " + fItem1 + "   " + fItem2;
		}

	}

}
