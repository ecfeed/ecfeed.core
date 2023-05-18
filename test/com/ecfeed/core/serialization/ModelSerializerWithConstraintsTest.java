package com.ecfeed.core.serialization;

import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.ecfeed.core.testutils.ModelTestUtils.assertElementsEqual;

public class ModelSerializerWithConstraintsTest {

    @Test
    public void test() {
        int version = ModelVersionDistributor.getCurrentSoftwareVersion();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ModelSerializer serializer = new ModelSerializer(os, version);

        RootNode model = createModelComposite(version);

        try {
            serializer.serialize(model);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            ModelParser parser = new ModelParser();
            RootNode parsedModel = parser.parseModel(is, null, new ListOfStrings());

            assertElementsEqual(model, parsedModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RootNode createModelComposite(int version) {
        RootNode model = new RootNode("model", null, version);
        BasicParameterNode p1 = RootNodeHelper.addNewGlobalBasicParameterToRoot(model, "P1", "int", true, null);
        p1.addChoice(new ChoiceNode("P1C1", "1"));
        p1.addChoice(new ChoiceNode("P1C2", "2"));
        p1.addChoice(new ChoiceNode("P1C3", "3"));

        CompositeParameterNode p2 = new CompositeParameterNode("P2", null);
        BasicParameterNode p21 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(p2, "P21", "int");
        p21.addChoice(new ChoiceNode("P21C1", "1"));
        p21.addChoice(new ChoiceNode("P21C2", "2"));
        p21.addChoice(new ChoiceNode("P21C3", "3"));
        BasicParameterNode p22 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(p2, "P22", "int");
        p22.addChoice(new ChoiceNode("P22C1", "1"));
        p22.addChoice(new ChoiceNode("P22C2", "2"));
        p22.addChoice(new ChoiceNode("P22C3", "3"));
        CompositeParameterNode p23 = new CompositeParameterNode("P23", null);
        BasicParameterNode p231 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(p2, "P231", "int");
        p231.addChoice(new ChoiceNode("P231C1", "1"));
        p231.addChoice(new ChoiceNode("P231C2", "2"));
        p231.addChoice(new ChoiceNode("P231C3", "3"));

        ClassNode c1 = new ClassNode("Class", null);

        BasicParameterNode c1p1 = ClassNodeHelper.addGlobalBasicParameterToClass(c1, "C1P1", "int", null);
        c1p1.addChoice(new ChoiceNode("C1P1C1", "1"));
        c1p1.addChoice(new ChoiceNode("C1P1C2", "2"));
        c1p1.addChoice(new ChoiceNode("C1P1C3", "3"));

        CompositeParameterNode c1p2 = new CompositeParameterNode("C1P2", null);
        BasicParameterNode c1p21 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(c1p2, "C1P21", "int");
        c1p21.addChoice(new ChoiceNode("C1P21C1", "1"));
        c1p21.addChoice(new ChoiceNode("C1P21C2", "2"));
        c1p21.addChoice(new ChoiceNode("C1P21C3", "3"));
        BasicParameterNode c1p22 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(c1p2, "C1P22", "int");
        c1p22.addChoice(new ChoiceNode("C1P22C1", "1"));
        c1p22.addChoice(new ChoiceNode("C1P22C2", "2"));
        c1p22.addChoice(new ChoiceNode("C1P22C3", "3"));
        CompositeParameterNode c1p23 = new CompositeParameterNode("C1P23", null);
        BasicParameterNode c1p231 = ParametersAndConstraintsParentNodeHelper.addGlobalParameterToParent(c1p2, "C1P231", "int");
        c1p231.addChoice(new ChoiceNode("C1P231C1", "1"));
        c1p231.addChoice(new ChoiceNode("C1P231C2", "2"));
        c1p231.addChoice(new ChoiceNode("C1P231C3", "3"));

        MethodNode m1 = ClassNodeHelper.addNewMethodToClass(c1, "Method1", true, null);

        BasicParameterNode m1p1 = 
        		//BasicParameterNode.createLocalStandardParameter("M1P1", "int", null, null);
        		new BasicParameterNode("M1P1", "int", "0", false, null);
        ChoiceNode m1p1c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p1, "M1P1C1", "1");
        ChoiceNode m1p1c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p1, "M1P1C2", "2");
//        ChoiceNode m1p1c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p1, "M1P1C3", "3");

        CompositeParameterNode m1p2 = new CompositeParameterNode("M1P2", null);
        BasicParameterNode m1p21 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p2, "M1P21", "int");
//        ChoiceNode m1p21c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p21, "M1P21C1", "1");
//        ChoiceNode m1p21c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p21, "M1P21C2", "2");
//        ChoiceNode m1p21c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p21, "M1P21C3", "3");
        BasicParameterNode m1p22 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p2, "M1P22", "int");
//        ChoiceNode m1p22c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p22, "M1P22C1", "1");
//        ChoiceNode m1p22c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p22, "M1P22C2", "2");
//        ChoiceNode m1p22c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p22, "M1P22C3", "3");

        BasicParameterNode m1p3 = new BasicParameterNode("M1P3", "int", "0", false, null);
//        ChoiceNode m1p3c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p3, "M1P3C1", "1");
//        ChoiceNode m1p3c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p3, "M1P3C2", "2");
        ChoiceNode m1p3c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p3, "M1P3C3", "3");

        CompositeParameterNode m1p4 = new CompositeParameterNode("M1P4", null);
        BasicParameterNode m1p41 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p4, "M1P41", "int");
