package com.ecfeed.core.parser.export.json;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelParserExportFlatBodyJSONTest {
    private ModelDataExport parser;

    @BeforeEach
    void setup() {

        parser = ModelDataExportJSON.getModelDataExport(false, false);
    }

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }
    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        String result = parser.getTest(test, 0);

        System.out.println(result);
    }

}
