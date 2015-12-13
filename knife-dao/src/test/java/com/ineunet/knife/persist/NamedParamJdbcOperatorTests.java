package com.ineunet.knife.persist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-4-4
 */
public class NamedParamJdbcOperatorTests {

	@Test
	public void testProcessParameter() {
		String sql = "select x from T where r_k=:instanceId and id=:inStoreDetailId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("instanceId", 20);
		args.put("inStoreDetailId", 20);
		args.put("inStore", "");
		List<Object> values = new ArrayList<Object>();
		String sqlOther = NamedParamJdbcOperator.processParameter(sql, args, values);
		Assert.assertEquals("select x from T where r_k=? and id=?", sqlOther);
		Assert.assertEquals(Arrays.asList(new Object[] {20, 20}), values);
		
		Long[] warehouseIds = new Long[] {1L};
		args = new HashMap<String, Object>();
		args.put("warehouseIds", warehouseIds);
		values = new ArrayList<Object>();
		sql = "select itemadmin_cd from COMPANY where id in(:warehouseIds)";
		sqlOther = NamedParamJdbcOperator.processParameter(sql, args, values);
		Assert.assertEquals("select itemadmin_cd from COMPANY where id in(?)", sqlOther);
		Assert.assertEquals(Arrays.asList(warehouseIds), values);
		
		warehouseIds = new Long[] {1L, 2L};
		args = new HashMap<String, Object>();
		args.put("warehouseIds", warehouseIds);
		values = new ArrayList<Object>();
		sql = "select itemadmin_cd from COMPANY where id in(:warehouseIds)";
		sqlOther = NamedParamJdbcOperator.processParameter(sql, args, values);
		Assert.assertEquals("select itemadmin_cd from COMPANY where id in(?,?)", sqlOther);
		Assert.assertEquals(Arrays.asList(warehouseIds), values);
		
		warehouseIds = new Long[] {1L, 2L};
		args = new HashMap<String, Object>();
		args.put("name", "xx");
		args.put("warehouseIds", warehouseIds);
		values = new ArrayList<Object>();
		sql = "select itemadmin_cd from COMPANY where name=:name and id in(:warehouseIds)";
		sqlOther = NamedParamJdbcOperator.processParameter(sql, args, values);
		Assert.assertEquals("select itemadmin_cd from COMPANY where name=? and id in(?,?)", sqlOther);
		List<Object> list = new ArrayList<Object>();
		list.add("xx");
		list.addAll(Arrays.asList(warehouseIds));
		Assert.assertEquals(list, values);
		
		// @2015-4-29
		args = new HashMap<String, Object>();
		args.put("$CURRENT_TIME", new Date());
		args.put("$CURRENT_ACCOUNT", "11");
		args.put("$CURRENT_TENANT_ID", "22");
		values = new ArrayList<Object>();
		sql = "insert into BSPD_STOCK(`create_person`,`create_time`,`tenant_id`) values (:$CURRENT_ACCOUNT,:$CURRENT_TIME,:$CURRENT_TENANT_ID)";
		sqlOther = NamedParamJdbcOperator.processParameter(sql, args, values);
		Assert.assertEquals("insert into BSPD_STOCK(`create_person`,`create_time`,`tenant_id`) values (?,?,?)", sqlOther);
		// Assert.assertEquals(Arrays.asList(new Object[] {"xx", warehouseIds}), values);
	}
	
}
