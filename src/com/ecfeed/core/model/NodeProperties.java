/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NodeProperties {
	private Map<String, NodeProperty> fMap = new HashMap<String, NodeProperty>();

	public void put(String key, NodeProperty property) {
		fMap.put(key, property);
	}

	public NodeProperty get(String key) {
		return fMap.get(key);
	}

	public Set<String> getKeys() {
		return fMap.keySet();
	}

	public int size() {
		return fMap.size();
	}

	public void remove(String key) {
		fMap.remove(key);
	}

	public boolean isMatch(NodeProperties other) {
		if (size() != other.size()) {
			return false;
		}

		if (size() == 0) {
			return true;
		}

		Set<String> keys = getKeys();

		for (String key : keys) {
			if (!elementsMatch(key, fMap, other.fMap)) {
				return false;
			}
		}

		return true;
	}

	private static boolean elementsMatch(String key, Map<String, NodeProperty> map1, Map<String, NodeProperty> map2) {
		NodeProperty property1 = map1.get(key);
		NodeProperty property2 = map2.get(key);

		if (property1 == null && property2 == null) {
			return true;
		}		

		if (property1 == null && property2 != null) {
			return false;
		}

		if (property2 == null && property1 != null) {
			return false;
		}

		if (!property1.isMatch(property2)) {
			return false;
		}

		return true;
	}

	NodeProperties getCopy() {
		NodeProperties copy = new NodeProperties();

		Set<String> keys = getKeys();

		for (String key : keys) {
			NodeProperty property = get(key);
			copy.put(key, property);
		}

		return copy;
	}
}
