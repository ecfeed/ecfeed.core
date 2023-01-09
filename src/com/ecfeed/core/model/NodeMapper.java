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

	private Map<AbstractNode, AbstractNode> fNodeMappingsSource;
	private Map<AbstractNode, AbstractNode> fNodeMappingsDeployment;

	public NodeMapper() {

		fNodeMappingsSource = new HashMap<>();
		fNodeMappingsDeployment = new HashMap<>();
	}

	public void addMappings(AbstractNode source, AbstractNode deployment) {

		fNodeMappingsSource.put(source, deployment);
		fNodeMappingsDeployment.put(deployment, source);
	}

	public AbstractNode getMappedNodeSource(AbstractNode nodeDeployment) {
		return fNodeMappingsDeployment.get(nodeDeployment);
	}

	public BasicParameterNode getMappedNodeSource(BasicParameterNode nodeDeployment) {
		return (BasicParameterNode) fNodeMappingsDeployment.get(nodeDeployment);
	}

	public ChoiceNode getMappedNodeSource(ChoiceNode nodeDeployment) {
		return (ChoiceNode) fNodeMappingsDeployment.get(nodeDeployment);
	}

	public AbstractNode getMappedNodeDeployment(AbstractNode nodeSource) {
		return fNodeMappingsSource.get(nodeSource);
	}

	public BasicParameterNode getMappedNodeDeployment(BasicParameterNode nodeSource) {
		return (BasicParameterNode) fNodeMappingsSource.get(nodeSource);
	}

	public ChoiceNode getMappedNodeDeployment(ChoiceNode nodeSource) {
		return (ChoiceNode) fNodeMappingsSource.get(nodeSource);
	}

	public AbstractNode getMappedNode(AbstractNode sourceNode) {
		AbstractNode mappedNode = fNodeMappingsSource.get(sourceNode);

		if (mappedNode == null) {
			return fNodeMappingsDeployment.get(sourceNode);
		} else {
			return mappedNode;
		}
	}
}
