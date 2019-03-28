package com.ecfeed.core.genservice.provider;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.provider.ITCProviderInitData;

public class RemoteTCProviderInitData implements ITCProviderInitData {

    public MethodNode methodNode;
    public String requestType;
    public String requestText;

    public RemoteTCProviderInitData(MethodNode methodNodeArg, String requestTypeArg, String requestTextArg) {
        methodNode = methodNodeArg;
        requestType = requestTypeArg;
        requestText = requestTextArg;
    }

}
