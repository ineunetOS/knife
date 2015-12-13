package com.ineunet.knife.security.utils;

import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-19
 */
public class EncryptUtilsTests {
	
	@Test
	public void testEncryptPasswd() {
		String p_a = "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb";
		Assert.assertEquals(p_a, EncryptUtils.encryptPasswd("a"));
		
		// +
		String p_plus = "a318c24216defe206feeb73ef5be00033fa9c4a74d0b967f6532a26ca5906d3b";
		Assert.assertEquals(p_plus, EncryptUtils.encryptPasswd("+"));
		
	}

}
