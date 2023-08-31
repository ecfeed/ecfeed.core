package com.ecfeed.core.parser.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelParserExportHeaderTest {
    private ModelDataExport parser;

    @BeforeEach
    void setup() {
        parser = ModelDataExportCSV.getModelDataExport();
    }

    @Test
    void bodyLocalTest() {
        MethodNode method = ModelParserExportHelper.modelLocal();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyGlobalClassTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClass();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyGlobalRootTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRoot();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyMixedTest() {
        MethodNode method = ModelParserExportHelper.modelMixed();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

//---------------------------------------------------------------------------------------------------------------

    @Test
    void bodyLocalStructureTest() {
        MethodNode method = ModelParserExportHelper.modelLocalStructure();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyGlobalClassStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalClassStructure();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyGlobalRootStructureTest() {
        MethodNode method = ModelParserExportHelper.modelGlobalRootStructure();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

    @Test
    void bodyMixedStructureTest() {
        MethodNode method = ModelParserExportHelper.modelMixedStructure();

        String result = parser.getHeader(method);

        System.out.println(result);
    }

//---------------------------------------------------------------------------------------------------------------

//    @Test
//    void bodyRandomTest() {
//        MethodNode method = ModelParserExportHelper.modelRandom();
//
//        String result = parser.getHeader(method);
//
//        System.out.println(result);
//    }
//
//    @Test
//    void bodyNestedTest() {
//        MethodNode method = ModelParserExportHelper.modelNested();
//
//        String result = parser.getHeader(method);
//
//        System.out.println(result);
//    }

}
