package com.ecfeed.core.parser.export.gherkin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportGherkin;

public class ModelParserExportGherkinTest {

    @Test
    void fileFlat() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);

        System.out.println(result);
        Assertions.assertTrue(result.contains("p1_dest1      | p2_dest2        | s1_dest3       | dest4           "));
        Assertions.assertTrue(result.contains("\"Value\"       | \"Value\"         | \"Value\"        | \"Value\"         "));
        Assertions.assertTrue(result.contains("\"Lorem Ipsum\" | \"Lorem \"Ipsum\"\" | \"Lorem, Ipsum\" | \"Lorem, \"Ipsum\"\""));

        String resultExplicit = parserExplicit.getFile(suite);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("p1!sgr1       | p2:sgc1         | s1             | dest4           "));
        Assertions.assertTrue(resultExplicit.contains("\"Value\"       | \"Value\"         | \"Value\"        | \"Value\"         "));
        Assertions.assertTrue(resultExplicit.contains("\"Lorem Ipsum\" | \"Lorem \"Ipsum\"\" | \"Lorem, Ipsum\" | \"Lorem, \"Ipsum\"\""));
    }

    @Test
    void fileNested() {
        MethodNode method = ModelParserExportHelper.modelSuite();

        ModelDataExport parser = ModelDataExportGherkin.getModelDataExport(method, false);
        ModelDataExport parserExplicit = ModelDataExportGherkin.getModelDataExport(method, true);

        TestSuiteNode suite = ModelParserExportHelper.getTestSuite(method);

        String result = parser.getFile(suite);

        System.out.println(result);
        Assertions.assertTrue(result.contains("p1_dest1      | p2_dest2        | s1_dest3       | dest4           "));
        Assertions.assertTrue(result.contains("\"Lorem Ipsum\" | \"Lorem \"Ipsum\"\" | \"Lorem, Ipsum\" | \"Lorem, \"Ipsum\"\""));
        Assertions.assertTrue(result.contains("\"Value\"       | \"Value\"         | \"Value\"        | \"Value\"         "));

        String resultExplicit = parserExplicit.getFile(suite);

        System.out.println(resultExplicit);
        Assertions.assertTrue(resultExplicit.contains("p1!sgr1       | p2:sgc1         | s1             | dest4           "));
        Assertions.assertTrue(resultExplicit.contains("\"Lorem Ipsum\" | \"Lorem \"Ipsum\"\" | \"Lorem, Ipsum\" | \"Lorem, \"Ipsum\"\""));
        Assertions.assertTrue(resultExplicit.contains("\"Value\"       | \"Value\"         | \"Value\"        | \"Value\"         "));
    }
}
