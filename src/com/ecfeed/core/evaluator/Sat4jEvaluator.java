package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

 //   private ArrayList<ArrayList<Integer>> fClauses;
    private ArrayList<VecInt>  fClausesVecInt; //internal type for Sat4j
    private int lastUsedID = 1;
    private HashMap<MethodParameterNode, HashMap<ChoiceNode, Integer>> argChoiceToID;
    private MethodNode fMethod;
    private ISolver fSolver;
    private Boolean isContradicting = false;



    public Sat4jEvaluator(Collection<Constraint> initConstraints, MethodNode method) {
        argChoiceToID = new HashMap<>();
        fClausesVecInt = new ArrayList<>();
        fMethod = method;
        if(fMethod == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("No method but there were constraints!");
        }
        if(initConstraints != null) {
            for (Constraint constraint : initConstraints) {
                ParseConstraint(constraint);
            }
        }
        final int maxVar = lastUsedID;
        final int nbClauses = fClausesVecInt.size();
        fSolver = SolverFactory.newLight();


        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(nbClauses);
            for(VecInt clause : fClausesVecInt)
                fSolver.addClause(clause);
        } catch (ContradictionException e)
        {
            isContradicting = true;
        }
    }

    private int newID()
    {
        lastUsedID++;
        return lastUsedID;
    }

    private void variablesForParameter(MethodParameterNode arg)
    {
        if(!argChoiceToID.containsKey(arg)) { //we need to create new set of variables, as we are seeing this parameter for the first time
            ArrayList<Integer> xList = new ArrayList<>(); //xList[i] ==  (this parameter takes value i)
            ArrayList<Integer> yList = new ArrayList<>(); //yList[i] == (this parameter takes one of values 0,...,i
            HashMap<ChoiceNode, Integer> choiceIDs = new HashMap<>();
            for (ChoiceNode choice : arg.getLeafChoices()) {
                Integer xID = newID();
                Integer yID = newID();
                xList.add(xID);
                yList.add(yID);
                choiceIDs.put(choice, xID);
            }
            int n = xList.size();

            fClausesVecInt.add(new VecInt(xList.stream().mapToInt(Integer::intValue).toArray())); //at least one value should be taken
            for(int i=0;i<n;i++)
                fClausesVecInt.add(new VecInt(new int[] {-xList.get(i),yList.get(i)})); // xList[i] => yList[i];
            for(int i=0;i<n-1;i++)
                fClausesVecInt.add(new VecInt(new int[] {-yList.get(i),yList.get(i+1)})); // yList[i] => yList[i+1];
            for(int i=0;i<n-1;i++)
                fClausesVecInt.add(new VecInt(new int[] {-xList.get(i+1),-yList.get(i)})); // NOT( xList[i+1] AND yList[i] ), to guarantee uniqueness

            argChoiceToID.put(arg, choiceIDs);
        }
    }


    private void ParseConstraint(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            Integer premiseID=null, consequenceID=null;
            try {
                premiseID = (Integer)premise.accept(new ParseStatementVisitor());
                consequenceID = (Integer)consequence.accept(new ParseStatementVisitor());
            } catch (Exception e) {
                e.printStackTrace();
            }
            fClausesVecInt.add(new VecInt(new int[] {-premiseID,consequenceID}));
        }
    }

    class ParseStatementVisitor implements IStatementVisitor
    {
        @Override
        public Object visit(StatementArray statement)
        {
            Integer myID = newID();
            switch(statement.getOperator()){
                case OR: // y = (x1 OR x2 OR .. OR xn) compiles to: (NOT x1 OR y) AND ... AND (NOT xn OR y) AND (x1 OR ... OR xn OR NOT y)
                {
                    ArrayList<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseStatementVisitor());
                        } catch (Exception e) {
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
                    ArrayList<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseStatementVisitor());
                        } catch (Exception e) {
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
            MethodParameterNode lParam = statement.getLeftParameter();

            variablesForParameter(lParam);

            Integer myID = newID();


            int lParamIndex = fMethod.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
                return null;
            }

            ArrayList<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethod.getParametersCount(), null));

            if(statement.getCondition() instanceof ParameterCondition) {
                MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

                variablesForParameter(rParam);

                int rParamIndex = fMethod.getMethodParameters().indexOf(rParam);
                if (rParamIndex == -1) {
                    ExceptionHelper.reportRuntimeException("Parameter not in method!");
                    return null;
                }

                for(ChoiceNode rChoice : rParam.getLeafChoices()) { //we iterate over all choices of rParam and lParam
                    Integer idOfRightArgChoice = argChoiceToID.get(rParam).get(rChoice);
                    dummyValues.set(rParamIndex, rChoice);
                    for (ChoiceNode lChoice : lParam.getLeafChoices()) {
                        dummyValues.set(lParamIndex, lChoice);
                        EvaluationResult result = statement.evaluate(dummyValues);
                        Integer idOfLeftArgChoice = argChoiceToID.get(lParam).get(lChoice);
                        if (result == EvaluationResult.TRUE) {
                            fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice,-idOfRightArgChoice, myID})); // thisChoice => me
                        } else if (result == EvaluationResult.FALSE) {
                            fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, -idOfRightArgChoice, -myID})); // thisChoice => NOT me
                        } else //INSUFFICIENT_DATA
                        {
                            ExceptionHelper.reportRuntimeException("INSUFFICIENT_DATA: You shouldn't be here!");
                            return null; //FIXME, ideally we will not have INSUFFICIENT_DATA
                        }
                    }
                }



            }
            else { //we need only to iterate over all choices of single lParam
                for (ChoiceNode lChoice : lParam.getLeafChoices()) {
                    dummyValues.set(lParamIndex, lChoice);
                    EvaluationResult result = statement.evaluate(dummyValues);
                    Integer idOfLeftArgChoice = argChoiceToID.get(lParam).get(lChoice);
                    if (result == EvaluationResult.TRUE) {
                        fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, myID})); // thisChoice => me
                    } else if (result == EvaluationResult.FALSE) {
                        fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, -myID})); // thisChoice => NOT me
                    } else //INSUFFICIENT_DATA
                    {
                        ExceptionHelper.reportRuntimeException("INSUFFICIENT_DATA: You shouldn't be here!");
                        return null; //FIXME, ideally we will not have INSUFFICIENT_DATA
                    }
                }
            }

            return myID;
        }

        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            return 0; //TODO
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

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment)
    {

        if(fMethod == null)
        {
            return EvaluationResult.TRUE; //no method so there were no constraints
        }

        if(isContradicting)
            return EvaluationResult.FALSE;


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
                if(argChoiceToID.get(p)==null)
                    continue; //no constraint on this method parameter
                Integer idOfParamChoiceVar = argChoiceToID.get(p).get(c);
                assumptions.add(idOfParamChoiceVar);
            }
        }

        if(cChoiceNode.hasNext()) {
            ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
            return null;
        }



        try {
                IProblem problem = fSolver;

            if (problem.isSatisfiable(new VecInt(assumptions.stream().mapToInt(Integer::intValue).toArray()))) {
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
        return null; //TODO
    }

}
