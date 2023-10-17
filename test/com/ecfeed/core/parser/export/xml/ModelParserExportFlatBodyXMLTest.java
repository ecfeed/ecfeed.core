package com.ecfeed.core.parser.export.xml;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportJSON;
import com.ecfeed.core.parser.model.export.ModelDataExportXML;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportFlatBodyXMLTest {

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<dest1>Lorem Ipsum</dest1>"));
            Assertions.assertTrue(result.contains("<dest2>Lorem \"Ipsum\"</dest2>"));
            Assertions.assertTrue(result.contains("<dest3>Lorem, Ipsum</dest3>"));
            Assertions.assertTrue(result.contains("<dest4>Lorem, \"Ipsum\"</dest4>"));
        });
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1>Lorem Ipsum</p1>"));
            Assertions.assertTrue(result.contains("<p2>Lorem \"Ipsum\"</p2>"));
            Assertions.assertTrue(result.contains("<p3>Lorem, Ipsum</p3>"));
            Assertions.assertTrue(result.contains("<p4>Lorem, \"Ipsum\"</p4>"));
        });
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1>Lorem Ipsum</p1>"));
            Assertions.assertTrue(result.contains("<p2>Lorem \"Ipsum\"</p2>"));
            Assertions.assertTrue(result.contains("<p3>Lorem, Ipsum</p3>"));
            Assertions.assertTrue(result.contains("<p4>Lorem, \"Ipsum\"</p4>"));
        });
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1>Lorem Ipsum</p1>"));
            Assertions.assertTrue(result.contains("<p2>Lorem \"Ipsum\"</p2>"));
            Assertions.assertTrue(result.contains("<dest3>Lorem, Ipsum</dest3>"));
            Assertions.assertTrue(result.contains("<dest4>Lorem, \"Ipsum\"</dest4>"));
        });
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<s1_dest1>Lorem Ipsum</s1_dest1>"));
            Assertions.assertTrue(result.contains("<s1_dest2>Lorem \"Ipsum\"</s1_dest2>"));
            Assertions.assertTrue(result.contains("<s2_dest3>Lorem, Ipsum</s2_dest3>"));
            Assertions.assertTrue(result.contains("<dest4>Lorem, \"Ipsum\"</dest4>"));
        });
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1_dest1>Lorem Ipsum</p1_dest1>"));
            Assertions.assertTrue(result.contains("<p1_dest2>Lorem \"Ipsum\"</p1_dest2>"));
            Assertions.assertTrue(result.contains("<p3>Lorem, \"Ipsum\"</p3>"));
            Assertions.assertTrue(result.contains("<p2_dest3>Lorem, Ipsum</p2_dest3>"));
        });
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1_dest1>Lorem Ipsum</p1_dest1>"));
            Assertions.assertTrue(result.contains("<p1_dest2>Lorem \"Ipsum\"</p1_dest2>"));
            Assertions.assertTrue(result.contains("<p3>Lorem, \"Ipsum\"</p3>"));
            Assertions.assertTrue(result.contains("<p2_dest3>Lorem, Ipsum</p2_dest3>"));
        });
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        String result = parser.getTest(test, 0);
        System.out.println(result);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(result.contains("<p1_dest1>Lorem Ipsum</p1_dest1>"));
            Assertions.assertTrue(result.contains("<p2_dest2>Lorem \"Ipsum\"</p2_dest2>"));
            Assertions.assertTrue(result.contains("<s1_dest3>Lorem, Ipsum</s1_dest3>"));
            Assertions.assertTrue(result.contains("<dest4>Lorem, \"Ipsum\"</dest4>"));
        });
    }
}
