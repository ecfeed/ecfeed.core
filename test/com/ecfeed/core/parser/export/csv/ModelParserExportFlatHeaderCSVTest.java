package com.ecfeed.core.parser.export.csv;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelParserExportFlatHeaderCSVTest {

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("dest1,dest2,dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("dest1,dest2,dest3,dest4", resultExplicit);
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,p3,p4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1:dest1,p2:dest2,p3:dest3,p4:dest4", resultExplicit);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,p3,p4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!dest1,p2!dest2,p3!dest3,p4!dest4", resultExplicit);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!dest1,p2:dest2,dest3,dest4", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("s1_dest1,s1_dest2,s2_dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("s1_dest1,s1_dest2,s2_dest3,dest4", resultExplicit);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1_dest1,p1_dest2,p2_dest3,p3", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1:sgc1_dest1,p1:sgc1_dest2,p2:sgc2_dest3,p3:dest4", resultExplicit);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1_dest1,p1_dest2,p2_dest3,p3", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!sgr1_dest1,p1!sgr1_dest2,p2!sgr2_dest3,p3!dest4", resultExplicit);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1_dest1,p2_dest2,s1_dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!sgr1_dest1,p2:sgc1_dest2,s1_dest3,dest4", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("dest1,dest2", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("dest1,dest2", resultExplicit);
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();

        ModelDataExport parser = ModelDataExportCSV.getModelDataExport(method, ",", false, false);
        ModelDataExport parserExplicit = ModelDataExportCSV.getModelDataExport(method, ",", false, true);

        String result = parser.getHeader().orElse("");

        System.out.println(result);
        Assertions.assertEquals("s3_s2_s1_p1,s3_s2_p2", result);

        String resultExplicit = parserExplicit.getHeader().orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("s3_s2_s1_p1:dest1,s3_s2_p2:dest2", resultExplicit);
    }
}