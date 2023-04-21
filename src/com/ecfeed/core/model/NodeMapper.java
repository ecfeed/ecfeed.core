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

public class NodeMapper { // XYX rename deployed to destination

	private final Map<AbstractNode, AbstractNode> fSourceToDeployedMap = new HashMap<>();
	private final Map<AbstractNode, AbstractNode> fDeployedToSourceMap = new HashMap<>();

	public void addMappings(AbstractNode sourceNode, AbstractNode deployedNode) {

		fSourceToDeployedMap.put(sourceNode, deployedNode);
		fDeployedToSourceMap.put(deployedNode, sourceNode);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getSourceNode(T deployedNode) {

		AbstractNode sourceNode = fDeployedToSourceMap.get(deployedNode);

		return sourceNode != null ? (T) sourceNode : deployedNode;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getDeployedNode(T sourceNode) {

		AbstractNode deployedNode = fSourceToDeployedMap.get(sourceNode);

		return deployedNode != null ? (T) deployedNode : sourceNode;
	}
}
