package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeComparator;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.google.common.collect.Multimap;

import java.util.*;

public class EvaluatorHelper {

    // TODO - where is the output ?
    public static void prepareVariablesForParameter(
            MethodParameterNode methodParameterNode,
            ParamChoiceSets paramChoiceSets,
            EcSatSolver satSolver,
            ChoicesMappingsBucket choicesMappingsBucket,
            ChoiceToSolverIdMappings choiceToSolverIdMappings) {

        if (choiceToSolverIdMappings.eQContainsKey(methodParameterNode))
            return;

        //we need to create new set of variables, as we are seeing this parameter for the first time
        //choiceVars control whether a choice is taken
        //prefixVars are used to enforce uniqueness of choice
        List<Integer> choiceVars = new ArrayList<>(); //choiceVars[i] ==  (this parameter takes choice i)
        List<Integer> prefixVars = new ArrayList<>(); //prefixVars[i] == (this parameter takes one of choices 0,...,i)
        List<Integer> lessEqVars = new ArrayList<>(); //lessEqVars[i] == (this parameter <= value at i)
        List<Integer> lessThVars = new ArrayList<>(); //lessThVars[i] == (this parameter < value at i)
//        HashMap<ChoiceNode, Integer> inverseEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLThVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> choiceID = new HashMap<>();


        List<ChoiceNode> sortedChoices = new ArrayList<>(paramChoiceSets.atomicGet(methodParameterNode));

        int n = sortedChoices.size();

        if (!JavaTypeHelper.isNumericTypeName(methodParameterNode.getType())) {
            for (int i = 0; i < n; i++) {
                choiceVars.add(satSolver.newId());
                choiceID.put(sortedChoices.get(i), choiceVars.get(i));
            }
            choiceToSolverIdMappings.eqPut(methodParameterNode, choiceID);
            return;
        }


        Collections.sort(sortedChoices, new ChoiceNodeComparator());


        prefixVars.add(satSolver.newId());

        for (int i = 0; i < n; i++) {
            choiceVars.add(satSolver.newId());
            prefixVars.add(satSolver.newId());
            choiceID.put(sortedChoices.get(i), choiceVars.get(i));
        }

        for (ChoiceNode sanitizedChoiceNode : paramChoiceSets.sainitizedGet(methodParameterNode))
            if (!choiceID.containsKey(sanitizedChoiceNode)) {
                Integer sanitizedID = satSolver.newId();
                choiceID.put(sanitizedChoiceNode, sanitizedID);

                List<Integer> bigClause = new ArrayList<>();
                for (ChoiceNode atomicValue : choicesMappingsBucket.sanToAtmGet(sanitizedChoiceNode)) {
                    Integer atomicID = choiceID.get(atomicValue);
                    final int[] clause = {-atomicID, sanitizedID};
                    satSolver.addSat4Clause(clause); // atomicID => sanitizedID
                    bigClause.add(atomicID);
                }
                bigClause.add(-sanitizedID);
                satSolver.addSat4Clause(bigClause.stream().mapToInt(Integer::intValue).toArray()); //sanitizedID => (atomicID1 OR ... OR atomicIDn)
            }

        for (ChoiceNode inputValue : paramChoiceSets.inputGet(methodParameterNode))
            if (!choiceID.containsKey(inputValue)) {
                Integer inputID = satSolver.newId();
                choiceID.put(inputValue, inputID);

                List<Integer> bigClause = new ArrayList<>();
                for (ChoiceNode sanitizedValue : choicesMappingsBucket.inputToSanGet(methodParameterNode).get(inputValue)) {
                    Integer sanitizedID = choiceID.get(sanitizedValue);
                    satSolver.addSat4Clause(new int[]{-sanitizedID, inputID}); // sanitizedID => inputID
                    bigClause.add(sanitizedID);
                }
                bigClause.add(-inputID);
                satSolver.addSat4Clause(bigClause.stream().mapToInt(Integer::intValue).toArray()); //inputID => (sanitizedID1 OR ... OR sanitizedIDn)
            }


        satSolver.addSat4Clause(new int[]{-prefixVars.get(0)});
        satSolver.addSat4Clause(new int[]{prefixVars.get(n)}); //at least one value should be taken
        for (int i = 0; i < n; i++) {
            // prefixVars[i+1] == prefixVars[i] OR choiceVars[i]
            satSolver.addSat4Clause(new int[]{-choiceVars.get(i), prefixVars.get(i + 1)}); // choiceVars[i] => prefixVars[i];
            satSolver.addSat4Clause(new int[]{-prefixVars.get(i), prefixVars.get(i + 1)}); // prefixVars[i] => prefixVars[i+1];
            satSolver.addSat4Clause(new int[]{choiceVars.get(i), prefixVars.get(i), -prefixVars.get(i + 1)}); // enforcing that last one is true only when at least one of first+second is true

            satSolver.addSat4Clause(new int[]{-choiceVars.get(i), -prefixVars.get(i)}); // NOT( choiceVars[i] AND prefixVars[i] ), to guarantee uniqueness
        }

        for (int i = 0; i < n; i++) {
            lessEqVars.add(prefixVars.get(i + 1));
            lessThVars.add(prefixVars.get(i));
        }
        for (int i = 1; i < n; i++) //all elements except first one
            if (new ChoiceNodeComparator().compare(sortedChoices.get(i - 1), sortedChoices.get(i)) == 0)
                lessThVars.set(i, lessThVars.get(i - 1));

        for (int i = n - 1; i > 0; i--) //all elements except first one, in reverse
            if (new ChoiceNodeComparator().compare(sortedChoices.get(i - 1), sortedChoices.get(i)) == 0)
                lessEqVars.set(i - 1, lessEqVars.get(i));

        for (int i = 0; i < n; i++) {
            ChoiceNode choice = sortedChoices.get(i);
            inverseLEqVars.put(choice, lessEqVars.get(i));
            inverseLThVars.put(choice, lessThVars.get(i));
        }

        choiceToSolverIdMappings.lePut(methodParameterNode, inverseLEqVars);
        choiceToSolverIdMappings.ltPut(methodParameterNode, inverseLThVars);
        choiceToSolverIdMappings.eqPut(methodParameterNode, choiceID);
    }

}
