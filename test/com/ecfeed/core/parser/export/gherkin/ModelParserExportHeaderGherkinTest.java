package com.ecfeed.core.parser.export.gherkin;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportGherkin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportHeaderGherkinTest {

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of dest1 is <dest1>.\n" +
                "    And the value of dest2 is <dest2>.\n" +
                "    And the value of dest3 is <dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of dest1 is <dest1>.\n" +
                "    And the value of dest2 is <dest2>.\n" +
                "    And the value of dest3 is <dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1 is <p1>.\n" +
                "    And the value of p2 is <p2>.\n" +
                "    And the value of p3 is <p3>.\n" +
                "    And the value of p4 is <p4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1:dest1 is <p1:dest1>.\n" +
                "    And the value of p2:dest2 is <p2:dest2>.\n" +
                "    And the value of p3:dest3 is <p3:dest3>.\n" +
                "    And the value of p4:dest4 is <p4:dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1 is <p1>.\n" +
                "    And the value of p2 is <p2>.\n" +
                "    And the value of p3 is <p3>.\n" +
                "    And the value of p4 is <p4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1!dest1 is <p1!dest1>.\n" +
                "    And the value of p2!dest2 is <p2!dest2>.\n" +
                "    And the value of p3!dest3 is <p3!dest3>.\n" +
                "    And the value of p4!dest4 is <p4!dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1 is <p1>.\n" +
                "    And the value of p2 is <p2>.\n" +
                "    And the value of dest3 is <dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1!dest1 is <p1!dest1>.\n" +
                "    And the value of p2:dest2 is <p2:dest2>.\n" +
                "    And the value of dest3 is <dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of s1_dest1 is <s1_dest1>.\n" +
                "    And the value of s1_dest2 is <s1_dest2>.\n" +
                "    And the value of s2_dest3 is <s2_dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of s1 is <s1>.\n" +
                "    And the value of s2 is <s2>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1_dest1 is <p1_dest1>.\n" +
                "    And the value of p1_dest2 is <p1_dest2>.\n" +
                "    And the value of p2_dest3 is <p2_dest3>.\n" +
                "    And the value of p3 is <p3>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1:sgc1 is <p1:sgc1>.\n" +
                "    And the value of p2:sgc2 is <p2:sgc2>.\n" +
                "    And the value of p3:dest4 is <p3:dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1_dest1 is <p1_dest1>.\n" +
                "    And the value of p1_dest2 is <p1_dest2>.\n" +
                "    And the value of p2_dest3 is <p2_dest3>.\n" +
                "    And the value of p3 is <p3>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1!sgr1 is <p1!sgr1>.\n" +
                "    And the value of p2!sgr2 is <p2!sgr2>.\n" +
                "    And the value of p3!dest4 is <p3!dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1_dest1 is <p1_dest1>.\n" +
                "    And the value of p2_dest2 is <p2_dest2>.\n" +
                "    And the value of s1_dest3 is <s1_dest3>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of p1!sgr1 is <p1!sgr1>.\n" +
                "    And the value of p2:sgc1 is <p2:sgc1>.\n" +
                "    And the value of s1 is <s1>.\n" +
                "    And the value of dest4 is <dest4>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of dest1 is <dest1>.\n" +
                "    And the value of dest2 is <dest2>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of dest1 is <dest1>.\n" +
                "    And the value of dest2 is <dest2>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of s3_s2_s1_p1 is <s3_s2_s1_p1>.\n" +
                "    And the value of s3_s2_p2 is <s3_s2_p2>.\n" +
                "\n" +
                "    Examples:\n", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("Feature: EcFeed\n" +
                "\n" +
                "  Scenario Outline: Execute method 'method'\n" +
                "    Given the value of s3 is <s3>.\n" +
                "\n" +
                "    Examples:\n", resultExplicit);
    }
}