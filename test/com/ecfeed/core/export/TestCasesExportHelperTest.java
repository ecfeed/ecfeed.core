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

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IExtLanguageManager;
import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodDeployer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;

public class TestCasesExportHelperTest {

    @Test
    public void shouldParseNoParams() {

        performTest("MIN_VALUE", "MAX_VALUE", "ABCD", "ABCD", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "ABCD", "ABCD", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseOneParamByParamNumber() {

        performTest("MIN_VALUE", "MAX_VALUE", "$1.value", "-2147483648", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value", "-2147483648", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseOneParamByParamName() {

        performTest("0", "1", "$par_0.value", "0", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value", "-2147483648", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value", "-2147483648", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseTwoParamsByParamNumber() {
        performTest("1", "2", "$1.value, $2.value", "1, 2", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value", "-2147483648, 2147483647", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value", "-2147483648, 2147483647", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseTwoParamsByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value, $par_1.value", "-2147483648, 2147483647", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value, $par 1.value", "-2147483648, 2147483647", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamNumber() {
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value, $3.value", "-2147483648, 2147483647, $3.value", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$1.value, $2.value, $3.value", "-2147483648, 2147483647, $3.value", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseThreeParamsTemplateByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.value, $par_1.value, $par_2.value", "-2147483648, 2147483647, $par_2.value", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.value, $par 1.value, $par 2.value", "-2147483648, 2147483647, $par 2.value", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseTwoParamsWithSpaces() {
        performTest("0", "1", "   $par_0.value, $par_1.value", "   0, 1", new ExtLanguageManagerForJava());
        performTest("0", "1", "   $par 0.value, $par 1.value", "   0, 1", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseIndex() {
        performTest(5, "%index", "5", new ExtLanguageManagerForJava());
        performTest(5, "%index", "5", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseIndexWithOneParamAndText() {
        performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD", new ExtLanguageManagerForJava());
        performTest(5, "55", "", "%index, $1.value ABCD", "5, 55 ABCD", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseNameByParamNumber() {
        performTest("0", "1", "$1.name", "par_0", new ExtLanguageManagerForJava());
        performTest("0", "1", "$1.name", "par 0", new ExtLanguageManagerForSimple());
    }

    @Test
    public void AAshouldParseNameByParamName() {
        performTest("MIN_VALUE", "MAX_VALUE", "$par_0.name", "par_0", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$par 0.name", "par 0", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParsePackageClassMethod() {
        performTest("MIN_VALUE", "MAX_VALUE", "%package, %class, %method", "package_1, Test_1, testMethod_1", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "%package, %class, %method", ", Test 1, testMethod 1", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseChoiceNames() {
        performTest("MIN_VALUE", "MAX_VALUE", "$1.choice, $2.full_choice", "c_0, c_1", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "$1.choice, $2.full_choice", "c_0, c_1", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldParseTestSuite() {
        performTest("MIN_VALUE", "MAX_VALUE", "%suite", "default", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "%suite", "default", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandAlphanumericToMinWidth() {
        performTest("MIN_VALUE", "MAX_VALUE", "(x).min_width(5)", "x    ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(x).min_width(5)", "x    ", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandSpaceToMinWidth() {
        performTest("MIN_VALUE", "MAX_VALUE", "( ).min_width(2)", "  ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "( ).min_width(2)", "  ", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldIgnoreInvalidWidthParameter() {
        performTest("MIN_VALUE", "MAX_VALUE", "(Q).min_width(C)", "(Q).min_width(C)", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(Q).min_width(C)", "(Q).min_width(C)", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators() {

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(7) | (arg0).min_width(7) |",
                "| arg     | arg0    |",
                new ExtLanguageManagerForJava());

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(7) | (arg0).min_width(7) |",
                "| arg     | arg0    |",
                new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldConvertMultipleMinWidthOperators2() {

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(3) | (arg0).min_width(4) |",
                "| arg | arg0 |",
                new ExtLanguageManagerForJava());

        performTest("MIN_VALUE", "MAX_VALUE", "| (arg).min_width(3) | (arg0).min_width(4) |",
                "| arg | arg0 |",
                new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandSpaceNegativeInteger() {
        performTest("MIN_VALUE", "MAX_VALUE", "(-5).min_width(3)", "-5 ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(-5).min_width(3)", "-5 ", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandToLeft() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,LEFT)", "X  ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,LEFT)", "X  ", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandToLeftWithBlanks() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(   3   ,   LEFT   )", "X  ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(   3   ,   LEFT   )", "X  ", new ExtLanguageManagerForSimple());
    }


    @Test
    public void shouldExpandToRight() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,RIGHT)", "  X", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,RIGHT)", "  X", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandToCenter() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,CENTER)", " X ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(3,CENTER)", " X ", new ExtLanguageManagerForSimple());
    }

    @Test
    public void shouldExpandToCenterOnTwo() {
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(2,CENTER)", "X ", new ExtLanguageManagerForJava());
        performTest("MIN_VALUE", "MAX_VALUE", "(X).min_width(2,CENTER)", "X ", new ExtLanguageManagerForSimple());
    }

//    private void performTest(String template, String expectedResult, ExtLanguage extLanguageManager) {
//        performTest("MIN_VALUE", "MAX_VALUE", template, expectedResult, extLanguageManager);
//    }

    private void performTest(String par0Value, String par1Value,  String template, String expectedResult, IExtLanguageManager extLanguageManager) {
        performTest(0, par0Value, par1Value, template, expectedResult, extLanguageManager);
    }

    private void performTest(int sequenceIndex, String template, String expectedResult, IExtLanguageManager extLanguageManager) {
        performTest(sequenceIndex, "", "",  template, expectedResult, extLanguageManager);
    }

	private void performTest(
			int sequenceIndex,
			String par0Value,
			String par1Value,
			String template,
			String expectedResult,
			IExtLanguageManager extLanguageManager) {

		ClassNode theClass = new ClassNode("package_1.Test_1", null);

		MethodNode methodNode = new MethodNode("testMethod_1", null);
		theClass.addMethod(methodNode);

		BasicParameterNode parameter0 = new BasicParameterNode("par_0", "int", "MAX_VALUE", false, null);
		ChoiceNode choiceNode00 = new ChoiceNode("c_0", par0Value, null);
		parameter0.addChoice(choiceNode00);
		methodNode.addParameter(parameter0);

		BasicParameterNode parameter1 = new BasicParameterNode("par_1", "int", "MIN_VALUE", false, null);
		ChoiceNode choiceNode11 = new ChoiceNode("c_1", par1Value, null);
		parameter1.addChoice(choiceNode11);
		methodNode.addParameter(parameter1);

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(choiceNode00);
		choices.add(choiceNode11);

		TestCaseNode testCase = new TestCaseNode("default", null, choices);
		testCase.setParent(methodNode);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);

		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);
		List<ParameterWithLinkingContext> deloyedParameterWithLinkingContexts = 
				methodNode.getDeployedParametersWithLinkingContexts();
		
		String result = 
				TestCasesExportHelper.generateTestCaseString(
						sequenceIndex, testCase, methodNode, deloyedParameterWithLinkingContexts,template, extLanguageManager);

		result = TestCasesExportHelper.evaluateMinWidthOperators(result);
		assertEquals(expectedResult, result);
	}

}