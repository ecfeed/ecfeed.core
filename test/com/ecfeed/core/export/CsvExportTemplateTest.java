/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 *******************************************************************************/

package com.ecfeed.core.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import org.junit.Test;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.StringHelper;


// TODO - add tests for other templates

public class CsvExportTemplateTest {

    @Test
    public void ShouldNotThrowWhenEmpty() {

        String templateText = new String();

        CsvExportTemplate csvExportTemplate = new CsvExportTemplate(createMethodNode(), new ExtLanguageManagerForJava());

        try {
            csvExportTemplate.setTemplateText(templateText);
        } catch (Exception e) {
            fail("Exception thrown during export.");
        }

        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getHeaderTemplate()));
        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getTestCaseTemplate()));
        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getFooterTemplate()));
    }

    @Test
    public void ShouldParseForTwoParamsTemplateRepeatedly() {

        String templateText =
                TemplateText.createTemplateText(
                        "$1.name,$2.name",
                        "$1.value,$2.value",
                        "");


        CsvExportTemplate csvExportTemplate =
                new CsvExportTemplate(createMethodNode(), new ExtLanguageManagerForJava());

        setAndVerifyTemplateTexts(csvExportTemplate, templateText);
        setAndVerifyTemplateTexts(csvExportTemplate, templateText);
        setAndVerifyTemplateTexts(csvExportTemplate, templateText);
    }

    private void setAndVerifyTemplateTexts(CsvExportTemplate csvExportTemplate, String templateText) {

        try {
            csvExportTemplate.setTemplateText(templateText);
        } catch (Exception e) {
            fail("Exception thrown during export.");
        }

        final String headerTemplate = csvExportTemplate.getHeaderTemplate();
        assertEquals("$1.name,$2.name", headerTemplate);

        final String testCaseTemplate = csvExportTemplate.getTestCaseTemplate();
        assertEquals("$1.value,$2.value", testCaseTemplate);

        final String footerTemplate = csvExportTemplate.getFooterTemplate();
        assertTrue(StringHelper.isNullOrEmpty(footerTemplate));
    }

    @Test
    public void ShouldParseMultiLineSectionsTemplate() {

        String templateText =
                TemplateText.createTemplateText(
                        "HEADER\n$1.name,$2.name",
                        "TEST CASE\n$1.value,$2.value",
                        "FOOTER 1\nFOOTER 2");

        CsvExportTemplate csvExportTemplate =
                new CsvExportTemplate(createMethodNode(), new ExtLanguageManagerForJava());

        try {
            csvExportTemplate.setTemplateText(templateText);
        } catch (Exception e) {
            fail("Exception thrown during export.");
        }

        String header = csvExportTemplate.getHeaderTemplate();
        String expectedHeader = "HEADER" + StringHelper.newLine() + "$1.name,$2.name";
        assertEquals(expectedHeader, header);

        String testCase = csvExportTemplate.getTestCaseTemplate();
        String expectedTestCase = "TEST CASE" + StringHelper.newLine() + "$1.value,$2.value";

        assertEquals(expectedTestCase, testCase);
    }

    @Test
    public void ShouldNotThrowWhenOnlyInvalidMarker() {

        String templateText = "[xxx]";

        CsvExportTemplate csvExportTemplate =
                new CsvExportTemplate(createMethodNode(), new ExtLanguageManagerForJava());

        try {
            csvExportTemplate.setTemplateText(templateText);
        } catch (Exception e) {
            fail("Exception thrown during export.");
        }

        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getHeaderTemplate()));
        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getTestCaseTemplate()));
        assertTrue(StringHelper.isNullOrEmpty(csvExportTemplate.getFooterTemplate()));
    }

    private MethodNode createMethodNode() {
        return new MethodNode("methodName", null);
    }

}