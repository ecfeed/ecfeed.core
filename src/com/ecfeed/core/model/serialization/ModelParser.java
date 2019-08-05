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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;

public class ModelParser {

	Builder fBuilder = new Builder();
	XomAnalyser fXomAnalyser = null;

	public RootNode parseModel(
			String modelXml, IModelChangeRegistrator modelChangeRegistrator, List<String> outErrorList) throws ParserException { 

		InputStream istream = new ByteArrayInputStream(modelXml.getBytes());

		return parseModel(istream, modelChangeRegistrator, outErrorList);
	}

	public RootNode parseModel(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, List<String> details) throws ParserException {

		try {
			Document document = fBuilder.build(istream);
			Element element = document.getRootElement();

			int modelVersion = XomModelVersionDetector.getVersion(element);
			int softwareVersion = ModelVersionDistributor.getCurrentSoftwareVersion(); 

			if (modelVersion > softwareVersion) {
				ExceptionHelper.reportRuntimeException(
						"Can not read ect file. It has newer version: " + modelVersion +
						" than this software: " + softwareVersion + 
						". Please install newer version of the software.");
			}

			createXomAnalyser(modelVersion);
			return getXomAnalyser().parseRoot(element, modelChangeRegistrator, details);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	private void createXomAnalyser(int version) throws ParserException {
		if (fXomAnalyser == null) {
			fXomAnalyser = XomAnalyserFactory.createXomAnalyser(version);
		}			
	}

	private XomAnalyser getXomAnalyser() throws ParserException {
		if (fXomAnalyser == null) {
			ParserException.report("XomAnalyzer must not be null.");
		}
		return fXomAnalyser;
	}

	// TODO
	public ClassNode parseClass(
			InputStream istream, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseClass(document.getRootElement(), null, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public MethodNode parseMethod(
			InputStream istream, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseMethod(document.getRootElement(), null, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public GlobalParameterNode parseGlobalParameter(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseGlobalParameter(document.getRootElement(), modelChangeRegistrator, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public MethodParameterNode parseMethodParameter(
			InputStream istream, MethodNode method, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseMethodParameter(document.getRootElement(), method, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public ChoiceNode parseChoice(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, List<String> errorList) throws ParserException {

		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoice(document.getRootElement(), modelChangeRegistrator, errorList).get();

		} catch (ParsingException e) {

			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;

		} catch (IOException e) {

			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public TestCaseNode parseTestCase(
			InputStream istream, MethodNode method, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseTestCase(document.getRootElement(), method, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public Optional<ConstraintNode> parseConstraint(
			InputStream istream, MethodNode method, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseConstraint(document.getRootElement(), method, errorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	// TODO
	public AbstractStatement parseStatement(
			InputStream istream, MethodNode method, List<String> errorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatement(document.getRootElement(), method, errorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public StaticStatement parseStaticStatement(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, List<String> details) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStaticStatement(document.getRootElement(), modelChangeRegistrator, details);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public RelationStatement parseChoicesParentStatement(
			InputStream istream, MethodNode method, List<String> details) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoiceStatement(document.getRootElement(), method, details);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			InputStream istream, MethodNode method, List<String> details) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseExpectedValueStatement(document.getRootElement(), method, details);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		}
	}

	public StatementArray parseStatementArray(
			InputStream istream, MethodNode method, List<String> details) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatementArray(document.getRootElement(), method, details);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new StatementArray(null, method.getModelChangeRegistrator());
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new StatementArray(null, method.getModelChangeRegistrator());
		}
	}
}
