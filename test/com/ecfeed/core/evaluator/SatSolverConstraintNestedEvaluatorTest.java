
package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
import com.ecfeed.core.model.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SatSolverConstraintNestedEvaluatorTest {

    private int countGeneratedTestCases(String xmlModel) {
        AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);

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
    public void linkedClassStructure() {
        assertEquals(1, countGeneratedTestCases(xmlLinkedClassStructure));
    }

    @Test
    public void accessParameterInNestedStructureFromMethod() {
        assertEquals(1, countGeneratedTestCases(xmlAccessParameterInNestedStructureFromMethod));
    }

    @Test
    public void accessParameterInNestedStructureFromMethodLinkedToRoot() {
        assertEquals(1, countGeneratedTestCases(xmlAccessParameterInNestedStructureFromMethodLinkedToRoot));
    }

    @Test
    public void accessParameterInNestedStructureFromMethodLinkedToClass() {
        assertEquals(1, countGeneratedTestCases(xmlAccessParameterInNestedStructureFromMethodLinkedToClass));
    }

    @Test
    public void accessParameterInNestedStructureFromStructure() {
        assertEquals(1, countGeneratedTestCases(xmlAccessParameterInNestedStructureFromStructure));
    }

    @Test
    public void accessParameterInNestedStructureFromStructureLinkedToRoot() {
        assertEquals(1, countGeneratedTestCases(dupa1));
    }

    @Test
    public void accessParameterInNestedStructureFromStructureLinkedToClass() {
        assertEquals(1, countGeneratedTestCases(dupa2));
    }

    private String xmlLinkedRootStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"M1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Structure name=\"S1\" linked=\"true\" link=\"S1\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "            </Structure>\n" +
            "            <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"S1:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Structure name=\"S1\">\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Parameter name=\"par1\" type=\"int\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        </Parameter>\n" +
            "    </Structure>\n" +
            "</Model>";

    private String xmlLinkedClassStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"M1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Structure name=\"S1\" linked=\"true\" link=\"C1:S1\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "            </Structure>\n" +
            "            <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"C1:S1:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "        <Structure name=\"S1\">\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Parameter name=\"par1\" type=\"int\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "        </Structure>\n" +
            "    </Class>\n" +
            "</Model>";

    private String xmlAccessParameterInNestedStructureFromMethod = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"M1\">\n" +
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
            "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Structure name=\"S1\" linked=\"false\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Structure name=\"S2\" linked=\"false\">\n" +
            "                    <Comments>\n" +
            "                        <TypeComments/>\n" +
            "                    </Comments>\n" +
            "                    <Structure name=\"S3\" linked=\"false\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                            <Properties>\n" +
            "                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                            </Properties>\n" +
            "                            <Comments>\n" +
            "                                <TypeComments/>\n" +
            "                            </Comments>\n" +
            "                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "                        </Parameter>\n" +
            "                    </Structure>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"S1:S2:S3:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>";

    private String xmlAccessParameterInNestedStructureFromMethodLinkedToRoot = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"M1\">\n" +
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
            "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Structure name=\"S1\" linked=\"false\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Structure name=\"S2\" linked=\"false\">\n" +
            "                    <Comments>\n" +
            "                        <TypeComments/>\n" +
            "                    </Comments>\n" +
            "                    <Structure name=\"S3\" linked=\"true\" link=\"S1\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                    </Structure>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"S1:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Structure name=\"S1\">\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Parameter name=\"par1\" type=\"int\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        </Parameter>\n" +
            "    </Structure>\n" +
            "</Model>";

    private String xmlAccessParameterInNestedStructureFromMethodLinkedToClass = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
            "        <Method name=\"M1\">\n" +
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
            "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Structure name=\"S1\" linked=\"false\">\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Structure name=\"S2\" linked=\"false\">\n" +
            "                    <Comments>\n" +
            "                        <TypeComments/>\n" +
            "                    </Comments>\n" +
            "                    <Structure name=\"S3\" linked=\"true\" link=\"C1:S1\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                    </Structure>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"C1:S1:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "        <Structure name=\"S1\">\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Parameter name=\"par1\" type=\"int\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "        </Structure>\n" +
            "    </Class>\n" +
            "</Model>";

    private String xmlAccessParameterInNestedStructureFromStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
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
            "                    <Structure name=\"S3\" linked=\"false\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                            <Properties>\n" +
            "                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                            </Properties>\n" +
            "                            <Comments>\n" +
            "                                <TypeComments/>\n" +
            "                            </Comments>\n" +
            "                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "                        </Parameter>\n" +
            "                    </Structure>\n" +
            "                    <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                        <Properties>\n" +
            "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                        </Properties>\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                    </Parameter>\n" +
            "                    <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                        <Premise>\n" +
            "                            <StaticStatement value=\"true\"/>\n" +
            "                        </Premise>\n" +
            "                        <Consequence>\n" +
            "                            <ParameterStatement context=\"S1:S2\" rightParameter=\"S3:par1\" parameter=\"par1\" relation=\"greaterthan\"/>\n" +
            "                        </Consequence>\n" +
            "                    </Constraint>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>";

    private String dupa1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
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
            "                    <Structure name=\"S3\" linked=\"true\" link=\"S1\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                    </Structure>\n" +
            "                    <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                        <Properties>\n" +
            "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                        </Properties>\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                    </Parameter>\n" +
            "                    <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                        <Premise>\n" +
            "                            <StaticStatement value=\"true\"/>\n" +
            "                        </Premise>\n" +
            "                        <Consequence>\n" +
            "                            <ParameterStatement context=\"S1:S2\" rightParameter=\"S1:par1\" parameter=\"S1:S2:par1\" relation=\"greaterthan\"/>\n" +
            "                        </Consequence>\n" +
            "                    </Constraint>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Structure name=\"S1\">\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Parameter name=\"par1\" type=\"int\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        </Parameter>\n" +
            "    </Structure>\n" +
            "</Model>";

    private String dupa2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"TestModel11\" version=\"5\">\n" +
            "    <Class name=\"C1\">\n" +
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
            "                    <Structure name=\"S3\"  linked=\"true\" link=\"C1:S1\">\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                    </Structure>\n" +
            "                    <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
            "                        <Properties>\n" +
            "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                        </Properties>\n" +
            "                        <Comments>\n" +
            "                            <TypeComments/>\n" +
            "                        </Comments>\n" +
            "                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                    </Parameter>\n" +
            "                    <Constraint name=\"constraint\" type=\"BF\">\n" +
            "                        <Premise>\n" +
            "                            <StaticStatement value=\"true\"/>\n" +
            "                        </Premise>\n" +
            "                        <Consequence>\n" +
            "                            <ParameterStatement rightParameter=\"C1:S1:par1\" parameter=\"S1:S2:par1\" relation=\"greaterthan\"/>\n" +
            "                        </Consequence>\n" +
            "                    </Constraint>\n" +
            "                </Structure>\n" +
            "            </Structure>\n" +
            "        </Method>\n" +
            "        <Structure name=\"S1\">\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Parameter name=\"par1\" type=\"int\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "        </Structure>\n" +
            "    </Class>\n" +
            "</Model>\n";
}
