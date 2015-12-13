package com.ineunet.knife.api;

import org.junit.Test;

import junit.framework.Assert;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
public class RecordStatusTests {
	
	@Test
	public void testNameOf() {
		Assert.assertEquals(RecordStatus.normal, RecordStatus.nameOf("normal"));
		Assert.assertEquals(RecordStatus.deleted, RecordStatus.nameOf("deleted"));
		Assert.assertEquals(RecordStatus.freezed, RecordStatus.nameOf("freezed"));
		Assert.assertEquals(RecordStatus.destroyed, RecordStatus.nameOf("destroyed"));
	}

}
