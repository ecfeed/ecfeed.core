package com.ecfeed.core.generators.algorithms;

import java.util.List;

public class TuplesIteratorStructure<E> {

	private int[][] fInternalState;
	
	private int[][] fMementoInternalState;
	private int fMementoInternalStateIndex = 0;
	
	TuplesIteratorStructure(List<List<E>> referenceArguments) {
		fInternalState = new int[referenceArguments.size()][2];
		
		for (int i = 0 ; i < referenceArguments.size() ; i++) {
			fInternalState[i][1] = referenceArguments.get(i).size();
		}
	}
	
	int getArgumentPosition(int index) {
		return fInternalState[index][0];
	}
	
	int getArgumentSize(int index) {
		return fInternalState[index][1];
	}
	
	int incrementArgumentPosition(int index) {
		return ++fInternalState[index][0];
	}
	
	void setArgumentPosition(int index, int value) {
		fInternalState[index][0] = value;
	}
	
	boolean isArgumentEnabled(int index) {
		return fInternalState[index][0] != Integer.MIN_VALUE;
	}
	
	boolean isArgumentDisabled(int index) {
		return fInternalState[index][0] == Integer.MIN_VALUE;
	}
	
	void setEnabled(int index, boolean setEnabled) {
		fInternalState[index][0] = setEnabled ? 0 : Integer.MIN_VALUE;
	}
	
	void mementoSave(int internalStateIndex) {
		if (fMementoInternalState == null) {
			fMementoInternalState = new int[fInternalState.length][2];
		}
		
		for (int i = 0 ; i < fInternalState.length ; i++) {
			fMementoInternalState[i][0] = fInternalState[i][0];
			fMementoInternalState[i][1] = fInternalState[i][1];
		}
		
		fMementoInternalStateIndex = internalStateIndex;
	}
	
	int mementoLoad() {
		if (fMementoInternalState == null) {
			throw new NullPointerException("The instance 'TuplesIterator' could not be reverted to the previous state!");
		}
		
		for (int i = 0 ; i < fMementoInternalState.length ; i++) {
			fInternalState[i][0] = fMementoInternalState[i][0];
			fInternalState[i][1] = fMementoInternalState[i][1];
		}
		
		return fMementoInternalStateIndex;
	}
	
}
