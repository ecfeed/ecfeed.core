/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.io.OutputStream;
import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.model.IsNodeIncludedInGenerationPredicate;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;

public class ModelPartialSerializer {

	private final OutputStream fOutputStream;
	private final int fModelVersion;

	public ModelPartialSerializer(
			OutputStream outputStream, 
			int modelVersion) {

		fOutputStream = outputStream;
		fModelVersion = modelVersion;
	}

	public void serializeModelPartForGenerator(
			MethodNode methodNode,
			List<List<ChoiceNode>> allowedChoiceInput,
			List<IConstraint<ChoiceNode>> allowedConstraints) throws Exception {

		serializeModelPartForGenerator(
				methodNode,
				allowedChoiceInput,
				allowedConstraints,
				true,
				true);
	}

	public void serializeModelPartForGenerator(
			MethodNode methodNode,
			List<List<ChoiceNode>> allowedChoiceInput,
			List<IConstraint<ChoiceNode>> allowedConstraints,
			boolean serializeProperties,
			boolean serializeComments) throws Exception {

		SerializerPredicate serializerPredicate = 
				new SerializerPredicate(methodNode, allowedChoiceInput, allowedConstraints);

		SerializatorParams serializatorParams = 
				new SerializatorParams(serializerPredicate, serializeProperties, serializeComments);

		ModelSerializer fModelSerializer = 
				new ModelSerializer(fOutputStream, fModelVersion, serializatorParams);

		RootNode rootNode = RootNodeHelper.findRootNode(methodNode);

		fModelSerializer.serialize(rootNode);
	}

	private static class SerializerPredicate implements ISerializerPredicate {

		private final MethodNode fMethodNode;
		private final List<List<ChoiceNode>> fAllowedChoiceInput;
		private final List<IConstraint<ChoiceNode>> fAllowedConstraints;

		public SerializerPredicate(
				MethodNode methodNode,
				List<List<ChoiceNode>> allowedChoiceInput,
				List<IConstraint<ChoiceNode>> allowedConstraints ) {

			fMethodNode = methodNode;
			fAllowedChoiceInput = allowedChoiceInput;
			fAllowedConstraints = allowedConstraints;
		}

		@Override
		public boolean shouldSerializeNode(IAbstractNode abstractNode) {

			IsNodeIncludedInGenerationPredicate predicate = 
					new IsNodeIncludedInGenerationPredicate(
							fMethodNode, fAllowedChoiceInput, fAllowedConstraints);

			return predicate.test(abstractNode );
		}

	}


}
