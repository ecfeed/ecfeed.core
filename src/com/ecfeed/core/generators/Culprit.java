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
import java.util.List;

import com.ecfeed.core.model.MethodNode;

public class Culprit {

	private static class OtherData {
		public int fFailureRate;
		
		public OtherData()
		{
			fFailureRate = 0;
		}
		public OtherData(int failureRate)
		{
			fFailureRate = failureRate;
		}
		
		public OtherData makeClone()
		{
			return new Culprit.OtherData(fFailureRate);
		}
	}

	private List<DimensionedString> fTestInput;
	private int fOccurenceCount = 0;
	private int fFailureCount = 0;
	private OtherData fOtherData;
	private MethodNode fMethodNode;

	public Culprit() {
		this(new ArrayList<DimensionedString>(), 0, 0);		
	}

	public Culprit(List<DimensionedString> testInput) {
		this(testInput, 0, 0);		
	}

	public Culprit(List<DimensionedString> testInput, int occurenceCount, int failureCount) {
		this(testInput, null, occurenceCount, failureCount);
	}
	
	public Culprit(List<DimensionedString> testInput, MethodNode methodNode, int occurenceCount, int failureCount) {
		fMethodNode = methodNode;
		fTestInput = testInput;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
		fOtherData = new OtherData();
	}
	
	public Culprit(DimensionedString[] testInput) {
		this(testInput, 0, 0);		
	}

	
	public Culprit(DimensionedString[] testInput, int occurenceCount, int failureCount) {

		List<DimensionedString> testInputList = new ArrayList<DimensionedString>();

		for (DimensionedString DimensionedString : testInput) {
			testInputList.add(DimensionedString);
		}

		fTestInput = testInputList;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
	}	

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("[occurences:");
		sb.append(fOccurenceCount);
		sb.append(", fails:");
		sb.append(fFailureCount);
		sb.append(", failureIndex:");
		sb.append(fOtherData.fFailureRate);
		sb.append(", items:[");

		boolean firstTime = true;

		for (DimensionedString DimensionedString : fTestInput) {

			if (!firstTime) {
				sb.append(", ");
			}

			sb.append(DimensionedString.toString());
			firstTime = false;
		}

		sb.append("]");
		sb.append("]");

