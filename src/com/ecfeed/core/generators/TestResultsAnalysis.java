/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestResultsAnalysis {

	private List<Culprit> fCulprits = new ArrayList<Culprit>();

	public void aggregateCulprit(Culprit culpritToAggregate) {

		Culprit extCulpritFromList = findCulpritByTuple(culpritToAggregate);

		if (extCulpritFromList == null) {
			fCulprits.add(culpritToAggregate);
		} else {
			extCulpritFromList.aggregateOccurencesAndFailures(culpritToAggregate);
		}
	}

	public Culprit findCulpritByTuple(Culprit culpritWithTupleToFind) {

		for (Culprit extCulprit: fCulprits) {
			if (extCulprit.isTupleMatch(culpritWithTupleToFind)) {
				return extCulprit;
			}
		}
		return null;
	}

	public List<Culprit> getCulpritList(){
		
		return fCulprits;
	}

	public int getCulpritCount() {

		return fCulprits.size();
	}

	public Culprit getCulprit(int index) {

		Culprit culprit = fCulprits.get(index);

		return culprit.makeClone();
	}

	public boolean containsCulprit(Culprit culprit) {

		int culpritCount = fCulprits.size();

		for (int culpritIndex = 0; culpritIndex < culpritCount; culpritIndex++) {
			Culprit culpritFromList = fCulprits.get(culpritIndex);

			if (culprit.isBasicMatch(culpritFromList)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("Test results analysis:\n");

		for (Culprit extCulprit : fCulprits) {

			sb.append("  ");
			sb.append(extCulprit.toString());
			sb.append("\n");
		}

		sb.append("-------\n");

		return sb.toString();
	}

	public void calculateFailureRates() {

		for (Culprit culprit : fCulprits) {
			int occurences = culprit.getOccurenceCount();
			int failures = culprit.getFailureCount();

			int failsByOccurs = 100 * failures / occurences;

			culprit.setFailureRate(failsByOccurs); 	
		}
		Collections.sort(fCulprits, new FailureRateComparator());
	}

	public void SortColumnInput(String dir, String name)
	{
		if (dir == "SWT.UP"){
			if (name == "Occurences"){
				Collections.sort(fCulprits, new OccurenceComparator());
			} if (name == "Fails"){
				Collections.sort(fCulprits, new FailureComparator());
			} if (name == "Failure rate"){
				Collections.sort(fCulprits, new FailureRateComparator());
			}
		} else {
			if (name == "Occurences"){
				Collections.sort(fCulprits, new OccurenceComparatorDecreasing());
			} if (name == "Fails"){
				Collections.sort(fCulprits, new FailureComparatorDecreasing());
			} if (name == "Failure rate"){
				Collections.sort(fCulprits, new FailureIndexComparatorDecreasing());
			}
		}
	}

	class FailureComparator implements Comparator<Culprit>{

		@Override
		public int compare(Culprit culprit1, Culprit culprit2){
			
			return Culprit.compareByFailureCountForSort(culprit1, culprit2);
		}
	}

	class OccurenceComparator implements Comparator<Culprit>{

		@Override
		public int compare(Culprit culprit1, Culprit culprit2){
			
			return Culprit.compareByOccurenceCountForSort(culprit1, culprit2);
		}
	}

	class FailureRateComparator implements Comparator<Culprit> {

		@Override
		public int compare(Culprit culprit1, Culprit culprit2) {

			return Culprit.compareForSort(culprit1, culprit2);
		}
	}

	class FailureComparatorDecreasing implements Comparator<Culprit>{

		@Override
		public int compare(Culprit culprit1, Culprit culprit2){
			
			return (Culprit.compareByFailureCountForSort(culprit1, culprit2) )* -1;
		}
	}

	class OccurenceComparatorDecreasing implements Comparator<Culprit>{

		@Override
		public int compare(Culprit culprit1, Culprit culprit2){
			
			return (Culprit.compareByOccurenceCountForSort(culprit1, culprit2)) * -1;
		}
	}

	class FailureIndexComparatorDecreasing implements Comparator<Culprit> {

		@Override
		public int compare(Culprit culprit1, Culprit culprit2) {

			return (Culprit.compareForSort(culprit1, culprit2)) * -1;
		}
	}
}

