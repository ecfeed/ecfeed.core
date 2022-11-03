package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SatSolverConstraintCreatorTest {

    static ClassNode classNode = new ClassNode("Class", null);

    static MethodNode m1 = ClassNodeHelper.addMethodToClass(classNode, "Method1", null);
    static MethodParameterNode m1p1 = MethodNodeHelper.addParameterToMethod(m1, "M1P1", "int");
    static ChoiceNode m1p1c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C1", "1");
    static ChoiceNode m1p1c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C2", "2");
    static ChoiceNode m1p1c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C3", "3");
    static MethodParameterNode m1p2 = MethodNodeHelper.addParameterToMethod(m1, "M1P2", "int");
    static ChoiceNode m1p2c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p2, "M1P2C1", "1");
    static ChoiceNode m1p2c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p2, "M1P2C2", "2");
    static ChoiceNode m1p2c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p2, "M1P2C3", "3");
    static MethodParameterNode m1p3 = MethodNodeHelper.addParameterToMethod(m1, "M1P3", "int");
    static ChoiceNode m1p3c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C1", "1");
    static ChoiceNode m1p3c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C2", "2");
    static ChoiceNode m1p3c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C3", "3");

    static MethodNode m2 = ClassNodeHelper.addMethodToClass(classNode, "Method2", null);
    static MethodParameterNode m2p1 = MethodNodeHelper.addParameterToMethod(m2, "M2P1", "int");
    static ChoiceNode m2p1c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p1, "M2P1C1", "1");
    static ChoiceNode m2p1c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p1, "M2P1C2", "2");
    static ChoiceNode m2p1c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p1, "M2P1C3", "3");
    static MethodParameterNode m2p2 = MethodNodeHelper.addParameterToMethod(m2, "M2P2", "int");
    static ChoiceNode m2p2c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p2, "M2P2C1", "1");
    static ChoiceNode m2p2c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p2, "M2P2C2", "2");
    static ChoiceNode m2p2c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p2, "M2P2C3", "3");
    static MethodParameterNode m2p3 = MethodNodeHelper.addParameterToMethod(m2, "M2P3", "int");
    static ChoiceNode m2p3c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p3, "M2P3C1", "1");
    static ChoiceNode m2p3c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p3, "M2P3C2", "2");
    static ChoiceNode m2p3c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m2p3, "M2P3C3", "3");

    @Test
    public void test1() {
        SatSolverConstraintEvaluator evaluator = new SatSolverConstraintEvaluator(getConstraints1(), m1.getMethodParameters());

        evaluator.initialize(getDomain1());

        EvaluationResult results = evaluator.evaluate(getSet1());

        System.out.println(results);
    }

    @Test
    public void test2() {
        SatSolverConstraintEvaluator evaluator = new SatSolverConstraintEvaluator(getConstraints2(), m2.getMethodParameters());

        evaluator.initialize(getDomain2());

        EvaluationResult results = evaluator.evaluate(getSet2());

        System.out.println(results);
    }

    @Test
    public void testAll() {
        List<MethodParameterNode> parameters = new ArrayList<>();

        parameters.addAll(m1.getMethodParameters());
        parameters.addAll(m2.getMethodParameters());

        SatSolverConstraintEvaluator evaluator = new SatSolverConstraintEvaluator(getConstraintsAll(), parameters);

        evaluator.initialize(getDomainAll());

        EvaluationResult results = evaluator.evaluate(getSetAll());

        System.out.println(results);
    }

    public static Set<Constraint> getConstraints1() {
        RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(m1p1, EMathRelation.EQUAL, m1p1c1);
        RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(m1p2, EMathRelation.LESS_THAN, m1p3c3);

        Constraint c1 = new Constraint("M1C1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);

        Set<Constraint> constraints = new HashSet<>();

        constraints.add(c1);

        return constraints;
    }

    public static Set<Constraint> getConstraints2() {
        RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(m2p1, EMathRelation.EQUAL, m2p1c1);
        RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(m2p2, EMathRelation.LESS_THAN, m2p3c3);

        Constraint c1 = new Constraint("M2C1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);

        Set<Constraint> constraints = new HashSet<>();

        constraints.add(c1);

        return constraints;
    }

    public static Set<Constraint> getConstraintsAll() {
        Set<Constraint> constraints = new HashSet<>();

        constraints.addAll(getConstraints1());
        constraints.addAll(getConstraints2());

        return constraints;
    }

    public static List<List<ChoiceNode>> getDomain1() {

        return m1.getTestDomain();
    }

    public static List<List<ChoiceNode>> getDomain2() {

        return m2.getTestDomain();
    }

    public static List<List<ChoiceNode>> getDomainAll() {
        List<List<ChoiceNode>> domain = new ArrayList<>();

        domain.addAll(getDomain1());
        domain.addAll(getDomain2());

        return domain;
    }

    public static List<ChoiceNode> getSet1() {
        List<ChoiceNode> set = new ArrayList<>();

        set.add(m1p1c1);
        set.add(m1p2c2);
        set.add(m1p3c3);

        return set;
    }

    public static List<ChoiceNode> getSet2() {
        List<ChoiceNode> set = new ArrayList<>();

        set.add(m2p1c1);
        set.add(m2p2c2);
        set.add(m2p3c3);

        return set;
    }

    public static List<ChoiceNode> getSetAll() {
        List<ChoiceNode> set = new ArrayList<>();

        set.addAll(getSet1());
        set.addAll(getSet2());

        return set;
    }

}
