package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
import com.ecfeed.core.model.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SatSolverConstraintAbstractChoices {

    private List<List<ChoiceNode>> getGeneratedTestCases(String xmlModel) {
        AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);

        NodeMapper mapper = new NodeMapper();

        MethodNode method = getMethod(xmlModel);
        MethodNode methodDeployed = MethodDeployer.deploy(method, mapper);

        return GeneratorHelper.generateTestCasesForMethod(methodDeployed, algorithm);
    }

    private MethodNode getMethod(String xmlModel) {
        RootNode model = ModelTestHelper.createModel(xmlModel);

        assert model != null;

        return model.getClasses().get(0).getMethods().get(0);
    }

//    @Test // TO-DO mo-re FIX
    public void abstractChoices() {
        List<List<ChoiceNode>> tests = getGeneratedTestCases(xmlLinkedRootStructure);

        for (List<ChoiceNode> test : tests) {
            assertEquals("true", test.get(1).getValueString());
        }
    }

    private String xmlLinkedRootStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"tests\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"abs\" value=\"0\" isRandomized=\"false\">\n" +
            "                    <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                    <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                    <Choice name=\"choice3\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                </Choice>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"par2\" type=\"boolean\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"false\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"true\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\" type=\"EF\">\n" +
            "                <Premise>\n" +
            "                    <Statement choice=\"abs\" parameter=\"par1\" relation=\"equal\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <Statement choice=\"choice2\" parameter=\"par2\" relation=\"equal\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Deployment>\n" +
            "                <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
            "                <Parameter name=\"par2\" type=\"boolean\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
            "            </Deployment>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";
}
