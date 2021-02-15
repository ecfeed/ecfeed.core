package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.AlgoLogger;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class AbstractScoreBasedAlgorithm<E> {

	private List<List<E>> fInput; // input domain
	private int fDimensionCount; // Total number of dimensions for an input domain
	private List<Integer> fInputIndex; // Store index of parameters in input domain
	private IScoreEvaluator<E> fScoreEvaluator; 
	private IConstraintEvaluator<E> fconstraintEvaluator;
	protected List<List<Integer>> fHistorydimensionOrder = new ArrayList<>(); // Store historical dimension orders (each randomly generated dimension order could be different)
	
    public AbstractScoreBasedAlgorithm(List<List<E>> input, IScoreEvaluator ScoreEvaluator, int argCount, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {
    	this.fInput = input;
    	this.fDimensionCount = input.size();
    	this.fScoreEvaluator = ScoreEvaluator;
    	this.fconstraintEvaluator = constraintEvaluator;
    	this.fInputIndex = IntStream.range(0, input.size()).boxed().collect(Collectors.toList());
        	
		if (fInput == null || fconstraintEvaluator == null) {
			GeneratorException.report("input or constraints cannot be null");
		}
		fconstraintEvaluator.initialize(fInput);
		
    }
	
	public List<E> nextTest() {
	    List<E> test = new ArrayList<>();
        List<Integer> dimensionOrder = dimensions();
        for (int i = 0; i < fDimensionCount; i++){
            int d = dimensionOrder.get(i);
            int maxScore = -2;
            E bestCandidateChoice = null;
            c: for (E choice : choices(d)){
            	if(test.size() == i) {
            		test.add(i, choice);
            	}else
                test.set(i, choice);
            	if (constraintCheck(test) == EvaluationResult.FALSE) continue c;
                int score = fScoreEvaluator.getScore(test);
                if (score > maxScore){
                    maxScore = score;
                    bestCandidateChoice = choice;
                }
            }
            if (bestCandidateChoice != null)
                test.set(i, bestCandidateChoice);
            else
                return null;
        }
        test = format(dimensionOrder, test);
        fScoreEvaluator.update(test);
        return test;
	}
	
	// returns a shuffled list of indices of input parameters 
    private List<Integer> dimensions(){
         List<Integer> dimensions = new ArrayList<>();
        dimensions.addAll(fInputIndex);
        int attempt = 3;
        do {
            attempt--;
            Collections.shuffle(dimensions);
        }while (fHistorydimensionOrder.contains(dimensions) && attempt > 0);

        fHistorydimensionOrder.add(dimensions);
        return dimensions.subList(0, fDimensionCount);
    }
    
    // return a list of choices for a specific parameter
    private List<E> choices(int index) {
        return fInput.get(index);
    }
    
    private List<E> format(List<Integer> dimension, List<E> test){
        return test.stream().sorted(Comparator.comparingInt(o -> dimension.get(test.indexOf(o)))).collect(Collectors.toList());
     }
    
	public EvaluationResult constraintCheck(List<E> tuple) {
		return fconstraintEvaluator.evaluate(tuple);
	}
}
