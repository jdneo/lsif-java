/*******************************************************************************
* Copyright (c) 2025 Microsoft Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Microsoft Corporation - initial API and implementation
*******************************************************************************/

package com.microsoft.java.lsif.core.internal.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.microsoft.java.lsif.core.internal.indexer.IdGenerator.IdType;

public class IdGeneratorTest {

	@Test
	public void testCounterIdGenerator() {
		IdGenerator generator = new IdGenerator(IdType.COUNTER);
		
		String id1 = generator.next();
		assertNotNull(id1, "First ID should not be null");
		assertEquals("1", id1, "First counter ID should be 1");
		
		String id2 = generator.next();
		assertEquals("2", id2, "Second counter ID should be 2");
		
		String id3 = generator.next();
		assertEquals("3", id3, "Third counter ID should be 3");
	}

	@Test
	public void testCounterIdGeneratorIncrementsSequentially() {
		IdGenerator generator = new IdGenerator(IdType.COUNTER);
		
		int previousId = 0;
		for (int i = 0; i < 10; i++) {
			String id = generator.next();
			int currentId = Integer.parseInt(id);
			assertEquals(previousId + 1, currentId, "Counter should increment by 1");
			previousId = currentId;
		}
	}

	@Test
	public void testUuidIdGenerator() {
		IdGenerator generator = new IdGenerator(IdType.UUID);
		
		String id1 = generator.next();
		assertNotNull(id1, "UUID ID should not be null");
		assertTrue(id1.length() > 0, "UUID ID should not be empty");
		
		String id2 = generator.next();
		assertNotNull(id2, "Second UUID ID should not be null");
		assertNotEquals(id1, id2, "UUID IDs should be unique");
	}

	@Test
	public void testUuidIdGeneratorFormat() {
		IdGenerator generator = new IdGenerator(IdType.UUID);
		
		String id = generator.next();
		assertNotNull(id, "UUID ID should not be null");
		
		// UUID format is: 8-4-4-4-12 characters separated by hyphens
		// Example: 550e8400-e29b-41d4-a716-446655440000
		String[] parts = id.split("-");
		assertEquals(5, parts.length, "UUID should have 5 parts separated by hyphens");
		assertEquals(8, parts[0].length(), "First part should be 8 characters");
		assertEquals(4, parts[1].length(), "Second part should be 4 characters");
		assertEquals(4, parts[2].length(), "Third part should be 4 characters");
		assertEquals(4, parts[3].length(), "Fourth part should be 4 characters");
		assertEquals(12, parts[4].length(), "Fifth part should be 12 characters");
	}

	@Test
	public void testMultipleCounterGeneratorsAreIndependent() {
		IdGenerator generator1 = new IdGenerator(IdType.COUNTER);
		IdGenerator generator2 = new IdGenerator(IdType.COUNTER);
		
		String id1 = generator1.next();
		String id2 = generator2.next();
		
		assertEquals("1", id1, "First generator should start at 1");
		assertEquals("1", id2, "Second generator should also start at 1");
		
		generator1.next();
		String id3 = generator1.next();
		String id4 = generator2.next();
		
		assertEquals("3", id3, "First generator should be at 3");
		assertEquals("2", id4, "Second generator should be at 2");
	}

	@Test
	public void testUuidUniqueness() {
		IdGenerator generator = new IdGenerator(IdType.UUID);
		
		// Generate multiple UUIDs and verify they are all unique
		String id1 = generator.next();
		String id2 = generator.next();
		String id3 = generator.next();
		String id4 = generator.next();
		String id5 = generator.next();
		
		assertNotEquals(id1, id2, "UUIDs should be unique");
		assertNotEquals(id1, id3, "UUIDs should be unique");
		assertNotEquals(id1, id4, "UUIDs should be unique");
		assertNotEquals(id1, id5, "UUIDs should be unique");
		assertNotEquals(id2, id3, "UUIDs should be unique");
		assertNotEquals(id2, id4, "UUIDs should be unique");
		assertNotEquals(id2, id5, "UUIDs should be unique");
		assertNotEquals(id3, id4, "UUIDs should be unique");
		assertNotEquals(id3, id5, "UUIDs should be unique");
		assertNotEquals(id4, id5, "UUIDs should be unique");
	}
}
