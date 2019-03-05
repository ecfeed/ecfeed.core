package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.generators.DimensionedItem;

final public class TuplesIterator <E> implements Iterable<List<DimensionedItem<E>>>, Iterator<List<DimensionedItem<E>>> {

	private final List<List<E>> fReferenceArguments;
	private final int fReferenceOrder;
	
	private int fInternalStateIndex = 0;
	
	private TuplesIteratorStructure<E> fInternalState;
	
	private final long fNumberOfTuples;
	private final long fNumberOfCombinations;
	
	public static <E> TuplesIterator<E> create(final List<List<E>> inputArguments, final int inputOrder) {
		validate(inputArguments, inputOrder);
		
		for (List<E> element : inputArguments) {
			if (element.size() == 0) {
				throw new IllegalArgumentException("At least one list of arguments is empty!");
			}
		}
		
		return new TuplesIterator<E>(inputArguments, inputOrder);
	}
	
	public static <E> TuplesIterator<E> createFromList(final List<E> inputArguments, final int inputOrder) {
		validate(inputArguments, inputOrder);
		
		List<List<E>> list = new ArrayList<>();
		for (E element : inputArguments) {
			List<E> localList = new ArrayList<>();
			localList.add(element);
			list.add(localList);
		}
		
		return new TuplesIterator<E>(list, inputOrder);
	}
	
	private static <E> void validate(final List<E> inputArguments, final int inputOrder) {
		if (inputArguments == null) {
			throw new NullPointerException("The list containing arguments is null!");
		} else if (inputArguments.size() == 0) {
			throw new IllegalArgumentException("The list containing arguments is empty!");
		} else if (inputOrder <= 0 || inputOrder > inputArguments.size()) {
			throw new IllegalArgumentException("The N value should be between 1 and the number of arguments!");
		}
	}
	
	private TuplesIterator(final List<List<E>> inputArguments, final int inputOrder) {
		fReferenceArguments = inputArguments;
		fReferenceOrder = inputOrder;
		
		fInternalState = new TuplesIteratorStructure<E>(fReferenceArguments);
		resetCombinationState();
		
		fNumberOfCombinations = calculateCombinations(fReferenceOrder, fReferenceArguments.size());
		fNumberOfTuples = calculateNumberOfTuplets();
	}
	
	@Override
	public Iterator<List<DimensionedItem<E>>> iterator() {
		return this;
	}
	
	@Override
	public boolean hasNext() {
		if (fInternalStateIndex < fNumberOfTuples) {
			return true;
		} else {
			resetIteratorState();
			return false;
		}
	}

	@Override
	public List<DimensionedItem<E>> next() {
		return prepareCurrentStateResults();
	}
	
	private List<DimensionedItem<E>> prepareCurrentStateResults() {
		List<DimensionedItem<E>> generatedResults = new ArrayList<>();
		
		for (int i = 0 ; i < fReferenceArguments.size() ; i++) {
			if (fInternalState.isArgumentEnabled(i)) {
				generatedResults.add(new DimensionedItem<E>(i, fReferenceArguments.get(i).get(fInternalState.getArgumentPosition(i))));
			}
		}
		
		nextState();
		return generatedResults;
	}
	
	private void nextState() {
		
		for (int i = 0 ; i < fReferenceArguments.size() ; i++) {
			if (fInternalState.isArgumentDisabled(i)) {
				continue;
			} else if (fInternalState.incrementArgumentPosition(i) < fInternalState.getArgumentSize(i)) {
				fInternalStateIndex++;
				return;
			} else {
				fInternalState.setEnabled(i, true);
				continue;
			}
		}
		
		fInternalStateIndex++;
		nextCombination();
		return;
	}
	
	private void nextCombination() {
		int index = fReferenceArguments.size() - 1;
		int trailingDisabledParameters = 0;
		
		for ( ; index >= 0 ; trailingDisabledParameters++, index--) {
			if (fInternalState.isArgumentDisabled(index)) {
				break;
			}
		}
		
		if (trailingDisabledParameters >= fReferenceOrder) {
			resetCombinationState();
			return;
		}

		for ( ; index >= 0 ; index--) {
			if (fInternalState.isArgumentEnabled(index)) {
				break;
			} 	
		}
		
		fInternalState.setEnabled(index, false);
		index++;
		
		for (int change = index + 1 + trailingDisabledParameters ; index < fReferenceArguments.size(); index++) {
			fInternalState.setEnabled(index, index < change);
		}		
	}
	
	private void resetCombinationState() {
		for (int i = 0 ; i < fReferenceArguments.size() ; i++) {
			fInternalState.setEnabled(i, i < fReferenceOrder);
		}
	}
	
	private void resetIteratorState() {
		resetCombinationState();
		fInternalStateIndex = 0;
	}
	
	private int calculateNumberOfTuplets() {
		int numberOfTuplets = 0;
		
		for (int i = 0 ; i < fNumberOfCombinations ; i++) {
			
			int numberOfCombinationTuplets = 1;
			for (int j = 0 ; j < fReferenceArguments.size() ; j++) {
				if (fInternalState.isArgumentEnabled(j)) {
					numberOfCombinationTuplets *= fInternalState.getArgumentSize(j);
				}
			}
			numberOfTuplets += numberOfCombinationTuplets;
			
			nextCombination();
		}
		
		return numberOfTuplets;
	}
	
	private long calculateCombinations(int k, int n) {
		return calculateNumerator(k, n) / calculateFactorial(k);
	}
 	
	private long calculateNumerator(int k, int n) {
		long numerator = n;
		
		for (int i = n - 1 ; i > (n - k) ; i--) {
			numerator *= i;
		}
		
		return numerator;
	}
	
	private long calculateFactorial(int x) {
		return x == 0 ? 1 : x * calculateFactorial(x - 1);
	}
	
	public int getIndex() {
		return fInternalStateIndex - 1;
	}
	
	public long getNumberOfTuples() {
		return fNumberOfTuples;
	}
	
	public long getNumberOfCombinations() {
		return fNumberOfCombinations;
	}
	
	void mementoSave() {
		fInternalState.mementoSave(fInternalStateIndex);
	}
	
	void mementoLoad() {
		fInternalStateIndex = fInternalState.mementoLoad();
	}
	
	@Override
	public String toString() {
		return "TuplesIterator @ " + fInternalStateIndex + " / " + fNumberOfTuples;
	}
	
}