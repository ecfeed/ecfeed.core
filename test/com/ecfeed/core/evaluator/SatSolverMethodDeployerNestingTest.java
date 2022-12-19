package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.*;
import org.junit.jupiter.api.Test;

import com.ecfeed.core.utils.EMathRelation;

public class SatSolverMethodDeployerNestingTest {

    static ClassNode classNode = new ClassNode("Class", null);

    static MethodNode m1 = ClassNodeHelper.addMethodToClass(classNode, "Method1", null);

    static BasicParameterNode m1p1 = new BasicParameterNode("M1P1", "int", "0", false);
    static ChoiceNode m1p1c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C1", "1");
    static ChoiceNode m1p1c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C2", "2");
    static ChoiceNode m1p1c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C3", "3");

    static CompositeParameterNode m1p2 = new CompositeParameterNode("M1P2", null);
    static BasicParameterNode m1p21 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p2, "M1P21", "int");
    static ChoiceNode m1p21c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C1", "1");
    static ChoiceNode m1p21c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C2", "2");
    static ChoiceNode m1p21c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C3", "3");
    static BasicParameterNode m1p22 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p2, "M1P22", "int");
    static ChoiceNode m1p22c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C1", "1");
    static ChoiceNode m1p22c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C2", "2");
    static ChoiceNode m1p22c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C3", "3");

    static BasicParameterNode m1p3 = new BasicParameterNode("M1P3", "int", "0", false);
    static ChoiceNode m1p3c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C1", "1");
    static ChoiceNode m1p3c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C2", "2");
    static ChoiceNode m1p3c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C3", "3");

    static CompositeParameterNode m1p4 = new CompositeParameterNode("M1P4", null);
    static BasicParameterNode m1p41 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p4, "M1P41", "int");
    static ChoiceNode m1p41c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C1", "1");
    static ChoiceNode m1p41c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C2", "2");
    static ChoiceNode m1p41c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C3", "3");
    static BasicParameterNode m1p42 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p4, "M1P42", "int");
    static ChoiceNode m1p42c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C1", "1");
    static ChoiceNode m1p42c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C2", "2");
    static ChoiceNode m1p42c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C3", "3");

    static BasicParameterNode m1p5 = new BasicParameterNode("M1P5", "int", "0", false);
    static ChoiceNode m1p5c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C1", "1");
    static ChoiceNode m1p5c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C2", "2");
    static ChoiceNode m1p5c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C3", "3");

    static CompositeParameterNode m1p6 = new CompositeParameterNode("M1P6", null);
    static CompositeParameterNode m1p61 = new CompositeParameterNode("M1P61", null);
    static BasicParameterNode m1p611 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p61, "M1P611", "int");
    static ChoiceNode m1p611c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C1", "1");
    static ChoiceNode m1p611c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C2", "2");
    static ChoiceNode m1p611c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C3", "3");
    static CompositeParameterNode m1p62 = new CompositeParameterNode("M1P62", null);
    static BasicParameterNode m1p621 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p62, "M1P621", "int");
    static ChoiceNode m1p621c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C1", "1");
    static ChoiceNode m1p621c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C2", "2");
    static ChoiceNode m1p621c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C3", "3");

    static RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(m1p1, EMathRelation.EQUAL, m1p1c1);
    static RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(m1p611, EMathRelation.LESS_THAN, m1p611c3);

    static Constraint m1c1 = new Constraint("M1C1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);

    static {
        m1.addParameter(m1p1);
        m1.addParameter(m1p2);
        m1.addParameter(m1p3);
        m1.addParameter(m1p4);
        m1.addParameter(m1p5);

        m1p6.addParameter(m1p61);
        m1p6.addParameter(m1p62);

        m1.addParameter(m1p6);

        m1.addConstraint(new ConstraintNode("M1C1", m1c1, null));
    }

    @Test
    public void testOrdered() {
        NodeMapper mapper = new NodeMapper();
//        MethodNode m2 = 
        		MethodDeployer.deploy(mapper, m1);

        System.out.println();
    }

}
