package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class SatSolverMethodDeployerTest {

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

    static RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(m1p1, EMathRelation.EQUAL, m1p1c1);
    static RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(m1p2, EMathRelation.LESS_THAN, m1p3c3);

    static Constraint m1c1 = new Constraint("M1C1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);

    static RelationStatement m2r1 = RelationStatement.createRelationStatementWithChoiceCondition(m2p1, EMathRelation.EQUAL, m2p1c1);
    static RelationStatement m2r2 = RelationStatement.createRelationStatementWithChoiceCondition(m2p2, EMathRelation.LESS_THAN, m2p3c3);

    static Constraint m2c1 = new Constraint("M2C1", ConstraintType.EXTENDED_FILTER, m2r1, m2r2,null);

    static {
        m1.addConstraint( new ConstraintNode("M1C1", m1c1, null));
        m2.addConstraint(new ConstraintNode("M2C1", m2c1, null));
    }

    public static MethodNode getMethodOrdered() {
        List<MethodParameterNode> parameters = new ArrayList<>();
        parameters.addAll(m1.getMethodParameters());
        parameters.addAll(m2.getMethodParameters());

        List<ConstraintNode> constraints = new ArrayList<>();
        constraints.addAll(m1.getConstraintNodes());
        constraints.addAll(m2.getConstraintNodes());

        return MethodDeployer.construct(parameters, constraints);
    }

    public static MethodNode getMethodAlternate() {
        List<MethodParameterNode> parameters = new ArrayList<>();
        parameters.add(m1.getMethodParameters().get(0));
        parameters.add(m2.getMethodParameters().get(0));
        parameters.add(m1.getMethodParameters().get(1));
        parameters.add(m2.getMethodParameters().get(1));
        parameters.add(m1.getMethodParameters().get(2));
        parameters.add(m2.getMethodParameters().get(2));

        List<ConstraintNode> constraints = new ArrayList<>();
        constraints.addAll(m1.getConstraintNodes());
        constraints.addAll(m2.getConstraintNodes());

        return MethodDeployer.construct(parameters, constraints);
    }

    @Test
    public void testOrdered() {
        MethodNode method = getMethodOrdered();

        Collection<Constraint> constraints = method.getConstraints();
        List<List<ChoiceNode>> testDomain = method.getTestDomain();

        SatSolverConstraintEvaluator evaluator = new SatSolverConstraintEvaluator(constraints, null);
        evaluator.initialize(testDomain);

        List<ChoiceNode> testSetTrue = Arrays.asList(
                testDomain.get(0).get(0),
                testDomain.get(1).get(1),
                testDomain.get(2).get(2),
                testDomain.get(3).get(0),
                testDomain.get(4).get(1),
                testDomain.get(5).get(2)
        );

        EvaluationResult resultsTrue = evaluator.evaluate(testSetTrue);
        System.out.println(resultsTrue);

        List<ChoiceNode> testSetFalse = Arrays.asList(
                testDomain.get(0).get(0),
                testDomain.get(1).get(2),
                testDomain.get(2).get(2),
                testDomain.get(3).get(0),
                testDomain.get(4).get(1),
                testDomain.get(5).get(2)
        );

        EvaluationResult resultsFalse = evaluator.evaluate(testSetFalse);
        System.out.println(resultsFalse);

        Assertions.assertEquals(resultsFalse.toString(), "FALSE");
        Assertions.assertEquals(resultsTrue.toString(), "TRUE");
    }

    @Test
    public void testAlternate() {
        MethodNode method = getMethodAlternate();

        Collection<Constraint> constraints = method.getConstraints();
        List<List<ChoiceNode>> testDomain = method.getTestDomain();

        SatSolverConstraintEvaluator evaluator = new SatSolverConstraintEvaluator(constraints, null);
        evaluator.initialize(testDomain);

        List<ChoiceNode> testSetTrue = Arrays.asList(
                testDomain.get(0).get(0),
                testDomain.get(1).get(0),
                testDomain.get(2).get(1),
                testDomain.get(3).get(1),
                testDomain.get(4).get(2),
                testDomain.get(5).get(2)
        );

        EvaluationResult resultsTrue = evaluator.evaluate(testSetTrue);
        System.out.println(resultsTrue);

        List<ChoiceNode> testSetFalse = Arrays.asList(
                testDomain.get(0).get(0),
                testDomain.get(1).get(0),
                testDomain.get(2).get(1),
                testDomain.get(3).get(2),
                testDomain.get(4).get(2),
                testDomain.get(5).get(2)
        );

        EvaluationResult resultsFalse = evaluator.evaluate(testSetFalse);
        System.out.println(resultsFalse);

        Assertions.assertEquals(resultsFalse.toString(), "FALSE");
        Assertions.assertEquals(resultsTrue.toString(), "TRUE");
    }
}
