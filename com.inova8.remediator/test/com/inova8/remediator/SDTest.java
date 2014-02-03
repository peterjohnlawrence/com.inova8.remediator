package com.inova8.remediator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.inova8.remediator.SD;

public class SDTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetURI() {
		assertEquals("testGetURI","http://www.w3.org/ns/sparql-service-description#",SD.getURI() );
	}

}
