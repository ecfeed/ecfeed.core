package com.ecfeed.core.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BenchmarkTestSet {
	
	public static enum ChoiceSelectionAlgorithm {RANDOM, ITERATED};
	public static enum InputShape {SYMMETRICAL, TRIANGULAR, VARIABLE};
	
	private Benchmark fArguments;
	private Random fRandom;
	
	public BenchmarkTestSet(Benchmark arguments) {
		fArguments = arguments;
		fRandom = new Random();
		
		fRandom.setSeed(10122018);
	}
	
	public List<List<Integer>> generateInput() {
		List<List<Integer>> testData = new ArrayList<>();
		
		for (int i = 0 ; i < fArguments.getNumberOfArguments() ; i++) {
			switch (fArguments.getShape()) {
				case SYMMETRICAL :
					testData.add(generateRow(fArguments.getNumberOfChoices()));
					break;
				case TRIANGULAR :
					testData.add(generateRow(i + 1));
					break;
				case VARIABLE : 
					testData.add(generateRow(fRandom.nextInt(fArguments.getNumberOfChoices()) + 1));
					break;
				default :
					throw new IllegalArgumentException("Operation not supported: " + fArguments.getShape());
			}
		}

		return testData;
	}
	
	private List<Integer> generateRow(int length) {
		List<Integer> argumentRow = new ArrayList<>();
		
		for (int i = 0 ; i < length ; i++) {	
			switch (fArguments.getSelection()) {
				case ITERATED : 
					argumentRow.add(i); 
					break;
				case RANDOM : 
					argumentRow.add(generateRowUniqueValue(argumentRow)); 
					break;
				default:
					throw new IllegalArgumentException("Operation not supported: " + fArguments.getSelection());
			}
		}
		
		return argumentRow;
	}
	
	private int generateRowUniqueValue(List<Integer> argumentRow) {
		int valueCandidate = 0;
		
		validate:
		while (true) {
			valueCandidate = fRandom.nextInt();
			
			for (Integer existingValue : argumentRow) {
				if (existingValue.equals(valueCandidate)) {
					continue validate;
				}
			}
			
			return valueCandidate;
		}
	}
}
