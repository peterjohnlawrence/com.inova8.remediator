package com.inova8.remediator;

import java.util.List;

import org.apache.jena.atlas.logging.Log;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.op.*;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.inova8.requiem.rewriter.Term;

public class Simplifier extends TransformCopy {

	private Op simplifiedOp;
	public Simplifier() {
		super();
		simplifiedOp = OpNull.create();
	}

	@Override
	public Op transform(OpTable opTable) {
		return (opTable);
		//return new OpBGP();
	}

	@Override
	public Op transform(OpBGP opBGP) {
		return (opBGP);
	}

	@Override
	public Op transform(OpQuadPattern opQuadPattern) {
		return (opQuadPattern);
	}

	@Override
	public Op transform(OpQuadBlock opQuadBlock) {
		return (opQuadBlock);
	}

	@Override
	public Op transform(OpTriple opTriple) {
		return (opTriple);
	}

	@Override
	public Op transform(OpQuad opQuad) {
		return (opQuad);
	}

	@Override
	public Op transform(OpPath opPath) {
		return (opPath);
	}

	@Override
	public Op transform(OpProcedure opProc, Op subOp) {
		return (subOp);
	}

	@Override
	public Op transform(OpPropFunc opPropFunc, Op subOp) {
		return (subOp);
	}

	@Override
	public Op transform(OpDatasetNames opDatasetNames) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpFilter opFilter, Op subOp) {
		return subOp;
	}

	@Override
	public Op transform(OpGraph opGraph, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpService opService, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpAssign opAssign, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpExtend opExtend, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpJoin opJoin, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpLeftJoin opLeftJoin, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpDiff opDiff, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpMinus opMinus, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpUnion opUnion, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpConditional opCond, Op left, Op right) {
		return mergeToBGP(left, right);
	}

	@Override
	public Op transform(OpSequence opSequence, List<Op> elts) {
		Op left = elts.get(0);
		for (int i = 1; i < elts.size(); i++) {
			left = mergeToBGP(left, elts.get(i));
		}
		return left;
	}

	// @Override
	// public Op transform(OpDisjunction opDisjunction, List<Op> elts) { return
	// xform(opDisjunction, elts) ; }
	//
	// @Override
	// public Op transform(OpExt opExt) { return null ; }
	//
	// @Override
	// public Op transform(OpNull opNull) { return opNull.copy() ; }
	// @Override
	// public Op transform(OpLabel opLabel, Op subOp) { return xform(opLabel,
	// subOp) ; }
	//
	@Override
	public Op transform(OpList opList, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpOrder opOrder, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpTopN opTop, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpProject opProject, Op subOp) {
		return subOp;
	}

	@Override
	public Op transform(OpDistinct opDistinct, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpReduced opReduced, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpSlice opSlice, Op subOp) {
		return new OpBGP();
	}

	@Override
	public Op transform(OpGroup opGroup, Op subOp) {
		return new OpBGP();
	}

	private Op mergeToBGP(Op left, Op right) {
		BasicPattern bgp = new BasicPattern();
		if (OpBGP.isBGP(left)) {
			bgp.addAll(((OpBGP) left).getPattern());
		} else {
			Log.warn(this, "mergeToBGP left not valid BGP");
		}
		if (OpBGP.isBGP(right)) {
			bgp.addAll(((OpBGP) right).getPattern());
			;
		} else {
			Log.warn(this, "mergeToBGP right not valid BGP");
		}
		return new OpBGP(bgp);
	}

}
