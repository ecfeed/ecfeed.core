package com.ecfeed.core.parser.export.csv;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportNestedBodyCSVTest {

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertTrue(result.contains("{\"s1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || result.contains("{\"s1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(result.contains("{\"s2\":{\"dest3\":\"Lorem, Ipsum\"}}"));

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("{\"s1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || resultExplicit.contains("{\"s1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(resultExplicit.contains("{\"s2\":{\"dest3\":\"Lorem, Ipsum\"}}"));
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertTrue(result.contains("{\"p1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || result.contains("{\"p1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(result.contains("{\"p2\":{\"dest3\":\"Lorem, Ipsum\"}}"));

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("{\"p1:sgc1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || resultExplicit.contains("{\"p1:sgc1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(resultExplicit.contains("{\"p2:sgc2\":{\"dest3\":\"Lorem, Ipsum\"}}"));
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertTrue(result.contains("{\"p1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || result.contains("{\"p1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(result.contains("{\"p2\":{\"dest3\":\"Lorem, Ipsum\"}}"));

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("{\"p1!sgr1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"") || resultExplicit.contains("{\"p1!sgr1\":{\"dest1\":\"Lorem Ipsum\""));
        Assertions.assertTrue(resultExplicit.contains("{\"p2!sgr2\":{\"dest3\":\"Lorem, Ipsum\"}}"));
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertTrue(result.contains("{\"p1\":{\"dest1\":\"Lorem Ipsum\"}}"));
        Assertions.assertTrue(result.contains("{\"p2\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"}}"));

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("{\"p1!sgr1\":{\"dest1\":\"Lorem Ipsum\"}}"));
        Assertions.assertTrue(resultExplicit.contains("{\"p2:sgc1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"}}"));
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals(7, result.length());

        String resultExplicit = parserExplicit .getTest(test);

        System.out.println(resultExplicit );
        Assertions.assertEquals(7, resultExplicit .length());
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertTrue(result.contains("{\"s3\":{\"s2\":{\"p2\":\"B\",\"s1\":{\"p1\":\"A\"}}}}"));

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("{\"s3\":{\"s2\":{\"p2:dest2\":\"B\",\"s1\":{\"p1:dest1\":\"A\"}}}}"));
    }

}
