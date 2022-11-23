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

public class NodeMapper {

	private Map<AbstractNode, AbstractNode> fNodeMappings;

	public NodeMapper() {

		fNodeMappings = new HashMap<>();
	}

	public void addMappings(AbstractNode node1,AbstractNode node2) {

		fNodeMappings.put(node1, node2);
		fNodeMappings.put(node2, node1);
	}

	public AbstractNode getMappedNode(AbstractNode sourceNode) {
		
		AbstractNode mappedNode = fNodeMappings.get(sourceNode);
		return mappedNode;
	}

}
