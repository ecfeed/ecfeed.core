package com.ecfeed.core.provider;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public interface ITCProvider {

    void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) throws Exception;
    void close();
    MethodNode getMethodNode();
    TestCaseNode getNextTestCase() throws Exception;
}

