package com.ecfeed.core.model;

class DeploymentStatementVisitor /* implements IStatementVisitor */ {

//	//private final RelationStatement EMPTY_RELATION_STATEMENT_TO_BE_CORRECTED_AFTER_DEPLOYMENT_OF_ROOT_STATEMENT = null; 
//
//	private DeploymentMapper fMethodDeploymentMapper;
//	private Map<RelationStatement, RelationStatement> fMappingOfOldToNewRelationStatement;
//
//	public DeploymentStatementVisitor(DeploymentMapper methodDeploymentMapper) {
//
//		fMethodDeploymentMapper = methodDeploymentMapper;
//		fMappingOfOldToNewRelationStatement = new HashMap<>();
//	}
//
//	@Override
//	public Object visit(StatementArray statement) {
//		
//		for (AbstractStatement child : statement.getChildren()) {
//			try {
//				child.accept(this);
//			} catch (Exception e) {
//				ExceptionHelper.reportRuntimeException(e);
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public Object visit(RelationStatement sourceRelationStatement) {
//
//		MethodParameterNode sourceParameter = sourceRelationStatement.getLeftParameter();
//		MethodParameterNode deployedParameter = fMethodDeploymentMapper.getDeployedParameterNode(sourceParameter);
//
//		IStatementCondition deployedStatementCondition = null;
//
//		try {
//			IStatementCondition sourceStatementCondition = sourceRelationStatement.getCondition();
//			deployedStatementCondition = (IStatementCondition)sourceStatementCondition.accept(this);
//		} catch (Exception e) {
//			ExceptionHelper.reportRuntimeException(e);
//		}
//
//		EMathRelation sourceRelation = sourceRelationStatement.getRelation();
//		
//		RelationStatement deployedRelationStatement = 
//				new RelationStatement(
//						deployedParameter, 
//						sourceRelation, 
//						deployedStatementCondition);
//		
//		fMappingOfOldToNewRelationStatement.put(sourceRelationStatement, deployedRelationStatement);
//
//		return deployedRelationStatement;
//	}
//
//	@Override
//	public Object visit(StaticStatement statement) {
//		return statement.makeClone();
//	}
//
//	@Override
//	public Object visit(ExpectedValueStatement statement) {
//
//		MethodParameterNode sourceParameter = statement.getLeftMethodParameterNode();
//		MethodParameterNode deployedParameter = fMethodDeploymentMapper.getDeployedParameterNode(sourceParameter);
//
//		ChoiceNode sourceChoiceNode = statement.getChoice();
//		ChoiceNode deployedChoiceNode = fMethodDeploymentMapper.getDeployedChoiceNode(sourceChoiceNode);
//
//		IPrimitiveTypePredicate deployedPredicate = statement.getPredicate();
//
//		ExpectedValueStatement expectedValueStatement = 
//				new ExpectedValueStatement(
//						deployedParameter, 
//						deployedChoiceNode, 
//						deployedPredicate);
//
//		return expectedValueStatement;
//	}
//
//	@Override
//	public Object visit(LabelCondition statement) {
//
//		String developedLabel = statement.getRightLabel();
//		
//		
//
//		LabelCondition deployedLabelCondition = 
//				new LabelCondition(
//						developedLabel, 
//						EMPTY_RELATION_STATEMENT_TO_BE_CORRECTED_AFTER_DEPLOYMENT_OF_ROOT_STATEMENT);
//
//		return deployedLabelCondition;
//	}
//
//	@Override
//	public Object visit(ChoiceCondition statement) {
//
//		ChoiceNode sourceChoiceNode = statement.getRightChoice();
//		ChoiceNode deployedChoiceNode = fMethodDeploymentMapper.getDeployedChoiceNode(sourceChoiceNode);
//
//		ChoiceCondition deployeChoiceCondition = 
//				new ChoiceCondition(
//						deployedChoiceNode, 
//						EMPTY_RELATION_STATEMENT_TO_BE_CORRECTED_AFTER_DEPLOYMENT_OF_ROOT_STATEMENT);
//
//		return deployeChoiceCondition;
//	}
//
//	@Override
//	public Object visit(ParameterCondition statement) {
//
//		MethodParameterNode sourcMethodParameterNode = statement.getRightParameterNode();
//		MethodParameterNode deployedMethodParameterNode = fMethodDeploymentMapper.getDeployedParameterNode(sourcMethodParameterNode);
//
//		ParameterCondition deployedParameterCondition = 
//				new ParameterCondition(
//						deployedMethodParameterNode, 
//						EMPTY_RELATION_STATEMENT_TO_BE_CORRECTED_AFTER_DEPLOYMENT_OF_ROOT_STATEMENT); 
//
//		return deployedParameterCondition;
//	}
//
//	@Override
//	public Object visit(ValueCondition statement) {
//
//		String deployedRightValue = statement.getRightValue();
//
//		ValueCondition deployedValueCondition = 
//				new ValueCondition(
//						deployedRightValue, 
//						EMPTY_RELATION_STATEMENT_TO_BE_CORRECTED_AFTER_DEPLOYMENT_OF_ROOT_STATEMENT);
//
//		return deployedValueCondition;
//	}

}
