package com.ecfeed.core.parser.export;

import com.ecfeed.core.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModelParserExportHelper {

    public static MethodNode getBaseMethodNode() {
        RootNode rootNode = new RootNode("root", null);
        ClassNode classNode = new ClassNode("class", null);
        MethodNode methodNode = new MethodNode("method");

        rootNode.addClass(classNode);
        classNode.addMethod(methodNode);

        return methodNode;
    }

    public static void addParameterAsLocal(AbstractParameterNode parameter, MethodNode method) {

        method.addParameter(parameter);
    }

    public static void addParameterAsGlobalClass(AbstractParameterNode parameter, MethodNode method) {

        method.getClassNode().addParameter(parameter);
    }

    public static void addParameterAsGlobalRoot(AbstractParameterNode parameter, MethodNode method) {

        ((RootNode) method.getRoot()).addParameter(parameter);
    }

    public static TestCaseNode getTestCase(MethodNode method) {
        List<BasicParameterNode> parameters = new ArrayList<>();

        method.getParameters().forEach(e -> {
            AbstractParameterNode node = e.getLinkDestination();

            if (node instanceof BasicParameterNode) {
                parameters.add((BasicParameterNode) node);
            } else if (node instanceof CompositeParameterNode) {
                parameters.addAll(((CompositeParameterNode) node).getNestedBasicParameters(true));
            }
        });

        List<ChoiceNode> choices = parameters.stream().map(e -> e.getChoices().get(0)).collect(Collectors.toList());

        return new TestCaseNode("suite", null, choices);
    }

    public static AbstractParameterNode getStructureNode(String parameterName, AbstractParameterNode... parameters) {
        CompositeParameterNode structure = new CompositeParameterNode(parameterName, null);

        Arrays.stream(parameters).forEach(structure::addParameter);

        return structure;
    }

    public static AbstractParameterNode getParameterNode(String parameterName, String parameterType, String choiceValue) {
        BasicParameterNode parameter = BasicParameterNode.createLocalStandardParameter(parameterName, parameterType, null, null);
        ChoiceNode choice = new ChoiceNode("choice", choiceValue, null);

        parameter.addChoice(choice);

        return parameter;
    }

    public static AbstractParameterNode getLinkedParameterNode(String parameterName, AbstractParameterNode reference) {
        AbstractParameterNode parameterLinked = null;

        if (reference instanceof BasicParameterNode) {
            parameterLinked = BasicParameterNode.createLocalStandardParameter(parameterName, ((BasicParameterNode) reference).getType(), null, null);
            parameterLinked.setLinkToGlobalParameter(reference);
        } else if (reference instanceof CompositeParameterNode) {
            parameterLinked = new CompositeParameterNode(parameterName, null);
            parameterLinked.setLinkToGlobalParameter(reference);
        }

        return parameterLinked;
    }

    public static BasicParameterNode getParameterNodeRandom(String parameterName, String parameterType, String choiceValue) {
        BasicParameterNode parameter = BasicParameterNode.createLocalStandardParameter(parameterName, parameterType, null, null);
        ChoiceNode choice = new ChoiceNode("choice", choiceValue, null);
        choice.setRandomizedValue(true);

        parameter.addChoice(choice);

        return parameter;
    }

//---------------------------------------------------------------------------------------------------------------

    public static MethodNode modelLocal() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode p1 = ModelParserExportHelper.getParameterNode("p1", "String", "Lorem Ipsum");
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getParameterNode("p2", "String", "Lorem \"Ipsum\"");
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getParameterNode("p3", "String", "Lorem, Ipsum");
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getParameterNode("p4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelGlobalClass() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gc1 = ModelParserExportHelper.getParameterNode("gc1", "String", "Lorem Ipsum");
        ModelParserExportHelper.addParameterAsGlobalClass(gc1, method);

        AbstractParameterNode gc2 = ModelParserExportHelper.getParameterNode("gc2", "String", "Lorem \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalClass(gc2, method);

        AbstractParameterNode gc3 = ModelParserExportHelper.getParameterNode("gc3", "String", "Lorem, Ipsum");
        ModelParserExportHelper.addParameterAsGlobalClass(gc3, method);

        AbstractParameterNode gc4 = ModelParserExportHelper.getParameterNode("gc4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalClass(gc4, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", gc1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", gc2);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getLinkedParameterNode("p3", gc3);
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getLinkedParameterNode("p4", gc4);
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelGlobalRoot() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gr1 = ModelParserExportHelper.getParameterNode("gr1", "String", "Lorem Ipsum");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr1, method);

        AbstractParameterNode gr2 = ModelParserExportHelper.getParameterNode("gr2", "String", "Lorem \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr2, method);

        AbstractParameterNode gr3 = ModelParserExportHelper.getParameterNode("gr3", "String", "Lorem, Ipsum");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr3, method);

        AbstractParameterNode gr4 = ModelParserExportHelper.getParameterNode("gr4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr4, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", gr1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", gr2);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getLinkedParameterNode("p3", gr3);
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getLinkedParameterNode("p4", gr4);
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelMixed() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gr1 = ModelParserExportHelper.getParameterNode("gr1", "String", "Lorem Ipsum");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr1, method);

        AbstractParameterNode gc1 = ModelParserExportHelper.getParameterNode("gc1", "String", "Lorem \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalClass(gc1, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", gr1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", gc1);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getParameterNode("p3", "String", "Lorem, Ipsum");
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getParameterNode("p4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelLocalStructure() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode p1 = ModelParserExportHelper.getParameterNode("p1", "String", "Lorem Ipsum");
        AbstractParameterNode p2 = ModelParserExportHelper.getParameterNode("p2", "String", "Lorem \"Ipsum\"");
        AbstractParameterNode s1 = ModelParserExportHelper.getStructureNode("s1", p1, p2);
        ModelParserExportHelper.addParameterAsLocal(s1, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getParameterNode("p3", "String", "Lorem, Ipsum");
        AbstractParameterNode s2 = ModelParserExportHelper.getStructureNode("s2", p3);
        ModelParserExportHelper.addParameterAsLocal(s2, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getParameterNode("p4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelGlobalClassStructure() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gc1 = ModelParserExportHelper.getParameterNode("gc1", "String", "Lorem Ipsum");
        AbstractParameterNode gc2 = ModelParserExportHelper.getParameterNode("gc2", "String", "Lorem \"Ipsum\"");
        AbstractParameterNode sgc1 = ModelParserExportHelper.getStructureNode("sgc1", gc1, gc2);
        ModelParserExportHelper.addParameterAsGlobalClass(sgc1, method);

        AbstractParameterNode gc3 = ModelParserExportHelper.getParameterNode("gc3", "String", "Lorem, Ipsum");
        AbstractParameterNode sgc2 = ModelParserExportHelper.getStructureNode("sgc2", gc3);
        ModelParserExportHelper.addParameterAsGlobalClass(sgc2, method);

        AbstractParameterNode gc4 = ModelParserExportHelper.getParameterNode("gc4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalClass(gc4, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", sgc1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", sgc2);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getLinkedParameterNode("p3", gc4);
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        return method;
    }

    public static MethodNode modelGlobalRootStructure() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gr1 = ModelParserExportHelper.getParameterNode("gr1", "String", "Lorem Ipsum");
        AbstractParameterNode gr2 = ModelParserExportHelper.getParameterNode("gr2", "String", "Lorem \"Ipsum\"");
        AbstractParameterNode sgr1 = ModelParserExportHelper.getStructureNode("sgr1", gr1, gr2);
        ModelParserExportHelper.addParameterAsGlobalRoot(sgr1, method);

        AbstractParameterNode gr3 = ModelParserExportHelper.getParameterNode("gr3", "String", "Lorem, Ipsum");
        AbstractParameterNode sgr2 = ModelParserExportHelper.getStructureNode("sgr2", gr3);
        ModelParserExportHelper.addParameterAsGlobalRoot(sgr2, method);

        AbstractParameterNode gr4 = ModelParserExportHelper.getParameterNode("gr4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsGlobalRoot(gr4, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", sgr1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", sgr2);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getLinkedParameterNode("p3", gr4);
        ModelParserExportHelper.addParameterAsLocal(p3, method);

        return method;
    }

    public static MethodNode modelMixedStructure() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gr1 = ModelParserExportHelper.getParameterNode("gr1", "String", "Lorem Ipsum");
        AbstractParameterNode sgr1 = ModelParserExportHelper.getStructureNode("sgr1", gr1);
        ModelParserExportHelper.addParameterAsGlobalRoot(sgr1, method);

        AbstractParameterNode gc1 = ModelParserExportHelper.getParameterNode("gc1", "String", "Lorem \"Ipsum\"");
        AbstractParameterNode sgc1 = ModelParserExportHelper.getStructureNode("sgc1", gc1);
        ModelParserExportHelper.addParameterAsGlobalClass(sgc1, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", sgr1);
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", sgc1);
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = ModelParserExportHelper.getParameterNode("p3", "String", "Lorem, Ipsum");
        AbstractParameterNode s1 = ModelParserExportHelper.getStructureNode("s1", p3);
        ModelParserExportHelper.addParameterAsLocal(s1, method);

        AbstractParameterNode p4 = ModelParserExportHelper.getParameterNode("p4", "String", "Lorem, \"Ipsum\"");
        ModelParserExportHelper.addParameterAsLocal(p4, method);

        return method;
    }

    public static MethodNode modelRandom() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode p1 = ModelParserExportHelper.getParameterNodeRandom("p1", "String", "[a-zA-Z0-9]{5}");
        ModelParserExportHelper.addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = ModelParserExportHelper.getParameterNodeRandom("p2", "int", "0:9");
        ModelParserExportHelper.addParameterAsLocal(p2, method);

        return method;
    }

    public static MethodNode modelNested() {
        MethodNode method = ModelParserExportHelper.getBaseMethodNode();

        AbstractParameterNode gr1 = ModelParserExportHelper.getParameterNode("gr1", "String", "A");
        AbstractParameterNode sgr1 = ModelParserExportHelper.getStructureNode("sgr1", gr1);
        ModelParserExportHelper.addParameterAsGlobalClass(sgr1, method);

        AbstractParameterNode gc1 = ModelParserExportHelper.getParameterNode("gc1", "String", "B");
        AbstractParameterNode sgc1 = ModelParserExportHelper.getStructureNode("sgc1", gc1);
        ModelParserExportHelper.addParameterAsGlobalClass(sgc1, method);

        AbstractParameterNode p1 = ModelParserExportHelper.getLinkedParameterNode("p1", gr1);
        AbstractParameterNode s1 = ModelParserExportHelper.getStructureNode("s1", p1);
        AbstractParameterNode p2 = ModelParserExportHelper.getLinkedParameterNode("p2", gc1);
        AbstractParameterNode s2 = ModelParserExportHelper.getStructureNode("s2", s1, p2);
        AbstractParameterNode s3 = ModelParserExportHelper.getStructureNode("s3", s2);
        ModelParserExportHelper.addParameterAsLocal(s3, method);

        return method;
    }

}
