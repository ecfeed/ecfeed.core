package com.ecfeed.core.parser;

import com.ecfeed.core.parser.model.load.ModelDataCSVLineProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ModelParserCSVProcessorTest {
    private ModelDataCSVLineProcessor processor;

    @BeforeEach
    void setUp() {

        processor = ModelDataCSVLineProcessor.get();
    }

//--------------------------------------------------------------------------

    @Test
    void parseBodyTest() {
        String input = "0,1,2";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(3, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("0", elements.get(0));
            Assertions.assertEquals("1", elements.get(1));
            Assertions.assertEquals("2", elements.get(2));
        });
    }

    @Test
    void parseBodyEscapeTest() {
        String input = "EL-SVG-000523,0560-05220,3,17.6,TRUE,TRUE,FALSE,TRUE,FALSE," +
                "\"{\"\"start\"\":\"\"2023-06-01T12:00:00.000+0000\"\",\"\"end\"\":\"\"2023-06-01T13:00:00.000+0000\"\"}\"," +
                "24.3,27.8,null,0,24.3,27.8,24.3,27.8";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(18, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("EL-SVG-000523", elements.get(0));
            Assertions.assertEquals("0560-05220", elements.get(1));
            Assertions.assertEquals("3", elements.get(2));
            Assertions.assertEquals("17.6", elements.get(3));
            Assertions.assertEquals("TRUE", elements.get(4));
            Assertions.assertEquals("TRUE", elements.get(5));
            Assertions.assertEquals("FALSE", elements.get(6));
            Assertions.assertEquals("TRUE", elements.get(7));
            Assertions.assertEquals("FALSE", elements.get(8));
            Assertions.assertEquals("{\"start\":\"2023-06-01T12:00:00.000+0000\",\"end\":\"2023-06-01T13:00:00.000+0000\"}", elements.get(9));
            Assertions.assertEquals("24.3", elements.get(10));
            Assertions.assertEquals("27.8", elements.get(11));
            Assertions.assertEquals("null", elements.get(12));
            Assertions.assertEquals("0", elements.get(13));
            Assertions.assertEquals("24.3", elements.get(14));
            Assertions.assertEquals("27.8", elements.get(15));
            Assertions.assertEquals("24.3", elements.get(16));
            Assertions.assertEquals("27.8", elements.get(17));
        });
    }

    @Test
    void parseBodyLineEmpty1Test() {
        String input = "";

        Assertions.assertThrows(RuntimeException.class, () -> processor.parseBody(input, ','));
    }

    @Test
    void parseBodyLineEmpty2Test() {
        String input = "  \t  ";

        Assertions.assertThrows(RuntimeException.class, () -> processor.parseBody(input, ','));
    }

    @Test
    void parseBodyValueEmptyTest() {
        String input = "0,1,2,3,4,5,,,6,,,7,,,";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(15, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("0", elements.get(0));
            Assertions.assertEquals("1", elements.get(1));
            Assertions.assertEquals("2", elements.get(2));
            Assertions.assertEquals("3", elements.get(3));
            Assertions.assertEquals("4", elements.get(4));
            Assertions.assertEquals("5", elements.get(5));
            Assertions.assertEquals("", elements.get(6));
            Assertions.assertEquals("", elements.get(7));
            Assertions.assertEquals("6", elements.get(8));
            Assertions.assertEquals("", elements.get(9));
            Assertions.assertEquals("", elements.get(10));
            Assertions.assertEquals("7", elements.get(11));
            Assertions.assertEquals("", elements.get(12));
            Assertions.assertEquals("", elements.get(13));
            Assertions.assertEquals("", elements.get(14));
        });
    }

    @Test
    void parseBodyLineEndTest() {
        String input = "0,1,2\r\n";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(3, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("0", elements.get(0));
            Assertions.assertEquals("1", elements.get(1));
            Assertions.assertEquals("2", elements.get(2));
        });
    }

    @Test
    void parseBodyValueTrimTest() {
        String input = "0, 1 , \" 2 \",\t \"\t3\t\" \t";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(4, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("0", elements.get(0));
            Assertions.assertEquals("1", elements.get(1));
            Assertions.assertEquals(" 2 ", elements.get(2));
            Assertions.assertEquals("\t3\t", elements.get(3));
        });
    }

    @Test
    void parseBodyValueSpanTest() {
        String input = "0,1,2\n2\n,3";

        List<String> elements = processor.parseBody(input, ',');

        Assertions.assertEquals(4, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("0", elements.get(0));
            Assertions.assertEquals("1", elements.get(1));
            Assertions.assertEquals("2\n2\n", elements.get(2));
            Assertions.assertEquals("3", elements.get(3));
        });
    }

//--------------------------------------------------------------------------

    @Test
    void parseHeaderTest() {
        String input = "A,B,C";

        List<String> elements = processor.parseHeader(input, ',');

        Assertions.assertEquals(3, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("A", elements.get(0));
            Assertions.assertEquals("B", elements.get(1));
            Assertions.assertEquals("C", elements.get(2));
        });
    }

    @Test
    void parseHeaderLineEmpty1Test() {
        String input = "";

        Assertions.assertThrows(RuntimeException.class, () -> processor.parseHeader(input, ','));
    }

    @Test
    void parseHeaderLineEmpty2Test() {
        String input = "  \t  ";

        Assertions.assertThrows(RuntimeException.class, () -> processor.parseHeader(input, ','));
    }

    @Test
    void parseHeaderValueEmpty1Test() {
        String input = "A,B,C,,";

        List<String> elements = processor.parseHeader(input, ',');

        Assertions.assertEquals(5, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("A", elements.get(0));
            Assertions.assertEquals("B", elements.get(1));
            Assertions.assertEquals("C", elements.get(2));
            Assertions.assertEquals("COL_0", elements.get(3));
            Assertions.assertEquals("COL_1", elements.get(4));
        });
    }

    @Test
    void parseHeaderValueEmpty2Test() {
        String input = "A,,B,,C";

        List<String> elements = processor.parseHeader(input, ',');

        Assertions.assertEquals(5, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("A", elements.get(0));
            Assertions.assertEquals("COL_0", elements.get(1));
            Assertions.assertEquals("B", elements.get(2));
            Assertions.assertEquals("COL_1", elements.get(3));
            Assertions.assertEquals("C", elements.get(4));
        });
    }

    @Test
    void parseHeaderDuplicateNameTest() {
        String input = "A,B,A";

        Assertions.assertThrows(RuntimeException.class, () -> processor.parseHeader(input, ','));
    }

    @Test
    void parseHeaderPatternTest() {
        String input = "'!A!',0B,*(C)*";

        List<String> elements = processor.parseHeader(input, ',');

        Assertions.assertEquals(3, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("__A__", elements.get(0));
            Assertions.assertEquals("_0B", elements.get(1));
            Assertions.assertEquals("__C__", elements.get(2));
        });
    }

    @Test
    void parseHeaderLineEndTest() {
        String input = "A,B,C\r\n";

        List<String> elements = processor.parseHeader(input, ',');

        Assertions.assertEquals(3, elements.size());

        Assertions.assertAll("Values", () -> {
            Assertions.assertEquals("A", elements.get(0));
            Assertions.assertEquals("B", elements.get(1));
            Assertions.assertEquals("C", elements.get(2));
        });
    }
}
