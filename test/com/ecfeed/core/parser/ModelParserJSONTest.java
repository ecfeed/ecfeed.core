package com.ecfeed.core.parser;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ModelChangeRegistrator;
import com.ecfeed.core.parser.model.load.ModelData;
import com.ecfeed.core.parser.model.load.ModelDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class ModelParserJSONTest {

    @Test
    void parseModelCSVTest() {

        ModelData model = ModelDataFactory.create("" +
                        "{\"tests\": [\n" +
                        "  {\n" +
                        "    \"index\": 0,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"index\": 1,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"Eva\",\n" +
                        "      \"age\": \"30\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"index\": 2,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"John\",\n" +
                        "      \"age\": \"30\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"index\": 3,\n" +
                        "    \"Person\": {\n" +
                        "      \"name\": \"Eva\",\n" +
                        "      \"age\": \"50\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "]}",
                ModelDataFactory.Type.JSON);

        List<String> data = model.getRaw();
        List<String> header = model.getHeader();
        List<Set<String>> body = model.getBody();

        System.out.println("test");
    }
}
