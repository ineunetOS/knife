package com.ineunet.knife.core.query;

import org.junit.Test;

import junit.framework.Assert;

public class SQLArgsTests {
	
	@Test
	public void testGetOrderByField() {
		QueryParamParser arg = new QueryParamParser(0, 10, "  commodityName  desc ");
		Assert.assertEquals("commodityName", arg.getOrderByField());
		Assert.assertEquals("desc", arg.getSort());
		
		arg = new QueryParamParser(0, 10, "commodityName");
		Assert.assertEquals("commodityName", arg.getOrderByField());
		Assert.assertEquals("ASC", arg.getSort());
		
		arg = new QueryParamParser(0, 10, "name");
		Assert.assertEquals("ASC", arg.getSort());
		Assert.assertEquals("name", arg.getOrderByField());
		
		arg = new QueryParamParser(0, 10, "name    DESC ");
		Assert.assertEquals("DESC", arg.getSort());
		Assert.assertEquals("name", arg.getOrderByField());
	}
	
	@Test
	public void testGetOrderByColumn() {
		QueryParamParser arg = new QueryParamParser(0, 10, "  commodityName  desc ");
		Assert.assertEquals("commodity_name", arg.getOrderByColumn());
		
		arg = new QueryParamParser(0, 10, "  name  desc ");
		Assert.assertEquals("name", arg.getOrderByColumn());
		
		arg = new QueryParamParser(0, 10, "   ");
		Assert.assertEquals(null, arg.getOrderByColumn());
	}
	
	public void testGetSqlOrderBy() {
		QueryParamParser arg = new QueryParamParser(0, 10, "  commodityName  desc ");
		Assert.assertEquals("commodity_name DESC", arg.getSqlOrderBy());
		
		arg = new QueryParamParser(0, 10, "   ");
		Assert.assertEquals("", arg.getSqlOrderBy());
		
		arg = new QueryParamParser(0, 10, null);
		Assert.assertEquals("", arg.getSqlOrderBy());
	}

}
