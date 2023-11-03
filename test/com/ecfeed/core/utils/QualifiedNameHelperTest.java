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
//package com.ecfeed.core.utils;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.Test;
//
//public class QualifiedNameHelperTest {
//
//	private static final String PACKAGE = "com.ecfeed.utils";
//	private static final String CLASS = "Class";
//	private static final String PACKAGE_WITH_CLASS = PACKAGE + QualifiedNameHelper.PACKAGE_CLASS_SEPARATOR + CLASS;
//
//	@Test
//	public void shouldGetPackage(){
//		String result = QualifiedNameHelper.getPackage(PACKAGE_WITH_CLASS);
//		assertEquals(PACKAGE, result);
//	}
//
//	@Test
//	public void shouldGetNonQualifiedName(){
//		String result = QualifiedNameHelper.getNonQualifiedName(PACKAGE_WITH_CLASS);
//		assertEquals(CLASS, result);
//	}
//
//	@Test
//	public void hasPackageNameTest(){
//		assertTrue(QualifiedNameHelper.hasPackageName(PACKAGE_WITH_CLASS));
//		assertFalse(QualifiedNameHelper.hasPackageName(CLASS));
//	}
//
//}
