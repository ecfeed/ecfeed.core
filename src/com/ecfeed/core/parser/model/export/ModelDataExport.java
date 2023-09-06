package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;

import java.util.Optional;

public interface ModelDataExport {

    String getFile(TestSuiteNode suite);
    Optional<String> getHeader(MethodNode method);
    Optional<String> getFooter(MethodNode method);
    String getTest(TestCaseNode test);
    String getTest(TestCaseNode test, int index);
}
