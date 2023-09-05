package com.ecfeed.core.parser.export.csv;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelParserExportFlatBodyCSVTest {
    private ModelDataExport parser;
    private ModelDataExport parserExplicit;

    @BeforeEach
    void setup() {

        parser = ModelDataExportCSV.getModelDataExport(",", false, false);
        parserExplicit = ModelDataExportCSV.getModelDataExport(",", false, true);
    }

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

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

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test);

        System.out.println(result);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", result);

        String resultExplicit = parserExplicit.getTest(test);

        System.out.println(resultExplicit);
        Assertions.assertEquals("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\"", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test);

        Assertions.assertEquals(7, result.length());

        String resultExplicit = parserExplicit.getTest(test);

        Assertions.assertEquals(7, resultExplicit.length());
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test);

        Assertions.assertEquals("A,B", result);

        String resultExplicit = parserExplicit.getTest(test);

        Assertions.assertEquals("A,B", resultExplicit);
    }
}
