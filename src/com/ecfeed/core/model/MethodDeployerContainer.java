package com.ecfeed.core.model;

public class MethodDeployerContainer {
	private MethodNode methodReference;
	private MethodNode methodDeployment;
	private NodeMapper nodeMapper;
	
	public static MethodDeployerContainer get() {
		return new MethodDeployerContainer();
	}
	
	public static MethodDeployerContainer get(MethodNode reference, MethodNode deployment, NodeMapper mapper) {
		return new MethodDeployerContainer(reference, deployment, mapper);
	}
	
	private MethodDeployerContainer() {
	}
	
	private MethodDeployerContainer(MethodNode reference, MethodNode deployment, NodeMapper mapper) {
		this.methodReference = reference;
		this.methodDeployment = deployment;
		this.nodeMapper = mapper;
	}
	
	public MethodNode getReference() {
		return this.methodReference;
	}
	
	public void setReference(MethodNode method) {
		this.methodReference = method;
	}
	
	public MethodNode getDeployment() {
		return this.methodDeployment;
	}
	
	public void setDeployment(MethodNode method) {
		this.methodDeployment = method;
	}
	
	public NodeMapper getMapper() {
		return this.nodeMapper;
	}
	
	public void setMapper(NodeMapper mapper) {
		this.nodeMapper = mapper;
	}
}
