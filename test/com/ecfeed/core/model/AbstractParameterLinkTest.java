package com.ecfeed.core.model;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractParameterLinkTest {

    @Test
    public void test() {
        RootNode rootNode = new RootNode("Root", null);

        BasicParameterNode globalBasic = new BasicParameterNode("Basic", "int", null);
        rootNode.addParameter(globalBasic);
        ChoiceNode globalBasicChoice = new ChoiceNode("B1", "1");
        globalBasic.addChoice(globalBasicChoice);

        CompositeParameterNode globalComposite = new CompositeParameterNode("Composite", null);
        rootNode.addParameter(globalComposite);

        BasicParameterNode globalCompositeBasic = new BasicParameterNode("CompositeBasic", "int", null);
        globalComposite.addParameter(globalCompositeBasic);
        ChoiceNode globalCompositeBasicChoice = new ChoiceNode("CB1", "2");
        globalCompositeBasic.addChoice(globalCompositeBasicChoice);

        CompositeParameterNode globalCompositeComposite = new CompositeParameterNode("CompositeComposite", null);
        globalComposite.addParameter(globalCompositeComposite);

        BasicParameterNode globalCompositeCompositeBasic = new BasicParameterNode("CompositeCompositeBasic", "int", null);
        globalCompositeComposite.addParameter(globalCompositeCompositeBasic);
        ChoiceNode globalCompositeCompositeBasicChoice = new ChoiceNode("CCB1", "3");
        globalCompositeCompositeBasic.addChoice(globalCompositeCompositeBasicChoice);

        ClassNode classNode = new ClassNode("Class", null);
        rootNode.addClass(classNode);

        MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

        BasicParameterNode methodBasic = new BasicParameterNode("MethodBasic", "int", null);
        methodNode.addParameter(methodBasic);

        CompositeParameterNode methodComposite = new CompositeParameterNode("MethodComposite", null);
        methodNode.addParameter(methodComposite);

        methodBasic.setLinkToGlobalParameter(globalBasic);
        methodComposite.setLinkToGlobalParameter(globalComposite);

        NodeMapper mapper = new NodeMapper();
        MethodNode methodDeployed = MethodDeployer.deploy(methodNode, mapper);

        List<List<ChoiceNode>> domain = methodDeployed.getTestDomain();

        assertEquals(3, domain.size());
    }
}
