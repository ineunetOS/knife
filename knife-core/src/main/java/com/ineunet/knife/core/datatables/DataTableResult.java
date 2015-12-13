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
package com.ineunet.knife.core.datatables;

import java.util.Collections;
import java.util.List;

/**
 * Return data of JQuery DataTables
 * @author hilbert.wang@hotmail.com
 * @param <T>
 */
public class DataTableResult<T> {

	private int draw = 1;
	private long recordsTotal;
	private long recordsFiltered;
	private List<T> data;
	private String error;
	
	public DataTableResult(long total, List<T> data) {
		this.recordsTotal = total;
		this.recordsFiltered = total;
		this.data = data;
	}
	
	public DataTableResult(long recordsTotal, long recordsFiltered, List<T> data) {
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public DataTableResult(String error) {
		this.data = Collections.EMPTY_LIST;
		this.error = error;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public List<T> getData() {
		return data;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
}
