package com.ecfeed.core.parser;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.parser.model.load.ModelData;
import com.ecfeed.core.parser.model.load.ModelDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ModelParserJSONTest {
    public static String SEPARATOR = "&";

    @Test
    void parseModelCSVTestA() {

        ModelData model = ModelDataFactory.create("" +
                        "{\"tests\": [\n" +
                        "  {\n" +
                        "    \"id\": 0,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"Eva\",\n" +
                        "      \"age\": \"30\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"30\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 3,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"Eva\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "]}",
                ModelDataFactory.Type.JSON);

        List<String> header = model.getHeader();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, header.size());
        });

        List<AbstractParameterNode> parameters = model.parse(null);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(2, parameters.size());
        });
    }

    @Test
    void parseModelCSVTestB() {

        ModelData model = ModelDataFactory.create("" +
                        "{\"tests\": [\n" +
                        "  {\n" +
                        "    \"id\": 123,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"timestamp\": \"1698318538\",\n" +
                        "    \"Person\": {\n" +
                        "      \"sex\": \"male\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"Marek\",\n" +
                        "      \"height\": \"189\",\n" +
                        "      \"weight\": \"80\",\n" +
                        "      \"additional\": {\n" +
                        "        \"license\": true\n" +
                        "\t  }\n" +
                        "    }\n" +
                        "  }\n" +
                        "]}",
                ModelDataFactory.Type.JSON);

        List<String> header = model.getHeader();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(8, header.size());
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "weight"));
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "sex"));
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "height"));
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "name"));
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "additional" + SEPARATOR + "license"));
            Assertions.assertTrue(header.contains("Person" + SEPARATOR + "age"));
            Assertions.assertTrue(header.contains("timestamp"));
            Assertions.assertTrue(header.contains("id"));
        });

        List<AbstractParameterNode> parameters = model.parse(null);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, parameters.size());
        });
    }

    @Test
    void parseModelCSVTestC() {

        ModelData model = ModelDataFactory.create("" +
                        "{\"tests\": [\n" +
                        "  {\n" +
                        "    \"id\": 0,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": [1, 2, 3, 4],\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": [\"Eva\", \"Marek\", \"Patryk\"],\n" +
                        "      \"age\": \"30\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "]}",
                ModelDataFactory.Type.JSON);

        List<String> header = model.getHeader();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, header.size());
        });

        List<AbstractParameterNode> parameters = model.parse(null);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(2, parameters.size());
        });
    }

    @Test
    void parseModelCSVTestD() {

        ModelData model = ModelDataFactory.create("" +
                        "{\n" +
                        "    \"index\": 0,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "}",
                ModelDataFactory.Type.JSON);

        List<String> header = model.getHeader();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, header.size());
        });

        List<AbstractParameterNode> parameters = model.parse(null);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(2, parameters.size());
        });
    }
}
