package com.ecfeed.core.parser.export.csv;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportCSVTest {

    @Test
    void fileFlat() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);

        System.out.println(result);
        Assertions.assertTrue(result.contains("p1_dest1,p2_dest2,s1_dest3,dest4"));
        Assertions.assertTrue(result.contains("Value,Value,Value,Value"));
        Assertions.assertTrue(result.contains("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\""));

        String resultExplicit = parserExplicit.getFile(suite);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("p1!sgr1_dest1,p2:sgc1_dest2,s1_dest3,dest4"));
        Assertions.assertTrue(resultExplicit.contains("Value,Value,Value,Value"));
        Assertions.assertTrue(resultExplicit.contains("Lorem Ipsum,\"Lorem \"\"Ipsum\"\"\",\"Lorem, Ipsum\",\"Lorem, \"\"Ipsum\"\"\""));
    }

    @Test
    void fileNested() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", true, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", true, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);

        System.out.println(result);
        Assertions.assertTrue(result.contains("p1,p2,s1,dest4"));
        Assertions.assertTrue(result.contains("{\"p1\":{\"dest1\":\"Value\"}},{\"p2\":{\"dest2\":\"Value\"}},{\"s1\":{\"dest3\":\"Value\"}},Value"));
        Assertions.assertTrue(result.contains("{\"p1\":{\"dest1\":\"Lorem Ipsum\"}},{\"p2\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"}},{\"s1\":{\"dest3\":\"Lorem, Ipsum\"}},\"Lorem, \"\"Ipsum\"\"\""));

        String resultExplicit = parserExplicit.getFile(suite);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("p1!sgr1,p2:sgc1,s1,dest4"));
        Assertions.assertTrue(resultExplicit.contains("{\"p1!sgr1\":{\"dest1\":\"Value\"}},{\"p2:sgc1\":{\"dest2\":\"Value\"}},{\"s1\":{\"dest3\":\"Value\"}},Value"));
        Assertions.assertTrue(resultExplicit.contains("{\"p1!sgr1\":{\"dest1\":\"Lorem Ipsum\"}},{\"p2:sgc1\":{\"dest2\":\"Lorem \\\"Ipsum\\\"\"}},{\"s1\":{\"dest3\":\"Lorem, Ipsum\"}},\"Lorem, \"\"Ipsum\"\"\""));
    }
}
