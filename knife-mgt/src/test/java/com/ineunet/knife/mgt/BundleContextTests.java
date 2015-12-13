package com.ineunet.knife.mgt;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ineunet.knife.mgt.Bundle;
import com.ineunet.knife.mgt.BundleContext;

public class BundleContextTests {

	@Test
	public void testSortBundles()
	{
		List<Bundle> list = new ArrayList<Bundle>();
		list.add(new Bundle("b1", 1));
		list.add(new Bundle("b5", 5));
		list.add(new Bundle("b2", 2));
		list.add(new Bundle("b8", 8));
		list.add(new Bundle("b7", 7));
		
		BundleContext.sortBundles(list);
		
		System.out.println(list);
	}
	
}
