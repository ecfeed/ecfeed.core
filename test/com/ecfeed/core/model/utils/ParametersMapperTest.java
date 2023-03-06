/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNodeHelper;
import com.ecfeed.core.model.ParametersAndConstraintsParentNodeHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.utils.ParametersMapper.ParameterType;

public class ParametersMapperTest {

	@Test
	public void localParametersBasic() {

		RootNode rootNode = new RootNode("Root", null);

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// parameter 1 of method 

		BasicParameterNode methodParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC1", "MC1");

		// 	Root
		// 		Class
		// 			Method
		//				MP1
		//					MC1

		// parameters container from level of method node

		ParametersMapper parametersContainer = new ParametersMapper();
		parametersContainer.calculateParametersData(methodNode, ParameterType.STANDARD);

		List<String> parameterNames = parametersContainer.getParameterNames();

		assertEquals(1, parameterNames.size());

		assertEquals("MP1", parameterNames.get(0));

		assertNull(parametersContainer.findBasicParameter("NO-PARAM"));

		BasicParameterNode resultMethodParameter1 = parametersContainer.findBasicParameter("MP1");
		assertEquals(methodParameterNode1, resultMethodParameter1);

		AbstractParameterNode linkingParameter1 = parametersContainer.findLinkingParameter("MP1");
		assertNull(linkingParameter1);
	}

	@Test
	public void localParameters() {

		RootNode rootNode = new RootNode("Root", null);

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// parameter 1 of method 

		BasicParameterNode methodParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC1", "MC1");

		// parameter 2 of method

		BasicParameterNode methodParameterNode2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP2", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode2, "MC2", "MC2");

		// composite parameter 1

		CompositeParameterNode compositeParameterNode1 = 
				MethodNodeHelper.addCompositeParameter(methodNode, "S1", null);

		// parameter 1 of composite 1

		BasicParameterNode basicParameterNode1OfComposite1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						compositeParameterNode1, "P11", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode1OfComposite1, "C1", "C1");

		// parameter 2 of composite 1

