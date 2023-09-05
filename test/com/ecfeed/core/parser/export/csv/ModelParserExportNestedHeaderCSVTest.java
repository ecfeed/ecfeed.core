package com.ecfeed.core.parser.export.csv;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.parser.export.ModelParserExportHelper;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelParserExportNestedHeaderCSVTest {
    private ModelDataExport parser;
    private ModelDataExport parserExplicit;

    @BeforeEach
    void setup() {

        parser = ModelDataExportCSV.getModelDataExport(",", true, false);
        parserExplicit = ModelDataExportCSV.getModelDataExport(",", true, true);
    }

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("dest1,dest2,dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("dest1,dest2,dest3,dest4", resultExplicit);
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,p3,p4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1:dest1,p2:dest2,p3:dest3,p4:dest4", resultExplicit);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,p3,p4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!dest1,p2!dest2,p3!dest3,p4!dest4", resultExplicit);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,dest3,dest4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!dest1,p2:dest2,dest3,dest4", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("s1,s2,dest4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("s1,s2,dest4", resultExplicit);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,p3", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1:sgc1,p2:sgc2,p3:dest4", resultExplicit);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();

        String result = parser.getHeader(method).orElse("");
        System.out.println(result);

        Assertions.assertEquals("p1,p2,p3", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");
        System.out.println(resultExplicit);

        Assertions.assertEquals("p1!sgr1,p2!sgr2,p3!dest4", resultExplicit);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("p1,p2,s1,dest4", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("p1!sgr1,p2:sgc1,s1,dest4", resultExplicit);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyRandomTest() {
        MethodNode method = ModelParserExportHelper.modelRandom();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("dest1,dest2", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("dest1,dest2", resultExplicit);
    }

    @Test
    void bodyNestedTest() {
        MethodNode method = ModelParserExportHelper.modelNested();

        String result = parser.getHeader(method).orElse("");

        System.out.println(result);
        Assertions.assertEquals("s3", result);

        String resultExplicit = parserExplicit.getHeader(method).orElse("");

        System.out.println(resultExplicit);
        Assertions.assertEquals("s3", resultExplicit);
    }
}