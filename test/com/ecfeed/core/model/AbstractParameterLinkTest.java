package com.ecfeed.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AbstractParameterLinkTest {

    private RootNode rootNode;

    private BasicParameterNode rootBasic;
    private ChoiceNode rootBasicChoice;

    private CompositeParameterNode rootComposite;

    private BasicParameterNode rootCompositeBasic;
    private ChoiceNode rootCompositeBasicChoice;

    private CompositeParameterNode rootCompositeComposite;

    private BasicParameterNode rootCompositeCompositeBasic;
    private ChoiceNode rootCompositeCompositeBasicChoice;

    private ClassNode classNode;

    private MethodNode methodNode;

    private BasicParameterNode methodBasic;
    private ChoiceNode methodBasicChoice;

    private CompositeParameterNode methodComposite;

    private BasicParameterNode methodCompositeBasic;
    private ChoiceNode methodCompositeBasicChoice;

    @Test
    public void test() {
        getModel();

        NodeMapper mapper;
        MethodNode methodDeployed;
        List<List<ChoiceNode>> domain;

        assertFalse(methodBasic.isLinked());
        assertFalse(methodComposite.isLinked());

        mapper = new NodeMapper();
        methodDeployed = MethodDeployer.deploy(methodNode, mapper);
        domain = methodDeployed.getTestDomain();

        assertEquals(2, domain.size());
        assertSame(mapper.getMappedNodeSource(domain.get(0).get(0)), methodBasicChoice);
        assertSame(mapper.getMappedNodeSource(domain.get(1).get(0)), methodCompositeBasicChoice);

        methodBasic.setLinkToGlobalParameter(rootBasic);
        methodComposite.setLinkToGlobalParameter(rootComposite);

        assertTrue(methodBasic.isLinked());
        assertTrue(methodComposite.isLinked());

        mapper = new NodeMapper();
        methodDeployed = MethodDeployer.deploy(methodNode, mapper);
        domain = methodDeployed.getTestDomain();

        assertEquals(3, domain.size());
        assertSame(mapper.getMappedNodeSource(domain.get(0).get(0)), rootBasicChoice);
        assertSame(mapper.getMappedNodeSource(domain.get(1).get(0)), rootCompositeBasicChoice);
        assertSame(mapper.getMappedNodeSource(domain.get(2).get(0)), rootCompositeCompositeBasicChoice);

        methodBasic.setLinkToGlobalParameter(null);
        methodComposite.setLinkToGlobalParameter(null);

        assertFalse(methodBasic.isLinked());
        assertFalse(methodComposite.isLinked());

        mapper = new NodeMapper();
        methodDeployed = MethodDeployer.deploy(methodNode, mapper);
        domain = methodDeployed.getTestDomain();

        assertEquals(2, domain.size());
    }

    private void getModel() {
        rootNode = new RootNode("Root", null);

        rootBasic = new BasicParameterNode("Basic", "int", null);
        rootNode.addParameter(rootBasic);
        rootBasicChoice = new ChoiceNode("B1", "1");
        rootBasic.addChoice(rootBasicChoice);

        rootComposite = new CompositeParameterNode("Composite", null);
        rootNode.addParameter(rootComposite);

        rootCompositeBasic = new BasicParameterNode("CompositeBasic", "int", null);
        rootComposite.addParameter(rootCompositeBasic);
        rootCompositeBasicChoice = new ChoiceNode("CB1", "2");
        rootCompositeBasic.addChoice(rootCompositeBasicChoice);

        rootCompositeComposite = new CompositeParameterNode("CompositeComposite", null);
        rootComposite.addParameter(rootCompositeComposite);

        rootCompositeCompositeBasic = new BasicParameterNode("CompositeCompositeBasic", "int", null);
        rootCompositeComposite.addParameter(rootCompositeCompositeBasic);
        rootCompositeCompositeBasicChoice = new ChoiceNode("CCB1", "3");
        rootCompositeCompositeBasic.addChoice(rootCompositeCompositeBasicChoice);

        classNode = new ClassNode("Class", null);
        rootNode.addClass(classNode);

        methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

        methodBasic = new BasicParameterNode("MethodBasic", "int", null);
        methodNode.addParameter(methodBasic);
        methodBasicChoice = new ChoiceNode("MB1", "4");
        methodBasic.addChoice(methodBasicChoice);

        methodComposite = new CompositeParameterNode("MethodComposite", null);
        methodNode.addParameter(methodComposite);

        methodCompositeBasic = new BasicParameterNode("MethodCompositeBasic", "int", null);
        methodComposite.addParameter(methodCompositeBasic);
        methodCompositeBasicChoice = new ChoiceNode("MCB1", "5");
        methodCompositeBasic.addChoice(methodCompositeBasicChoice);
    }
}
