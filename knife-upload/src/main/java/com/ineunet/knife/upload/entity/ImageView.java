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
package com.ineunet.knife.upload.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ineunet.knife.api.AbstractTenantEntity;
import com.ineunet.knife.upload.IFileView;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
@Table(name = "knife_image_view")
@Entity
public class ImageView extends AbstractTenantEntity<Long> implements IFileView {

	private static final long serialVersionUID = -3963773311550448611L;

	private String fileName;
	private byte[] content;
	private String description;
	
	/**
	 * Category table name.<br>
	 * Means which table this image belong to.
	 */
	private String categoryTable;
	
	/**
	 * Name of column which the actual column mapped with in table <tt>categoryTable</tt>
	 */
	private String categoryColumn;
	
	/**
	 * Id value of <tt>categoryTable</tt> which <tt>categoryColumn</tt> belongs to.
	 */
	private Long categoryId;
	
	/**
	 * 通过associatedId
	 */
	private Long associatedId;
	
	public ImageView() {}
	
	/**
	 * @param content
	 * @param description
	 * @param categoryTable
	 * @param categoryColumn
	 * @param categoryId
	 */
	public ImageView(byte[] content, String description, String categoryTable, String categoryColumn, Long categoryId) {
		this.content = content;
		this.description = description;
		this.categoryTable = categoryTable;
		this.categoryColumn = categoryColumn;
		this.categoryId = categoryId;
	}
	
	/**
	 * @param description
	 * @param categoryTable
	 * @param categoryColumn
	 * @param categoryId
	 */
	public ImageView(String description, String categoryTable, String categoryColumn, Long categoryId) {
		this(null, description, categoryTable, categoryColumn, categoryId);
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@Transient
	public String getName() {
		return description;
	}
	
	@Column(name = "file_name", length = 128)
	@Basic(optional = false)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Column(name = "description", length = 255)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String name) {
		this.description = name;
	}

	@Lob
	@Column(name = "img_data")
	@Basic(fetch = FetchType.LAZY, optional = false)
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}

	@Column(name = "category_table", length = 32)
	@Basic(optional = false)
	public String getCategoryTable() {
		return categoryTable;
	}

	public void setCategoryTable(String categoryTable) {
		this.categoryTable = categoryTable;
	}

	@Column(name = "category_column", length = 32)
	@Basic(optional = false)
	public String getCategoryColumn() {
		return categoryColumn;
	}

	public void setCategoryColumn(String categoryColumn) {
		this.categoryColumn = categoryColumn;
	}

	@Column(name = "category_id")
	@Basic(optional = false)
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	@Column(name = "associated_id")
	@Basic(optional = false)
	public Long getAssociatedId() {
		return associatedId;
	}

	public void setAssociatedId(Long associatedId) {
		this.associatedId = associatedId;
	}

	@Transient
	public Type getType() {
		return Type.image;
	}

	@Override
	public String toString() {
		return "DBImageView [id=" + id + ", description=" + description + ", categoryTable="
				+ categoryTable + ", categoryColumn=" + categoryColumn + ", categoryId=" + categoryId + "]";
	}

}
