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

	private final Map<AbstractNode, AbstractNode> fNodeMappingsSource = new HashMap<>();
	private final Map<AbstractNode, AbstractNode> fNodeMappingsDeployment = new HashMap<>();

	public void addMappings(AbstractNode source, AbstractNode deployment) {

		fNodeMappingsSource.put(source, deployment);
		fNodeMappingsDeployment.put(deployment, source);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getMappedNodeSource(T deployment) {
		AbstractNode source = fNodeMappingsDeployment.get(deployment);

		return source != null ? (T) source : deployment;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getMappedNodeDeployment(T source) {
		AbstractNode deployment = fNodeMappingsSource.get(source);

		return deployment != null ? (T) deployment : source;
	}
}
