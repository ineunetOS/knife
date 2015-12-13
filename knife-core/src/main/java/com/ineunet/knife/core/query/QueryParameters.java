/*
 * Copyright 2013-2016 iNeunet OpenSource and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ineunet.knife.core.query;

import java.util.Map;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.0
 *
 */
public class QueryParameters implements IPagination {
	
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String param6;
	private String param7;
	private String param8;
	private String param9;
	private String param10;
	private String param90;
	private String param91;
	private String param92;
	private String param93;
	private Map<String, Object> dynaParams;
	
	private String order; // e.g. ASC
	private String sort; // o.time
	private int page = Pageable.NON_PAGEABLE;
	protected int rows;
	
	public static final int UNKOWN_PAGE = -1;
	public static final int UNKOWN_ROWS = -1;

	@Override
	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String getOrder() {
		return order;
	}

	@Override
	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	public String getSort() {
		return sort;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public int getRows() {
		return rows;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public String getParam6() {
		return param6;
	}

	public void setParam6(String param6) {
		this.param6 = param6;
	}

	public String getParam7() {
		return param7;
	}

	public void setParam7(String param7) {
		this.param7 = param7;
	}

	public String getParam8() {
		return param8;
	}

	public void setParam8(String param8) {
		this.param8 = param8;
	}

	public String getParam9() {
		return param9;
	}

	public void setParam9(String param9) {
		this.param9 = param9;
	}

	public String getParam10() {
		return param10;
	}

	public void setParam10(String param10) {
		this.param10 = param10;
	}

	public String getParam90() {
		return param90;
	}

	public void setParam90(String param90) {
		this.param90 = param90;
	}

	public String getParam91() {
		return param91;
	}

	public void setParam91(String param91) {
		this.param91 = param91;
	}

	public String getParam92() {
		return param92;
	}

	public void setParam92(String param92) {
		this.param92 = param92;
	}

	public String getParam93() {
		return param93;
	}

	public void setParam93(String param93) {
		this.param93 = param93;
	}

	public Map<String, Object> getDynaParams() {
		return dynaParams;
	}

	public void setDynaParams(Map<String, Object> dynaParams) {
		this.dynaParams = dynaParams;
	}

}
