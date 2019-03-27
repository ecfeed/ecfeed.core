package com.ecfeed.core.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.benchmark.BenchmarkTestSet.ChoiceSelectionAlgorithm;
import com.ecfeed.core.benchmark.BenchmarkTestSet.InputShape;
import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.algorithms.IteratedRandomizedNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomizedNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IConstraint;

public final class Benchmark {

	private static final int DEFAULT_SIZE = 6;
	private static final int DEFAULT_ORDER = 2;
	private static final int DEFAULT_COVERAGE = 100;
	
	private int fNumberOfArguments;
	private int fNumberOfChoices;
	private int fCoverage;
	private int fOrder;
	
	private ChoiceSelectionAlgorithm fSelection;
	private InputShape fShape;
	
	private Path fPath;
	
	private Set<Class<?>> fAlgorithms;
	
	public Benchmark(Class<?>... usedAlgorithms) {
		if (usedAlgorithms == null) {
			throw new NullPointerException("The list of algorithms cannot be empty");
		} else if (usedAlgorithms.length == 0) {
			throw new IllegalArgumentException("The number of algorithms should be at least one");
		}

		fAlgorithms = new HashSet<>();
		for (Class<?> algorithm : usedAlgorithms) {
			fAlgorithms.add(algorithm);
		}
	}
	
	public List<BenchmarkResults> runCustomizedBenchmark() {
		validateBenchmarkParameters();
		
		List<BenchmarkResults> results = new ArrayList<>();
		
		for (Class<?> algorithmClass : fAlgorithms) {
			AbstractAlgorithm<Integer> singleAlgorithm = getAlgorithmFromClass(algorithmClass, fOrder, fCoverage);
			BenchmarkResults output = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(output.toString());
			System.out.println(output);
			results.add(output);
		}
		
		return results;
	}
	
	public List<List<BenchmarkResults>> runDefaultBenchmark() {
		validateBenchmarkParameters();
		
		List<List<BenchmarkResults>> allResults = new ArrayList<>();
		
		for (Class<?> algorithmClass : fAlgorithms) {
			List<BenchmarkResults> singleResults = new ArrayList<>();
			BenchmarkResults singleBenchmark;
			
			AbstractAlgorithm<Integer> singleAlgorithm;
			singleAlgorithm = getAlgorithmFromClass(algorithmClass, fOrder, fCoverage);
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
	
			int previousNumberOfChoices = fNumberOfChoices;
			fNumberOfChoices *= 2;
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
			
			fNumberOfChoices = previousNumberOfChoices;
			int previousNumberOfArguments = fNumberOfArguments;
			fNumberOfArguments *= 2;
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
			
			fNumberOfArguments = previousNumberOfArguments;
			InputShape previousShape = fShape;
			fShape = InputShape.TRIANGULAR;
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
			
			fShape = InputShape.VARIABLE;
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
			
			fShape = previousShape;
			
			singleAlgorithm = getAlgorithmFromClass(algorithmClass, fOrder + 1, fCoverage);
			
			singleBenchmark = runBenchmark(singleAlgorithm, generateBenchmarkDescription());
			updateFile(singleBenchmark.toString());
			System.out.println(singleBenchmark);
			singleResults.add(singleBenchmark);
			
			allResults.add(singleResults);
		}
		
		return allResults;		
	}
	
	public List<List<BenchmarkResults>> runFullBenchmark() {	
		int previousOrder = fOrder;
		int previousNumberOfChoices = fNumberOfChoices;
		int previousNumberOfArguments = fNumberOfArguments;

		List<List<BenchmarkResults>> benchmarkResults = new ArrayList<>();
		
		fNumberOfArguments = 100;
		fNumberOfChoices = 2;
		fOrder = 2;
		benchmarkResults.add(runCustomizedBenchmark());
		
		fNumberOfArguments = 10;
		fNumberOfChoices = 10;
		fOrder = 2;
		benchmarkResults.add(runCustomizedBenchmark());
		
		fNumberOfArguments = 20;
		fNumberOfChoices = 10;
		fOrder = 2;
		benchmarkResults.add(runCustomizedBenchmark());
		
		fNumberOfArguments = 30;
		fNumberOfChoices = 10;
		fOrder = 2;
		benchmarkResults.add(runCustomizedBenchmark());
		
		for (int arguments = 6 ; arguments < 10 ; arguments++) {
			for (int choices = 6 ; choices < 10 ; choices++) {
				for (int order = 2 ; order < 5 ; order++) {
					fNumberOfArguments = arguments;
					fNumberOfChoices = choices;
					fOrder = order;
					benchmarkResults.add(runCustomizedBenchmark());
				}
			}
		}
		
		fOrder = previousOrder;
		fNumberOfChoices = previousNumberOfChoices;
		fNumberOfArguments = previousNumberOfArguments;
			
		return benchmarkResults;
	}
	