//        ChoiceNode m1p41c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p41, "M1P41C1", "1");
//        ChoiceNode m1p41c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p41, "M1P41C2", "2");
//        ChoiceNode m1p41c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p41, "M1P41C3", "3");
        BasicParameterNode m1p42 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p4, "M1P42", "int");
//        ChoiceNode m1p42c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p42, "M1P42C1", "1");
//        ChoiceNode m1p42c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p42, "M1P42C2", "2");
//        ChoiceNode m1p42c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p42, "M1P42C3", "3");

        BasicParameterNode m1p5 = new BasicParameterNode("M1P5", "int", "0", false, null);
//        ChoiceNode m1p5c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p5, "M1P5C1", "1");
//        ChoiceNode m1p5c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p5, "M1P5C2", "2");
//        ChoiceNode m1p5c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p5, "M1P5C3", "3");

        CompositeParameterNode m1p6 = new CompositeParameterNode("M1P6", null);
        CompositeParameterNode m1p61 = new CompositeParameterNode("M1P61", null);
        BasicParameterNode m1p611 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p61, "M1P611", "int");
        ChoiceNode m1p611c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p611, "M1P611C1", "1");
//        ChoiceNode m1p611c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p611, "M1P611C2", "2");
//        ChoiceNode m1p611c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p611, "M1P611C3", "3");
        BasicParameterNode m1p612 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p61, "M1P612", "int");
//        ChoiceNode m1p612c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p612, "M1P612C1", "1");
//        ChoiceNode m1p612c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p612, "M1P612C2", "2");
//        ChoiceNode m1p612c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p612, "M1P612C3", "3");
        BasicParameterNode m1p613 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p61, "M1P613", "int");
//        ChoiceNode m1p613c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p613, "M1P613C1", "1");
//        ChoiceNode m1p613c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p613, "M1P613C2", "2");
//        ChoiceNode m1p613c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p613, "M1P613C3", "3");
        CompositeParameterNode m1p62 = new CompositeParameterNode("M1P62", null);
        BasicParameterNode m1p621 = ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(m1p62, "M1P621", "int");
//        ChoiceNode m1p621c1 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p621, "M1P621C1", "1");
//        ChoiceNode m1p621c2 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p621, "M1P621C2", "2");
        ChoiceNode m1p621c3 =
                MethodParameterNodeHelper.addNewChoiceToMethodParameter(m1p621, "M1P621C3", "3");
//        ChoiceNode m1p621c3a =
                ChoiceNodeHelper.addChoiceToChoice(m1p621c3, "M1P621C3A", "4");
//        ChoiceNode m1p621c3b =
                ChoiceNodeHelper.addChoiceToChoice(m1p621c3, "M1P621C3B", "5");
        CompositeParameterNode m1p63 = new CompositeParameterNode("M1P63", null);
        CompositeParameterNode m1p64 = new CompositeParameterNode("M1P64", null);
        CompositeParameterNode m1p65 = new CompositeParameterNode("M1P65", null);
        CompositeParameterNode m1p66 = new CompositeParameterNode("M1P66", null);

        model.addParameter(p2);

        p2.addParameter(p23);

        c1.addParameter(c1p2);

        c1p2.addParameter(c1p23);

        m1.addParameter(m1p1);
        m1.addParameter(m1p2);
        m1.addParameter(m1p3);
        m1.addParameter(m1p4);
        m1.addParameter(m1p5);

        m1p6.addParameter(m1p61);
        m1p6.addParameter(m1p62);
        m1p6.addParameter(m1p63);
        m1p6.addParameter(m1p64);
        m1p6.addParameter(m1p65);
        m1p6.addParameter(m1p66);

        m1.addParameter(m1p6);

        model.addClass(c1);

// External constraint.

        RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(
                m1p1, null, EMathRelation.EQUAL, m1p1c1);
        RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(
                m1p3, null, EMathRelation.LESS_THAN, m1p3c3);

        Constraint m1con1 = new Constraint("M1Con1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);
        m1.addConstraint(new ConstraintNode("M1Con1", m1con1, null));

// Internal constraint.

        RelationStatement m1p61r1 = RelationStatement.createRelationStatementWithChoiceCondition(
                m1p611, m1p61, EMathRelation.EQUAL, m1p611c1);
        RelationStatement m1p61r2 = RelationStatement.createRelationStatementWithParameterCondition(
                m1p612, m1p61, EMathRelation.LESS_THAN, m1p613, null);

        Constraint m1p61con1 = new Constraint("M1P61Con1", ConstraintType.EXTENDED_FILTER, m1p61r1, m1p61r2,null);
        m1p61.addConstraint(new ConstraintNode("M1P61Con1", m1p61con1, null));

// Mixed constraint.

        RelationStatement m1r3 = RelationStatement.createRelationStatementWithChoiceCondition(
                m1p1, null, EMathRelation.EQUAL, m1p1c2);
        RelationStatement m1r4 = RelationStatement.createRelationStatementWithParameterCondition(
                m1p612, null, EMathRelation.LESS_THAN, m1p613, null);

        Constraint m1con2 = new Constraint("M1P61Con1", ConstraintType.EXTENDED_FILTER, m1r3, m1r4,null);

        m1.addConstraint(new ConstraintNode("M1Con2", m1con2, null));

        NodeMapper mapper = new NodeMapper();
        MethodDeployer.deploy(m1, mapper);

// Link to a parameter (global).

        m1p63.setLinkToGlobalParameter(p2);

// Link to a parameter (global, nested).

        m1p64.setLinkToGlobalParameter(p23);

// Link to a parameter (class).

        m1p65.setLinkToGlobalParameter(c1p2);

// Link to a parameter (class, nested).

        m1p66.setLinkToGlobalParameter(c1p23);

        return model;
    }

}
