package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
import com.ecfeed.core.model.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserAndGeneratorTest {

	@Test
	public void accessParameterInNestedStructureFromMethod() { // OK

		RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethod);

		assertEquals(1, countGeneratedTestCases(model));
	}

	// XYX
	//    @Test
	//    public void accessParameterInNestedStructureFromStructure() { // ERR
	//    	
	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructure);
	//    	
	//        assertEquals(1, countGeneratedTestCases(model));
	//    }

	// XYX
	//    @Test
	//    public void linkedRootStructure() { // ERR
	//    	
	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethodLinkedToRoot);
	//    	
	//        assertEquals(1, countGeneratedTestCases(model));
	//    }


	// XYX
	//    @Test
	//    public void linkedClassStructure() { // ERR
	//    	
	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethodLinkedToClass);
	//    	
	//        assertEquals(1, countGeneratedTestCases(model));
	//    }

	// XYX
	//    @Test
	//    public void accessParameterInNestedStructureFromStructureLinkedToRoot() { // ERR
	//    	
	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructureLinkedToRoot);
	//    	
	//        assertEquals(1, countGeneratedTestCases(model));
	//    }

	// XYX
	//    @Test
	//    public void accessParameterInNestedStructureFromStructureLinkedToClass() { // ERR
	//    	
	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructureLinkedToClass);
	//    	
	//        assertEquals(1, countGeneratedTestCases(model));
	//    }

	private int countGeneratedTestCases(RootNode model) {

		AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);

		NodeMapper mapper = new NodeMapper();

		assert model != null;

		MethodNode method = model.getClasses().get(0).getMethods().get(0);
		MethodNode methodDeployed = MethodDeployer.deploy(method, mapper);

		List<List<ChoiceNode>> tests = GeneratorHelper.generateTestCasesForMethod(methodDeployed, algorithm);

		return tests.size();
	}

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
			"            <Structure name=\"S1\" linked=\"false\">\n" +
			"                <Comments>\n" +
			"                    <TypeComments/>\n" +
			"                </Comments>\n" +
			"                <Structure name=\"S2\" linked=\"false\">\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"            </Structure>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"GS1:gs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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
			"            <Structure name=\"S1\" linked=\"false\">\n" +
			"                <Comments>\n" +
			"                    <TypeComments/>\n" +
			"                </Comments>\n" +
			"                <Structure name=\"S2\" linked=\"false\">\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Structure name=\"S3\" linked=\"true\" link=\"C1:CS1\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"            </Structure>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"C1:CS1:cs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"C1:CS1:cs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"            </Structure>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"S1:S2:S3:par1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                <Premise>\n" +
			"                    <StaticStatement value=\"true\"/>\n" +
			"                </Premise>\n" +
			"                <Consequence>\n" +
			"                    <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"S1:S2:S3:par1\" relation=\"notequal\"/>\n" +
			"                </Consequence>\n" +
			"            </Constraint>\n" +
			"            <Deployment>\n" +
			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"            </Deployment>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"S1:S2:S3:par1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"S1:S2:S3:par1\" relation=\"notequal\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"            </Structure>\n" +
			"            <Deployment>\n" +
			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"            </Deployment>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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

	private String xmlAccessParameterInNestedStructureFromStructureLinkedToRoot = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
			"                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"GS1:gs1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"            </Structure>\n" +
			"            <Deployment>\n" +
			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"            </Deployment>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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

	private String xmlAccessParameterInNestedStructureFromStructureLinkedToClass = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
			"                    <Structure name=\"S3\" linked=\"true\" link=\"C1:CS1\">\n" +
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
			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                        <Properties>\n" +
			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                        </Properties>\n" +
			"                        <Comments>\n" +
			"                            <TypeComments/>\n" +
			"                        </Comments>\n" +
			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                    </Parameter>\n" +
			"                </Structure>\n" +
			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
			"                    <Properties>\n" +
			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
			"                    </Properties>\n" +
			"                    <Comments>\n" +
			"                        <TypeComments/>\n" +
			"                    </Comments>\n" +
			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
			"                </Parameter>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"C1:CS1:cs1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
			"                    <Premise>\n" +
			"                        <StaticStatement value=\"true\"/>\n" +
			"                    </Premise>\n" +
			"                    <Consequence>\n" +
			"                        <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"C1:CS1:cs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
			"                    </Consequence>\n" +
			"                </Constraint>\n" +
			"            </Structure>\n" +
			"            <Deployment>\n" +
			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
			"            </Deployment>\n" +
			"        </Method>\n" +
			"        <Structure name=\"CS1\">\n" +
			"            <Comments>\n" +
			"                <TypeComments/>\n" +
			"            </Comments>\n" +
			"            <Parameter name=\"cs1\" type=\"int\">\n" +
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
			"    <Structure name=\"GS1\">\n" +
			"        <Comments>\n" +
			"            <TypeComments/>\n" +
			"        </Comments>\n" +
			"        <Parameter name=\"gs1\" type=\"int\">\n" +
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
}
