package com.ecfeed.core.export;

import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

public class TemplateText {

    private static final String HEADER_MARKER = "[Header]";
    private static final String TEST_CASE_MARKER = "[TestCase]";
    private static final String FOOTER_MARKER = "[Footer]";

    public static void divideIntoSubtemplates(
            String templateText,
            StringHolder fHeaderTemplate,
            StringHolder fTestCaseTemplate,
            StringHolder fFooterTemplate) {

        StringHolder currentSectionMarker = new StringHolder();

        String[] lines = templateText.split("\n");

        for (String line : lines) {

            if (isCommentLine(line)) {
                continue;
            }

            if (isSectionMarker(line)) {
                currentSectionMarker.set(getMarker(line));
            }

            if (currentSectionMarker.isNull()) {
                continue;
            }

            updateTemplatePart(
                    currentSectionMarker.get(),
                    line,
                    fHeaderTemplate,
                    fTestCaseTemplate,
                    fFooterTemplate);
        }
    }

    private static void updateTemplatePart(
            String marker,
            String line,
            StringHolder fHeaderTemplate,
            StringHolder fTestCaseTemplate,
            StringHolder fFooterTemplate) {

        StringHolder templatePart =
                getCurrentTemplatePart(
                        marker,
                        fHeaderTemplate,
                        fTestCaseTemplate,
                        fFooterTemplate);

        if (StringHelper.isNullOrEmpty(templatePart.get())) {
            templatePart.set(line);
            return;
        }

        templatePart.append(StringHelper.newLine() + line);
    }

    private static StringHolder getCurrentTemplatePart(
            String marker,
            StringHolder headerTemplate,
            StringHolder testCaseTemplate,
            StringHolder footerTemplate) {

        if (marker.equals(HEADER_MARKER)) {
            return headerTemplate;
        }
        if (marker.equals(TEST_CASE_MARKER)) {
            return testCaseTemplate;
        }
        if (marker.equals(FOOTER_MARKER)) {
            return footerTemplate;
        }
        return null;
    }

    private static boolean isCommentLine(String line) {

        final String COMMENTED_LINE_REGEX = "^\\s*#.*";

        if (line.matches(COMMENTED_LINE_REGEX)) {
            return true;
        }
        return false;
    }

    private static boolean isSectionMarker(String line) {

        String trimmedLine = line.trim();

        if (trimmedLine.equals(HEADER_MARKER)) {
            return true;
        }

        if (trimmedLine.equals(TEST_CASE_MARKER)) {
            return true;
        }

        if (trimmedLine.equals(FOOTER_MARKER)) {
            return true;
        }

        return false;
    }

    private static String getMarker(String line) {

        int sectionTitleStart = line.indexOf('[');
        int sectionTitleStop = line.indexOf(']') + 1;

        return line.substring(sectionTitleStart, sectionTitleStop);
    }

    private static boolean setCurrentSectionMarker(String line, StringHolder currentMarker) {

        if (!isSectionMarker(line)) {
            return false;
        }

        currentMarker.set(getMarker(line));

        return true;
    }

}
