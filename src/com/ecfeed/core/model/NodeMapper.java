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

	public enum MappingDirection {
		SOURCE_TO_DESTINATION,
		DESTINATION_TO_SOURCE
	}

	private final Map<AbstractNode, AbstractNode> fSourceToDestination = new HashMap<>();
	private final Map<AbstractNode, AbstractNode> fDestinationToSource = new HashMap<>();

	public void addMappings(AbstractNode sourceNode, AbstractNode destinationNode) {

		fSourceToDestination.put(sourceNode, destinationNode);
		fDestinationToSource.put(destinationNode, sourceNode);
	}

	public void addMappingsForOneNode(AbstractNode sourceNode) {
		addMappings(sourceNode, sourceNode);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getSourceNode(T deployedNode) {

		AbstractNode sourceNode = fDestinationToSource.get(deployedNode);

		return sourceNode != null ? (T) sourceNode : deployedNode;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> T getDestinationNode(T sourceNode) {

		AbstractNode deployedNode = fSourceToDestination.get(sourceNode);

		return deployedNode != null ? (T) deployedNode : sourceNode;
	}

	public <T extends AbstractNode> T getMappedNode(T node, MappingDirection mappingDirection) {

		if (mappingDirection == MappingDirection.SOURCE_TO_DESTINATION) {
			return getDestinationNode(node);
		} else {
			return getSourceNode(node);
		}
	}

}
