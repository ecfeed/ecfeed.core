package com.ecfeed.core.export;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class TemplateText {

    private static final String HEADER_TAG = "[Header]";
    private static final String FOOTER_TAG = "[Footer]";
    private static final String TEST_CASE_TAG = "[TestCase]";

    public static final String TEMPLATE_EMPTY = "Template is empty.";
    public static final String MISSING_TEST_CASE_TAG = "Missing " + TEST_CASE_TAG + " tag.";
    public static final String INVALID_TEMPLATE_TAG_LINE = "Invalid template tag line: ";
    public static final String EMPTY_TEST_CASE_SECTION = "Empty test case section.";
    public static final String INVALID_TAG_SEQUENCE = "Invalid tag sequence.";
    public static final String INVALID_LINE = "Invalid line.";

    private String fInitialTemplateText;
    private String fCurrentTemplateText;
    private StringHolder fHeaderTemplateText;
    private StringHolder fTestCaseTemplateText;
    private StringHolder fFooterTemplateText;
    private boolean fIsCorrect;
    //private boolean fIsStandardized;
    private String fErrorMessage;

    public TemplateText(String completeTemplateText) {

        fInitialTemplateText = completeTemplateText;

        fHeaderTemplateText = new StringHolder();
        fTestCaseTemplateText = new StringHolder();
        fFooterTemplateText = new StringHolder();

        if (StringHelper.isNullOrBlank(completeTemplateText)) {
            markTemplateAsFaulty(TEMPLATE_EMPTY);
            return;
        }

        setTemplateText(completeTemplateText);
    }

    public boolean setTemplateText(String completeTemplateText) {

        fCurrentTemplateText = completeTemplateText;

        if (completeTemplateText.startsWith("RFC") || completeTemplateText.startsWith("STD")) {
            fIsCorrect = true;
            //fIsStandardized = true;
        	return true;
        }
        
        try {
            divideIntoSubtemplates(
                    completeTemplateText,
                    fHeaderTemplateText,
                    fTestCaseTemplateText,
                    fFooterTemplateText);

        } catch (Exception e) {

            markTemplateAsFaulty(e.getMessage());
            return false;
        }
        
        return true;
    }

    private void divideIntoSubtemplates(
            String templateText,
            StringHolder fHeaderTemplate,
            StringHolder fTestCaseTemplate,
            StringHolder fFooterTemplate) throws IOException {

        String currentSectionTag = null;

        fHeaderTemplate.reset();
        fTestCaseTemplate.reset();
        fFooterTemplate.reset();

        fIsCorrect = true;
        fErrorMessage = null;

        int lineNumber = 0;
        boolean wasTestCaseTag = false;
        boolean wasFooterTag = false;
        boolean wasTestCaseContent = false;

        StringReader stringReader = new StringReader(templateText);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String line;


        for (;;) {

            line = bufferedReader.readLine();

            if (line == null) {
                break;
            }

            lineNumber++;

            if (isCommentLine(line)) {
                continue;
            }

            String tmpSectionTag = getSectionTag(line, lineNumber);

            if (tmpSectionTag == null) {

                if (currentSectionTag == null) {
                    reportLineException(INVALID_LINE, lineNumber, line);
                }

            } else {

                if (isFooterTag(tmpSectionTag)) {

                    if (!wasTestCaseTag) {
                        reportLineException(MISSING_TEST_CASE_TAG, lineNumber, line);
                    }
                }

                if (isHeaderTag(tmpSectionTag)) {

                    if (wasTestCaseTag || wasFooterTag) {
                        reportLineException(INVALID_TAG_SEQUENCE, lineNumber, line);
                    }
                }

                if (isTestCaseTag(tmpSectionTag)) {
                    wasTestCaseTag = true;
                }

                if (isFooterTag(tmpSectionTag)) {
                    wasFooterTag = true;
                }

            }

            if (tmpSectionTag != null) {
                currentSectionTag = tmpSectionTag;
                continue;
            }

            if (isTestCaseTag(currentSectionTag)) {
                wasTestCaseContent = true;
            }

            updateTemplatePart(
                    currentSectionTag,
                    line,
                    fHeaderTemplate,
                    fTestCaseTemplate,
                    fFooterTemplate);
        }

        if (!wasTestCaseTag) {
            ExceptionHelper.reportRuntimeException(MISSING_TEST_CASE_TAG);
        }

        if (!wasTestCaseContent) {
            ExceptionHelper.reportRuntimeException(EMPTY_TEST_CASE_SECTION);
        }
    }

    public boolean isTemplateTextModified() {

        if (fCurrentTemplateText.equals(fInitialTemplateText)) {
            return false;
        }

        return true;
    }

    public String getInitialTemplateText() {

        return fInitialTemplateText;
    }
    
    public String getCompleteTemplateText() {

        return fCurrentTemplateText;
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

    public boolean isCorrect() {

        return fIsCorrect;
    }

    public String getErrorMessage() {

        return fErrorMessage;
    }

    public static String createTemplateText(
            String headerTemplate,
            String testCaseTemplate,
            String footerTemplate) {

        String templateText = "";

        if (headerTemplate != null) {
            templateText += StringHelper.appendNewline(HEADER_TAG)
                    + StringHelper.appendNewline(headerTemplate);
        }

        if (testCaseTemplate != null) {
            templateText += StringHelper.appendNewline(TEST_CASE_TAG)
                    + StringHelper.appendNewline(testCaseTemplate);
        }

        if (footerTemplate != null) {
            templateText += StringHelper.appendNewline(FOOTER_TAG)
                    + StringHelper.appendNewline(footerTemplate);
        }

        return templateText;
    }

    private static void updateTemplatePart(
            String templateTag,
            String line,
            StringHolder fHeaderTemplate,
            StringHolder fTestCaseTemplate,
            StringHolder fFooterTemplate) {

        StringHolder templatePart =
                getCurrentTemplatePart(
                        templateTag,
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
            String templateTag,
            StringHolder headerTemplate,
            StringHolder testCaseTemplate,
            StringHolder footerTemplate) {

        if (isHeaderTag(templateTag))
            return headerTemplate;

        if (isTestCaseTag(templateTag))
            return testCaseTemplate;

        if (isFooterTag(templateTag)) return footerTemplate;
        return null;
    }

    private static boolean isCommentLine(String line) {

        final String COMMENTED_LINE_REGEX = "^\\s*#.*";

        if (line.matches(COMMENTED_LINE_REGEX)) {
            return true;
        }
        return false;
    }

    private static String getSectionTag(String line, int lineNumber) {

        line = line.trim();

        String tag = extractTemplateTag(line);

        if (tag == null) {
            return null;
        }

        if (!line.equals(tag)) {
            reportLineException(INVALID_TEMPLATE_TAG_LINE + tag + ".", lineNumber, line);
        }

        return tag;
    }

    private static String extractTemplateTag(String line) {

        if (line.indexOf(HEADER_TAG) >= 0) {
            return HEADER_TAG;
        }

        if (line.indexOf(TEST_CASE_TAG) >= 0) {
            return TEST_CASE_TAG;
        }

        if (line.indexOf(FOOTER_TAG) >= 0) {
            return FOOTER_TAG;
        }

        return null;
    }

    private static boolean isFooterTag(String tag) {

        if (tag.equals(FOOTER_TAG)) {
            return true;
        }

        return false;
    }

    private static boolean isTestCaseTag(String tag) {

        if (tag == null) {
            return false;
        }

        if (tag.equals(TEST_CASE_TAG)) {
            return true;
        }

        return false;
    }

    private static boolean isHeaderTag(String tag) {

        if (tag.equals(HEADER_TAG)) {
            return true;
        }

        return false;
    }

    private static void reportLineException(String message, int lineNumber, String line) {

        String completeMessage = message + " Line: " + lineNumber + ", " + line;
        ExceptionHelper.reportRuntimeException(completeMessage);
    }

    private void markTemplateAsFaulty(String errorMessage) {
        fIsCorrect = false;
        fErrorMessage = errorMessage;
        fHeaderTemplateText.reset();
        fTestCaseTemplateText.reset();
        fFooterTemplateText.reset();
    }

}

