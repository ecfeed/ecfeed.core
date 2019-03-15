package com.ecfeed.core.provider;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public interface ITestCasesProvider {

    MethodNode getMethodNode();
    TestCaseNode getNextTestCase() throws Exception;
    boolean canCalculateProgress();
    int getTotalProgress();
    int getActualProgress();

}
