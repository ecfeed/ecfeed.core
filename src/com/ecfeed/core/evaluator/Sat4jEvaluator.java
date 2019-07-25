package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import com.ecfeed.core.utils.*;

import java.util.*;
import java.util.List;

import static com.ecfeed.core.utils.EMathRelation.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private ArrayList<VecInt>  fClausesVecInt; //internal type for Sat4j
    private int fFirstFreeID = 1;

    private Map<MethodParameterNode, Set<ChoiceNode>> fArgAllInputValues;
    private Map<MethodParameterNode, Set<ChoiceNode>> fArgAllSanitizedValues;
    private Map<MethodParameterNode, Set<ChoiceNode>> fArgAllAtomicValues;
    private Map<ChoiceNode, ChoiceNode> fSanitizedValToInputVal;
    private Map<ChoiceNode, ChoiceNode> fAtomicValToSanitizedVal;
    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;
    private Multimap<ChoiceNode, ChoiceNode> fSanitizedValToAtomicVal;

//    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fArgEqualChoiceID;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fArgLessEqChoiceID;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fArgLessThChoiceID;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fArgChoiceID;
    private List<RelationStatement> fAllRelationConditions;
    private List<Pair<Integer, ExpectedValueStatement>> fExpectedValConstraints; //Integer is the variable of pre-condition enforcing postcondition ExpectedValueStatement
    private MethodNode fMethod;
    private ISolver fSolver;
    private Boolean fIsContradicting = false;


    public Sat4jEvaluator(Collection<Constraint> initConstraints, MethodNode method) {
 //       fArgEqualChoiceID = new HashMap<>();
        fArgLessEqChoiceID = new HashMap<>();
        fArgLessThChoiceID = new HashMap<>();
        fArgChoiceID = new HashMap<>();
        fClausesVecInt = new ArrayList<>();
        fArgAllInputValues = new HashMap<>();
        fArgAllSanitizedValues = new HashMap<>();
        fArgAllAtomicValues = new HashMap<>();
        fSanitizedValToInputVal = new HashMap<>();
        fAtomicValToSanitizedVal = new HashMap<>();
        fExpectedValConstraints = new ArrayList<>();
        fAllRelationConditions = new ArrayList<>();
        fArgInputValToSanitizedVal = new HashMap<>();
        fSanitizedValToAtomicVal = HashMultimap.create();
        fMethod = method;
        if(fMethod == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("No method but there were constraints!");
        }
        if(initConstraints != null) {
            for (Constraint constraint : initConstraints) {
                preParse(constraint); //this fills fArgAllInputValues and fAllRelationConditions
            }

            for(MethodParameterNode arg : fArgAllInputValues.keySet()) {
                Set<ChoiceNode> setCopy = new HashSet<>(fArgAllInputValues.get(arg));
                fArgAllSanitizedValues.put(arg, setCopy);
                for(ChoiceNode node : setCopy) //maintaining the dependencies
                    fSanitizedValToInputVal.put(node,node);
            }

            while(true)
            {
                Boolean anyChange = false;
                for(RelationStatement rel : fAllRelationConditions)
                {
                    if(SanitizeValsWithRelation(rel))
                        anyChange = true;
                }
                if(!anyChange)
                    break;
            }


            for(MethodParameterNode param : fArgAllSanitizedValues.keySet()) {
                fArgInputValToSanitizedVal.put(param, HashMultimap.create());
                for(ChoiceNode sanitizedChoice : fArgAllSanitizedValues.get(param)) { //build InputVal -> SanitizedVal mapping
                    ChoiceNode inputChoice = fSanitizedValToInputVal.get(sanitizedChoice);
                    fArgInputValToSanitizedVal.get(param).put(inputChoice, sanitizedChoice);
                }


                fArgAllAtomicValues.put(param, new HashSet<>());
                for (ChoiceNode it : fArgAllSanitizedValues.get(param)) //build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
                    if (it.isRandomizedValue() &&
                            ( JavaTypeHelper.isExtendedIntTypeName(param.getType())
                            || JavaTypeHelper.isFloatingPointTypeName(param.getType())
                            ) )
                    {
                        List<ChoiceNode> interleaved = ChoiceNodeHelper.interleavedValues(it, fArgAllSanitizedValues.size());
                        fArgAllAtomicValues.get(param).addAll(interleaved);
                        for(ChoiceNode c : interleaved) {
                            fAtomicValToSanitizedVal.put(c, it);
                            fSanitizedValToAtomicVal.put(it, c);
                        }
                    }
                    else {
                        fArgAllAtomicValues.get(param).add(it);
                        fAtomicValToSanitizedVal.put(it,it);
                        fSanitizedValToAtomicVal.put(it,it);
                    }
            }


            for (Constraint constraint : initConstraints) {
                ParseConstraintToSAT(constraint);
            }
        }
        final int maxVar = fFirstFreeID;
        final int nbClauses = fClausesVecInt.size();
        fSolver = SolverFactory.newDefault();


        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(nbClauses);
            for(VecInt clause : fClausesVecInt)
                fSolver.addClause(clause);
            System.out.println("variables: " + maxVar + " clauses: " + nbClauses);
        } catch (ContradictionException e)
        {
            fIsContradicting = true;
        }
    }


    private Boolean SanitizeValsWithRelation(RelationStatement relation)
    {
        IStatementCondition condition = relation.getCondition();
        if(condition instanceof LabelCondition)
            return false;

        MethodParameterNode lParam = relation.getLeftParameter();

        if( ! JavaTypeHelper.isExtendedIntTypeName(lParam.getType())
                && ! JavaTypeHelper.isFloatingPointTypeName(lParam.getType()) )
            return false;

        List<ChoiceNode> allLVals = new ArrayList<>(fArgAllSanitizedValues.get(lParam));




        if(condition instanceof ParameterCondition)
        {
            MethodParameterNode rParam = ((ParameterCondition) condition).getRightParameterNode();
            List<ChoiceNode> allRVals = new ArrayList<>(fArgAllSanitizedValues.get(rParam));

            boolean anyChange = false;
            List<ChoiceNode> allLValsCopy = new ArrayList<>(allLVals);
            for(ChoiceNode it : allRVals)
            {
                Pair<Boolean, List<ChoiceNode>> changeResult = SplitListWithChoiceNode(allLValsCopy, it);
                anyChange = anyChange || changeResult.getFirst();
                allLValsCopy = changeResult.getSecond();
            }

            List<ChoiceNode> allRValsCopy = new ArrayList<>(allRVals);
            for(ChoiceNode it : allLVals)
            {
                Pair<Boolean, List<ChoiceNode>> changeResult = SplitListWithChoiceNode(allRValsCopy, it);
                anyChange = anyChange || changeResult.getFirst();
                allRValsCopy = changeResult.getSecond();
            }

            fArgAllSanitizedValues.put(lParam, new HashSet<>(allLValsCopy));

            fArgAllSanitizedValues.put(rParam, new HashSet<>(allRValsCopy));

            return anyChange;
        }
        if((condition instanceof ValueCondition) || (condition instanceof ChoiceCondition)) {
            ChoiceNode it;

            if (condition instanceof ValueCondition) {
                String val = ((ValueCondition) condition).getRightValue();

                it = allLVals.get(0).makeClone();
                it.setRandomizedValue(false);
                it.setValueString(val);
            }
            else {
                it = ((ChoiceCondition) condition).getRightChoice();
            }

            Pair<Boolean, List<ChoiceNode>> changeResult = SplitListWithChoiceNode(allLVals, it);

            fArgAllSanitizedValues.put(lParam, new HashSet<>(changeResult.getSecond()));
            return changeResult.getFirst();
        }

        ExceptionHelper.reportRuntimeException("We shouldn't be here.");
        return true;
    }


    private enum TypeOfEndpoint
    {
        LEFT_ENDPOINT,
        RIGHT_ENDPOINT
    }

    private Pair<Boolean, List<ChoiceNode>> SplitListWithChoiceNode(List<ChoiceNode> toSplit, ChoiceNode val)
    {
        ChoiceNode start,end;
        if(val.isRandomizedValue()) {
            Pair<ChoiceNode, ChoiceNode> startEnd = ChoiceNodeHelper.rangeSplit(val);
            start = startEnd.getFirst();
            end = startEnd.getSecond();
        }
        else
        {
            start = val;
            end = val;
        }
        Pair<Boolean, List<ChoiceNode>> changeResultLeft = SplitListByValue(toSplit, start, TypeOfEndpoint.LEFT_ENDPOINT);
        Pair<Boolean, List<ChoiceNode>> changeResultRight = SplitListByValue(changeResultLeft.getSecond(), end, TypeOfEndpoint.RIGHT_ENDPOINT);

        return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
    }

    private Pair<Boolean, List<ChoiceNode>> SplitListByValue(List<ChoiceNode> toSplit, ChoiceNode val, TypeOfEndpoint type)
    {
        Boolean anyChange = false;
        List<ChoiceNode> newList = new ArrayList<>();
        for(ChoiceNode it : toSplit)
            if(! it.isRandomizedValue())
                newList.add(it);
            else {
                Pair<ChoiceNode, ChoiceNode> startEnd = ChoiceNodeHelper.rangeSplit(it);
                ChoiceNode start = startEnd.getFirst();
                ChoiceNode end = startEnd.getSecond();
//                if((new ChoiceNodeComparator().compare(start,val))<0 && (new ChoiceNodeComparator().compare(val,end))<0)

                ChoiceNode val1, val2;
                val1 = start.makeClone();
                val2 = end.makeClone();
                val1.setValueString(ChoiceNodeHelper.convertValueToNumeric(val).getValueString());
                val2.setValueString(ChoiceNodeHelper.convertValueToNumeric(val).getValueString());
                if( JavaTypeHelper.isExtendedIntTypeName(start.getParameter().getType())
                        && JavaTypeHelper.isFloatingPointTypeName(val.getParameter().getType()) )
                {
                    val1 = ChoiceNodeHelper.roundValueDown(val1);
                    val2 = ChoiceNodeHelper.roundValueUp(val2);
                }
                if(new ChoiceNodeComparator().compare(val1,val2) == 0) {
                    if (type == TypeOfEndpoint.LEFT_ENDPOINT)
                        val1 = ChoiceNodeHelper.precedingVal(val1);
                    else //RIGHT_ENDPOINT
                        val2 = ChoiceNodeHelper.followingVal(val2);
                }


                if(new ChoiceNodeComparator().compare(val1,val2) == 0) //only happens if one was too extreme to be further moved, as in Long.MAX_VALUE or so
                {
                    newList.add(it);
                    continue;
                }
                int cmp1 = new ChoiceNodeComparator().compare(start, val1);
                int cmp2 = new ChoiceNodeComparator().compare(val2, end);
                if(cmp1 > 0 || cmp2 > 0) {
                    newList.add(it);
                    continue;
                }
                ChoiceNode it1, it2;
                if(cmp1<0)
                    it1 = ChoiceNodeHelper.toRangeFromFirst(start, val1);
                else
                    it1 = start;
                if(cmp2<0)
                    it2 = ChoiceNodeHelper.toRangeFromSecond(val2,end);
                else
                    it2 = end;

                anyChange = true;

                fSanitizedValToInputVal.put(it1, fSanitizedValToInputVal.get(it));
                fSanitizedValToInputVal.put(it2, fSanitizedValToInputVal.get(it));
                newList.add(it1);
                newList.add(it2);
            }
        return new Pair<>(anyChange, newList);

    }

    private int newID()
    {
        return fFirstFreeID++;
    }

    private void variablesForParameter(MethodParameterNode arg)
    {
        if(fArgChoiceID.containsKey(arg))
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


        List<ChoiceNode> sortedChoices = new ArrayList<>(fArgAllAtomicValues.get(arg));
        int n = sortedChoices.size();

        if(! JavaTypeHelper.isNumericTypeName(arg.getType()))
        {
            for(int i=0; i<n; i++) {
                choiceVars.add(newID());
                choiceID.put(sortedChoices.get(i), choiceVars.get(i));
            }
            fArgChoiceID.put(arg, choiceID);
            return;
        }


        Collections.sort(sortedChoices, new ChoiceNodeComparator());


        prefixVars.add(newID());
        for(int i=0; i<n; i++) {
            choiceVars.add(newID());
            prefixVars.add(newID());
            choiceID.put(sortedChoices.get(i), choiceVars.get(i));
        }

        for(ChoiceNode sanitizedValue : fArgAllSanitizedValues.get(arg))
            if(! choiceID.containsKey(sanitizedValue)) {
                Integer sanitizedID = newID();
                choiceID.put(sanitizedValue, sanitizedID);

                List<Integer> bigClause = new ArrayList<>();
                for(ChoiceNode atomicValue : fSanitizedValToAtomicVal.get(sanitizedValue))
                {
                    Integer atomicID = choiceID.get(atomicValue);
                    fClausesVecInt.add(new VecInt(new int[]{-atomicID, sanitizedID})); // atomicID => sanitizedID
                    bigClause.add(atomicID);
                }
                bigClause.add(-sanitizedID);
                fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray())); //sanitizedID => (atomicID1 OR ... OR atomicIDn)
            }

        for(ChoiceNode inputValue : fArgAllInputValues.get(arg))
            if(! choiceID.containsKey(inputValue)) {
                Integer inputID = newID();
                choiceID.put(inputValue, inputID);

                List<Integer> bigClause = new ArrayList<>();
                for(ChoiceNode sanitizedValue : fArgInputValToSanitizedVal.get(arg).get(inputValue))
                {
                    Integer sanitizedID = choiceID.get(sanitizedValue);
                    fClausesVecInt.add(new VecInt(new int[]{-sanitizedID, inputID})); // sanitizedID => inputID
                    bigClause.add(sanitizedID);
                }
                bigClause.add(-inputID);
                fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray())); //inputID => (sanitizedID1 OR ... OR sanitizedIDn)
            }


        fClausesVecInt.add(new VecInt(new int[]{-prefixVars.get(0)}));
        fClausesVecInt.add(new VecInt(new int[]{prefixVars.get(n)})); //at least one value should be taken
        for (int i = 0; i < n; i++) {
            // prefixVars[i+1] == prefixVars[i] OR choiceVars[i]
            fClausesVecInt.add(new VecInt(new int[]{-choiceVars.get(i), prefixVars.get(i+1)})); // choiceVars[i] => prefixVars[i];
            fClausesVecInt.add(new VecInt(new int[]{-prefixVars.get(i), prefixVars.get(i + 1)})); // prefixVars[i] => prefixVars[i+1];
            fClausesVecInt.add(new VecInt(new int[]{choiceVars.get(i), prefixVars.get(i), -prefixVars.get(i+1)})); // enforcing that last one is true only when at least one of first+second is true

            fClausesVecInt.add(new VecInt(new int[]{-choiceVars.get(i), -prefixVars.get(i)})); // NOT( choiceVars[i] AND prefixVars[i] ), to guarantee uniqueness
        }

        for(int i=0;i<n;i++)
        {
            lessEqVars.add(prefixVars.get(i+1));
            lessThVars.add(prefixVars.get(i));
        }
        for(int i=1;i<n;i++) //all elements except first one
            if(new ChoiceNodeComparator().compare(sortedChoices.get(i-1),sortedChoices.get(i))==0)
                lessThVars.set(i, lessThVars.get(i-1));

        for(int i=n-1;i>0;i--) //all elements except first one, in reverse
            if(new ChoiceNodeComparator().compare(sortedChoices.get(i-1),sortedChoices.get(i))==0)
                lessEqVars.set(i-1, lessEqVars.get(i));

        for(int i=0;i<n;i++)
        {
            ChoiceNode choice = sortedChoices.get(i);
//            inverseEqVars.put(choice, choiceVars.get(i));
            inverseLEqVars.put(choice, lessEqVars.get(i));
            inverseLThVars.put(choice, lessThVars.get(i));
        }

