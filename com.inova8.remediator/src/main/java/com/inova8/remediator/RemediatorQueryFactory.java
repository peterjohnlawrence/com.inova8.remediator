package com.inova8.remediator;

import com.hp.hpl.jena.query.QueryException;

public class RemediatorQueryFactory {

	private RemediatorQueryFactory() {
	}

    /** Create a SPARQL query from the given string.
    *
    * @param queryString      The query string
    * @throws QueryException  Thrown when a parse error occurs
    */
   
   static public RemediatorQuery create(String queryString)
   {
	   return new RemediatorQuery(queryString);
   }
}
