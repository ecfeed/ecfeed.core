package com.ecfeed.core.provider;

import com.ecfeed.core.model.TestCase;

public interface ITCProcessor {

    void processTestCase(TestCase testCase);
    int getGeneratedDataSize();
}