		return sb.toString();
	}
	
	public String toTableString() {
		if (fMethodNode != null) {
			StringBuilder builder = new StringBuilder();
			for (DimensionedString element : fTestInput) {
				builder.append("{");
				builder.append(fMethodNode.getMethodParameter(element.fDimension).getName());
				builder.append(", ");
				builder.append(element.getItem());
				builder.append("} ");
			}
			return builder.toString().trim();
		} else {
			return toString();
		}
	}

	public static int compareForSort(Culprit culprit1, Culprit culprit2) {

		if (culprit1.fOtherData.fFailureRate > culprit2.fOtherData.fFailureRate) 
		{
			return -1;
		}

		if (culprit1.fOtherData.fFailureRate < culprit2.fOtherData.fFailureRate) 
		{
			return 1;
		}	
		
		if (culprit1.fOccurenceCount > culprit2.fOccurenceCount)
		{
			return -1;
		}
		
		if (culprit1.fOccurenceCount < culprit2.fOccurenceCount)
		{
			return 1;
		}

		int compareSize = compareByTestInputSize(culprit1, culprit2);
		if (compareSize != 0)
		{
			return compareSize;
		}  
		
		int compareResult = compareResult(culprit1, culprit2);
		if(compareResult != 0)
		{
			return compareResult;
		}
		
		int compOccur = compareByOccurenceCountForSort(culprit1, culprit2);
		if(compOccur>0)
		{
			return compOccur;
		}
		
		return 0;
	}

	public static int compareByFailureCountForSort(Culprit culprit1, Culprit culprit2) {
		
		if (culprit1.fFailureCount > culprit2.fFailureCount)
		{
			return -1;
		}
		
		if (culprit1.fFailureCount < culprit2.fFailureCount)
		{
			return 1;
		}
		if (culprit1.fOccurenceCount > culprit2.fOccurenceCount)
		{
			return -1;
		}
		
		if (culprit1.fOccurenceCount < culprit2.fOccurenceCount)
		{
			return 1;
		}
		
		int compareSize = compareByTestInputSize(culprit1, culprit2);
		if (compareSize != 0)
		{
			return compareSize;
		}
		
		int compareResult = compareResult(culprit1, culprit2);
		if (compareResult != 0)
		{
			return compareResult;
		}
		
		return 0;
	}
	
	public static int compareByOccurenceCountForSort(Culprit culprit1, Culprit culprit2)
	{
		if (culprit1.fOccurenceCount > culprit2.fOccurenceCount)
		{
			return -1;
		}
		
		if (culprit1.fOccurenceCount < culprit2.fOccurenceCount)
		{
			return 1;
		}
		
		if (culprit1.fOtherData.fFailureRate > culprit2.fOtherData.fFailureRate) 
		{
			return -1;
		}

		if (culprit1.fOtherData.fFailureRate < culprit2.fOtherData.fFailureRate) 
		{
			return 1;
		}
		
		int compareSize = compareByTestInputSize(culprit1, culprit2);
		if (compareSize != 0)
		{
			return compareSize;
		}
		
		int compareResult = compareResult(culprit1, culprit2);
		if (compareResult != 0)
		{
			return compareResult;
		}
		
		return 0;
	}

	private static int compareByTestInputSize(Culprit culprit1, Culprit culprit2) {
		
		if (culprit1.fTestInput.size() > culprit2.fTestInput.size())
		{
			return -1;
		}
		
		if (culprit1.fTestInput.size() < culprit2.fTestInput.size())
		{
			return 1;
		}
		
		return 0;
	}

	private static int compareResult(Culprit culprit1, Culprit culprit2) {
		int compareResult;
		int size = culprit1.fTestInput.size();

		for (int index = 0; index < size; index++) {

			DimensionedString DimensionedString1 = culprit1.fTestInput.get(index);
			DimensionedString DimensionedString2 = culprit2.fTestInput.get(index);

			compareResult = DimensionedString.compareForSort(DimensionedString1, DimensionedString2);
			
			if (compareResult != 0) {
				return compareResult;
			}
		}
		return 0;
	}

	public int getOccurenceCount() {
		return fOccurenceCount;
	}

	public int getFailureCount() {
		return fFailureCount;
	}

	public int getFailureIndex() {
		return fOtherData.fFailureRate;
	}

	public void setFailureRate(int rate) {
		fOtherData.fFailureRate = rate;
	}	

	public List<DimensionedString> getItem() {
		return fTestInput;
	}

	public void incrementFailures(int failureCount) {
		fFailureCount += failureCount;
	}

	public void incrementOccurenceCount(int occurenceCount) {
		fOccurenceCount += occurenceCount;
	}

	public void aggregateOccurencesAndFailures(Culprit culpritToAggregate) {

		incrementOccurenceCount(culpritToAggregate.getOccurenceCount());
		incrementFailures(culpritToAggregate.getFailureCount());
	}

	public boolean isTupleMatch(Culprit other) {

		if(fTestInput.size() != other.fTestInput.size()) {
			return false;
		}

		for(int index = 0; index < fTestInput.size(); index++) {
			if(!fTestInput.get(index).equals(other.fTestInput.get(index))) {
				return false;
			}
		}
		return true;
	}

	public boolean isBasicMatch(Culprit other) {

		if (other.fOccurenceCount != fOccurenceCount) {
			return false;
		}

		if (other.fFailureCount != fFailureCount) {
			return false;
		}

		if (!isTupleMatch(other)) {
			return false;
		}

		return true;
	}

	public Culprit makeClone() {

		Culprit culprit = new Culprit(fTestInput, fOccurenceCount, fFailureCount);
		culprit.fOtherData = fOtherData.makeClone();
		return culprit;
	}

}

