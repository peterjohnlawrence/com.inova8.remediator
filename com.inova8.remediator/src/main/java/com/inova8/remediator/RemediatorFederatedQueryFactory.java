package com.inova8.remediator;

public class RemediatorFederatedQueryFactory {

	private RemediatorFederatedQueryFactory() {
	}
	public static RemediatorFederatedQuery create(RemediatorQuery remediatorQuery,Void voidModel, Boolean optimize){
		return new RemediatorFederatedQuery(remediatorQuery,voidModel, optimize);
	}
}
