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

import java.util.ArrayList;
import java.util.List;

public class RootNode extends GlobalParametersParentNode {
	private List<ClassNode> fClasses;
	private int fModelVersion;

	@Override
	public List<IAbstractNode> getChildren(){
		List<IAbstractNode> children = new ArrayList<>(super.getChildren());
		children.addAll(fClasses);
		return children;
	}

	@Override
	public int getMaxChildIndex(IAbstractNode potentialChild){
		if(potentialChild instanceof AbstractParameterNode) return getParameters().size();
		if(potentialChild instanceof MethodParameterNode) return getParameters().size();
		if(potentialChild instanceof ClassNode) return getClasses().size();
		return super.getMaxChildIndex(potentialChild);
	}

	@Override
	public RootNode makeClone() {
		RootNode copy = new RootNode(getName(), getModelChangeRegistrator(), fModelVersion);

		copy.setProperties(getProperties());

		for (GlobalParameterNode parameter : getGlobalParameters()) {
			copy.addParameter(parameter.makeClone());
		}

		for (ClassNode classnode : fClasses){
			copy.addClass(classnode.makeClone());
		}
		
		copy.setParent(this.getParent());
		return copy;
	}

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator, int modelVersion) {

		super(name, modelChangeRegistrator);

		fClasses = new ArrayList<ClassNode>();
		fModelVersion = modelVersion;
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator) {

		this(name, modelChangeRegistrator, ModelVersionDistributor.getCurrentSoftwareVersion());

		registerChange();
	}

	public boolean addClass(ClassNode node){
		return addClass(node, fClasses.size());
	}

	public boolean addClass(ClassNode node, int index){
		if(index >= 0 && index <= fClasses.size()){
			fClasses.add(index, node);
			node.setParent(this);
			boolean result = fClasses.indexOf(node) == index;
			registerChange();
			return result;
		}
		return false;
	}

	public List<ClassNode> getClasses() {
		return fClasses;
	}

	public int getModelVersion() {
		return fModelVersion;
	}

	public void setVersion(int modelVersion) {
		fModelVersion = modelVersion;
	}	

	public ClassNode getClass(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getName().equals(name)){
				return childClass;
			}
		}
		return null;
	}

	public boolean removeClass(ClassNode classNode){

		boolean result = fClasses.remove(classNode);
		registerChange();

		return result;
	}

	@Override
	public boolean isMatch(IAbstractNode node){
		if(node instanceof RootNode == false){
			return false;
		}

		RootNode root = (RootNode)node;
		if(getClasses().size() != root.getClasses().size()){
			return false;
		}

		for(int i = 0; i < getClasses().size(); i++){
			if(getClasses().get(i).isMatch(root.getClasses().get(i)) == false){
				return false;
			}
		}

		return super.isMatch(root);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	@Override
	public List<MethodNode> getMethods(AbstractParameterNode parameter) {
		List<MethodNode> result = new ArrayList<>();
		for(ClassNode classNode : getClasses()){
			result.addAll(classNode.getMethods(parameter));
		}
		return result;
	}

}
