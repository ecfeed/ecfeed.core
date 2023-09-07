package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;

import java.util.List;
import java.util.Optional;

public interface ModelDataExport {

    String getFilePreview(List<TestCaseNode> suite);
    String getFile(List<TestCaseNode> suite);
    String getFile(TestSuiteNode suite);
    Optional<String> getHeader(MethodNode method);
    Optional<String> getFooter(MethodNode method);
    String getTest(TestCaseNode test);
    String getTest(TestCaseNode test, int index);
}
