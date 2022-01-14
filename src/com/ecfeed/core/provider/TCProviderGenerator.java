/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 *******************************************************************************/

package com.ecfeed.core.provider;

import java.util.List;

import com.ecfeed.core.evaluator.SatSolverConstraintEvaluator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class TCProviderGenerator implements ITCProvider {

    private MethodNode fMethodNode;
    private IGenerator<ChoiceNode> fGenerator;
    private IEcfProgressMonitor fProgressMonitor;
    private List<Constraint> fConstraints;

    public TCProviderGenerator(MethodNode methodNode, IGenerator<ChoiceNode> generator) {

        fMethodNode = methodNode;
        fGenerator = generator;
    }

    @Override
    public void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) {

        fProgressMonitor = progressMonitor;

        TCProviderGenInitData genInitData = (TCProviderGenInitData) initData;

        initializeGenerator(progressMonitor, genInitData);
    }

    private void initializeGenerator(IEcfProgressMonitor progressMonitor, TCProviderGenInitData genInitData) {

        try {
            fConstraints = genInitData.getConstraints();
            
            // fAmbiguousConstraintAction = genInitData.getAmbiguousConstraintAction(); //  TODO EX-AM

            fGenerator.initialize(
                    genInitData.getChoiceInput(),
                    new SatSolverConstraintEvaluator(fConstraints, genInitData.getMethodNode()),
                    genInitData.getGeneratorArguments(),
                    progressMonitor);
        } catch (Exception e) {

            ExceptionHelper.reportRuntimeException("Cannot initalize generator.", e);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public MethodNode getMethodNode() {
        return fMethodNode;
    }

    @Override
    public TestCaseNode getNextTestCase() {

        List<ChoiceNode> choices = getNext();

        if (choices == null) {

            if (fProgressMonitor != null) {
                fProgressMonitor.setTaskEnd();
            }

            return null;
        }

        return new TestCaseNode("", null, choices);
    }

    @Override
    public List<Constraint> getConstraints() {
        return fConstraints;
    }

    private List<ChoiceNode> getNext() {

        try {
            return fGenerator.next();
        } catch (GeneratorException e) {
            ExceptionHelper.reportRuntimeException("Cannot get next test case.", e);
            return null;
        }
    }

}
