package com.ecfeed.core.generators.algorithms;

import org.junit.Test;

public class AwesomeNWiseAlgorithmTest extends NWiseAlgorithmTest {
    @Test
    public void testCorrectness() {
        testCorrectness(AwesomeNWiseAlgorithm.class);
    }

    @Test
    public void testConstraints() {
        testConstraints(AwesomeNWiseAlgorithm.class);
    }
}
