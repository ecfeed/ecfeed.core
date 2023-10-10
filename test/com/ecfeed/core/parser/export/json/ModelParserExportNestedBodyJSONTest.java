package com.ecfeed.core.parser.export.json;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportJSON;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportNestedBodyJSONTest {

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", json.get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("dest4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("dest4"));
        });
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.get("p1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.get("p2"));
            Assertions.assertEquals("Lorem, Ipsum", json.get("p3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("p4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.get("p1:dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.get("p2:dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.get("p3:dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("p4:dest4"));
        });
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.get("p1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.get("p2"));
            Assertions.assertEquals("Lorem, Ipsum", json.get("p3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("p4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.get("p1!dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.get("p2!dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.get("p3!dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("p4!dest4"));
        });
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.get("p1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.get("p2"));
            Assertions.assertEquals("Lorem, Ipsum", json.get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("dest4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.get("p1!dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.get("p2:dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("dest4"));
        });
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.getJSONObject("s1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.getJSONObject("s1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", json.getJSONObject("s2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("dest4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.getJSONObject("s1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.getJSONObject("s1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.getJSONObject("s2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("dest4"));
        });
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.getJSONObject("p1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.getJSONObject("p1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", json.getJSONObject("p2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("p3"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.getJSONObject("p1:sgc1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.getJSONObject("p1:sgc1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.getJSONObject("p2:sgc2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("p3:dest4"));
        });
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.getJSONObject("p1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.getJSONObject("p1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", json.getJSONObject("p2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("p3"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.getJSONObject("p1!sgr1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.getJSONObject("p1!sgr1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.getJSONObject("p2!sgr2").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("p3!dest4"));
        });
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", json.getJSONObject("p1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", json.getJSONObject("p2").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", json.getJSONObject("s1").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", json.get("dest4"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("Lorem Ipsum", jsonExplicit.getJSONObject("p1!sgr1").get("dest1"));
            Assertions.assertEquals("Lorem \"Ipsum\"", jsonExplicit.getJSONObject("p2:sgc1").get("dest2"));
            Assertions.assertEquals("Lorem, Ipsum", jsonExplicit.getJSONObject("s1").get("dest3"));
            Assertions.assertEquals("Lorem, \"Ipsum\"", jsonExplicit.get("dest4"));
        });
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, json.get("dest1").toString().length());
            Assertions.assertEquals(1, json.get("dest2").toString().length());
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, jsonExplicit.get("dest1").toString().length());
            Assertions.assertEquals(1, jsonExplicit.get("dest2").toString().length());
        });
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();
        TestCaseNode test = ModelParserExportHelper.getTestCase(method);

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        String result = parser.getTest(test, 0);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("A", json.getJSONObject("s3").getJSONObject("s2").getJSONObject("s1").get("p1"));
            Assertions.assertEquals("B", json.getJSONObject("s3").getJSONObject("s2").get("p2"));
        });

        String resultExplicit = parserExplicit.getTest(test, 0);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals("A", jsonExplicit.getJSONObject("s3").getJSONObject("s2").getJSONObject("s1").get("p1:dest1"));
            Assertions.assertEquals("B", jsonExplicit.getJSONObject("s3").getJSONObject("s2").get("p2:dest2"));
        });
    }

}
