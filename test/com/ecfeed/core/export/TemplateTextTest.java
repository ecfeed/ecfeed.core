package com.ecfeed.core.export;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTextTest {

    @Test
    void testEmptyTemplate1() {

        String templateTextStr = "";

        testIncorrectTemplate(templateTextStr, TemplateText.TEMPLATE_EMPTY);
    }

    @Test
    void testEmptyTemplate2() {

        String templateTextStr = null;

        testIncorrectTemplate(templateTextStr, TemplateText.TEMPLATE_EMPTY);
    }

    @Test
    void testEmptyTemplate3() {

        String templateTextStr = "   ";

        testIncorrectTemplate(templateTextStr, TemplateText.TEMPLATE_EMPTY);
    }

    @Test
    void testEmptyTemplate4() {

        String templateTextStr = "\n";

        testIncorrectTemplate(templateTextStr, TemplateText.TEMPLATE_EMPTY);
    }

    @Test
    void testEmptyTemplate5() {

        String templateTextStr = "\t";

        testIncorrectTemplate(templateTextStr, TemplateText.TEMPLATE_EMPTY);
    }

    @Test
    void testInvalidPrefix1() {

        String templateTextStr = "XXX\n[Header]";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_LINE, "XXX");
    }

    @Test
    void testInvalidPrefix2() {

        String templateTextStr = "XXX\n[TestCase]";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_LINE, "XXX");
    }

    @Test
    void testInvalidPrefixLine() {

        String templateTextStr = "\n[TestCase]\nX";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_LINE, "Line: 1");
    }


    @Test
    void testMissingTestCaseTag() {

        String templateTextStr = "[Footer]";

        testIncorrectTemplate(templateTextStr, TemplateText.MISSING_TEST_CASE_TAG);
    }

    @Test
    void testInvalidHeaderMarker() {

        String templateTextStr = "[Header]ddd";

        testIncorrectTemplate(templateTextStr, TemplateText.INVALID_TEMPLATE_TAG_LINE, "[Header]");
    }

    @Test
    void testInvalidTestCaseMarker() {

        String templateTextStr = "[TestCase]ddd";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "[TestCase]");
    }

    @Test
    void testInvalidFooterMarker() {

        String templateTextStr = "[Footer]ddd";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "[Footer]");
    }

    @Test
    void testInvalidMarkerSequence() {

        testIncorrectTemplate(
                "[TestCase]\nX\n[Header]",
                TemplateText.INVALID_TAG_SEQUENCE, "[Header]");

        testIncorrectTemplate(
                "[Header]\n[Footer]\n[TestCase]\nX\n",
                TemplateText.MISSING_TEST_CASE_TAG, "[Footer]");

        testIncorrectTemplate(
                "[Footer]\n[Header]\n[TestCase]\nX\n",
                TemplateText.MISSING_TEST_CASE_TAG, "[Footer]");
    }

    @Test
    void testEmptyTestCaseContent1() {

        String templateTextStr = "[TestCase]";
        testIncorrectTemplate(templateTextStr, TemplateText.EMPTY_TEST_CASE_SECTION);
    }

    @Test
    void testEmptyTestCaseContent2() {

        String templateTextStr = "[TestCase]\n[Footer]";
        testIncorrectTemplate(templateTextStr, TemplateText.EMPTY_TEST_CASE_SECTION);
    }

    @Test
    void testContentInTheSameLine1A() {

        String templateTextStr = "[TestCase]X";
        testIncorrectTemplate(templateTextStr, TemplateText.INVALID_TEMPLATE_TAG_LINE, templateTextStr);
    }

    @Test
    void testContentInTheSameLine1B() {

        String templateTextStr = "X[TestCase]";

        testIncorrectTemplate(templateTextStr, TemplateText.INVALID_TEMPLATE_TAG_LINE, templateTextStr);
    }


    @Test
    void testContentInTheSameLine2A() {

        String templateTextStr = "[Header]x\n[TestCase]\nX";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "[Header]x");
    }

    @Test
    void testContentInTheSameLine2B() {

        String templateTextStr = "x[Header]\n[TestCase]\nX";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "x[Header]");
    }

    @Test
    void testContentInTheSameLine3A() {

        String templateTextStr = "[TestCase]\nX\n[Footer]Y";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "[Footer]Y");
    }

    @Test
    void testContentInTheSameLine3B() {

        String templateTextStr = "[TestCase]\nX\nY[Footer]";

        testIncorrectTemplate(
                templateTextStr,
                TemplateText.INVALID_TEMPLATE_TAG_LINE, "Y[Footer]");
    }

    @Test
    void testCorrectTemplate1() {

        String templateTextStr = "[TestCase]\nX";

        checkCorrectTeplate(
                templateTextStr,
                null, "X", null);
    }

    @Test
    void testCorrectTemplate2() {

        String templateTextStr = "[Header]\n[TestCase]\nX\n[Footer]";

        checkCorrectTeplate(
                templateTextStr,
                null, "X", null);
    }

    @Test
    void testCorrectTemplate3() {

        String templateTextStr = "[Header]\nHHH\n[TestCase]\nTTT\n[Footer]\nFFF";

        checkCorrectTeplate(
                templateTextStr,
                "HHH", "TTT", "FFF");
    }

    @Test
    void testCorrectTemplate4() {

        String templateTextStr = "[Header]\n[H]\n[TestCase]\n[T]\n[Footer]\n[F]";

        checkCorrectTeplate(
                templateTextStr,
                "[H]", "[T]", "[F]");
    }


    private void checkCorrectTeplate(
            String templateTextStr,
            String expectedHeaderStr,
            String expectedTestCaseStr,
            String expectedFooterStr) {

        TemplateText templateText = new TemplateText(templateTextStr);

        assertTrue(templateText.isCorrect());
        assertNull(templateText.getErrorMessage());

        assertEquals(expectedHeaderStr, templateText.getHeaderTemplateText());
        assertEquals(expectedTestCaseStr, templateText.getTestCaseTemplateText());
        assertEquals(expectedFooterStr, templateText.getFooterTemplateText());
    }

    private void testIncorrectTemplate(String templateTextStr, String... partialErrorMessages) {

        TemplateText templateText = new TemplateText(templateTextStr);

        assertFalse(templateText.isCorrect());
        String errorMessage = templateText.getErrorMessage();


        for (String partialMessage : partialErrorMessages) {

            if (!errorMessage.contains(partialMessage)) {
                fail();
            }
        }

        assertNull(templateText.getHeaderTemplateText());
        assertNull(templateText.getTestCaseTemplateText());
        assertNull(templateText.getFooterTemplateText());
    }

}
