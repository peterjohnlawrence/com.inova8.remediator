package com.inova8.remediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.logging.Log;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.op.Op0;
import com.hp.hpl.jena.sparql.algebra.op.Op1;
import com.hp.hpl.jena.sparql.algebra.op.Op2;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpN;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadBlock;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprTransformSubstitute;
import com.hp.hpl.jena.sparql.expr.ExprTransformer;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.inova8.requiem.rewriter.Term;

public class Substituter extends TransformCopy {

	private static final String PRUNED = "#Pruned:";
	//private Op substitutedOp;
	private QueryClause queryClause;
	private QueryVars queryVars;
	private QueryVars mentionedVars;
	private HashMap<Integer,Triple>  simplifiedTriples;

	public Substituter(QueryVars queryVars, QueryClause queryClause, HashMap<Integer,Triple>  simplifiedTriples) {
		super();
		this.queryVars = queryVars;
		this.queryClause = queryClause;
		this.simplifiedTriples = simplifiedTriples;
		this.mentionedVars = queryClause.getClauseVariables(queryVars);
	}

	@Override
	public Op transform(OpTable opTable) {
		return substitute(opTable);
	}

	@Override
	public Op transform(OpBGP opBGP) {
		
		OpBGP substitutedOp = new OpBGP();
		for (Triple triple : opBGP.getPattern().getList()) {
			if (simplifiedTriples.containsKey(triple.hashCode())) {
				Term rewrittenTerm = queryClause.getRewrittenTriple(triple
						.hashCode());
				if (rewrittenTerm != null) {
					Triple rewrittenTriple = queryClause.getDataset()
							.termToTriple(queryVars, rewrittenTerm);
					substitutedOp.getPattern().add(rewrittenTriple);
				}
			}
		}
		if(substitutedOp.getPattern().size()==0){
			return OpLabel.create(PRUNED ,null);
		}else
		{
			return substitutedOp;
		}
	}

	@Override
	public Op transform(OpQuadPattern opQuadPattern) {
		return substitute(opQuadPattern);
	}

	@Override
	public Op transform(OpQuadBlock opQuadBlock) {
		return substitute(opQuadBlock);
	}

	@Override
	public Op transform(OpTriple opTriple) {
		if (simplifiedTriples.containsKey(opTriple.hashCode())) {
			Term rewrittenTerm = queryClause.getRewrittenTriple(opTriple
					.hashCode());
			if (rewrittenTerm != null) {
				return new OpTriple(queryClause.getDataset().termToTriple(
						queryVars, rewrittenTerm));
			} else {
				return OpLabel.create(PRUNED ,null);
			}
		} else {
			return OpLabel.create(PRUNED ,null);
		}
	}

	@Override
	public Op transform(OpQuad opQuad) {
		return substitute(opQuad);
	}

	@Override
	public Op transform(OpPath opPath) {
		return substitute(opPath);
	}

	@Override
	public Op transform(OpProcedure opProc, Op subOp) {
		return substitute(opProc, subOp);
	}

	@Override
	public Op transform(OpPropFunc opPropFunc, Op subOp) {
		return substitute(opPropFunc, subOp);
	}

	@Override
	public Op transform(OpDatasetNames opDatasetNames) {
		return substitute(opDatasetNames);
	}

	@Override
	public Op transform(OpFilter opFilter, Op subOp) {

		ExprList substitutedExprList = new ExprList();
		
		for (Expr expr: opFilter.getExprs().getList()){
			Map<String, Expr> substitutions = new HashMap<String, Expr>();
			//for each expression see if it contains variables that are not mentioned within this clause
			for (Var var: expr.getVarsMentioned()){
				QueryVar mentionedQueryVar=this.mentionedVars.get(var);
				if (mentionedQueryVar==null) {
					//No mentioned variables so filter not relevant
					return subOp;
				}else
				{
					//Mentioned so add the new name to the substitutions set.
					Var substitutedVar = Var.alloc(mentionedQueryVar.getLinkedName(this.queryClause.getDataset()));
					substitutions.put(var.getName(), new ExprVar(substitutedVar));
				}
			}
			//Only get here if all vars mentioned in expression are within clause
		
			ExprTransformSubstitute exprTransformSubstitute = new ExprTransformSubstitute(	substitutions );
			Expr substitutedExpression = (Expr) ExprTransformer.transform(exprTransformSubstitute, expr);
			substitutedExprList.add(substitutedExpression);
		}
		return OpFilter.filter(substitutedExprList, subOp);
	}

	@Override
	public Op transform(OpGraph opGraph, Op subOp) {
		return substitute(opGraph, subOp);
	}

