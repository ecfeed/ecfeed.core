package com.ecfeed.core.generators.blackbox;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class GeneratorBlackBoxTest {

    @Test
    void nullPointerExceptionTest() {

        NWiseGeneratorTester tester = new NWiseGeneratorTester(getModel1Xml());
        tester.runGeneration();
    }

    private String getModel1Xml() {

        String result =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Model name=\"Untitled\" version=\"3\">\n" +
                        "    <Class name=\"com.example.test.TestClass1\">\n" +
//                        "        <Properties>\n" +
//                        "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
//                        "        </Properties>\n" +
                        "        <Method name=\"testMethod1\">\n" +
                        "            <Properties>\n" +
                        "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
                        "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
                        "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
                        "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
                        "            </Properties>\n" +
                        "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
                        "                <Properties>\n" +
                        "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
                        "                </Properties>\n" +
                        "                <Comments>\n" +
                        "                    <TypeComments/>\n" +
                        "                </Comments>\n" +
                        "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
                        "                <Choice name=\"choice2\" value=\"2\" isRandomized=\"false\"/>\n" +
                        "            </Parameter>\n" +
                        "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
                        "                <Properties>\n" +
                        "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
                        "                </Properties>\n" +
                        "                <Comments>\n" +
                        "                    <TypeComments/>\n" +
                        "                </Comments>\n" +
                        "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
                        "                <Choice name=\"choice2\" value=\"2\" isRandomized=\"false\"/>\n" +
                        "            </Parameter>\n" +
                        "            <Parameter name=\"arg3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
                        "                <Properties>\n" +
                        "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
                        "                </Properties>\n" +
                        "                <Comments>\n" +
                        "                    <TypeComments/>\n" +
                        "                </Comments>\n" +
                        "                <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
                        "                <Choice name=\"choice2\" value=\"2\" isRandomized=\"false\"/>\n" +
                        "            </Parameter>\n" +
                        "            <ImplicationConstraint name=\"constraint\">\n" +
                        "                <Premise>\n" +
                        "                    <StaticStatement value=\"true\"/>\n" +
                        "                </Premise>\n" +
                        "                <Consequence>\n" +
                        "                    <Statement choice=\"choice1\" parameter=\"arg1\" relation=\"notequal\"/>\n" +
                        "                </Consequence>\n" +
                        "            </ImplicationConstraint>\n" +
                        "        </Method>\n" +
                        "    </Class>\n" +
                        "</Model>\n";

        return result;
    }
}
