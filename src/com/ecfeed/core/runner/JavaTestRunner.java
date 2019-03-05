/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.implementation.ModelClassLoader;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.FixedChoiceValueFactory;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.JavaTypeHelper;

public class JavaTestRunner {

	private ModelClassLoader fLoader;
	private boolean fIsExport;
	private MethodNode fMethodNode;
	private Class<?> fTestClass;
	private Method fTestMethod;
	private ITestMethodInvoker fTestMethodInvoker;

	public JavaTestRunner(ModelClassLoader loader, boolean isExport, ITestMethodInvoker testMethodInvoker) {
		fLoader = loader;
		fIsExport = isExport;
		fTestMethodInvoker = testMethodInvoker;
		fTestClass = null;
		fTestMethod = null;
	}

	public void setOwnMethodNode(MethodNode methodNode) throws RunnerException {
		fMethodNode = methodNode;
	}

	public void createTestClassAndMethod(MethodNode methodNode) throws RunnerException {
		ClassNode classNode = methodNode.getClassNode();
		fTestClass = getTestClass(classNode.getFullName());
		fTestMethod = getTestMethod(fTestClass, fMethodNode);
	}	

	public void runTestCase(List<ChoiceNode> testData) throws RunnerException{

		validateTestData(testData);

		Object instance = null;

		if (fTestMethodInvoker.isClassInstanceRequired())	{
			try {
				instance = fTestClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				RunnerException.report(
						Messages.CANNOT_INVOKE_TEST_METHOD(
								fMethodNode.toString(), 
								testData.toString(), 
								e.getMessage()));
			}
		}

		Object[] arguments = getArguments(testData);
		Object[] choiceNames = getChoiceNames(testData);

		try {
			fTestMethodInvoker.invoke(fTestMethod, getClassName(fTestClass), instance, arguments, choiceNames, testData.toString());
		} catch (Exception e) {
			RunnerException.report(e.getMessage());
		}
	}

	private static String getClassName(Class<?> theClass) {
		if (theClass == null) {
			return null;
		}
		return theClass.getName();
	}

	public void prepareTestCaseForExport(List<ChoiceNode> testData) throws RunnerException{
		validateTestData(testData);
		Object[] arguments = getArguments(testData);
		fTestMethodInvoker.invoke(null, null, null, arguments, null, null);
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) throws RunnerException {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel)){
				return method;
			}
		}
		RunnerException.report(Messages.METHOD_NOT_FOUND(methodModel.toString()));
		return null;
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		List<String> types = new ArrayList<String>();
		for(Class<?> type : parameterTypes){
			types.add(JavaTypeHelper.getTypeName(type.getCanonicalName()));
		}
		return methodName.equals(methodModel.getFullName()) && types.equals(methodModel.getParameterTypes());
	}

	protected Object[] getArguments(List<ChoiceNode> testData) throws RunnerException {
		List<Object> args = new ArrayList<Object>();
		FixedChoiceValueFactory factory = new FixedChoiceValueFactory(fLoader, fIsExport);
		for(ChoiceNode choice : testData){
			Object value = factory.createValue(choice);
			if(value == null){
				String type = choice.getParameter().getType();
				//check if null value acceptable
				if(JavaTypeHelper.isStringTypeName(type) || JavaTypeHelper.isUserType(type)){
					if(choice.getValueString().equals(JavaTypeHelper.VALUE_REPRESENTATION_NULL) == false){
						RunnerException.report(Messages.CANNOT_PARSE_PARAMETER(type, choice.getValueString()));
					}
				}
			}

			args.add(value);
		}
		return args.toArray();
	}

	protected Object[] getChoiceNames(List<ChoiceNode> testData) throws RunnerException {
		List<String> args = new ArrayList<String>();

		for(ChoiceNode choice : testData){
			args.add(choice.getFullName());
		}
		return args.toArray();
	}	

	private void validateTestData(List<ChoiceNode> testData) throws RunnerException {
		List<String> dataTypes = new ArrayList<String>();
		for (ChoiceNode parameter : testData) {
			dataTypes.add(parameter.getParameter().getType());
		}
		if(dataTypes.equals(fMethodNode.getParameterTypes()) == false){
			RunnerException.report(Messages.WRONG_TEST_METHOD_SIGNATURE(fMethodNode.toString()));
		}
	}

	private Class<?> getTestClass(String qualifiedName) throws RunnerException {
		Class<?> testClass = fLoader.loadClass(qualifiedName);
		if(testClass == null){
			RunnerException.report(Messages.CANNOT_LOAD_CLASS(qualifiedName));
		}
		return testClass;
	}

}