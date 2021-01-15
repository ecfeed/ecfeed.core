package com.ecfeed.core.generators.algorithms;
import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;

import java.util.Arrays;
import java.util.List;

public class NwiseScoreEvaluator_RepTest {
	
		/* input = [a1, a2], [b1, b2], [c1, c2], [d1, d2], a1=>b1 AND !c1
	
	Note: the example from the space page has some mistakes: ([a1, c2, d1], 1), ([a1, c2, d2], 1) should be 
		  removed due to the constraint a1=>b1. Also, [b2,c2] should be 3 in the constructed table S2.
		  Therefore, all the constructed tables S1, S2, S3 are updated as below.
	      Space page link: https://testifyas.atlassian.net/wiki/spaces/ECFEED/pages/777945091/Score+based+NWise+generator
	    	
	 S3 = ([a1, b1, c2], 1), ([a2, b1, c2], 1), ([a2, b2, c2], 1), ([a1, b1, d1], 1),
          ([a1, b1, d2], 1), ([a2, b1, d1], 1), ([a2, b1, d2], 1), ([a2, b2, d1], 1),
          ([a2, b2, d2], 1), ([a2, c2, d1], 1), ([a2, c2, d2], 1), ([b1, c2, d1], 1), 
          ([b1, c2, d2], 1), ([b2, c2, d1], 1), ([b2, c2, d2], 1)
	
	S2 = ([a1, b1], 3), ([a2, b1], 3), ([a2, b2], 3), ([a1, c2], 1),
         ([a2, c2], 3), ([a1, d1], 1), ([a1, d2], 1), ([a2, d1], 3),
         ([a2, d2], 3),   ([b1, c2], 4), ([b1, d1], 3), ([b1, d2], 3),
         ([b2, c2], 3),   ([b2, d1], 2), ([b2, d2], 2), ([c2, d1], 3),
         ([c2, d2], 3)
         
    S1 = ([a1], 3), ([a2], 8), ([b1], 8), ([b2], 5), ([c2], 9), ([d1], 6), ([d2], 6)
	
	    */
	
	    @Test
	    public void test() throws GeneratorException{
	        
	    	/* Score[b2, c2, d1] = 3*(5+9) + 2*(5+6) + 3*(9+6) = 109 
	    	
	    	 Score[a1, b1, c2] = 3*(3+8) + 1*(3+9) + 4*(8+9) = 113
	    	 
	    	also test if the score returned -1 when the input tuple is invalid
	    	*/
	    	
	    	List<List<String>> input = Arrays.asList(Arrays.asList("a1","a2"), Arrays.asList("b1", "b2"), Arrays.asList("c1", "c2"), Arrays.asList("d1", "d2"));
	        NwiseScoreEvaluator_Rep<String> fScores = new NwiseScoreEvaluator_Rep<>(input,null,3);
	        
	        assert(109 == fScores.getScore(Arrays.asList("b2","c2","d1")));
	        assert(113 == fScores.getScore(Arrays.asList("a1","b1","c2")));
	        assert(-1 == fScores.getScore(Arrays.asList("e1")));
	        
	    }
	   
}
