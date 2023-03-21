package com.ecfeed.core.provider;

import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IEcfProgressMonitor;

import java.util.List;

public interface ITCProvider {

    void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor);
    void close();
    MethodNode getMethodNode();
    TestCaseNode getNextTestCase();
    List<Constraint> getConstraints();
}

