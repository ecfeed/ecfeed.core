package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public interface ModelDataExport {

    String getHeader(MethodNode method);
    String getTest(TestCaseNode tests);
}