		BasicParameterNode basicParameterNode2Ofcomposite1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						compositeParameterNode1, "P12", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode2Ofcomposite1, "C2", "C2");

		// parameter 0 of composite 1

		BasicParameterNode basicParameterNode0OfComposite1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						compositeParameterNode1, "P10", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode0OfComposite1, "C0", "C0");

		// composite 2

		CompositeParameterNode compositeParameterNode2 = 
				CompositeParameterNodeHelper.addCompositeParameter(compositeParameterNode1, "S2", null);

		// parameter 1 of composite 1

		BasicParameterNode basicParameterNode1OfComposite2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						compositeParameterNode2, "P21", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode1OfComposite2, "C1", "C1");

		// 	Root
		// 		Class
		// 			Method
		//				MP1
		//					MC1
		//				MP2
		//					MC2
		//				S1
		//					P11
		//						C1
		//					P12
		// 						C2
		//					P10
		//						C0
		//					S2
		//						P21
		//							C1

		// parameters container from level of method node

		ParametersMapper parametersContainer = new ParametersMapper();
		parametersContainer.calculateParametersData(methodNode, ParameterType.STANDARD);

		List<String> parameterNames = parametersContainer.getParameterNames();

		assertEquals(6, parameterNames.size());

		assertEquals("MP1", parameterNames.get(0));
		assertEquals("MP2", parameterNames.get(1));		
		assertEquals("S1:P10", parameterNames.get(2));
		assertEquals("S1:P11", parameterNames.get(3));
		assertEquals("S1:P12", parameterNames.get(4));
		assertEquals("S1:S2:P21", parameterNames.get(5));

		assertNull(parametersContainer.findBasicParameter("NO-PARAM"));

		BasicParameterNode resultMethodParameter1 = parametersContainer.findBasicParameter("MP1");
		BasicParameterNode resultMethodParameter2 = parametersContainer.findBasicParameter("MP2");

		assertEquals(methodParameterNode1, resultMethodParameter1);
		assertEquals(methodParameterNode2, resultMethodParameter2);

		BasicParameterNode resultParameter0OfComposite1 = parametersContainer.findBasicParameter("S1:P10");
		BasicParameterNode resultParameter1OfComposite1 = parametersContainer.findBasicParameter("S1:P11");
		BasicParameterNode resultParameter2OfComposite1 = parametersContainer.findBasicParameter("S1:P12");

		assertEquals(basicParameterNode0OfComposite1, resultParameter0OfComposite1);
		assertEquals(basicParameterNode1OfComposite1, resultParameter1OfComposite1);
		assertEquals(basicParameterNode2Ofcomposite1, resultParameter2OfComposite1);

		BasicParameterNode resultParameter1OfComposite2 =
				parametersContainer.findBasicParameter("S1:S2:P21");

		assertEquals(basicParameterNode1OfComposite2, resultParameter1OfComposite2);

		// parameters container from level of composite 1

		parametersContainer.calculateParametersData(compositeParameterNode1, ParameterType.STANDARD);
		parameterNames = parametersContainer.getParameterNames();

		assertEquals(4, parameterNames.size());

		assertEquals("S1:P10", parameterNames.get(0));
		assertEquals("S1:P11", parameterNames.get(1));
		assertEquals("S1:P12", parameterNames.get(2));
		assertEquals("S1:S2:P21", parameterNames.get(3));

		resultMethodParameter1 = parametersContainer.findBasicParameter("MP1");
		resultMethodParameter2 = parametersContainer.findBasicParameter("MP2");

		assertNull(resultMethodParameter1);
		assertNull(resultMethodParameter2);

		resultParameter0OfComposite1 = parametersContainer.findBasicParameter("S1:P10");
		resultParameter1OfComposite1 = parametersContainer.findBasicParameter("S1:P11");
		resultParameter2OfComposite1 = parametersContainer.findBasicParameter("S1:P12");

		assertEquals(basicParameterNode0OfComposite1, resultParameter0OfComposite1);
		assertEquals(basicParameterNode1OfComposite1, resultParameter1OfComposite1);
		assertEquals(basicParameterNode2Ofcomposite1, resultParameter2OfComposite1);

		resultParameter1OfComposite2 =
				parametersContainer.findBasicParameter("S1:S2:P21");

		assertEquals(basicParameterNode1OfComposite2, resultParameter1OfComposite2);

		// parameters container from level of composite 2

		parametersContainer.calculateParametersData(compositeParameterNode2, ParameterType.STANDARD);
		parameterNames = parametersContainer.getParameterNames();

		assertEquals(1, parameterNames.size());
		assertEquals("S1:S2:P21", parameterNames.get(0));

		resultMethodParameter1 = parametersContainer.findBasicParameter("MP1");
		resultMethodParameter2 = parametersContainer.findBasicParameter("MP2");

		assertNull(resultMethodParameter1);
		assertNull(resultMethodParameter2);

		resultParameter0OfComposite1 = parametersContainer.findBasicParameter("S1:P10");
		resultParameter1OfComposite1 = parametersContainer.findBasicParameter("S1:P11");
		resultParameter2OfComposite1 = parametersContainer.findBasicParameter("S1:P12");

		assertNull(resultParameter0OfComposite1);
		assertNull(resultParameter1OfComposite1);
		assertNull(resultParameter2OfComposite1);

		resultParameter1OfComposite2 = parametersContainer.findBasicParameter("S1:S2:P21");

		assertEquals(basicParameterNode1OfComposite2, resultParameter1OfComposite2);
	}

	@Test
	public void globalBasicParameters() {

		RootNode rootNode = new RootNode("Root", null);

		// root parameter node 1

		BasicParameterNode rootParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(rootNode, "RP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(rootParameterNode1, "RC1", "RC1");

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// class parameter node 

		BasicParameterNode classParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(classNode, "CP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(classParameterNode1, "CC1", "CC1");

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// parameter 1 of method 

		BasicParameterNode methodParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", "String");
		methodParameterNode1.setLinkToGlobalParameter(rootParameterNode1);

		// parameter 2 of method

		BasicParameterNode methodParameterNode2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP2", "String");
		methodParameterNode2.setLinkToGlobalParameter(classParameterNode1);

		// 	Root
		//		RP1
		//			RC1
		// 		Class
		//			CP1
		//				CC1
		// 			Method
		//				MP1 -> RP1 
		//				MP2 -> CP1

		// parameters container from level of method node

		ParametersMapper parametersContainer = new ParametersMapper();
		parametersContainer.calculateParametersData(methodNode, ParameterType.STANDARD);

		List<String> parameterNames = parametersContainer.getParameterNames();

		assertEquals(2, parameterNames.size());

		assertEquals("MP1", parameterNames.get(0));
		assertEquals("MP2", parameterNames.get(1));		

		assertNull(parametersContainer.findBasicParameter("NO-PARAM"));

		// check parameter 1

		BasicParameterNode resultMethodParameter1 = parametersContainer.findBasicParameter("MP1");
		assertEquals(methodParameterNode1, resultMethodParameter1);

		CompositeParameterNode resultLinkingParameter1 = parametersContainer.findLinkingParameter("MP1");
		assertNull(resultLinkingParameter1);

		// check parameter 2

		BasicParameterNode resultMethodParameter2 = parametersContainer.findBasicParameter("MP2");
		assertEquals(methodParameterNode2, resultMethodParameter2);

		CompositeParameterNode resultLinkingParameter2 = parametersContainer.findLinkingParameter("MP2");
		assertNull(resultLinkingParameter2);
	}

	@Test
	public void globalStructure() {

		RootNode rootNode = new RootNode("Root", null);

		CompositeParameterNode globalCompositeParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addCompositeParameter(rootNode, "GS1");

		// parameter 1 of composite with choice

		BasicParameterNode rootParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						globalCompositeParameterNode1, "GP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(rootParameterNode1, "GC1", "GC1");

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// local composite

		CompositeParameterNode localCompositeParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addCompositeParameter(methodNode, "S1");

		localCompositeParameterNode.setLinkToGlobalParameter(globalCompositeParameterNode1);

		// 	Root
		//		GS1
		//			GP1
		//				SC1
		// 		Class
		// 			Method
		//				S1 -> GS1 

		// parameters container from level of method node

		ParametersMapper parametersContainer = new ParametersMapper();
		parametersContainer.calculateParametersData(methodNode, ParameterType.STANDARD);

		List<String> parameterNames = parametersContainer.getParameterNames();

		assertEquals(1, parameterNames.size());

		assertEquals("S1:GP1", parameterNames.get(0));

		BasicParameterNode resultParameter1 = parametersContainer.findBasicParameter("S1:GP1");
		assertEquals(rootParameterNode1, resultParameter1);

		AbstractParameterNode linkingParameter = parametersContainer.findLinkingParameter("S1:GP1");
		assertEquals(localCompositeParameterNode, linkingParameter);
	}

	// TODO MO-RE add tests for	ParameterType.EXPECTED
}
