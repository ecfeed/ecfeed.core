package com.ecfeed.core.benchmark;

public class BenchmarkResults {

	private long fNumberOfGeneratedTests;
	private long fProcessingTime;
	private long fUsedMemory;
	private String fNameAlgorithm;
	private String fNameBenchmark;
	
	private int fUsedMemoryIterations;
	
	private Runtime fRuntime;
	
	public BenchmarkResults() {
		// This part does not have to work. We could try though.
		// VM arguments -verbose:gc
		System.gc();
		System.runFinalization();
		
		fNameAlgorithm = "";
		fNameBenchmark = "";
		
		fRuntime = Runtime.getRuntime();
	}
	
	BenchmarkResults setNameAlgorithm(String nameAlgorithm) {
		fNameAlgorithm = nameAlgorithm;
		return this;
	}
	
	BenchmarkResults setNameBenchmark(String nameBenchmark) {
		fNameBenchmark = nameBenchmark;
		return this;
	}
	
	BenchmarkResults updateNumberOfGeneratedTests() {
		fNumberOfGeneratedTests++;
		return this;
	}
	
	BenchmarkResults updateUsedMemory() {
		fUsedMemory += (fRuntime.totalMemory() - fRuntime.freeMemory());
		fUsedMemoryIterations++;
		return this;
	}
	
	BenchmarkResults processingTimeStart() {
		fProcessingTime = System.currentTimeMillis();
		return this;
	}
	
	BenchmarkResults processingTimeEnd() {
		fProcessingTime = System.currentTimeMillis() - fProcessingTime;
		return this;
	}
	
	public long getNumberOfGeneratedTests() {
		return fNumberOfGeneratedTests;
	}

	public long getProcessingTime() {
		return fProcessingTime;
	}
	
	public long getUsedMemory() {
		return fUsedMemory > 0 ? (fUsedMemory / fUsedMemoryIterations) / 1024 / 1024 : 0;
	}

	public String getNameAlgorithm() {
		return fNameAlgorithm;
	}

	public String getNameBenchmark() {
		return fNameBenchmark;
	}
	
	@Override
	public String toString() {
		return fNameAlgorithm 
				+ " | Case: " + fNumberOfGeneratedTests 
				+ " , Time: " +  fProcessingTime 
				+ " , Memory: " + getUsedMemory() + " MB"
				+ " | " + fNameBenchmark;
	}
}
