package com.ecfeed.core.benchmark;

import java.util.ArrayList;
import java.util.List;

public final class BenchmarkResultsHelper {

	public static List<String> compareBenchmarkResults(List<BenchmarkResults> benchmarkA, List<BenchmarkResults> benchmarkB) {
		List<String> benchmarkResults = new ArrayList<>();
		
		for (int i = 0 ; i < benchmarkA.size() ; i++) {
			benchmarkResults.add(
					"Case: " + benchmarkA.get(i).getNumberOfGeneratedTests() + " / " + benchmarkB.get(i).getNumberOfGeneratedTests() + " ; " +
					"Time: " + benchmarkA.get(i).getProcessingTime() + " / " + benchmarkB.get(i).getProcessingTime() + " ; " +
					"Memory: " + benchmarkA.get(i).getUsedMemory() + " / " + benchmarkB.get(i).getUsedMemory() + " MB | " +
					benchmarkA.get(i).getNameBenchmark()
			);
			
			System.out.println(benchmarkResults.get(i));
		}
		
		return benchmarkResults;
	}
}
