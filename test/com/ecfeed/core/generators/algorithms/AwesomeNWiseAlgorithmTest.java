package com.ecfeed.core.generators.algorithms;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AwesomeNWiseAlgorithmTest extends NWiseAlgorithmTest {
    @Test
    public void testCorrectness() {
        testCorrectness(AwesomeNWiseAlgorithm.class);
    }

    @Test
    public void testConstraints() {
        testConstraints(AwesomeNWiseAlgorithm.class);
    }

    @Test
    public void testSize() {
        try {
            for (int variables : new int[] { 1, 2, 5 }) {
                for (int choices : new int[] { 1, 2, 5 }) {
                    for (int n = 1; n <= variables; n++) {
                        List<List<String>> input = GeneratorTestUtils.prepareInput(variables, choices);
                        IAlgorithm<String> algorithm = new AwesomeNWiseAlgorithm<>(n, 100);

                        algorithm.initialize(input, new DummyEvaluator<>(), new SimpleProgressMonitor());
                        int generatedDataSize = GeneratorTestUtils.algorithmResult(algorithm).size();
                        int referenceDataSize = referenceResult(input, n).size();
                        System.out.println(""+generatedDataSize+" "+referenceDataSize);
                        assertTrue(Math.abs(generatedDataSize - referenceDataSize)*5 <= referenceDataSize);
                    }
                }
            }
        } catch (GeneratorException e) {
            fail("Unexpected generator exception: " + e.getMessage());
        }
    }

    private Set<List<String>> referenceResult(List<List<String>> input, int n) throws GeneratorException {
        List<Set<String>> referenceInput = GeneratorTestUtils.referenceInput(input);
        Set<List<String>> cartesianProduct = Sets.cartesianProduct(referenceInput);
        Set<List<String>> referenceResult = new HashSet<List<String>>();
        Set<List<String>> remainingTuples = getAllTuples(input, n);
        for (int k = maxTuples(input, n); k > 0; k--) {
            for (List<String> vector : cartesianProduct) {
                Set<List<String>> originalTuples = getTuples(vector, n);
                originalTuples.retainAll(remainingTuples);
                if (originalTuples.size() == k) {
                    referenceResult.add(vector);
                    remainingTuples.removeAll(originalTuples);
                }
            }
        }
        return referenceResult;
    }

    protected int maxTuples(List<List<String>> input, int n) {
        return (new Tuples<List<String>>(input, n)).getAll().size();
    }

    protected Set<List<String>> getTuples(List<String> vector, int n) {
        return (new Tuples<String>(vector, n)).getAll();
    }
}