	@Override
	public Op transform(OpService opService, Op subOp) {
		return substitute(opService, subOp);
	}

	@Override
	public Op transform(OpAssign opAssign, Op subOp) {
		return substitute(opAssign, subOp);
	}

	@Override
	public Op transform(OpExtend opExtend, Op subOp) {
		return substitute(opExtend, subOp);
	}

	@Override
	public Op transform(OpJoin opJoin, Op left, Op right) {
		if(left instanceof OpLabel){
			if(right instanceof OpLabel){
				return OpLabel.create(PRUNED ,null);
			}else
			{
				return substitute((Op0) right);
			}
		}else{
			if(right instanceof OpLabel){
				return substitute((Op0) left);
			}
		}
		return substitute(opJoin, left, right);
	}

	@Override
	public Op transform(OpLeftJoin opLeftJoin, Op left, Op right) {
		
		if(right instanceof OpLabel){
			if(left instanceof OpLabel){
				return OpLabel.create(PRUNED ,null);
			}else
			{
				return substitute((Op0) left);
			}
		}else{
			if(left instanceof OpLabel){
				return substitute((Op0) right);
			}
		}
		
		return substitute(opLeftJoin, left, right);
	}

	@Override
	public Op transform(OpDiff opDiff, Op left, Op right) {
		return substitute(opDiff, left, right);
	}

	@Override
	public Op transform(OpMinus opMinus, Op left, Op right) {
		return substitute(opMinus, left, right);
	}

	@Override
	public Op transform(OpUnion opUnion, Op left, Op right) {
		if(left instanceof OpLabel){
			if(right instanceof OpLabel){
				return OpLabel.create(PRUNED ,null);
			}else
			{
				return substitute((Op0) right);
			}
		}else{
			if(right instanceof OpLabel){
				return substitute((Op0) left);
			}
		}	
		return substitute(opUnion, left, right);
	}

	@Override
	public Op transform(OpConditional opCond, Op left, Op right) {
		return substitute(opCond, left, right);
	}

	@Override
	public Op transform(OpSequence opSequence, List<Op> elts) {
		return substitute(opSequence, elts);
	}

	@Override
	public Op transform(OpDisjunction opDisjunction, List<Op> elts) {
		return substitute(opDisjunction, elts);
	}

	@Override
	public Op transform(OpExt opExt) {
		return substitute(opExt);
	}

	@Override
	public Op transform(OpNull opNull) {
		return opNull.copy();
	}

	@Override
	public Op transform(OpLabel opLabel, Op subOp) {
		return substitute(opLabel, subOp);
	}

	@Override
	public Op transform(OpList opList, Op subOp) {
		return substitute(opList, subOp);
	}

	@Override
	public Op transform(OpOrder opOrder, Op subOp) {
		return substitute(opOrder, subOp);
	}

	@Override
	public Op transform(OpTopN opTop, Op subOp) {
		return substitute(opTop, subOp);
	}

	@Override
	public Op transform(OpProject opProject, Op subOp) {
		return substitute(opProject, subOp);
	}

	@Override
	public Op transform(OpDistinct opDistinct, Op subOp) {
		return substitute(opDistinct, subOp);
	}

	@Override
	public Op transform(OpReduced opReduced, Op subOp) {
		return substitute(opReduced, subOp);
	}

	@Override
	public Op transform(OpSlice opSlice, Op subOp) {
		return substitute(opSlice, subOp);
	}

	@Override
	public Op transform(OpGroup opGroup, Op subOp) {
		return substitute(opGroup, subOp);
	}

	private Op substitute(Op0 op) {
		return op.copy() ;
	}

	private Op substitute(Op1 op, Op subOp) {
		 if ( op.getSubOp() == subOp )
	            return op ;
		return op.copy(subOp) ;
	}

	private Op substitute(Op2 op, Op left, Op right) {
		if (op.getLeft() == left && op.getRight() == right)
			return op;
		return op.copy(left, right);
	}

	private Op substitute(OpN op, List<Op> elts) {
		// Need to do one-deep equality checking.
		if ( equals1(elts, op.getElements()))
			return op;
		return op.copy(elts);
	}

	private Op substitute(OpExt op) {
		try {
			return op.apply(this);
		} catch (Exception e) {
			// May happen if the OpExt doesn't implement apply()
			return op;
		}
	}

	private boolean equals1(List<Op> list1, List<Op> list2) {
		if (list1.size() != list2.size())
			return false;
		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i) != list2.get(i))
				return false;
		}
		return true;
	}
}
