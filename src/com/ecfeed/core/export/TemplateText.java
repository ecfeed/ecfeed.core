package com.ecfeed.core.export;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

public class TemplateText {

    private static final String HEADER_MARKER = "[Header]";
    private static final String TEST_CASE_MARKER = "[TestCase]";
    private static final String FOOTER_MARKER = "[Footer]";

    String fInitialTemplateText;
    String fCompleteTemplateText;
    StringHolder fHeaderTemplateText;
    StringHolder fTestCaseTemplateText;
    StringHolder fFooterTemplateText;

    public TemplateText(String completeTemplateText) {

        fInitialTemplateText = completeTemplateText;

        fHeaderTemplateText = new StringHolder();
        fTestCaseTemplateText = new StringHolder();
        fFooterTemplateText = new StringHolder();

        setTemplateText(completeTemplateText);
    }

    public void setTemplateText(String completeTemplateText) {

        verifyTemplateText(completeTemplateText);

        divideIntoSubtemplates(
                completeTemplateText,
                fHeaderTemplateText,
                fTestCaseTemplateText,
                fFooterTemplateText);

        fCompleteTemplateText = completeTemplateText;
    }

    public boolean isTemplateTextModified() {

        if (fCompleteTemplateText.equals(fInitialTemplateText)) {
            return false;
        }

        return true;
    }

    public String getCompleteTemplateText() {

        return fCompleteTemplateText;
    }

    public String getHeaderTemplateText() {

        return fHeaderTemplateText.get();
    }

    public String getTestCaseTemplateText() {

        return fTestCaseTemplateText.get();
    }

    public String getFooterTemplateText() {

        return fFooterTemplateText.get();
    }

    public static void verifyTemplateText(String templateText) {

        int headerTagIndex = getTagIndex(HEADER_MARKER, templateText);

        if (headerTagIndex < 0) {
            ExceptionHelper.reportRuntimeException("Header tag not found.");
        }

        int testCaseTagIndex = getTagIndex(TEST_CASE_MARKER, templateText);

        if (testCaseTagIndex < 0) {
            ExceptionHelper.reportRuntimeException("Test case tag not found.");
        }

        int footerTagIndex = getTagIndex(TEST_CASE_MARKER, templateText);

        if (footerTagIndex < 0) {
            ExceptionHelper.reportRuntimeException("Footer tag not found.");
        }
    }

    private static int getTagIndex(String sectionMarker, String templateText) {

        String[] lines = templateText.split(System.getProperty("line.separator"));

        int tagIndex = -1;

        for (String line : lines) {

            tagIndex = line.indexOf(sectionMarker);

            if (tagIndex < 0) {
                continue;
            }

            String trimmedLine = line.trim();

            if (trimmedLine.equals(sectionMarker)) {
                return tagIndex;
            }

            reportExceptionInvalidShortLine(line);
        }

        return -1;
    }

    private static void reportExceptionInvalidShortLine(String line) {

        String shortLine = line.substring(0, 100);
        if (shortLine.length() < line.length()) {
            shortLine += "...";
        }

        ExceptionHelper.reportRuntimeException("Invalid tag line: " + shortLine);
    }

    public static String createTemplateText(
            String headerTemplate,
            String testCaseTemplate,
            String footerTemplate) {

        String defaultTemplateText =
                StringHelper.appendNewline(HEADER_MARKER)
                        + StringHelper.appendNewline(headerTemplate)
                        + StringHelper.appendNewline(TEST_CASE_MARKER)
                        + StringHelper.appendNewline(testCaseTemplate)
                        + StringHelper.appendNewline(FOOTER_MARKER)
                        + StringHelper.appendNewline(footerTemplate);

        return defaultTemplateText;
    }

    private static void divideIntoSubtemplates(
            String templateText,
            StringHolder fHeaderTemplate,
            StringHolder fTestCaseTemplate,
            StringHolder fFooterTemplate) {

        StringHolder currentSectionMarker = new StringHolder();

        fHeaderTemplate.reset();
        fTestCaseTemplate.reset();
        fFooterTemplate.reset();

        String[] lines = templateText.split("\n");

        for (String line : lines) {

            if (isCommentLine(line)) {
                continue;
            }

            if (isSectionMarker(line)) {
                currentSectionMarker.set(getMarker(line));
                continue;
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

}
