/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package com.ecfeed.core.utils;

public class TestModel {

    // REMARK - this model must be a copy of model used in dbupdater (project ecfeed.db, ecfeed.db.util.TestModel

    public static String getModelXml() {

        String xml = new String();

        xml += "<?xml version='1.0' encoding='UTF-8'?>";
        xml += "<Model name='TestModel1' version='2'>";
        xml += "    <Class name='test.Class1'>";
        xml += "        <Properties>";
        xml += "            <Property name='runOnAndroid' type='boolean' value='false'/>";
        xml += "        </Properties>";
        xml += "        <Method name='testMethod'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice11' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice12' value='2' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice21' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice22' value='2' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Constraint name='c1'>";
        xml += "            	<Premise>";
        xml += "            		<StaticStatement value='true'/>";
        xml += "            	</Premise>";
        xml += "            	<Consequence>";
        xml += "            		<Statement choice='choice11' parameter='arg1' relation='='/>";
        xml += "            	</Consequence>";
        xml += "            </Constraint>";
        xml += "            <Constraint name='c2'>";
        xml += "            	<Premise>";
        xml += "            		<StaticStatement value='true'/>";
        xml += "            	</Premise>";
        xml += "            	<Consequence>";
        xml += "            		<Statement choice='choice22' parameter='arg2' relation='='/>";
        xml += "            	</Consequence>";
        xml += "            </Constraint>";
        xml += "            <TestCase testSuite='default'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='default'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='default'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='default'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "        </Method>";
        xml += "        <Method name='testMethod'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='String' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice11' value='V11' isRandomized='false'/>";
        xml += "                <Choice name='choice12' value='V12' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='String' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice21' value='V21' isRandomized='false'/>";
        xml += "                <Choice name='choice22' value='V22' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <TestCase testSuite='first'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='first'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='first'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='first'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='second'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='second'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice21'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='second'>";
        xml += "                <TestParameter choice='choice11'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "            <TestCase testSuite='second'>";
        xml += "                <TestParameter choice='choice12'/>";
        xml += "                <TestParameter choice='choice22'/>";
        xml += "            </TestCase>";
        xml += "        </Method>";


        xml += "        <Method name='testMethodExpected'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='String' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice11' value='V11' isRandomized='false'/>";
        xml += "                <Choice name='choice12' value='V12' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='String' isExpected='true' expected='5' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "            </Parameter>";
        xml += "        </Method>";


        xml += "        <Method name='testMethod'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice11' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice12' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice13' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice14' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice15' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice16' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice17' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice18' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice21' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice22' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice23' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice24' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice25' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice26' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice27' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice28' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice31' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice32' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice33' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice34' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice35' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice36' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice37' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice38' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice41' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice42' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice43' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice44' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice45' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice46' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice47' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice48' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg5' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice51' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice52' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice53' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice54' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice55' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice56' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice57' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice58' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg6' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice61' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice62' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice63' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice64' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice65' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice66' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice67' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice68' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg7' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice71' value='1' isRandomized='false'/>";
        xml += "                <Choice name='choice72' value='2' isRandomized='false'/>";
        xml += "                <Choice name='choice73' value='3' isRandomized='false'/>";
        xml += "                <Choice name='choice74' value='4' isRandomized='false'/>";
        xml += "                <Choice name='choice75' value='5' isRandomized='false'/>";
        xml += "                <Choice name='choice76' value='6' isRandomized='false'/>";
        xml += "                <Choice name='choice77' value='7' isRandomized='false'/>";
        xml += "                <Choice name='choice78' value='8' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "        </Method>";

        xml += 	"        <Method name='randomized'>\n" +
                "            <Properties>\n" +
                "                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
                "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
                "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
                "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
                "            </Properties>\n" +
                "            <Parameter name='par1' type='int' isExpected='false' expected='0' linked='false'>\n" +
                "                <Properties>\n" +
                "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
                "                </Properties>\n" +
                "                <Comments>\n" +
                "                    <TypeComments/>\n" +
                "                </Comments>\n" +
                "                <Choice name='choiceR' value='1:3' isRandomized='true'/>\n" +
                "                <Choice name='choiceNR' value='2' isRandomized='false'/>\n" +
                "            </Parameter>\n" +
                "            <Parameter name='par2' type='String' isExpected='false' expected='0' linked='false'>\n" +
                "                <Properties>\n" +
                "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
                "                </Properties>\n" +
                "                <Comments>\n" +
                "                    <TypeComments/>\n" +
                "                </Comments>\n" +
                "                <Choice name='choiceR1' value='A[123]B' isRandomized='true'/>\n" +
                "                <Choice name='choiceR2' value='A[234]B' isRandomized='true'/>\n" +
                "            </Parameter>\n" +
                "            <Parameter name='expectedNumber' type='byte' isExpected='true' expected='0' linked='false'>\n" +
                "                <Properties>\n" +
                "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
                "                </Properties>\n" +
                "                <Comments>\n" +
                "                    <TypeComments/>\n" +
                "                </Comments>\n" +
                "            </Parameter>\n" +
                "            <Parameter name='expectedText' type='String' isExpected='true' expected='default-text' linked='false'>\n" +
                "                <Properties>\n" +
                "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
                "                </Properties>\n" +
                "                <Comments>\n" +
                "                    <TypeComments/>\n" +
                "                </Comments>\n" +
                "            </Parameter>\n" +
                "            <Constraint name='filterPar1' type='BF'>\n" +
                "                <Premise>\n" +
                "                    <StaticStatement value='true'/>\n" +
                "                </Premise>\n" +
                "                <Consequence>\n" +
                "                    <ValueStatement rightValue='3' parameter='par1' relation='equal'/>\n" +
                "                </Consequence>\n" +
                "            </Constraint>\n" +
                "            <Constraint name='assignValues' type='AS'>\n" +
                "                <Premise>\n" +
                "                    <StaticStatement value='true'/>\n" +
                "                </Premise>\n" +
                "                <Consequence>\n" +
                "                    <StatementArray operator='assign'>\n" +
                "                        <ValueStatement rightValue='7' parameter='expectedNumber' relation='assign'/>\n" +
                "                        <ValueStatement rightValue='ok' parameter='expectedText' relation='assign'/>\n" +
                "                    </StatementArray>\n" +
                "                </Consequence>\n" +
                "            </Constraint>\n" +
                "            <TestCase testSuite='with-constraints'>\n" +
                "                <TestParameter choice='choiceR'/>\n" +
                "                <TestParameter choice='choiceR2'/>\n" +
                "                <ExpectedValue value='7'/>\n" +
                "                <ExpectedValue value='ok'/>\n" +
                "            </TestCase>\n" +
                "            <TestCase testSuite='with-constraints'>\n" +
                "                <TestParameter choice='choiceR'/>\n" +
                "                <TestParameter choice='choiceR1'/>\n" +
                "                <ExpectedValue value='7'/>\n" +
                "                <ExpectedValue value='ok'/>\n" +
                "            </TestCase>\n" +
                "            <TestCase testSuite='no-constraints'>\n" +
                "                <TestParameter choice='choiceNR'/>\n" +
                "                <TestParameter choice='choiceR2'/>\n" +
                "                <ExpectedValue value='0'/>\n" +
                "                <ExpectedValue value='default-text'/>\n" +
                "            </TestCase>\n" +
                "            <TestCase testSuite='no-constraints'>\n" +
                "                <TestParameter choice='choiceR'/>\n" +
                "                <TestParameter choice='choiceR1'/>\n" +
                "                <ExpectedValue value='0'/>\n" +
                "                <ExpectedValue value='default-text'/>\n" +
                "            </TestCase>\n" +
                "            <TestCase testSuite='no-constraints'>\n" +
                "                <TestParameter choice='choiceR'/>\n" +
                "                <TestParameter choice='choiceR2'/>\n" +
                "                <ExpectedValue value='0'/>\n" +
                "                <ExpectedValue value='default-text'/>\n" +
                "            </TestCase>\n" +
                "            <TestCase testSuite='no-constraints'>\n" +
                "                <TestParameter choice='choiceNR'/>\n" +
                "                <TestParameter choice='choiceR1'/>\n" +
                "                <ExpectedValue value='0'/>\n" +
                "                <ExpectedValue value='default-text'/>\n" +
                "            </TestCase>\n" +
                "        </Method>";

        xml += "        <Method name='alternative1'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='String' isExpected='false' expected='' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='alt1p1c1' value='' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='alt1p2c1' value='0' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "        </Method>";
        xml += "        <Method name='alternative2'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='String' isExpected='false' expected='' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='alt2p1c1' value='' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='alt2p2c1' value='0' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "        </Method>";
        xml += "    </Class>";
        xml += "    <Class name='test.Class2'>";
        xml += "        <Properties>";
        xml += "            <Property name='runOnAndroid' type='boolean' value='false'/>";
        xml += "        </Properties>";
        xml += "        <Method name='abstractChoices'>";
        xml += "            <Properties>";
        xml += "                <Property name='methodRunner' type='String' value='Java Runner'/>";
        xml += "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>";
        xml += "                <Property name='wbBrowser' type='String' value='Chrome'/>";
        xml += "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>";
        xml += "            </Properties>";
        xml += "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "                <Comments>";
        xml += "                    <TypeComments/>";
        xml += "                </Comments>";
        xml += "                <Choice name='choice1' value='0' isRandomized='false'>";
        xml += "                    <Choice name='choice1' value='0' isRandomized='false'>";
        xml += "                        <Choice name='choice1' value='0' isRandomized='false'/>";
        xml += "                        <Choice name='choice2' value='0' isRandomized='false'/>";
        xml += "                    </Choice>";
        xml += "                </Choice>";
        xml += "                <Choice name='choice2' value='0' isRandomized='false'>";
        xml += "                    <Choice name='choice1' value='0' isRandomized='false'/>";
        xml += "                </Choice>";
        xml += "                <Choice name='choice3' value='0' isRandomized='false'/>";
        xml += "            </Parameter>";
        xml += "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>";
        xml += "                <Properties>";
        xml += "                    <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "                </Properties>";
        xml += "            </Parameter>";
        xml += "        </Method>";
        xml += "    </Class>";
        xml += "    <Parameter name='arg1' type='int'>";
        xml += "        <Properties>";
        xml += "            <Property name='wbIsOptional' type='boolean' value='false'/>";
        xml += "        </Properties>";
        xml += "        <Comments>";
        xml += "            <TypeComments/>";
        xml += "        </Comments>";
        xml += "        <Choice name='choice1' value='0' isRandomized='false'/>";
        xml += "        <Choice name='choice2' value='0' isRandomized='false'/>";
        xml += "    </Parameter>";
        xml += "</Model>";

        xml = xml.replace("'", "\"");

        return xml;
    }

}
