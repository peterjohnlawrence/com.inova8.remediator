/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inova8.sparql.engine;

import java.util.ArrayDeque ;
import java.util.ArrayList;
import java.util.Deque ;
import java.util.Iterator ;

import org.apache.jena.atlas.logging.Log ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.query.QueryExecException ;
import com.hp.hpl.jena.sparql.algebra.Op ;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.OpVisitor ;
import com.hp.hpl.jena.sparql.algebra.Table ;
import com.hp.hpl.jena.sparql.algebra.TableFactory ;
import com.hp.hpl.jena.sparql.algebra.op.* ;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator ;
import com.hp.hpl.jena.sparql.engine.http.Service ;

/**  Class to provide type-safe eval() dispatch using the visitor support of Op */

public class EvaluatorDispatch implements OpVisitor
{
    private Deque<Table> stack = new ArrayDeque<Table>() ;
    protected Evaluator evaluator ;
    
    public EvaluatorDispatch(Evaluator evaluator)
    {
        this.evaluator = evaluator ;
    }

    protected Table eval(Op op, Table input)
    {
    	push(input);
        op.visit(this) ;
        return pop() ;
    }
    
    Table getResult()
    {
        if ( stack.size() != 1 )
            Log.warn(this, "Warning: getResult: stack size = "+stack.size()) ;
        
        Table table = pop() ;
        return table ;
    }
    
    @Override
    public void visit(OpBGP opBGP)
    {
    	Table input = pop();
        Table table = evaluator.basicPattern(opBGP.getPattern()) ;
        push(table) ;
    }

    @Override
    public void visit(OpQuadPattern quadPattern)
    {
    	Table input = pop();
        push(Eval.evalQuadPattern(quadPattern, evaluator)) ;
    }

    @Override
    public void visit(OpQuadBlock quadBlock)
    {
    	Table input = pop();
    	push(eval(quadBlock.convertOp(),input)) ;
        //push(Eval.evalQuadPattern(quadBlock, evaluator)) ;
    }

    @Override
    public void visit(OpTriple opTriple)
    {
    	Table input = pop();
    	visit(opTriple.asBGP()) ;
    }

    @Override
    public void visit(OpQuad opQuad)
    {
    	Table input = pop();
    	visit(opQuad.asQuadPattern()) ;
    }
    @Override
    public void visit(OpPath opPath)
    {
    	Table input = pop();
    	Table table = evaluator.pathPattern(opPath.getTriplePath()) ;
        push(table) ;
    }

    @Override
    public void visit(OpProcedure opProc)
    {
    	Table input = pop();
    	Table table = eval(opProc.getSubOp(),input) ;
        table = evaluator.procedure(table, opProc.getProcId(), opProc.getArgs()) ;
        push(table) ;
    }

    @Override
    public void visit(OpPropFunc opPropFunc)
    {
    	Table input = pop();
    	Table table = eval(opPropFunc.getSubOp(),input) ;
        table = evaluator.propertyFunction(table, opPropFunc.getProperty(), opPropFunc.getSubjectArgs(), opPropFunc.getObjectArgs()) ;
        push(table) ;
    }

    @Override
    public void visit(OpJoin opJoin)
    {
    	Table input = pop();
    	Table left = eval(opJoin.getLeft(),input) ;
        Table right = eval(opJoin.getRight(),left) ;
        Table table = evaluator.join(left, right) ;
        push(table) ;
    }
    
    @Override
    public void visit(OpSequence opSequence)
    {
    	Table input = pop();
    	// Evaluation is as a sequence of joins.
        Table table = TableFactory.createUnit() ;
        
        for ( Iterator<Op> iter = opSequence.iterator() ; iter.hasNext() ; )
        {
            Op op = iter.next() ;
            Table eltTable = eval(op,input) ;
            table = evaluator.join(table, eltTable) ;
        }
        push(table) ;
    }

    @Override
    public void visit(OpDisjunction opDisjunction)
    {
    	Table input = pop();
    	// Evaluation is as a concatentation of alternatives 
        Table table = TableFactory.createEmpty() ;
        
        for ( Iterator<Op> iter = opDisjunction.iterator() ; iter.hasNext() ; )
        {
            Op op = iter.next() ;
            Table eltTable = eval(op,input) ;
            table = evaluator.union(table, eltTable) ;
        }
        push(table) ;
    }
    
    @Override
    public void visit(OpLeftJoin opLeftJoin)
    {
    	Table input = pop();
    	Table left = eval(opLeftJoin.getLeft(),input) ;
        Table right = eval(opLeftJoin.getRight(),input) ;
        Table table = evaluator.leftJoin(left, right, opLeftJoin.getExprs()) ;
        push(table) ;
    }

    @Override
    public void visit(OpDiff opDiff)
    {
    	Table input = pop();
    	Table left = eval(opDiff.getLeft(),input) ;
        Table right = eval(opDiff.getRight(),input) ;
        Table table = evaluator.diff(left, right) ;
        push(table) ;
    }

    @Override
    public void visit(OpMinus opMinus)
    {
    	Table input = pop();
    	Table left = eval(opMinus.getLeft(),input) ;
        Table right = eval(opMinus.getRight(),input) ;
        Table table = evaluator.minus(left, right) ;
        push(table) ;
    }

