
package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.model.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SatSolverConstraintNestedEvaluatorTest {

    private int countGeneratedTestCases(String xmlModel) {
        AbstractAlgorithm<ChoiceNode> algorithm = new CartesianProductAlgorithm<>();

        NodeMapper mapper = new NodeMapper();

        MethodNode method = getMethod(xmlModel);
        MethodNode methodDeployed = MethodDeployer.deploy(method, mapper);

        List<List<ChoiceNode>> tests = GeneratorHelper.generateTestCasesForMethod(methodDeployed, algorithm);

        return tests.size();
    }

    private MethodNode getMethod(String xmlModel) {
        RootNode model = ModelTestHelper.createModel(xmlModel);

        assert model != null;

        return model.getClasses().get(0).getMethods().get(0);
    }

    @Test
    public void linkedRootStructure() {
        assertEquals(1, countGeneratedTestCases(xmlLinkedRootStructure));
    }

    @Test
    public void nestedMethod() {
        assertEquals(1, countGeneratedTestCases(xmlNestedMethod));
    }

    private String xmlLinkedRootStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"tests\" version=\"5\">\n" +
            "    <Class name=\"C1.TestClass1\">\n" +
            "        <Method name=\"M1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Structure name=\"S1\" linked=\"false\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Structure name=\"S2\" linked=\"true\" link=\"S1\">\n" +
            "                    <Comments>\n" +
            "                        <TypeComments/>\n" +
            "                    </Comments>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "            <Parameter name=\"par2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"S1:S2:P1\" parameter=\"par2\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Structure name=\"S1\">\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Structure name=\"S2\">\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Parameter name=\"P1\" type=\"int\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"-1\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "        </Structure>\n" +
            "    </Structure>\n" +
            "</Model>";

    private String xmlNestedMethod = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"tests\" version=\"5\">\n" +
            "    <Class name=\"C1.TestClass1\">\n" +
            "        <Method name=\"M1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Structure name=\"S1\" linked=\"false\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Structure name=\"S2\" linked=\"false\">\n" +
            "                    <Comments>\n" +
            "                        <TypeComments/>\n" +
            "                    </Comments>\n" +
            "                    <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                        <Properties>\n" +
            "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                        </Properties>\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Choice name=\"choice1\" value=\"-1\" isRandomized=\"false\"/>\n" +
            "                        <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                        <Choice name=\"choice3\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                    </Parameter>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "            <Parameter name=\"par2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"S1:S2:par1\" parameter=\"par2\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Deployment>\n" +
            "                <Parameter name=\"S1:S2:par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
            "                <Parameter name=\"par2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
            "            </Deployment>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";
}
