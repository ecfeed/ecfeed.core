package com.ecfeed.core.evaluator;

import java.util.Collection;

import com.ecfeed.core.model.ChoiceNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ChoiceMultiMappings {

	//    private String fDebugCode;
    private Multimap<ChoiceNode, ChoiceNode> fMap;

    public ChoiceMultiMappings(String debugCode) {

    	//        fDebugCode = debugCode;
        fMap = HashMultimap.create();
    }

    public void put(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fMap.put(keyChoiceNode, valueChoiceNode);
    }

    public Collection<ChoiceNode> get(ChoiceNode keyChoiceNode) {

        return fMap.get(keyChoiceNode);
    }
}
