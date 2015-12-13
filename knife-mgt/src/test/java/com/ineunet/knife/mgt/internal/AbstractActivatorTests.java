package com.ineunet.knife.mgt.internal;

import junit.framework.Assert;

import org.junit.Test;

import com.ineunet.knife.mgt.AbstractActivator;
import com.ineunet.knife.mgt.IBundleContext;

public class AbstractActivatorTests extends AbstractActivator {

	@Override
	protected void start(IBundleContext bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stop(IBundleContext bundle) {
		// TODO Auto-generated method stub
		
	}
	
	@Test
	public void testGetShortPackage()
	{
		AbstractActivatorTests a = new AbstractActivatorTests();
		Assert.assertEquals("com.ineunet.mgt", a.getBundleName());
	}

}
