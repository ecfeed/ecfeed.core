package com.ecfeed.core.serialization;

public class ModelParserStructureTest {

	// TODO MO-RE divide test into smaller tests which check concrete feature or data set
	//    @Test
	//    public void parseModelWithStructuresAndTestCases() {
	//
	//        String modelXml = getModelXml();
	//
	//        ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());
	//        ModelParser parser = new ModelParser();
	//        try {
	//            final ListOfStrings outErrorList = new ListOfStrings();
	//
	//            RootNode parsedModel = parser.parseModel(istream, null, outErrorList);
	//
	//            if (!outErrorList.isEmpty()) {
	//                fail(outErrorList.getCollectionOfStrings().get(0));
	//            }
	//
	//            assertEquals(20, parsedModel.getClasses().get(0).getMethods().get(0).getTestCases().size());
	//
	//        } catch (ParserException e) {
	//            fail();
	//        }
	//    }

	//    private String getModelXml() {
	//        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	//                "<Model name=\"model\" version=\"5\">\n" +
	//                "    <Class name=\"Class\">\n" +
	//                "        <Method name=\"Method1\">\n" +
	//                "            <Properties>\n" +
	//                "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
	//                "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
	//                "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
	//                "            </Properties>\n" +
	//                "            <Parameter name=\"M1P1\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                <Properties>\n" +
	//                "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                </Properties>\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Choice name=\"M1P1C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P1C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P1C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "            </Parameter>\n" +
	//                "            <Structure name=\"M1P2\">\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Parameter name=\"M1P21\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                    <Properties>\n" +
	//                "                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                    </Properties>\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Choice name=\"M1P21C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P21C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P21C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "                </Parameter>\n" +
	//                "                <Parameter name=\"M1P22\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                    <Properties>\n" +
	//                "                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                    </Properties>\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Choice name=\"M1P22C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P22C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P22C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "                </Parameter>\n" +
	//                "            </Structure>\n" +
	//                "            <Parameter name=\"M1P3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                <Properties>\n" +
	//                "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                </Properties>\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Choice name=\"M1P3C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P3C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P3C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "            </Parameter>\n" +
	//                "            <Structure name=\"M1P4\">\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Parameter name=\"M1P41\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                    <Properties>\n" +
	//                "                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                    </Properties>\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Choice name=\"M1P41C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P41C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P41C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "                </Parameter>\n" +
	//                "                <Parameter name=\"M1P42\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                    <Properties>\n" +
	//                "                        <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                    </Properties>\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Choice name=\"M1P42C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P42C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                    <Choice name=\"M1P42C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "                </Parameter>\n" +
	//                "            </Structure>\n" +
	//                "            <Parameter name=\"M1P5\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                <Properties>\n" +
	//                "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                </Properties>\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Choice name=\"M1P5C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P5C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"M1P5C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "            </Parameter>\n" +
	//                "            <Structure name=\"M1P6\">\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Structure name=\"M1P61\">\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Parameter name=\"M1P611\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                        <Properties>\n" +
	//                "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                        </Properties>\n" +
	//                "                        <Comments>\n" +
	//                "                            <TypeComments/>\n" +
	//                "                        </Comments>\n" +
	//                "                        <Choice name=\"M1P611C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                        <Choice name=\"M1P611C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                        <Choice name=\"M1P611C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "                    </Parameter>\n" +
	//                "                </Structure>\n" +
	//                "                <Structure name=\"M1P62\">\n" +
	//                "                    <Comments>\n" +
	//                "                        <TypeComments/>\n" +
	//                "                    </Comments>\n" +
	//                "                    <Parameter name=\"M1P621\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                        <Properties>\n" +
	//                "                            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                        </Properties>\n" +
	//                "                        <Comments>\n" +
	//                "                            <TypeComments/>\n" +
	//                "                        </Comments>\n" +
	//                "                        <Choice name=\"M1P621C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                        <Choice name=\"M1P621C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                        <Choice name=\"M1P621C3\" value=\"3\" isRandomized=\"false\">\n" +
	//                "                            <Choice name=\"M1P621C3A\" value=\"4\" isRandomized=\"false\"/>\n" +
	//                "                            <Choice name=\"M1P621C3B\" value=\"5\" isRandomized=\"false\"/>\n" +
	//                "                        </Choice>\n" +
	//                "                    </Parameter>\n" +
	//                "                </Structure>\n" +
	//                "            </Structure>\n" +
	//                "            <Constraint name=\"M1C1\" type=\"EF\">\n" +
	//                "                <Premise>\n" +
	//                "                    <Statement choice=\"M1P1C1\" parameter=\"M1P1\" relation=\"equal\"/>\n" +
	//                "                </Premise>\n" +
	//                "                <Consequence>\n" +
	//                "                    <Statement choice=\"M1P3C3\" parameter=\"M1P3\" relation=\"lessthan\"/>\n" +
	//                "                </Consequence>\n" +
	//                "            </Constraint>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3B\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C2\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C2\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C1\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C1\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3B\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3B\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C1\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C2\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3B\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C2\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3B\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C1\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C3:M1P621C3A\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <TestCase testSuite=\"default\\ssuite\">\n" +
	//                "                <TestParameter choice=\"M1P1C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P21C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P22C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P3C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P41C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P42C3\"/>\n" +
	//                "                <TestParameter choice=\"M1P5C2\"/>\n" +
	//                "                <TestParameter choice=\"M1P611C1\"/>\n" +
	//                "                <TestParameter choice=\"M1P621C2\"/>\n" +
	//                "            </TestCase>\n" +
	//                "            <Deployment>\n" +
	//                "                <Parameter name=\"M1P1\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P2:M1P21\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P2:M1P22\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P4:M1P41\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P4:M1P42\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P5\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P6:M1P61:M1P611\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "                <Parameter name=\"M1P6:M1P62:M1P621\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\"/>\n" +
	//                "            </Deployment>\n" +
	//                "        </Method>\n" +
	//                "        <Parameter name=\"C1P1\" type=\"int\">\n" +
	//                "            <Properties>\n" +
	//                "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "            </Properties>\n" +
	//                "            <Comments>\n" +
	//                "                <TypeComments/>\n" +
	//                "            </Comments>\n" +
	//                "            <Choice name=\"C1P1C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"C1P1C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"C1P1C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "        </Parameter>\n" +
	//                "        <Structure name=\"C1P2\">\n" +
	//                "            <Comments>\n" +
	//                "                <TypeComments/>\n" +
	//                "            </Comments>\n" +
	//                "            <Parameter name=\"C1P21\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                <Properties>\n" +
	//                "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                </Properties>\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Choice name=\"C1P21C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"C1P21C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"C1P21C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "            </Parameter>\n" +
	//                "            <Parameter name=\"C1P22\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "                <Properties>\n" +
	//                "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "                </Properties>\n" +
	//                "                <Comments>\n" +
	//                "                    <TypeComments/>\n" +
	//                "                </Comments>\n" +
	//                "                <Choice name=\"C1P22C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"C1P22C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "                <Choice name=\"C1P22C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "            </Parameter>\n" +
	//                "        </Structure>\n" +
	//                "    </Class>\n" +
	//                "    <Parameter name=\"P1\" type=\"int\">\n" +
	//                "        <Properties>\n" +
	//                "            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "        </Properties>\n" +
	//                "        <Comments>\n" +
	//                "            <TypeComments/>\n" +
	//                "        </Comments>\n" +
	//                "        <Choice name=\"P1C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "        <Choice name=\"P1C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "        <Choice name=\"P1C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "    </Parameter>\n" +
	//                "    <Structure name=\"P2\">\n" +
	//                "        <Comments>\n" +
	//                "            <TypeComments/>\n" +
	//                "        </Comments>\n" +
	//                "        <Parameter name=\"P21\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "            <Properties>\n" +
	//                "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "            </Properties>\n" +
	//                "            <Comments>\n" +
	//                "                <TypeComments/>\n" +
	//                "            </Comments>\n" +
	//                "            <Choice name=\"P21C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"P21C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"P21C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "        </Parameter>\n" +
	//                "        <Parameter name=\"P22\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
	//                "            <Properties>\n" +
	//                "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
	//                "            </Properties>\n" +
	//                "            <Comments>\n" +
	//                "                <TypeComments/>\n" +
	//                "            </Comments>\n" +
	//                "            <Choice name=\"P22C1\" value=\"1\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"P22C2\" value=\"2\" isRandomized=\"false\"/>\n" +
	//                "            <Choice name=\"P22C3\" value=\"3\" isRandomized=\"false\"/>\n" +
	//                "        </Parameter>\n" +
	//                "    </Structure>\n" +
	//                "</Model>\n";
	//    }
}
