package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ModelDataExport {

    String getFile(List<TestCaseNode> suite);
    String getFile(TestSuiteNode suite);
    Optional<String> getHeader(MethodNode method);
    Optional<String> getFooter(MethodNode method);
    String getTest(TestCaseNode test);
    String getTest(TestCaseNode test, int index);

    default String getFilePreview(List<TestCaseNode> suite) {

        if (suite.size() == 0) {
            throw new RuntimeException("The test suite should consist of at least one test case!");
        }

        List<TestCaseNode> suiteUpdated = new ArrayList<>();

        for (int i = 0 ; i < suite.size() && i < 2 ; i++) {
            suiteUpdated.add(suite.get(0));
        }

        return getFile(suiteUpdated);
    }
}