//        fArgEqualChoiceID.put(arg, inverseEqVars);
        fArgLessEqChoiceID.put(arg, inverseLEqVars);
        fArgLessThChoiceID.put(arg, inverseLThVars);
        fArgChoiceID.put(arg, choiceID);
    }

    private void preParse(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            if(consequence instanceof ExpectedValueStatement)
            {
                try {
                    premise.accept(new PreParseVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    premise.accept(new PreParseVisitor());
                    consequence.accept(new PreParseVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class PreParseVisitor implements IStatementVisitor
    {
        @Override
        public Object visit(StatementArray statement)
        {
            for (AbstractStatement child : statement.getChildren()) {
                try {
                    child.accept(new PreParseVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        public Object visit(RelationStatement statement)
        {
            fAllRelationConditions.add(statement);
            MethodParameterNode lParam = statement.getLeftParameter();
            if(!fArgAllInputValues.containsKey(lParam))
                fArgAllInputValues.put(lParam, new HashSet<>(lParam.getLeafChoices()));
            if(statement.getCondition() instanceof ParameterCondition) {
                MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();
                if(!fArgAllInputValues.containsKey(rParam))
                    fArgAllInputValues.put(rParam, new HashSet<>(rParam.getLeafChoices()));
            }
            return null;
        }
        @Override
        public Object visit(StaticStatement statement)
        {
            return null;
        }


        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null;
        }
        @Override
        public Object visit(LabelCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
    }


    private void ParseConstraintToSAT(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            if(consequence instanceof ExpectedValueStatement) {
                Integer premiseID = null;
                try {
                    premiseID = (Integer) premise.accept(new ParseConstraintToSATVisitor());
                    fExpectedValConstraints.add(new Pair<>(premiseID, (ExpectedValueStatement) consequence));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {
                Integer premiseID = null, consequenceID = null;
                try {
                    premiseID = (Integer) premise.accept(new ParseConstraintToSATVisitor());
                    consequenceID = (Integer) consequence.accept(new ParseConstraintToSATVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fClausesVecInt.add(new VecInt(new int[] {-premiseID,consequenceID}));
            }
        }
    }

    class ParseConstraintToSATVisitor implements IStatementVisitor
    {
        @Override
        public Object visit(StatementArray statement)
        {
            Integer myID = newID();
            switch(statement.getOperator()){
                case OR: // y = (x1 OR x2 OR .. OR xn) compiles to: (NOT x1 OR y) AND ... AND (NOT xn OR y) AND (x1 OR ... OR xn OR NOT y)
                {
                    List<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseConstraintToSATVisitor());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bigClause.add(childID);
                        fClausesVecInt.add(new VecInt(new int[] {-childID, myID})); //small fClauses
                    }
                    bigClause.add(-myID);
                    fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
                case AND: // y = (x1 AND x2 AND .. AND xn) compiles to: (x1 OR NOT y) AND ... AND (xn OR NOT y) AND (NOT x1 OR ... OR NOT xn OR y)
                {
                    List<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseConstraintToSATVisitor());
                        } catch (Exception e) { e.printStackTrace();
                        }
                        bigClause.add(-childID);
                        fClausesVecInt.add(new VecInt(new int[] {childID, -myID})); //small fClauses
                    }
                    bigClause.add(myID);
                    fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
            }
//            statementToID.put(statement,myID); not really necessary, as we are never reusing the same statements
            return myID;
        }
        @Override
        public Object visit(RelationStatement statement)
        {
            if(statement.getCondition() instanceof ParameterCondition) {
                return doubleChoiceParamConstraints(statement);
            }
            else if(statement.getCondition() instanceof ChoiceCondition &&
                        ((ChoiceCondition) statement.getCondition()).getRightChoice().isRandomizedValue() &&
                    ( JavaTypeHelper.isExtendedIntTypeName(statement.getLeftParameter().getType())
                    || JavaTypeHelper.isFloatingPointTypeName(statement.getLeftParameter().getType()) )
                    )
                    {
                        switch(statement.getRelation())
                        {
                            case GREATER_THAN:
                            case LESS_EQUAL:
                            {
                                RelationStatement statement1 = statement.getCopy();
                                ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                                val = ChoiceNodeHelper.rangeSplit(val).getSecond();
                                statement1.setCondition(val);
                                return singleChoiceParamConstraints(statement);
                            }
                            case GREATER_EQUAL:
                            case LESS_THAN:
                            {
                                RelationStatement statement1 = statement.getCopy();
                                ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                                val = ChoiceNodeHelper.rangeSplit(val).getFirst();
                                statement1.setCondition(val);
                                return singleChoiceParamConstraints(statement);
                            }
                            case NOT_EQUAL:
                            case EQUAL:
                            {
                                RelationStatement statementLow = statement.getCopy();
                                RelationStatement statementHigh = statement.getCopy();
                                ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                                ChoiceNode valLow = ChoiceNodeHelper.rangeSplit(val).getFirst();
                                ChoiceNode valHigh = ChoiceNodeHelper.rangeSplit(val).getSecond();
                                statementLow.setCondition(valLow);
                                statementHigh.setCondition(valHigh);
                                statementLow.setRelation(GREATER_EQUAL);
                                statementHigh.setRelation(LESS_EQUAL);

                                Integer statementLowID = singleChoiceParamConstraints(statementLow);
                                Integer statementHighID = singleChoiceParamConstraints(statementHigh);

                                Integer myID = newID();

                                fClausesVecInt.add(new VecInt(new int[] {-statementLowID, -statementHighID, myID}));
                                fClausesVecInt.add(new VecInt(new int[] {-myID, statementLowID}));
                                fClausesVecInt.add(new VecInt(new int[] {-myID, statementHighID}));
                                if(statement.getRelation()==EQUAL)
                                    return myID; //myID == (statementLowID AND statementHighID)
                                else //NOT_EQUAL
                                    return -myID;
                            }
                        }
                    }
                else {
                    //we need only to iterate over all choices of single lParam
                    return singleChoiceParamConstraints(statement);
                }
                ExceptionHelper.reportRuntimeException("You shouldn't be here!");
                return null;
        }

        private Integer singleChoiceParamConstraints(RelationStatement statement)
        {
            MethodParameterNode lParam = statement.getLeftParameter();
            variablesForParameter(lParam);
            Integer myID = newID();

            int lParamIndex = fMethod.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            for (ChoiceNode lChoice : fArgAllAtomicValues.get(lParam)) {
                List<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethod.getParametersCount(), null));
                dummyValues.set(lParamIndex, lChoice);
                EvaluationResult result = statement.evaluate(dummyValues);
                Integer idOfLeftArgChoice = fArgChoiceID.get(lParam).get(lChoice);
                if (result == EvaluationResult.TRUE) {
                    fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, myID})); // thisChoice => me
                } else if (result == EvaluationResult.FALSE) {
                    fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, -myID})); // thisChoice => NOT me
                } else //INSUFFICIENT_DATA
                {
                    ExceptionHelper.reportRuntimeException("INSUFFICIENT_DATA: You shouldn't be here!");
                }
            }

            return myID;
        }

        private Integer doubleChoiceParamConstraints(RelationStatement statement)
        {
            MethodParameterNode lParam = statement.getLeftParameter();
            variablesForParameter(lParam);
            Integer myID = newID();

            int lParamIndex = fMethod.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

            variablesForParameter(rParam);

            int rParamIndex = fMethod.getMethodParameters().indexOf(rParam);
            if (rParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }

            List<ChoiceNode> sortedLChoices = new ArrayList<>(fArgAllAtomicValues.get(lParam));
            Collections.sort(sortedLChoices, new ChoiceNodeComparator());
            int m = sortedLChoices.size();
            List<ChoiceNode> sortedRChoices = new ArrayList<>(fArgAllAtomicValues.get(rParam));
            Collections.sort(sortedRChoices, new ChoiceNodeComparator());
            int n = sortedRChoices.size();

            for(int i=0,j=0; i<m; i++) {
                while (j < n && new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) > 0) {
                    j++;
                }

                Integer leftLessTh = fArgLessThChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer leftLessEq = fArgLessEqChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer rightLessTh = null;
                Integer rightLessEq = null;
                if(j<n) {
                    rightLessTh = fArgLessThChoiceID.get(rParam).get(sortedRChoices.get(j));
                    rightLessEq = fArgLessEqChoiceID.get(rParam).get(sortedRChoices.get(j));
                }

                switch (statement.getRelation()) {
                    case EQUAL:
                    case NOT_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {

                            // NOT(i<x) AND i<=x IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) AND j<=y IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, -rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));

                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, -myID}));
                        }
                        break;
                    }

                    case LESS_THAN:
                    case GREATER_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<=y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessEq, -myID}));
                        }
                        break;
                    }
                    case LESS_EQUAL:
                    case GREATER_THAN: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        }
                        break;
                    }
                }
            }

            if(statement.getRelation() == EQUAL || statement.getRelation() == LESS_THAN || statement.getRelation() == LESS_EQUAL)
                return myID;
            else
                return -myID;
        }

        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            return null; //TODO
        }

        @Override
        public Object visit(StaticStatement statement)
        {
            Integer myID = newID();
            if(statement.getValue() == EvaluationResult.TRUE)
                fClausesVecInt.add(new VecInt(new int[] {myID}));
            else
                fClausesVecInt.add(new VecInt(new int[] {-myID}));
            return myID;
        }

        @Override
        public Object visit(LabelCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
    }

    private List<Integer> assumptionsFromValues(List<ChoiceNode> valueAssignment)
    {

        List<MethodParameterNode> params = fMethod.getMethodParameters();

        List<Integer> assumptions = new ArrayList<>();

        //iterate params and valueAssignment simultanously
        Iterator<ChoiceNode> cChoiceNode = valueAssignment.iterator();
        for(MethodParameterNode p : params) {
            if(!cChoiceNode.hasNext())
            {
                ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
                return null;
            }
            ChoiceNode c = cChoiceNode.next();
            if(c!=null) {
                if(fArgChoiceID.get(p)==null)
                    continue; //no constraint on this method parameter
                Integer idOfParamChoiceVar = fArgChoiceID.get(p).get(c);
                assumptions.add(idOfParamChoiceVar);
            }
        }

        if(cChoiceNode.hasNext()) {
            ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
            return null;
        }

        return assumptions;
    }

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment)
    {
        if(fMethod == null)
        {
            return EvaluationResult.TRUE; //no method so there were no constraints
        }

        if(fIsContradicting)
            return EvaluationResult.FALSE;


        try {
            IProblem problem = fSolver;
            if (problem.isSatisfiable(new VecInt(assumptionsFromValues(valueAssignment).stream().mapToInt(Integer::intValue).toArray()))) {
                return EvaluationResult.TRUE;
            } else {
                return EvaluationResult.FALSE;
            }
        } catch (TimeoutException e) {
            ExceptionHelper.reportRuntimeException("Timeout, sorry!");
            return null;
        }

    }


    @Override
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment)
    {
            try {
                IProblem problem = fSolver;
                boolean b = problem.isSatisfiable(new VecInt(assumptionsFromValues(valueAssignment).stream().mapToInt(Integer::intValue).toArray())); //necessary to make a call so solver can prepare a model
                if (!b) {
                    ExceptionHelper.reportRuntimeException("Cannot adapt, it's unsatisfiable!");
                    return null;
                }
                for(int i=0;i<valueAssignment.size();i++)
                {
       			    ChoiceNode p = valueAssignment.get(i);
				    MethodParameterNode parameter = fMethod.getMethodParameters().get(i);
				    if(parameter.isExpected()){
                        valueAssignment.set(i, p.makeClone());
				    }
			    }

                Set<Integer> vars = new HashSet<>(Ints.asList(problem.model()));
                for (Pair<Integer, ExpectedValueStatement> p : fExpectedValConstraints) {
                    if (vars.contains(p.getFirst())) {
                        p.getSecond().adapt(valueAssignment);
                    }
                }
            } catch (TimeoutException e) {
                ExceptionHelper.reportRuntimeException("Timeout, sorry!");
                return null;
            }
        return valueAssignment;
    }

}
