///*******************************************************************************
// *
// * Copyright (c) 2016 ecFeed AS.                                                
// * All rights reserved. This program and the accompanying materials              
// * are made available under the terms of the Eclipse Public License v1.0         
// * which accompanies this distribution, and is available at                      
// * http://www.eclipse.org/legal/epl-v10.html 
// *  
// *******************************************************************************/
//
//package com.ecfeed.core.implementation;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.ecfeed.core.model.BasicParameterNode;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.core.model.ClassNode;
//import com.ecfeed.core.model.ConstraintNode;
//import com.ecfeed.core.model.IAbstractNode;
//import com.ecfeed.core.model.IModelVisitor;
//import com.ecfeed.core.model.MethodNode;
//import com.ecfeed.core.model.RootNode;
//import com.ecfeed.core.model.TestCaseNode;
//import com.ecfeed.core.model.TestSuiteNode;
//import com.ecfeed.core.type.adapter.IPrimitiveTypePredicate;
//import com.ecfeed.core.utils.LogHelperCore;
//
//
//public abstract class CachedImplementationStatusResolver extends AbstractImplementationStatusResolver {
//
//	private static Map<IAbstractNode, EImplementationStatus> fCache = new HashMap<>();
//
//
//	public CachedImplementationStatusResolver(IPrimitiveTypePredicate primitiveTypePredicate) {
//		super(primitiveTypePredicate);
//	}
//
//	@Override
//	public EImplementationStatus getImplementationStatus(IAbstractNode node) {
//
//		EImplementationStatus status = fCache.get(node);
//
//		if (status == null) {
//			status = super.getImplementationStatus(node);
//			updateCache(node, status);
//		}
//
//		return status;
//	}
//
//	public static void clearCache(IAbstractNode node) {
//
//		if (node == null) {
//			return;
//		}
//
//		try {
//			node.accept(new CacheCleaner());
//		} catch(Exception e) {
//			LogHelperCore.logCatch(e);
//		}
//
//		clearCache(node.getParent());
//	}
//
//	public void updateCache(IAbstractNode node, EImplementationStatus status) {
//
//		fCache.put(node, status);
//
//		if (node != null && node.getParent() != null) {
//			clearCache(node.getParent());
//		}
//	}
//
//	public static void clearCache() {
//		fCache.clear();
//	}
//
//	private static class CacheCleaner implements IModelVisitor{
//
//		@Override
//		public Object visit(RootNode node) throws Exception {
//			fCache.remove(node);
//			return null;
//		}
//
//		@Override
//		public Object visit(ClassNode node) throws Exception {
//			fCache.remove(node);
//			return null;
//		}
//
//		@Override
//		public Object visit(MethodNode node) throws Exception {
//			fCache.remove(node);
//			return null;
//		}
//
//		@Override
//		public Object visit(BasicParameterNode node) throws Exception {
//			
//			if (node.isGlobalParameter()) {
//
//				fCache.remove(node);
//
//				for (BasicParameterNode parameter : node.getLinkedMethodParameters()) {
//					fCache.remove(parameter);
//				}
//
//				return null;
//				
//			} else {
//			
//			fCache.remove(node);
//			return null;
//			}
//		}
//
//		@Override
//		public Object visit(TestSuiteNode node) throws Exception {
//
//			fCache.remove(node);
//			return null;
//		}
//		
//		@Override
//		public Object visit(TestCaseNode node) throws Exception {
//
//			fCache.remove(node);
//			return null;
//		}
//
//		@Override
//		public Object visit(ConstraintNode node) throws Exception {
//
//			fCache.remove(node);
//			return null;
//		}
//
//		@Override
//		public Object visit(ChoiceNode node) throws Exception {
//
//			fCache.remove(node);
//
//			for (MethodNode method : node.getParameter().getMethods()) {
//				for (TestCaseNode testCase : method.getMentioningTestCases(node)) {
//					fCache.remove(testCase);
//				}
//			}
//
//			return null;
//		}
//
//	}
//
//}
