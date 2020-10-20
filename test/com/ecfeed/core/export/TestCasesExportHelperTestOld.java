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

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCasesExportHelperTestOld {

    @Test
    public void shouldParseNoParams() {
        performTest("ABCD", "ABCD");
    }

    @Test
    public void shouldParseOneParamByParamNumber() {
        performTest("$1.value", "0");
    }

    @Test
    public void shouldParseOneParamByParamName() {
        performTest("$par0.value", "0");
    }

    @Test
    public void shouldParseTwoParamsByParamNumber() {
        performTest("$1.value, $2.value", "0, 1");
    }

    @Test
    public void shouldParseTwoParamsByParamName() {
        performTest("$par0.value, $par1.value", "0, 1");
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamNumber() {
        performTest("$1.value, $2.value, $3.value", "0, 1, $3.value");
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamName() {
        performTest("$par0.value, $par1.value, $par2.value", "0, 1, $par2.value");
    }

    @Test
    public void shouldParseTwoParamsWithSpaces() {
        performTest("   $par0.value, $par1.value", "   0, 1");
    }

    @Test
    public void shouldParseIndex() {
        performTest(5, "%index", "5");
    }

    @Test
    public void shouldParseIndexWithOneParamAndText() {
        performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD");
    }

    @Test
    public void shouldParseNameByParamNumber() {
        performTest("$1.name", "par0");
    }

    @Test
    public void shouldParseNameByParamName() {
        performTest("$par0.name", "par0");
    }

    @Test
    public void shouldParsePackageClassMethod() {
        performTest("%package, %class, %method", "package, Test, testMethod");
    }

    @Test
    public void shouldParseChoiceNames() {
        performTest("$1.choice, $2.full_choice", "p0, p1");
    }

    @Test
    public void shouldParseTestSuite() {
        performTest("%suite", "default");
    }

    @Test
    public void shouldExpandAlphanumericToMinWidth() {
        performTest("(x).min_width(5)", "x    ");
    }

    @Test
    public void shouldExpandSpaceToMinWidth() {
        performTest("( ).min_width(2)", "  ");
    }

    @Test
    public void shouldIgnoreInvalidWidthParameter() {
        performTest("(Q).min_width(C)", "(Q).min_width(C)");
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators() {
        performTest("| (arg).min_width(7) | (arg0).min_width(7) |",
                "| arg     | arg0    |");
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators2() {
        performTest("| (arg).min_width(3) | (arg0).min_width(4) |",
                "| arg | arg0 |");
    }

    @Test
    public void shouldExpandSpaceNegativeInteger() {
        performTest("(-5).min_width(3)", "-5 ");
    }

    @Test
    public void shouldExpandToLeft() {
        performTest("(X).min_width(3,LEFT)", "X  ");
    }

    @Test
    public void shouldExpandToLeftWithBlanks() {
        performTest("(X).min_width(   3   ,   LEFT   )", "X  ");
    }


    @Test
    public void shouldExpandToRight() {
        performTest("(X).min_width(3,RIGHT)", "  X");
    }

    @Test
    public void shouldExpandToCenter() {
        performTest("(X).min_width(3,CENTER)", " X ");
    }

    @Test
    public void shouldExpandToCenterOnTwo() {
        performTest("(X).min_width(2,CENTER)", "X ");
    }

    void performTest(String template, String expectedResult) {
        performTest("0", "1", template, expectedResult);
    }

    void performTest(String par0Value, String par1Value,  String template, String expectedResult) {
        performTest(0, par0Value, par1Value, template, expectedResult);
    }

    void performTest(int sequenceIndex, String template, String expectedResult) {
        performTest(sequenceIndex, "", "",  template, expectedResult);
    }

    void performTest(int sequenceIndex, String par0Value, String par1Value,  String template, String expectedResult) {
        ClassNode theClass = new ClassNode("package.Test", null);

        MethodNode method = new MethodNode("testMethod", null);
        theClass.addMethod(method);

        MethodParameterNode parameter0 = new MethodParameterNode("par0", "int", "0", false, null);
        ChoiceNode choiceNode00 = new ChoiceNode("p0", par0Value, null);
        parameter0.addChoice(choiceNode00);
        method.addParameter(parameter0);

        MethodParameterNode parameter1 = new MethodParameterNode("par1", "int", "0", false, null);
        ChoiceNode choiceNode11 = new ChoiceNode("p1", par1Value, null);
        parameter1.addChoice(choiceNode11);
        method.addParameter(parameter1);

        List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
        choices.add(choiceNode00);
        choices.add(choiceNode11);

        TestCaseNode testCase = new TestCaseNode("default", null, choices);
        testCase.setParent(method);

        String result = TestCasesExportHelperOld.generateTestCaseString(sequenceIndex, testCase, template, new ExtLanguageManagerForJava());
        result = TestCasesExportHelperOld.evaluateMinWidthOperators(result);
        assertEquals(expectedResult, result);
    }

}