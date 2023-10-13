package com.ecfeed.core.parser.export.xml;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportXML;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportXMLTest {

    @Test
    void fileFlat() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, false);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);
        System.out.println(result);

        JSONObject json = XML.toJSONObject(result);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, json.getJSONObject("suite").getJSONArray("test").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, json.getJSONObject("suite").getJSONArray("test").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, json.getJSONObject("suite").getJSONArray("test").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, json.getJSONObject("suite").getJSONArray("test").getJSONObject(1).get("index"));
        });
    }

    @Test
    void fileNested() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportXML.getModelDataExport(method, 0, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);
        System.out.println(result);

        JSONObject json = XML.toJSONObject(result);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, json.getJSONObject("suite").getJSONArray("test").getJSONObject(0).keySet().size());
            Assertions.assertEquals(5, json.getJSONObject("suite").getJSONArray("test").getJSONObject(1).keySet().size());
            Assertions.assertEquals(0, json.getJSONObject("suite").getJSONArray("test").getJSONObject(0).get("index"));
            Assertions.assertEquals(1, json.getJSONObject("suite").getJSONArray("test").getJSONObject(1).get("index"));
        });
    }
}
