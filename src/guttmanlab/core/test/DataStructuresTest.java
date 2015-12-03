package guttmanlab.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import guttmanlab.core.datastructures.Pair;

public class DataStructuresTest {
	
	Pair<Integer> a;
	Pair<Integer> b;
	Pair<Integer> c;
	Pair<Integer> allNull;
	Pair<Integer> allNull2;
	Pair<Integer> leftNull;
	Pair<Integer> rightNull;
	Pair<Integer> nullObject;
	
	@Before
	public void setUp() {
		a = new Pair<Integer>(1, 2);
		b = new Pair<Integer>(1, 2);
		c = new Pair<Integer>(1, 3);
		allNull = new Pair<Integer>(null, null);
		leftNull = new Pair<Integer>(null, 1);
		rightNull = new Pair<Integer>(1, null);
		allNull2 = new Pair<Integer>(null, null);
	}
	
	@Test
	public void testPairSimpleEqualsBehavior() {
		assertEquals(a, b);
	}
	
	@Test
	public void testPairSimpleNotEqualsBehavior() {
		assertNotEquals(a, c);
	}
	
	@Test
	public void testPairLeftNullBehavior() {
		assertNotEquals(a, leftNull);
	}
	
	@Test
	public void testPairRightNullBehavior() {
		assertNotEquals(a, rightNull);
	}
	
	@Test
	public void testPairAllNullBehavior() {
		assertNotEquals(a, allNull);
	}
	
	@Test
	public void testPairNullBehavior() {
		assertNotEquals(a, nullObject);
	}
	
	@Test
	public void testPairReflexiveBehavior() {
		assertEquals(allNull, allNull);
	}
	
	@Test
	public void testPairAllNullBehavior2() {
		assertEquals(allNull, allNull2);
	}
}
