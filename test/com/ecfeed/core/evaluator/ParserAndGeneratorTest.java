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
//public class ParserAndGeneratorTest { // TODO MO-RE separate tests of serialization from tests of generation 
//
//
//	//	@Test
//	//	public void accessParameterInNestedStructureFromMethod1() {
//	//
//	//		RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethod1);
//	//
//	//		assertEquals(1, countGeneratedTestCases(model));
//	//	}
//
//
//	//	@Test
//	//	public void accessParameterInNestedStructureFromStructure2() {
//	//
//	//		RootNode rootNodeXml = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructure2);
//	//		//		RootNode rootNode = createModelAccessParameterInNestedStructureFromStructure2();
//	//		//
//	//		//		ModelComparator.compareRootNodes(rootNodeXml, rootNode);
//	//
//	//		assertEquals(1, countGeneratedTestCases(rootNodeXml));
//	//	}
//
//
//	//	@Test
//	//	public void AAlinkedRootStructure3() { // ERR
//	//
//	//		RootNode rootNodeXml = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethodLinkedToRoot3);
//	//		//		RootNode rootNode = createXmlAccessParameterInNestedStructureFromMethodLinkedToRoot3();
//	//		//		ModelComparator.compareRootNodes(rootNodeXml, rootNode);
//	//
//	//		assertEquals(1, countGeneratedTestCases(rootNodeXml));
//	//	}
//
//	//    @Test
//	//    public void linkedClassStructure() { // ERR
//	//    	
//	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromMethodLinkedToClass);
//	//    	
//	//        assertEquals(1, countGeneratedTestCases(model));
//	//    }
//
//	//    @Test
//	//    public void accessParameterInNestedStructureFromStructureLinkedToRoot() { // ERR
//	//    	
//	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructureLinkedToRoot);
//	//    	
//	//        assertEquals(1, countGeneratedTestCases(model));
//	//    }
//
//	//    @Test
//	//    public void accessParameterInNestedStructureFromStructureLinkedToClass() { // ERR
//	//    	
//	//    	RootNode model = ModelTestHelper.createModel(xmlAccessParameterInNestedStructureFromStructureLinkedToClass);
//	//    	
//	//        assertEquals(1, countGeneratedTestCases(model));
//	//    }
//
//	int countGeneratedTestCases(RootNode model) {
//
//		AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);
//
//		NodeMapper mapper = new NodeMapper();
//
//		assert model != null;
//
//<<<<<<< HEAD
//    @Test
//    public void linkTwoMethodParametersToGlobalIntRandomParameter() {
//        assertEquals(1, countGeneratedTestCases(xmlLinkTwoMethodParametersToGlobalIntRandomParameter));
//    }
//
//    @Test
//    public void accessParameterInNestedStructureFromMethod() {
//        assertEquals(1, countGeneratedTestCases(xmlAccessParameterInNestedStructureFromMethod));
//    }
//=======
//		MethodNode method = model.getClasses().get(0).getMethods().get(0);
//		MethodNode methodDeployed = MethodDeployer.deploy(method, mapper);
//>>>>>>> dev-model-rework-01
//
//		List<List<ChoiceNode>> tests = GeneratorHelper.generateTestCasesForMethod(methodDeployed, algorithm);
//
//		return tests.size();
//	}
//
//	//	private String xmlAccessParameterInNestedStructureFromMethod1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//	//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//	//			"    <Class name=\"C1\">\n" +
//	//			"        <Method name=\"M1\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//	//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//	//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Structure name=\"S3\" linked=\"false\">\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                            <Properties>\n" +
//	//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                            </Properties>\n" +
//	//			"                            <Comments>\n" +
//	//			"                                <TypeComments/>\n" +
//	//			"                            </Comments>\n" +
//	//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"                        </Parameter>\n" +
//	//			"                    </Structure>\n" +
//	//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                        <Properties>\n" +
//	//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                        </Properties>\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                    </Parameter>\n" +
//	//			"                </Structure>\n" +
//	//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                    <Properties>\n" +
//	//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                    </Properties>\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                </Parameter>\n" +
//	//			"            </Structure>\n" +
//	//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                <Premise>\n" +
//	//			"                    <StaticStatement value=\"true\"/>\n" +
//	//			"                </Premise>\n" +
//	//			"                <Consequence>\n" +
//	//			"                    <ParameterStatement rightParameter=\"S1:S2:S3:par1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\"/>\n" +
//	//			"                </Consequence>\n" +
//	//			"            </Constraint>\n" +
//	//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                <Premise>\n" +
//	//			"                    <StaticStatement value=\"true\"/>\n" +
//	//			"                </Premise>\n" +
//	//			"                <Consequence>\n" +
//	//			"                    <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"S1:S2:S3:par1\" relation=\"notequal\"/>\n" +
//	//			"                </Consequence>\n" +
//	//			"            </Constraint>\n" +
//	//			"            <Deployment>\n" +
//	//			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"            </Deployment>\n" +
//	//			"        </Method>\n" +
//	//			"        <Structure name=\"CS1\">\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//	//			"                <Properties>\n" +
//	//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                </Properties>\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"            </Parameter>\n" +
//	//			"        </Structure>\n" +
//	//			"    </Class>\n" +
//	//			"    <Structure name=\"GS1\">\n" +
//	//			"        <Comments>\n" +
//	//			"            <TypeComments/>\n" +
//	//			"        </Comments>\n" +
//	//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"        </Parameter>\n" +
//	//			"    </Structure>\n" +
//	//			"</Model>";
//
//	//	private String xmlAccessParameterInNestedStructureFromStructure2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//	//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//	//			"    <Class name=\"C1\">\n" +
//	//			"        <Method name=\"M1\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//	//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//	//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Structure name=\"S3\" linked=\"false\">\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                            <Properties>\n" +
//	//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                            </Properties>\n" +
//	//			"                            <Comments>\n" +
//	//			"                                <TypeComments/>\n" +
//	//			"                            </Comments>\n" +
//	//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"                        </Parameter>\n" +
//	//			"                    </Structure>\n" +
//	//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                        <Properties>\n" +
//	//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                        </Properties>\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                    </Parameter>\n" +
//	//			"                </Structure>\n" +
//	//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                    <Properties>\n" +
//	//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                    </Properties>\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                </Parameter>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"S2:S3:par1\" parameter=\"S2:ref\" relation=\"greaterthan\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"S2:ref\" parameter=\"S2:S3:par1\" relation=\"notequal\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"            </Structure>\n" +
//	//			"            <Deployment>\n" +
//	//			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"            </Deployment>\n" +
//	//			"        </Method>\n" +
//	//			"        <Structure name=\"CS1\">\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//	//			"                <Properties>\n" +
//	//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                </Properties>\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"            </Parameter>\n" +
//	//			"        </Structure>\n" +
//	//			"    </Class>\n" +
//	//			"    <Structure name=\"GS1\">\n" +
//	//			"        <Comments>\n" +
//	//			"            <TypeComments/>\n" +
//	//			"        </Comments>\n" +
//	//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"        </Parameter>\n" +
//	//			"    </Structure>\n" +
//	//			"</Model>";
//
//	//	private RootNode createModelAccessParameterInNestedStructureFromStructure2() {
//	//
//	//		RootNode rootNode = new RootNode("TestModel11", null);
//	//
//	//		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "C1", null);
//	//
//	//		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "M1", true, null);
//	//
//	//		CompositeParameterNode s1 = MethodNodeHelper.addNewCompositeParameter(methodNode, "S1", true, null);
//	//
//	//		CompositeParameterNode s2 = CompositeParameterNodeHelper.addNewCompositeParameter(s1, "S2", true, null);
//	//
//	//		// composite s3 under s2
//	//
//	//		CompositeParameterNode s3 = CompositeParameterNodeHelper.addNewCompositeParameter(s2, "S3", true, null);
//	//
//	//		BasicParameterNode par1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s3, "par1", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice3", "2", false, true, null);
//	//
//	//		// parameter under s2
//	//
//	//		BasicParameterNode ref = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s2, "ref", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(ref, "choice1", "1", false, true, null);
//	//
//	//		// parameter under S1
//	//
//	//		BasicParameterNode ref2 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s1, "ref", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(ref2, "choice1", "1", false, true, null);
//	//
//	//		// constraint under S1
//	//
//	//		StaticStatement precondition1 = new StaticStatement(EvaluationResult.TRUE);
//	//
//	//		RelationStatement postcondition1 =
//	//				RelationStatement.createRelationStatementWithParameterCondition(
//	//						ref, null, 
//	//						EMathRelation.GREATER_THAN, 
//	//						par1, null);
//	//
//	//		Constraint constraint1 = new Constraint(
//	//				"constraint", ConstraintType.BASIC_FILTER, precondition1, postcondition1, null);
//	//
//	//		ConstraintsParentNodeHelper.addNewConstraintNode(s1, constraint1, true, null);
//	//
//	//		// constraint under S2
//	//
//	//		StaticStatement precondition2 = new StaticStatement(EvaluationResult.TRUE);
//	//
//	//		RelationStatement postcondition2 =
//	//				RelationStatement.createRelationStatementWithParameterCondition(
//	//						par1, null, 
//	//						EMathRelation.NOT_EQUAL, 
//	//						ref, null);
//	//
//	//		Constraint constraint2 = new Constraint(
//	//				"constraint", ConstraintType.BASIC_FILTER, precondition2, postcondition2, null);
//	//
//	//		ConstraintsParentNodeHelper.addNewConstraintNode(s1, constraint2, true, null);
//	//
//	//		// structure C1 under class with parameter cs1 and choices
//	//
//	//		CompositeParameterNode c1 = ClassNodeHelper.addNewCompositeParameter(classNode, "CS1", true, null);
//	//
//	//		BasicParameterNode cs1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(c1, "cs1", "int", "0", true, null);
//	//
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice3", "2", false, true, null);
//	//
//	//		// structure GS1 under root node
//	//
//	//		CompositeParameterNode gs1 = RootNodeHelper.addNewCompositeParameter(rootNode, "GS1", true, null);
//	//
//	//		BasicParameterNode parGs1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(gs1, "gs1", "int", "0", true, null);
//	//
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice3", "2", false, true, null);
//	//
//	//		return rootNode;
//	//	}
//
//<<<<<<< HEAD
//    private String xmlLinkTwoMethodParametersToGlobalIntRandomParameter = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<Model name=\"TestModel11\" version=\"5\">\n" +
//            "    <Class name=\"TestClass\">\n" +
//            "        <Method name=\"testMethod\">\n" +
//            "            <Properties>\n" +
//            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//            "            </Properties>\n" +
//            "            <Parameter name=\"source1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"target\">\n" +
//            "                <Properties>\n" +
//            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                </Properties>\n" +
//            "            </Parameter>\n" +
//            "            <Parameter name=\"source2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"target\">\n" +
//            "                <Properties>\n" +
//            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                </Properties>\n" +
//            "            </Parameter>\n" +
//            "            <Deployment>\n" +
//            "                <Parameter PathOfParameter=\"source1\"/>\n" +
//            "                <Parameter PathOfParameter=\"source2\"/>\n" +
//            "            </Deployment>\n" +
//            "        </Method>\n" +
//            "    </Class>\n" +
//            "    <Parameter name=\"target\" type=\"int\">\n" +
//            "        <Properties>\n" +
//            "            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "        </Properties>\n" +
//            "        <Comments>\n" +
//            "            <TypeComments/>\n" +
//            "        </Comments>\n" +
//            "        <Choice name=\"choice1\" value=\"0:100\" isRandomized=\"true\"/>\n" +
//            "    </Parameter>\n" +
//            "</Model>";
//
//    private String xmlAccessParameterInNestedStructureFromMethodLinkedToRoot = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<Model name=\"TestModel11\" version=\"5\">\n" +
//            "    <Class name=\"C1\">\n" +
//            "        <Method name=\"M1\">\n" +
//            "            <Properties>\n" +
//            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//            "            </Properties>\n" +
//            "            <Structure name=\"S1\" linked=\"false\">\n" +
//            "                <Comments>\n" +
//            "                    <TypeComments/>\n" +
//            "                </Comments>\n" +
//            "                <Structure name=\"S2\" linked=\"false\">\n" +
//            "                    <Comments>\n" +
//            "                        <TypeComments/>\n" +
//            "                    </Comments>\n" +
//            "                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
//            "                        <Comments>\n" +
//            "                            <TypeComments/>\n" +
//            "                        </Comments>\n" +
//            "                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//            "                            <Properties>\n" +
//            "                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                            </Properties>\n" +
//            "                            <Comments>\n" +
//            "                                <TypeComments/>\n" +
//            "                            </Comments>\n" +
//            "                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//            "                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//            "                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//            "                        </Parameter>\n" +
//            "                    </Structure>\n" +
//            "                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//            "                        <Properties>\n" +
//            "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                        </Properties>\n" +
//            "                        <Comments>\n" +
//            "                            <TypeComments/>\n" +
//            "                        </Comments>\n" +
//            "                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//            "                    </Parameter>\n" +
//            "                </Structure>\n" +
//            "                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//            "                    <Properties>\n" +
//            "                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                    </Properties>\n" +
//            "                    <Comments>\n" +
//            "                        <TypeComments/>\n" +
//            "                    </Comments>\n" +
//            "                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//            "                </Parameter>\n" +
//            "            </Structure>\n" +
//            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
//            "                <Premise>\n" +
//            "                    <StaticStatement value=\"true\"/>\n" +
//            "                </Premise>\n" +
//            "                <Consequence>\n" +
//            "                    <ParameterStatement rightParameter=\"GS1:gs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//            "                </Consequence>\n" +
//            "            </Constraint>\n" +
//            "            <Constraint name=\"constraint\" type=\"BF\">\n" +
//            "                <Premise>\n" +
//            "                    <StaticStatement value=\"true\"/>\n" +
//            "                </Premise>\n" +
//            "                <Consequence>\n" +
//            "                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//            "                </Consequence>\n" +
//            "            </Constraint>\n" +
//            "        </Method>\n" +
//            "        <Structure name=\"CS1\">\n" +
//            "            <Comments>\n" +
//            "                <TypeComments/>\n" +
//            "            </Comments>\n" +
//            "            <Parameter name=\"cs1\" type=\"int\">\n" +
//            "                <Properties>\n" +
//            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "                </Properties>\n" +
//            "                <Comments>\n" +
//            "                    <TypeComments/>\n" +
//            "                </Comments>\n" +
//            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//            "                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//            "                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//            "            </Parameter>\n" +
//            "        </Structure>\n" +
//            "    </Class>\n" +
//            "    <Structure name=\"GS1\">\n" +
//            "        <Comments>\n" +
//            "            <TypeComments/>\n" +
//            "        </Comments>\n" +
//            "        <Parameter name=\"gs1\" type=\"int\">\n" +
//            "            <Properties>\n" +
//            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//            "            </Properties>\n" +
//            "            <Comments>\n" +
//            "                <TypeComments/>\n" +
//            "            </Comments>\n" +
//            "            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//            "            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//            "            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//            "        </Parameter>\n" +
//            "    </Structure>\n" +
//            "</Model>";
//=======
//	String xmlAccessParameterInNestedStructureFromMethodLinkedToRoot3 = 
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
//>>>>>>> dev-model-rework-01
//
//	//	private RootNode createXmlAccessParameterInNestedStructureFromMethodLinkedToRoot3() {
//	//
//	//		RootNode rootNode = new RootNode("TestModel11", null);
//	//
//	//		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "C1", null);
//	//
//	//		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "M1", true, null);
//	//
//	//		CompositeParameterNode s1 = MethodNodeHelper.addNewCompositeParameter(methodNode, "S1", true, null);
//	//
//	//		CompositeParameterNode s2 = CompositeParameterNodeHelper.addNewCompositeParameter(s1, "S2", true, null);
//	//
//	//		// composite s3 under s2
//	//
//	//		CompositeParameterNode s3 = CompositeParameterNodeHelper.addNewCompositeParameter(s2, "S3", true, null);
//	//
//	//		BasicParameterNode par1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s3, "par1", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(par1, "choice3", "2", false, true, null);
//	//
//	//		// parameter under s2
//	//
//	//		BasicParameterNode ref = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s2, "ref", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(ref, "choice1", "1", false, true, null);
//	//
//	//		// parameter under S1
//	//
//	//		BasicParameterNode ref2 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(s1, "ref", "int", "", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(ref2, "choice1", "1", false, true, null);
//	//
//	//		// structure C1 under class with parameter cs1 and choices
//	//
//	//		CompositeParameterNode c1 = ClassNodeHelper.addNewCompositeParameter(classNode, "CS1", true, null);
//	//
//	//		BasicParameterNode cs1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(c1, "cs1", "int", "0", true, null);
//	//
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(cs1, "choice3", "2", false, true, null);
//	//
//	//		// structure GS1 under root node
//	//
//	//		CompositeParameterNode gs1 = RootNodeHelper.addNewCompositeParameter(rootNode, "GS1", true, null);
//	//
//	//		// linking local structure S3 g
//	//
//	//		s3.setLinkToGlobalParameter(gs1);
//	//
//	//		// parameter under GS1 with choices
//	//
//	//		BasicParameterNode parGs1 = 
//	//				CompositeParameterNodeHelper.addNewBasicParameter(gs1, "gs1", "int", "0", true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice1", "0", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice2", "1", false, true, null);
//	//
//	//		BasicParameterNodeHelper.addNewChoice(parGs1, "choice3", "2", false, true, null);
//	//
//	//
//	//		// constraint under S1
//	//
//	//		StaticStatement precondition1 = new StaticStatement(EvaluationResult.TRUE);
//	//
//	//		RelationStatement postcondition1 =
//	//				RelationStatement.createRelationStatementWithParameterCondition(
//	//						ref, null, 
//	//						EMathRelation.GREATER_THAN, 
//	//						par1, null);
//	//
//	//		Constraint constraint1 = new Constraint(
//	//				"constraint", ConstraintType.BASIC_FILTER, precondition1, postcondition1, null);
//	//
//	//		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint1, true, null);
//	//
//	//		// constraint under S2
//	//
//	//		StaticStatement precondition2 = new StaticStatement(EvaluationResult.TRUE);
//	//
//	//		RelationStatement postcondition2 =
//	//				RelationStatement.createRelationStatementWithParameterCondition(
//	//						par1, null, 
//	//						EMathRelation.NOT_EQUAL, 
//	//						ref, null);
//	//
//	//		Constraint constraint2 = new Constraint(
//	//				"constraint", ConstraintType.BASIC_FILTER, precondition2, postcondition2, null);
//	//
//	//		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint2, true, null);
//	//
//	//
//	//		return rootNode;
//	//	}
//
//
//	//	private String xmlAccessParameterInNestedStructureFromMethodLinkedToClass = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//	//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//	//			"    <Class name=\"C1\">\n" +
//	//			"        <Method name=\"M1\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//	//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//	//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Structure name=\"S3\" linked=\"true\" link=\"C1:CS1\">\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                            <Properties>\n" +
//	//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                            </Properties>\n" +
//	//			"                            <Comments>\n" +
//	//			"                                <TypeComments/>\n" +
//	//			"                            </Comments>\n" +
//	//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"                        </Parameter>\n" +
//	//			"                    </Structure>\n" +
//	//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                        <Properties>\n" +
//	//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                        </Properties>\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                    </Parameter>\n" +
//	//			"                </Structure>\n" +
//	//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                    <Properties>\n" +
//	//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                    </Properties>\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                </Parameter>\n" +
//	//			"            </Structure>\n" +
//	//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                <Premise>\n" +
//	//			"                    <StaticStatement value=\"true\"/>\n" +
//	//			"                </Premise>\n" +
//	//			"                <Consequence>\n" +
//	//			"                    <ParameterStatement rightParameter=\"C1:CS1:cs1\" parameter=\"S1:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                </Consequence>\n" +
//	//			"            </Constraint>\n" +
//	//			"            <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                <Premise>\n" +
//	//			"                    <StaticStatement value=\"true\"/>\n" +
//	//			"                </Premise>\n" +
//	//			"                <Consequence>\n" +
//	//			"                    <ParameterStatement rightParameter=\"S1:ref\" parameter=\"C1:CS1:cs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                </Consequence>\n" +
//	//			"            </Constraint>\n" +
//	//			"        </Method>\n" +
//	//			"        <Structure name=\"CS1\">\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//	//			"                <Properties>\n" +
//	//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                </Properties>\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"            </Parameter>\n" +
//	//			"        </Structure>\n" +
//	//			"    </Class>\n" +
//	//			"    <Structure name=\"GS1\">\n" +
//	//			"        <Comments>\n" +
//	//			"            <TypeComments/>\n" +
//	//			"        </Comments>\n" +
//	//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"        </Parameter>\n" +
//	//			"    </Structure>\n" +
//	//			"</Model>";
//
//	//	private String xmlAccessParameterInNestedStructureFromStructureLinkedToRoot = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//	//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//	//			"    <Class name=\"C1\">\n" +
//	//			"        <Method name=\"M1\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//	//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//	//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Structure name=\"S3\" linked=\"true\" link=\"GS1\">\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                            <Properties>\n" +
//	//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                            </Properties>\n" +
//	//			"                            <Comments>\n" +
//	//			"                                <TypeComments/>\n" +
//	//			"                            </Comments>\n" +
//	//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"                        </Parameter>\n" +
//	//			"                    </Structure>\n" +
//	//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                        <Properties>\n" +
//	//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                        </Properties>\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                    </Parameter>\n" +
//	//			"                </Structure>\n" +
//	//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                    <Properties>\n" +
//	//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                    </Properties>\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                </Parameter>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"GS1:gs1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"GS1:gs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"            </Structure>\n" +
//	//			"            <Deployment>\n" +
//	//			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"            </Deployment>\n" +
//	//			"        </Method>\n" +
//	//			"        <Structure name=\"CS1\">\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//	//			"                <Properties>\n" +
//	//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                </Properties>\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"            </Parameter>\n" +
//	//			"        </Structure>\n" +
//	//			"    </Class>\n" +
//	//			"    <Structure name=\"GS1\">\n" +
//	//			"        <Comments>\n" +
//	//			"            <TypeComments/>\n" +
//	//			"        </Comments>\n" +
//	//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"        </Parameter>\n" +
//	//			"    </Structure>\n" +
//	//			"</Model>";
//
//	//	private String xmlAccessParameterInNestedStructureFromStructureLinkedToClass = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//	//			"<Model name=\"TestModel11\" version=\"5\">\n" +
//	//			"    <Class name=\"C1\">\n" +
//	//			"        <Method name=\"M1\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
//	//			"                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
//	//			"                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Structure name=\"S1\" linked=\"false\">\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Structure name=\"S2\" linked=\"false\">\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Structure name=\"S3\" linked=\"true\" link=\"C1:CS1\">\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Parameter name=\"par1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                            <Properties>\n" +
//	//			"                                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                            </Properties>\n" +
//	//			"                            <Comments>\n" +
//	//			"                                <TypeComments/>\n" +
//	//			"                            </Comments>\n" +
//	//			"                            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"                        </Parameter>\n" +
//	//			"                    </Structure>\n" +
//	//			"                    <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                        <Properties>\n" +
//	//			"                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                        </Properties>\n" +
//	//			"                        <Comments>\n" +
//	//			"                            <TypeComments/>\n" +
//	//			"                        </Comments>\n" +
//	//			"                        <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                    </Parameter>\n" +
//	//			"                </Structure>\n" +
//	//			"                <Parameter name=\"ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\">\n" +
//	//			"                    <Properties>\n" +
//	//			"                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                    </Properties>\n" +
//	//			"                    <Comments>\n" +
//	//			"                        <TypeComments/>\n" +
//	//			"                    </Comments>\n" +
//	//			"                    <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                </Parameter>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"C1:CS1:cs1\" parameter=\"S1:S2:ref\" relation=\"greaterthan\" rightParameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"                <Constraint name=\"constraint\" type=\"BF\">\n" +
//	//			"                    <Premise>\n" +
//	//			"                        <StaticStatement value=\"true\"/>\n" +
//	//			"                    </Premise>\n" +
//	//			"                    <Consequence>\n" +
//	//			"                        <ParameterStatement rightParameter=\"S1:S2:ref\" parameter=\"C1:CS1:cs1\" relation=\"notequal\" parameterContext=\"S1:S2:S3\"/>\n" +
//	//			"                    </Consequence>\n" +
//	//			"                </Constraint>\n" +
//	//			"            </Structure>\n" +
//	//			"            <Deployment>\n" +
//	//			"                <Parameter name=\"CS1:cs1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:S2:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"                <Parameter name=\"S1:ref\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\"/>\n" +
//	//			"            </Deployment>\n" +
//	//			"        </Method>\n" +
//	//			"        <Structure name=\"CS1\">\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Parameter name=\"cs1\" type=\"int\">\n" +
//	//			"                <Properties>\n" +
//	//			"                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"                </Properties>\n" +
//	//			"                <Comments>\n" +
//	//			"                    <TypeComments/>\n" +
//	//			"                </Comments>\n" +
//	//			"                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"                <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"            </Parameter>\n" +
//	//			"        </Structure>\n" +
//	//			"    </Class>\n" +
//	//			"    <Structure name=\"GS1\">\n" +
//	//			"        <Comments>\n" +
//	//			"            <TypeComments/>\n" +
//	//			"        </Comments>\n" +
//	//			"        <Parameter name=\"gs1\" type=\"int\">\n" +
//	//			"            <Properties>\n" +
//	//			"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
//	//			"            </Properties>\n" +
//	//			"            <Comments>\n" +
//	//			"                <TypeComments/>\n" +
//	//			"            </Comments>\n" +
//	//			"            <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice2\" value=\"1\" isRandomized=\"false\"/>\n" +
//	//			"            <Choice name=\"choice3\" value=\"2\" isRandomized=\"false\"/>\n" +
//	//			"        </Parameter>\n" +
//	//			"    </Structure>\n" +
//	//			"</Model>";
//}