    @Override
    public void visit(OpUnion opUnion)
    {
    	Table input = pop();
    	Table left = eval(opUnion.getLeft(),input) ;
        Table right = eval(opUnion.getRight(),input) ;
        Table table = evaluator.union(left, right) ;
        push(table) ;
    }

    @Override
    public void visit(OpConditional opCond)
    {
    	Table input = pop();
    	Table left = eval(opCond.getLeft(),input) ;
        // Ref engine - don;'t care about efficiency
        Table right = eval(opCond.getRight(),input) ;
        Table table = evaluator.condition(left, right) ;
        push(table) ;
    }
    
    @Override
    public void visit(OpFilter opFilter)
    {
    	Table input = pop();
    	Table table = eval(opFilter.getSubOp(),input) ;
        table = evaluator.filter(opFilter.getExprs(), table) ;
        push(table) ;
    }

    @Override
    public void visit(OpGraph opGraph)
    {
    	Table input = pop();
    	push(Eval.evalGraph(opGraph, evaluator)) ;
    }

    @Override
	public void visit(OpService opService) {
		Table input = pop();
		if (!input.isEmpty()) {
			
	    	TableFactory tableFactory = new TableFactory();
	    	ArrayList<Var>  vars= new ArrayList<Var>( OpVars.mentionedVars(opService));
	    	//Table inputVars = TableFactory.create(input,vars);
	    	Table inputVars = new TableFiltered(input,vars);
			
			Op op1 = OpSequence.create(opService.getSubOp(),
					OpTable.create(inputVars));
			opService = new OpService(opService.getService(), op1,
					opService.getSilent());
		}
		QueryIterator qIter = Service.exec(opService, ARQ.getContext());
		Table table = TableFactory.create(qIter);
		push(table);
	}

    @Override
    public void visit(OpDatasetNames dsNames)
    {
    	Table input = pop();
    	push(Eval.evalDS(dsNames, evaluator)) ;
    }

    @Override
    public void visit(OpTable opTable)
    {
    	Table input = pop();
    	push(opTable.getTable()) ;
    }

    @Override
    public void visit(OpExt opExt)
    { throw new QueryExecException("Encountered OpExt during execution of reference engine") ; }

    @Override
    public void visit(OpNull opNull)
    { 
    	Table input = pop();
    	push(TableFactory.createEmpty()) ;
    }
    
    @Override
    public void visit(OpLabel opLabel)
    {
    	Table input = pop();
    	if ( opLabel.hasSubOp() )
            push(eval(opLabel.getSubOp(),input)) ;
        else
            // No subop.
            push(TableFactory.createUnit()) ;
    }

    @Override
    public void visit(OpList opList)
    {
    	Table input = pop();
    	Table table = eval(opList.getSubOp(),input) ;
        table = evaluator.list(table) ;
        push(table) ;
    }

    @Override
    public void visit(OpOrder opOrder)
    {
    	Table input = pop();
    	Table table = eval(opOrder.getSubOp(),input) ;
        table = evaluator.order(table, opOrder.getConditions()) ;
        push(table) ;
    }

    @Override
    public void visit(OpTopN opTop)
    {
    	Table input = pop();
    	Table table = eval(opTop.getSubOp(),input) ;
        //table = evaluator.topN(table, opTop.getLimti(), opTop.getConditions()) ;
        table = evaluator.order(table, opTop.getConditions()) ;
        table = evaluator.slice(table, 0, opTop.getLimit()) ;
        push(table) ;
    }

    @Override
    public void visit(OpProject opProject)
    {
    	Table input = pop();
    	Table table = eval(opProject.getSubOp(),input) ;
        table = evaluator.project(table, opProject.getVars()) ;
        push(table) ;
    }

    @Override
    public void visit(OpDistinct opDistinct)
    {
    	Table input = pop();
    	Table table = eval(opDistinct.getSubOp(),input) ;
        table = evaluator.distinct(table) ;
        push(table) ;
    }

    @Override
    public void visit(OpReduced opReduced)
    {
    	Table input = pop();
    	Table table = eval(opReduced.getSubOp(),input) ;
        table = evaluator.reduced(table) ;
        push(table) ;
    }

    @Override
    public void visit(OpSlice opSlice)
    {
    	Table input = pop();
    	Table table = eval(opSlice.getSubOp(),input) ;
        table = evaluator.slice(table, opSlice.getStart(), opSlice.getLength()) ;
        push(table) ;
    }

    @Override
    public void visit(OpAssign opAssign)
    {
    	Table input = pop();
    	Table table = eval(opAssign.getSubOp(),input) ;
        table = evaluator.assign(table, opAssign.getVarExprList()) ;
        push(table) ;
    }

    @Override
    public void visit(OpExtend opExtend)
    {
    	Table input = pop();
    	Table table = eval(opExtend.getSubOp(),input) ;
        table = evaluator.extend(table, opExtend.getVarExprList()) ;
        push(table) ;
    }

    @Override
    public void visit(OpGroup opGroup)
    {
    	Table input = pop();
    	Table table = eval(opGroup.getSubOp(),input) ;
        table = evaluator.groupBy(table, opGroup.getGroupVars(), opGroup.getAggregators()) ;
        push(table) ;
    }

    protected void push(Table table)  { stack.push(table) ; }
    protected Table pop()
    { 
        if ( stack.size() == 0 )
            Log.warn(this, "Warning: pop: empty stack") ;
        return stack.pop() ;
    }

}
