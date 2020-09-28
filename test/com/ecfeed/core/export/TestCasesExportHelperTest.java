package com.ecfeed.core.export;

/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;

public class TestCasesExportHelperTest {

    @Test
    public void shouldParseNoParams() {

        performTest("MIN_VALUE", "MAX_VALUE", "ABCD", "ABCD", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "ABCD", "ABCD", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseOneParamByParamNumber() {

        performTest("MIN_VALUE", "MAX_VALUE", "$1.value", "-2147483648", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value", "-2147483648", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseOneParamByParamName() {

        performTest("0", "1", "$par_0.value", "0", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value", "-2147483648", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value", "-2147483648", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseTwoParamsByParamNumber() {
        performTest("1", "2", "$1.value, $2.value", "1, 2", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value", "-2147483648, 2147483647", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value", "-2147483648, 2147483647", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseTwoParamsByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value, $par_1.value", "-2147483648, 2147483647", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value, $par 1.value", "-2147483648, 2147483647", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamNumber() {
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value, $3.value", "-2147483648, 2147483647, $3.value", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value, $3.value", "-2147483648, 2147483647, $3.value", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value, $par_1.value, $par_2.value", "-2147483648, 2147483647, $par_2.value", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value, $par 1.value, $par 2.value", "-2147483648, 2147483647, $par 2.value", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseTwoParamsWithSpaces() {
        performTest("0", "1", "   $par_0.value, $par_1.value", "   0, 1", ExtLanguage.JAVA);
        performTest("0", "1", "   $par 0.value, $par 1.value", "   0, 1", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseIndex() {
        performTest(5, "%index", "5", ExtLanguage.JAVA);
        performTest(5, "%index", "5", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseIndexWithOneParamAndText() {
        performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD", ExtLanguage.JAVA);
        performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseNameByParamNumber() {
        performTest("0", "1", "$1.name", "par_0", ExtLanguage.JAVA);
        performTest("0", "1", "$1.name", "par 0", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseNameByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.name", "par_0", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.name", "par 0", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParsePackageClassMethod() {
        performTest("MIN_VALUE", "MAX_VALUE", "%package, %class, %method", "package_1, Test_1, testMethod_1", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "%package, %class, %method", ", Test 1, testMethod 1", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseChoiceNames() {
        performTest("MIN_VALUE", "MAX_VALUE", "$1.choice, $2.full_choice", "c_0, c_1", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "$1.choice, $2.full_choice", "c 0, c 1", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldParseTestSuite() {
        performTest("MIN_VALUE", "MAX_VALUE", "%suite", "default", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "%suite", "default", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandAlphanumericToMinWidth() {
        performTest("MIN_VALUE", "MAX_VALUE", "(x).min_width(5)", "x    ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(x).min_width(5)", "x    ", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandSpaceToMinWidth() {
        performTest("MIN_VALUE", "MAX_VALUE", "( ).min_width(2)", "  ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "( ).min_width(2)", "  ", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldIgnoreInvalidWidthParameter() {
        performTest("MIN_VALUE", "MAX_VALUE", "(Q).min_width(C)", "(Q).min_width(C)", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(Q).min_width(C)", "(Q).min_width(C)", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators() {

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(7) | (arg0).min_width(7) |",
                "| arg     | arg0    |",
                ExtLanguage.JAVA);

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(7) | (arg0).min_width(7) |",
                "| arg     | arg0    |",
                ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators2() {

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(3) | (arg0).min_width(4) |",
                "| arg | arg0 |",
                ExtLanguage.JAVA);

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(3) | (arg0).min_width(4) |",
                "| arg | arg0 |",
                ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandSpaceNegativeInteger() {
        performTest("MIN_VALUE", "MAX_VALUE", "(-5).min_width(3)", "-5 ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(-5).min_width(3)", "-5 ", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandToLeft() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,LEFT)", "X  ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,LEFT)", "X  ", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandToLeftWithBlanks() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(   3   ,   LEFT   )", "X  ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(   3   ,   LEFT   )", "X  ", ExtLanguage.SIMPLE);
    }


    @Test
    public void shouldExpandToRight() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,RIGHT)", "  X", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,RIGHT)", "  X", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandToCenter() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,CENTER)", " X ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,CENTER)", " X ", ExtLanguage.SIMPLE);
    }

    @Test
    public void shouldExpandToCenterOnTwo() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(2,CENTER)", "X ", ExtLanguage.JAVA);
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(2,CENTER)", "X ", ExtLanguage.SIMPLE);
    }

//    private void performTest(String template, String expectedResult, ExtLanguage extLanguage) {
//        performTest("MIN_VALUE", "MAX_VALUE", template, expectedResult, extLanguage);
//    }

    private void performTest(String par0Value, String par1Value,  String template, String expectedResult, ExtLanguage extLanguage) {
        performTest(0, par0Value, par1Value, template, expectedResult, extLanguage);
    }

    private void performTest(int sequenceIndex, String template, String expectedResult, ExtLanguage extLanguage) {
        performTest(sequenceIndex, "", "",  template, expectedResult, extLanguage);
    }

    private void performTest(
            int sequenceIndex,
            String par0Value,
            String par1Value,
            String template,
            String expectedResult,
            ExtLanguage extLanguage) {

        ClassNode theClass = new ClassNode("package_1.Test_1", null);

        MethodNode method = new MethodNode("testMethod_1", null);
        theClass.addMethod(method);

        MethodParameterNode parameter0 = new MethodParameterNode("par_0", null, "int", "MAX_VALUE", false);
        ChoiceNode choiceNode00 = new ChoiceNode("c_0", null, par0Value);
        parameter0.addChoice(choiceNode00);
        method.addParameter(parameter0);

        MethodParameterNode parameter1 = new MethodParameterNode("par_1", null, "int", "MIN_VALUE", false);
        ChoiceNode choiceNode11 = new ChoiceNode("c_1", null, par1Value);
        parameter1.addChoice(choiceNode11);
        method.addParameter(parameter1);

        List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
        choices.add(choiceNode00);
        choices.add(choiceNode11);

        TestCaseNode testCase = new TestCaseNode("default", null, choices);
        testCase.setParent(method);

        String result = TestCasesExportHelper.generateTestCaseString(sequenceIndex, testCase, template, extLanguage);
        result = TestCasesExportHelper.evaluateMinWidthOperators(result);
        assertEquals(expectedResult, result);
    }


}