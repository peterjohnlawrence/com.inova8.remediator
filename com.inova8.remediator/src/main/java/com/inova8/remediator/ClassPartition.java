/*
 * 
 */
package com.inova8.remediator;

import com.hp.hpl.jena.ontology.OntResource;

// TODO: Auto-generated Javadoc
/**
 * The Class ClassPartition.
 */
class ClassPartition extends Partition {
	
	/** The entities. */
	private Integer entities;

	/**
	 * Instantiates a new Void dataset class partition.
	 *
	 * @param model the model
	 * @param clazz the class
	 * @param entities the entities
	 */
	ClassPartition( OntResource clazz, Integer entities) {
		super( clazz);
		this.entities = entities;
	}

	/**
	 * Gets the number of Class entities in this Void dataset partition.
	 *
	 * @return the entities
	 */
	public Integer getEntities() {
		return entities;
	}

	/**
	 * Sets the number of Class entities in this Void dataset partition.
	 *
	 * @param entities the new entities
	 */
	public void setEntities(Integer entities) {
		this.entities = entities;
	}

	/**
	 * Gets the class of the Void dataset partition.
	 *
	 * @return the clazz
	 */
	public OntResource getClazz() {
		return partition;
	}

	/**
	 * Sets the class of the Void dataset partition.
	 *
	 * @param clazz the new clazz
	 */
	public void setClazz(OntResource clazz) {
		this.partition = clazz;
	}
	@Override
	public String toString() {
		return this.partition.toString();
	}
}
