package com.inova8.sparql.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.sparql.algebra.Table;
import com.hp.hpl.jena.sparql.algebra.table.TableN;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;

public class TableFiltered extends TableN{

    public TableFiltered(Table table, List<Var> variables) {
		materialize(table,variables) ;
	}
	private void materialize(Table table, List<Var> variables) {
	    HashSet<Binding> newRows = new HashSet<Binding>();
	    Iterator<Binding> qIter = table.rows();
		while (qIter.hasNext()) {
			Binding binding = qIter.next();
			BindingHashMap newBinding = new BindingHashMap();
			for (Var var : variables) {
				if (binding.contains(var)) {
					newBinding.add(var, binding.get(var));
					if (!vars.contains(var))  vars.add(var);
				}
			}
			newRows.add(newBinding);
		}
		rows = new ArrayList<Binding>(newRows);
	}
}