	private void validateBenchmarkParameters() {
		if (fNumberOfArguments == 0) {
			fNumberOfArguments = DEFAULT_SIZE;
		}
		
		if (fNumberOfChoices == 0) {
			fNumberOfChoices = DEFAULT_SIZE;
		}
		
		if (fOrder == 0) {
			fOrder = DEFAULT_ORDER;
		}
		
		if (fCoverage == 0) {
			fCoverage = DEFAULT_COVERAGE;
		}
		
		if (fSelection == null) {
			fSelection = ChoiceSelectionAlgorithm.RANDOM;
		}
		
		if (fShape == null) {
			fShape = InputShape.SYMMETRICAL;
		}
	}
	
	private void updateFile(String line) {
		if (fPath != null) {
			try {
				Files.write(fPath, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				System.out.println("The file " + fPath.getRoot().toString() + " could not be updated");
				fPath = null;
			}
		}
	}
	
	private String generateBenchmarkDescription() {
		return "Size: " + fNumberOfArguments + "x" + fNumberOfChoices + ", Order: " + fOrder 
				+ ", Shape: " + fShape + ", Time: " + LocalDateTime.now();
	}
	
	private BenchmarkResults runBenchmark(AbstractAlgorithm<Integer> algorithm, String benchmarkDescription) {
		
		BenchmarkResults results = new BenchmarkResults();
		
		results.setNameAlgorithm(algorithm.getClass().getSimpleName());
		results.setNameBenchmark(benchmarkDescription);
		
		try {
			algorithm.initialize(new BenchmarkTestSet(this).generateInput(), new HomebrewConstraintEvaluator<Integer>(new HashSet<IConstraint<Integer>>()), null);
		} catch (GeneratorException e) {
			System.out.println("unexpected exception during initialization: ");
			e.printStackTrace();
		}
		
		results.processingTimeStart();
		
		algorithm.reset();
			
		try {
			while (algorithm.getNext() != null) {
				results.updateNumberOfGeneratedTests();
				results.updateUsedMemory();
			}
		} catch (GeneratorException e) {
			e.printStackTrace();
		}
		
		results.processingTimeEnd();
		
		return results; 
	}
	
	private AbstractAlgorithm<Integer> getAlgorithmFromClass(Class<?> algorithm, int order, int coverage) {
		if (algorithm.equals(RandomizedNWiseAlgorithm.class)) {
			return new RandomizedNWiseAlgorithm<>(order, coverage);
		} else if (algorithm.equals(IteratedRandomizedNWiseAlgorithm.class)) {
			return new IteratedRandomizedNWiseAlgorithm<>(order, coverage);
		} else if (algorithm.equals(CartesianProductAlgorithm.class)) {
			return new CartesianProductAlgorithm<>();
		} else {
			throw new RuntimeException("The algorithm is not (yet) implemented.");
		}
	}
	
	public Benchmark setNumberOfArguments(int numberOfArguments) {
		if (numberOfArguments < 1) {
			throw new IllegalArgumentException("The number of argumnets cannot be lower than one");
		}
		
		fNumberOfArguments = numberOfArguments;
		return this;
	}
	
	public Benchmark setNumberOfChoices(int numberOfChoices) {
		if (numberOfChoices < 1) {
			throw new IllegalArgumentException("The number of choices cannot be lower than one");
		}
		
		fNumberOfChoices = numberOfChoices;
		return this;
	}
	
	public Benchmark setCoverage(int coverage) {
		if (coverage < 1) {
			throw new IllegalArgumentException("The coverage cannot be lower than one");
		}
		
		fCoverage = coverage;
		return this;
	}
	
	public Benchmark setOrder(int order) {
		if (order < 1) {
			throw new IllegalArgumentException("The order cannot be lower than one");
		}
		
		fOrder = order;
		return this;
	}
	
	public Benchmark setSelection(ChoiceSelectionAlgorithm selection) {
		fSelection = selection;
		return this;
	}
	
	public Benchmark setShape(InputShape shape) {
		fShape = shape;
		return this;
	}
	
	public Benchmark setPath(Path path) {
		if (path == null) {
			throw new NullPointerException("The filepath cannot be null");
		}
		
		if (Files.isDirectory(path)) {
			throw new IllegalArgumentException("The filepath cannot be a directory");
		}
		
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				throw new RuntimeException("The file could not be created");
			}
		}
		
		fPath = path;
		return this;
	}
	
	public int getNumberOfArguments() {
		return fNumberOfArguments;
	}
	
	public int getNumberOfChoices() {
		return fNumberOfChoices;
	}
	
	public int getCoverage() {
		return fCoverage;
	}
	
	public int getOrder() {
		return fOrder;
	}

	public ChoiceSelectionAlgorithm getSelection() {
		return fSelection;
	}
	
	public InputShape getShape() {
		return fShape;
	}
	
	public Path getPath() {
		return fPath;
	}
	
	public static void main(String[] args) {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Benchmark(IteratedRandomizedNWiseAlgorithm.class, RandomizedNWiseAlgorithm.class)
			.setPath(Paths.get("/home/krzysztof/Desktop/tmpbenchmark.data"))
			.runFullBenchmark();
	}
}
