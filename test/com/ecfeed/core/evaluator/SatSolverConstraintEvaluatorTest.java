
package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SatSolverConstraintEvaluatorTest {

    @Test
    public void TestOrderOfInts() {
        assertEquals(9 * 8 * 7 * 6 / 2 / 3 / 4, countGeneratedTestCases(xmlOrderOfInts));
    }

    @Test
    public void TestInequalityOfInts() {
        assertEquals(6 * 3 * 3 * 3 * 3 * 3, countGeneratedTestCases(xmlInequalityOfInts));
    }

    @Test
    public void TestNondistinctValuesInts() {
        assertEquals(4 * 3 * 3 * 3 * 3, countGeneratedTestCases(xmlNondistinctValuesInts));
    }

    @Test
    public void TestDeeperConstraints1() {
        assertEquals(4 * 3 * 2, countGeneratedTestCases(xmlDeeperConstraints1));
    }

    @Test
    public void TestDeeperConstraints2() {
        assertEquals(4 * 4 * 4 * 4 - 4 * 3 * 2, countGeneratedTestCases(xmlDeeperConstraints2));
    }

    @Test
    public void TestDeeperConstraints3() {
        assertEquals(4 * 3 * 2, countGeneratedTestCases(xmlDeeperConstraints3));
    }

    @Test
    public void TestExpectedValue1() {
        assertEquals(2, countGeneratedTestCases(xmlExpectedValue1));
    }

    @Test
    public void TestExpectedValue2() {
        assertEquals(4, countGeneratedTestCases(xmlExpectedValue2));
    }

    @Test
    public void TestRanges1() {
        assertEquals(4, countGeneratedTestCases(xmlRanges1));
    }

    @Test
    public void TestRanges2() {
        assertEquals(6, countGeneratedTestCases(xmlRanges2));
    }

    @Test
    public void TestRanges3() {
        assertEquals(4, countGeneratedTestCases(xmlRanges3));
    }

    @Test
    public void TestRangesDouble() {
        assertEquals(7, countGeneratedTestCases(xmlRangesDouble));
    }

    @Test
    public void TestRangesDoubleSmall() {
        assertEquals(0, countGeneratedTestCases(xmlRangesDoubleSmall));
    }

    @Test
    public void TestMixedTypeOrder() {
        assertEquals(10, countGeneratedTestCases(xmlMixedTypeOrder));
    }

    @Test
    public void TestNastyRanges() {
        assertEquals(2, countGeneratedTestCases(xmlNastyRanges));
    }

    @Test
    public void TestMixedTypeEq1() {
        assertEquals(1, countGeneratedTestCases(xmlMixedTypeEq1));
    }

    @Test
    public void TestMixedTypeEq2() {
        assertEquals(1, countGeneratedTestCases(xmlMixedTypeEq2));
    }

    @Test
    public void TestMixedTypeEq3() {
        assertEquals(1, countGeneratedTestCases(xmlMixedTypeEq3));
    }

    @Test
    public void TestMixedTypeOverflow() {
        assertEquals(0, countGeneratedTestCases(xmlMixedTypeOverflow));
    }

    @Test
    public void TestCmpFixedVsRange() {
        assertEquals(3, countGeneratedTestCases(xmlCmpFixedVsRange));
    }

    private int countGeneratedTestCases(String xmlModel) {
        RootNode model = ModelTestHelper.createModel(xmlModel);

        ClassNode classNode = model.getClasses().get(0);
        MethodNode methodNode = classNode.getMethods().get(0);

        List<List<ChoiceNode>> input = new ArrayList<>();
        for (MethodParameterNode arg : methodNode.getMethodParameters())
            if (arg.isExpected()) {
                input.add(Collections.singletonList(null));
            } else
                input.add(arg.getLeafChoicesWithCopies());


        IConstraintEvaluator<ChoiceNode> evaluator = new SatSolverConstraintEvaluator(methodNode.getAllConstraints(), methodNode);

        IAlgorithm<ChoiceNode> algorithm = new CartesianProductAlgorithm<>();
//        IAlgorithm<ChoiceNode> algorithm = new AwesomeNWiseAlgorithm<>(2,100);


        int cnt = 0;
        try {
            algorithm.initialize(input, evaluator, new SimpleProgressMonitor());

            while (algorithm.getNext() != null)
                cnt++;

        } catch (Exception e) {
            fail("Unexpected generator exception: " + e.getMessage());
        }

        return cnt;
    }

    private String xmlOrderOfInts = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg5' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StatementArray operator='and'>\n" +
            "                        <ParameterStatement rightParameter='arg2' parameter='arg1' relation='&lt;='/>\n" +
            "                        <ParameterStatement rightParameter='arg3' parameter='arg2' relation='&lt;='/>\n" +
            "                        <ParameterStatement rightParameter='arg3' parameter='arg4' relation='&gt;='/>\n" +
            "                        <ParameterStatement rightParameter='arg4' parameter='arg5' relation='&gt;='/>\n" +
            "                    </StatementArray>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='4' isRandomized='false'/>\n" +
            "        <Choice name='choice5' value='5' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlInequalityOfInts = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg5' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg6' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StatementArray operator='and'>\n" +
            "                        <ParameterStatement rightParameter='arg2' parameter='arg1' relation='≠'/>\n" +
            "                        <ParameterStatement rightParameter='arg3' parameter='arg2' relation='≠'/>\n" +
            "                        <ParameterStatement rightParameter='arg4' parameter='arg3' relation='≠'/>\n" +
            "                        <ParameterStatement rightParameter='arg5' parameter='arg4' relation='≠'/>\n" +
            "                        <ParameterStatement rightParameter='arg6' parameter='arg5' relation='≠'/>\n" +
            "                    </StatementArray>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice5' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice6' value='2' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlNondistinctValuesInts = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter='arg2' parameter='arg1' relation='='/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter='arg3' parameter='arg1' relation='&lt;'/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter='arg4' parameter='arg2' relation='&gt;='/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice5' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice6' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice7' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice8' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice9' value='3' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlDeeperConstraints1 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StatementArray operator='and'>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice1' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice2' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice3' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice4' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                    </StatementArray>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='4' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlDeeperConstraints2 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StatementArray operator='and'>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice1' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice1' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice2' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice2' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice3' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice3' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice4' parameter='arg1' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg2' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg3' relation='='/>\n" +
            "                            <Statement choice='choice4' parameter='arg4' relation='='/>\n" +
            "                        </StatementArray>\n" +
            "                    </StatementArray>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StaticStatement value='false'/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='4' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlDeeperConstraints3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Model name='n0000wEct25656' version='2'>\n" +
            "    <Class name='com.ecfeed.core.junit5.EcFeedModelTest'>\n" +
            "        <Properties>\n" +
            "            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Method name='ecFeedModelTest'>\n" +
            "            <Properties>\n" +
            "                <Property name='methodRunner' type='String' value='Web Runner'/>\n" +
            "                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
            "                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
            "                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
            "            </Properties>\n" +
            "            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg3' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name='arg4' type='int' isExpected='false' expected='0' linked='true' link='arg1'>\n" +
            "                <Properties>\n" +
            "                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name='constraint'>\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value='true'/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StatementArray operator='and'>\n" +
            "                        <StatementArray operator='or'>\n" +
            "                            <Statement choice='choice1' parameter='arg1' relation='='/>\n" +
            "                            <StatementArray operator='or'>\n" +
            "                                <Statement choice='choice1' parameter='arg2' relation='='/>\n" +
            "                                <StatementArray operator='or'>\n" +
            "                                    <Statement choice='choice1' parameter='arg3' relation='='/>\n" +
            "                                    <StatementArray operator='or'>\n" +
            "                                        <Statement choice='choice1' parameter='arg4' relation='='/>\n" +
            "                                    </StatementArray>\n" +
            "                                </StatementArray>\n" +
            "                            </StatementArray>\n" +
            "                        </StatementArray>\n" +
            "                        <StatementArray operator='and'>\n" +
            "                            <StatementArray operator='or'>\n" +
            "                                <Statement choice='choice2' parameter='arg1' relation='='/>\n" +
            "                                <StatementArray operator='or'>\n" +
            "                                    <Statement choice='choice2' parameter='arg2' relation='='/>\n" +
            "                                    <StatementArray operator='or'>\n" +
            "                                        <Statement choice='choice2' parameter='arg3' relation='='/>\n" +
            "                                        <StatementArray operator='or'>\n" +
            "                                            <Statement choice='choice2' parameter='arg4' relation='='/>\n" +
            "                                        </StatementArray>\n" +
            "                                    </StatementArray>\n" +
            "                                </StatementArray>\n" +
            "                            </StatementArray>\n" +
            "                            <StatementArray operator='and'>\n" +
            "                                <StatementArray operator='or'>\n" +
            "                                    <Statement choice='choice3' parameter='arg1' relation='='/>\n" +
            "                                    <StatementArray operator='or'>\n" +
            "                                        <Statement choice='choice3' parameter='arg2' relation='='/>\n" +
            "                                        <StatementArray operator='or'>\n" +
            "                                            <Statement choice='choice3' parameter='arg3' relation='='/>\n" +
            "                                            <StatementArray operator='or'>\n" +
            "                                                <Statement choice='choice3' parameter='arg4' relation='='/>\n" +
            "                                            </StatementArray>\n" +
            "                                        </StatementArray>\n" +
            "                                    </StatementArray>\n" +
            "                                </StatementArray>\n" +
            "                                <StatementArray operator='and'>\n" +
            "                                    <StatementArray operator='or'>\n" +
            "                                        <Statement choice='choice4' parameter='arg1' relation='='/>\n" +
            "                                        <StatementArray operator='or'>\n" +
            "                                            <Statement choice='choice4' parameter='arg2' relation='='/>\n" +
            "                                            <StatementArray operator='or'>\n" +
            "                                                <Statement choice='choice4' parameter='arg3' relation='='/>\n" +
            "                                                <StatementArray operator='or'>\n" +
            "                                                    <Statement choice='choice4' parameter='arg4' relation='='/>\n" +
            "                                                </StatementArray>\n" +
            "                                            </StatementArray>\n" +
            "                                        </StatementArray>\n" +
            "                                    </StatementArray>\n" +
            "                                </StatementArray>\n" +
            "                            </StatementArray>\n" +
            "                        </StatementArray>\n" +
            "                    </StatementArray>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name='arg1' type='int'>\n" +
            "        <Properties>\n" +
            "            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name='choice1' value='1' isRandomized='false'/>\n" +
            "        <Choice name='choice2' value='2' isRandomized='false'/>\n" +
            "        <Choice name='choice3' value='3' isRandomized='false'/>\n" +
            "        <Choice name='choice4' value='4' isRandomized='false'/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlExpectedValue1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Root\" version=\"2\">\n" +
            "    <Class name=\"com.example.test.TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
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
            "                <Choice name=\"choice1\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"true\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "            </Parameter>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlExpectedValue2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"TestClass1:arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"TestClass1:arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg8\" type=\"int\" isExpected=\"true\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <Statement choice=\"choice1\" parameter=\"arg2\" relation=\"=\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ExpectedValueStatement parameter=\"arg8\" value=\"1\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <Statement choice=\"choice1\" parameter=\"arg1\" relation=\"=\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ExpectedValueStatement parameter=\"arg8\" value=\"0\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "        <Parameter name=\"arg1\" type=\"int\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Choice name=\"choice1\" value=\"1\" isRandomized=\"false\"/>\n" +
            "            <Choice name=\"choice2\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        </Parameter>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlRanges1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"TestClass1:arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"true\" link=\"TestClass1:arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "        <Parameter name=\"arg1\" type=\"int\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Comments>\n" +
            "                <TypeComments/>\n" +
            "            </Comments>\n" +
            "            <Choice name=\"choice1\" value=\"1:5\" isRandomized=\"true\"/>\n" +
            "            <Choice name=\"choice2\" value=\"3:8\" isRandomized=\"true\"/>\n" +
            "        </Parameter>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlRanges2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"5:20\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"9\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice3\" value=\"7:9\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice4\" value=\"6\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice5\" value=\"11\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"&gt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"&gt;\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <Statement choice=\"choice2\" parameter=\"arg3\" relation=\"≠\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlRanges3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"5:20\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"9\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1:15\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice3\" value=\"7:9\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice4\" value=\"6\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice5\" value=\"11\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg3\" relation=\"&gt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg3\" parameter=\"arg1\" relation=\"&gt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlRangesDouble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1.0:10.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6.0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"5.0:20.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"9.0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1.0:15.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"6.0:10.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice3\" value=\"7.0:9.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice4\" value=\"6.0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice5\" value=\"11.0\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg3\" relation=\"&gt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg3\" parameter=\"arg1\" relation=\"&gt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";


    private String xmlRangesDoubleSmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Root\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1.0:4.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"2.0:5.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"3.0:6.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg3\" parameter=\"arg2\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg1\" parameter=\"arg3\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlMixedTypeOrder = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"n0000wEct25656\" version=\"2\">\n" +
            "    <Class name=\"com.ecfeed.core.junit5.EcFeedModelTest\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"ecFeedModelTest\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Web Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"7\" linked=\"true\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbElementType\" type=\"String\" value=\"Text\"/>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                    <Property name=\"wbFindByType\" type=\"String\" value=\"Id\"/>\n" +
            "                    <Property name=\"wbFindByValue\" type=\"String\" value=\"arg1\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"true\" link=\"arg2\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "    <Parameter name=\"arg1\" type=\"int\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name=\"choice1a\" value=\"1\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice1b\" value=\"1\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice1c\" value=\"1\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice2a\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice2b\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice2c\" value=\"2\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice3\" value=\"3\" isRandomized=\"false\"/>\n" +
            "    </Parameter>\n" +
            "    <Parameter name=\"arg2\" type=\"float\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Comments>\n" +
            "            <TypeComments/>\n" +
            "        </Comments>\n" +
            "        <Choice name=\"choice1\" value=\"0.0\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice2\" value=\"1.0\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice3\" value=\"1.5\" isRandomized=\"false\"/>\n" +
            "        <Choice name=\"choice4\" value=\"4.0\" isRandomized=\"false\"/>\n" +
            "    </Parameter>\n" +
            "</Model>\n";

    private String xmlNastyRanges = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"int\" isExpected=\"false\" expected=\"\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:20\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"3\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"5:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"float\" isExpected=\"false\" expected=\"0.0\" linked=\"false\" link=\"arg1\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1.0:5.0\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"7.5\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"5.0:8.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"long\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"-10:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"1:1000000000\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg4\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0.0:1.0E-4\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"100.0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"1.0:1.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg5\" type=\"byte\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"-128:127\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"127\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"0:0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <StatementArray operator=\"and\">\n" +
            "                        <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"=\"/>\n" +
            "                        <ParameterStatement rightParameter=\"arg3\" parameter=\"arg2\" relation=\"=\"/>\n" +
            "                        <ParameterStatement rightParameter=\"arg4\" parameter=\"arg3\" relation=\"=\"/>\n" +
            "                        <ParameterStatement rightParameter=\"arg5\" parameter=\"arg4\" relation=\"=\"/>\n" +
            "                    </StatementArray>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlMixedTypeEq1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0.0:10.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg4\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"=\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg3\" parameter=\"arg2\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg4\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlMixedTypeEq2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0.0:10.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg3\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"=\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg3\" parameter=\"arg2\" relation=\"&lt;\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlMixedTypeEq3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"Untitled_1\" version=\"2\">\n" +
            "    <Class name=\"TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
            "        <Method name=\"testMethod1\">\n" +
            "            <Properties>\n" +
            "                <Property name=\"methodRunner\" type=\"String\" value=\"Java Runner\"/>\n" +
            "                <Property name=\"wbMapBrowserToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "                <Property name=\"wbBrowser\" type=\"String\" value=\"Chrome\"/>\n" +
            "                <Property name=\"wbMapStartUrlToParam\" type=\"boolean\" value=\"false\"/>\n" +
            "            </Properties>\n" +
            "            <Parameter name=\"arg1\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0.0:10.0\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"int\" isExpected=\"false\" expected=\"0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"0:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg2\" parameter=\"arg1\" relation=\"=\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlMixedTypeOverflow = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"n0000wEct25656\" version=\"2\">\n" +
            "    <Class name=\"com.example.test.TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
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
            "                <Choice name=\"choice1\" value=\"1:10\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Parameter name=\"arg2\" type=\"double\" isExpected=\"false\" expected=\"0.0\" linked=\"false\">\n" +
            "                <Properties>\n" +
            "                    <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" +
            "                </Properties>\n" +
            "                <Comments>\n" +
            "                    <TypeComments/>\n" +
            "                </Comments>\n" +
            "                <Choice name=\"choice1\" value=\"1.0E18:8.0E18\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"8.0E18:1.0E19\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1.0E19:1.0E20\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1.0E20:1.0E21\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1.0E21:1.0E22\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"1.0E22:9.0E22\" isRandomized=\"true\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <ParameterStatement rightParameter=\"arg1\" parameter=\"arg2\" relation=\"&lt;=\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

    private String xmlCmpFixedVsRange = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Model name=\"n0000wEct25656\" version=\"2\">\n" +
            "    <Class name=\"com.example.test.TestClass1\">\n" +
            "        <Properties>\n" +
            "            <Property name=\"runOnAndroid\" type=\"boolean\" value=\"false\"/>\n" +
            "        </Properties>\n" +
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
            "                <Choice name=\"choice1\" value=\"1:10\" isRandomized=\"true\"/>\n" +
            "                <Choice name=\"choice2\" value=\"MAX_VALUE\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice3\" value=\"1\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice4\" value=\"0\" isRandomized=\"false\"/>\n" +
            "                <Choice name=\"choice5\" value=\"11\" isRandomized=\"false\"/>\n" +
            "            </Parameter>\n" +
            "            <Constraint name=\"constraint\">\n" +
            "                <Premise>\n" +
            "                    <StaticStatement value=\"true\"/>\n" +
            "                </Premise>\n" +
            "                <Consequence>\n" +
            "                    <Statement choice=\"choice1\" parameter=\"arg1\" relation=\"&lt;=\"/>\n" +
            "                </Consequence>\n" +
            "            </Constraint>\n" +
            "        </Method>\n" +
            "    </Class>\n" +
            "</Model>\n";

}
