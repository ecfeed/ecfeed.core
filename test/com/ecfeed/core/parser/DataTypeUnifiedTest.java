package com.ecfeed.core.parser;

import com.ecfeed.core.parser.model.load.DataType;
import com.ecfeed.core.parser.model.load.DataTypeFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataTypeUnifiedTest {
    private DataType type;

    @BeforeEach
    void setUp() {

        type = DataTypeFactory.create(true);
    }

    @Test
    void determineBooleanTest() {
        type.feed("TRUE");
        type.feed("FALSE");
        type.feed("true");
        type.feed("false");

        Assertions.assertEquals("boolean", type.determine());
    }

    @Test
    void determineBooleanFalseTest() {
        type.feed("TRUE");
        type.feed("FALSE");
        type.feed("true");
        type.feed("false");
        type.feed("1");

        Assertions.assertNotEquals("boolean", type.determine());
    }

    @Test
    void determineCharTest() {
        type.feed("A");
        type.feed("B");
        type.feed("1");
        type.feed("2");
        type.feed("!");
        type.feed("\t");
        type.feed("\n");

        Assertions.assertEquals("char", type.determine());
    }

    @Test
    void determineCharFalseTest() {
        type.feed("A");
        type.feed("B");
        type.feed("1");
        type.feed("2");
        type.feed("!");
        type.feed("\t");
        type.feed("\n");
        type.feed("true");

        Assertions.assertNotEquals("char", type.determine());
    }

    @Test
    void determineFloatTest() {
        type.feed("1.5");
        type.feed("+1.5");
        type.feed("-1.5");

        Assertions.assertEquals("double", type.determine());
    }

    @Test
    void determineFloatFalseTest() {
        type.feed("1.5");
        type.feed("+1.5");
        type.feed("-1.5");
        type.feed("true");

        Assertions.assertNotEquals("double", type.determine());
    }

    @Test
    void determineDoubleTest() {
        type.feed("1.0");
        type.feed("1");
        type.feed("+3.14");
        type.feed("-3.14");

        Assertions.assertEquals("double", type.determine());
    }

    @Test
    void determineDoubleFalseTest() {
        type.feed("1.0");
        type.feed("1");
        type.feed("+3.14");
        type.feed("-3.14");
        type.feed("true");

        Assertions.assertNotEquals("double", type.determine());
    }

    @Test
    void determineByteTest() {
        type.feed("100");
        type.feed("+100");
        type.feed("-100");

        Assertions.assertEquals("int", type.determine());
    }

    @Test
    void determineByteFalseTest() {
        type.feed("100");
        type.feed("+100");
        type.feed("-100");
        type.feed("3.14");

        Assertions.assertNotEquals("int", type.determine());
    }

    @Test
    void determineShortTest() {
        type.feed("1000");
        type.feed("+1000");
        type.feed("-1000");

        Assertions.assertEquals("int", type.determine());
    }

    @Test
    void determineShortFalseTest() {
        type.feed("1000");
        type.feed("+1000");
        type.feed("-1000");
        type.feed("3.14");

        Assertions.assertNotEquals("int", type.determine());
    }

    @Test
    void determineIntegerTest() {
        type.feed("100000");
        type.feed("+100000");
        type.feed("-100000");

        Assertions.assertEquals("int", type.determine());
    }

    @Test
    void determineIntegerFalseTest() {
        type.feed("100000");
        type.feed("+100000");
        type.feed("-100000");
        type.feed("3.14");

        Assertions.assertNotEquals("int", type.determine());
    }

    @Test
    void determineLongTest() {
        type.feed("3000000000");
        type.feed("+3000000000");
        type.feed("-3000000000");

        Assertions.assertEquals("long", type.determine());
    }

    @Test
    void determineLongFalseTest() {
        type.feed("3000000000");
        type.feed("+3000000000");
        type.feed("-3000000000");
        type.feed("3.14");

        Assertions.assertNotEquals("long", type.determine());
    }

}
