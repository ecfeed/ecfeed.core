package com.ecfeed.core.parser.export.json;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportJSON;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportJSONTest {

    @Test
    void fileFlat() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, false, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, false, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, json.getJSONArray("tests").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, json.getJSONArray("tests").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, json.getJSONArray("tests").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, json.getJSONArray("tests").getJSONObject(1).get("index"));
        });

        String resultExplicit = parserExplicit.getFile(suite);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, jsonExplicit.getJSONArray("tests").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, jsonExplicit.getJSONArray("tests").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, jsonExplicit.getJSONArray("tests").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, jsonExplicit.getJSONArray("tests").getJSONObject(1).get("index"));
        });
    }

    @Test
    void fileNested() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportJSON.getModelDataExport(method, 0, true, false);
        ModelDataExport parserExplicit = ModelDataExportJSON.getModelDataExport(method, 0, true, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);
        System.out.println(result);

        JSONObject json = new JSONObject(result);
        System.out.println(json);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, json.getJSONArray("tests").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, json.getJSONArray("tests").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, json.getJSONArray("tests").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, json.getJSONArray("tests").getJSONObject(1).get("index"));
        });

        String resultExplicit = parserExplicit.getFile(suite);

        JSONObject jsonExplicit = new JSONObject(resultExplicit);
        System.out.println(jsonExplicit);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, jsonExplicit.getJSONArray("tests").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, jsonExplicit.getJSONArray("tests").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, jsonExplicit.getJSONArray("tests").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, jsonExplicit.getJSONArray("tests").getJSONObject(1).get("index"));
        });
    }
}
