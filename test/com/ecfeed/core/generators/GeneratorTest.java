//package com.ecfeed.core.evaluator;
//
//import java.util.List;
//
//import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
//import com.ecfeed.core.generators.algorithms.GeneratorHelper;
//import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.core.model.MethodDeployer;
//import com.ecfeed.core.model.MethodNode;
//import com.ecfeed.core.model.NodeMapper;
//import com.ecfeed.core.model.RootNode;
//
//public class GeneratorTest { 
//
//	@Test
//	public void accessParameterInNestedStructureFromMethod1() {
//
//		RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethod1);
//
//		assertEquals(1, countGeneratedTestCases(model));
//	}
//
//	int countGeneratedTestCases(RootNode model) {
//
//		AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);
//
//		NodeMapper mapper = new NodeMapper();
//
//		assert model != null;
//
//		MethodNode method = model.getClasses().get(0).getMethods().get(0);
//		MethodNode methodDeployed = MethodDeployer.deploy(method, mapper);
//
//
//		List<List<ChoiceNode>> tests = GeneratorHelper.generateTestCasesForMethod(methodDeployed, algorithm);
//
//		return tests.size();
//	}
//
////	private String xmlLinkTwoMethodParametersToGlobalIntRandomParameter = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
////			"<Model name=\"TestModel11\" version=\"5\">\n" +
////			"    <Class name=\"TestClass\">\n" +
////			"        <Method name=\"testMethod\">\n" +
////			"            <Properties>\n" +
////			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
////			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
////			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
////			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
////			"            </Properties>\n" +
////			"            <Parameter name=\"source1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"target\">\n" +
////			"                <Properties>\n" +
////			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
////			"                </Properties>\n" +
////			"            </Parameter>\n" +
////			"            <Parameter name=\"source2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"target\">\n" +
////			"                <Properties>\n" +
////			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
////			"                </Properties>\n" +
////			"            </Parameter>\n" +
////			"            <Deployment>\n" +
////			"                <Parameter PathOfParameter=\"source1\"/>\n" +
////			"                <Parameter PathOfParameter=\"source2\"/>\n" +
////			"            </Deployment>\n" +
////			"        </Method>\n" +
////			"    </Class>\n" +
////			"    <Parameter name=\"target\" type=\"int\">\n" +
////			"        <Properties>\n" +
////			"            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
////			"        </Properties>\n" +
////			"        <Comments>\n" +
////			"            <TypeComments/>\n" +
////			"        </Comments>\n" +
////			"        <Choice name=\"choice1\" value=\"0:100\" isRandomized=\"true\"/>\n" +
////			"    </Parameter>\n" +
////			"</Model>";
//
//	private String xmlAccessParameterInNestedStructureFromMethodLinkedToRoot = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//			"    <Class name=\"C1\">\n" +
//			"        <Method name=\"M1\">\n" +
//			"            <Properties>\n" +
//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//			"            </Properties>\n" +
//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//			"                <Comments>\n" +
//			"                    <TypeComments/>\n" +
//			"                </Comments>\n" +
//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//			"                    <Comments>\n" +
//			"                        <TypeComments/>\n" +
//			"                    </Comments>\n" +
//			"                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
//			"                        <Comments>\n" +
//			"                            <TypeComments/>\n" +
//			"                        </Comments>\n" +
//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//			"                            <Properties>\n" +
//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//			"                            </Properties>\n" +
//			"                            <Comments>\n" +
//			"                                <TypeComments/>\n" +
//			"                            </Comments>\n" +
//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//			"                        </Parameter>\n" +
//			"                    </Structure>\n" +
//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//			"                        <Properties>\n" +
//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//			"                        </Properties>\n" +
//			"                        <Comments>\n" +
//			"                            <TypeComments/>\n" +
//			"                        </Comments>\n" +
//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//			"                    </Parameter>\n" +
//			"                </Structure>\n" +
//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//			"                    <Properties>\n" +
//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//			"                    </Properties>\n" +
//			"                    <Comments>\n" +
//			"                        <TypeComments/>\n" +
//			"                    </Comments>\n" +
//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//			"                </Parameter>\n" +
//			"            </Structure>\n" +
//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//			"                <Premise>\n" +
//			"                    <StaticStatement value=\"true\"/>\n" +
//			"                </Premise>\n" +
//			"                <Consequence>\n" +
//			"                    <ParameterStatement rightParameter=\"GS1:gs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//			"                </Consequence>\n" +
//			"            </Constraint>\n" +
//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//			"                <Premise>\n" +
//			"                    <StaticStatement value=\"true\"/>\n" +
//			"                </Premise>\n" +
//			"                <Consequence>\n" +
//			"                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//			"                </Consequence>\n" +
//			"            </Constraint>\n" +
//			"        </Method>\n" +
//			"        <Structure name=\"CS1\">\n" +
//			"            <Comments>\n" +
//			"                <TypeComments/>\n" +
//			"            </Comments>\n" +
//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//			"                <Properties>\n" +
//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//			"                </Properties>\n" +
//			"                <Comments>\n" +
//			"                    <TypeComments/>\n" +
//			"                </Comments>\n" +
//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//			"            </Parameter>\n" +
//			"        </Structure>\n" +
//			"    </Class>\n" +
//			"    <Structure name=\"GS1\">\n" +
//			"        <Comments>\n" +
//			"            <TypeComments/>\n" +
//			"        </Comments>\n" +
//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//			"            <Properties>\n" +
//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//			"            </Properties>\n" +
//			"            <Comments>\n" +
//			"                <TypeComments/>\n" +
//			"            </Comments>\n" +
//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//			"        </Parameter>\n" +
//			"    </Structure>\n" +
//			"</Model>";
//	=======
//			String xmlAccessParameterInNestedStructureFromMethodLinkedToRoot3 = 
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//					"<Model name=\"TestModel11\" version=\"5\">\n" +
//					"    <Class name=\"C1\">\n" +
//					"        <Method name=\"M1\">\n" +
//					"            <Properties>\n" +
//					"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//					"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//					"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//					"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//					"            </Properties>\n" +
//					"            <Structure name=\"S1\" linked=\"false\">\n" +
//					"                <Comments>\n" +
//					"                    <TypeComments/>\n" +
//					"                </Comments>\n" +
//					"                <Structure name=\"S2\" linked=\"false\">\n" +
//					"                    <Comments>\n" +
//					"                        <TypeComments/>\n" +
//					"                    </Comments>\n" +
//					"                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
//					"                        <Comments>\n" +
//					"                            <TypeComments/>\n" +
//					"                        </Comments>\n" +
//					"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//					"                            <Properties>\n" +
//					"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//					"                            </Properties>\n" +
//					"                            <Comments>\n" +
//					"                                <TypeComments/>\n" +
//					"                            </Comments>\n" +
//					"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//					"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//					"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//					"                        </Parameter>\n" +
//					"                    </Structure>\n" +
//					"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//					"                        <Properties>\n" +
//					"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//					"                        </Properties>\n" +
//					"                        <Comments>\n" +
//					"                            <TypeComments/>\n" +
//					"                        </Comments>\n" +
//					"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//					"                    </Parameter>\n" +
//					"                </Structure>\n" +
//					"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//					"                    <Properties>\n" +
//					"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//					"                    </Properties>\n" +
//					"                    <Comments>\n" +
//					"                        <TypeComments/>\n" +
//					"                    </Comments>\n" +
//					"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//					"                </Parameter>\n" +
//					"            </Structure>\n" +
//					"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//					"                <Premise>\n" +
//					"                    <StaticStatement value=\"true\"/>\n" +
//					"                </Premise>\n" +
//					"                <Consequence>\n" +
//					"                    <ParameterStatement rightParameter=\"@TestModel11:GS1:gs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//					"                </Consequence>\n" +
//					"            </Constraint>\n" +
//					"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//					"                <Premise>\n" +
//					"                    <StaticStatement value=\"true\"/>\n" +
//					"                </Premise>\n" +
//					"                <Consequence>\n" +
//					"                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"@TestModel11:GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//					"                </Consequence>\n" +
//					"            </Constraint>\n" +
//					"        </Method>\n" +
//					"        <Structure name=\"CS1\">\n" +
//					"            <Comments>\n" +
//					"                <TypeComments/>\n" +
//					"            </Comments>\n" +
//					"            <Parameter name=\"cs1\" type=\"int\">\n" +
//					"                <Properties>\n" +
//					"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//					"                </Properties>\n" +
//					"                <Comments>\n" +
//					"                    <TypeComments/>\n" +
//					"                </Comments>\n" +
//					"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//					"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//					"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//					"            </Parameter>\n" +
//					"        </Structure>\n" +
//					"    </Class>\n" +
//					"    <Structure name=\"GS1\">\n" +
//					"        <Comments>\n" +
//					"            <TypeComments/>\n" +
//					"        </Comments>\n" +
//					"        <Parameter name=\"gs1\" type=\"int\">\n" +
//					"            <Properties>\n" +
//					"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//					"            </Properties>\n" +
//					"            <Comments>\n" +
//					"                <TypeComments/>\n" +
//					"            </Comments>\n" +
//					"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//					"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//					"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//					"        </Parameter>\n" +
//					"    </Structure>\n" +
//					"</Model>";
//
//}
