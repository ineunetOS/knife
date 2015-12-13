package com.ineunet.knife.validation.annotation;

import java.util.Set;

import javax.persistence.Column;

import com.ineunet.knife.api.AbstractOrgEntity;
import com.ineunet.knife.api.IUploadEntity;
import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.api.security.IRole;
import com.ineunet.knife.api.security.IUser;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * 
 * @author Hilbert Wang
 * @since 1.0.0
 * Created on 2015年3月19日 
 */
public class User extends AbstractOrgEntity<Long> implements IUser, IUploadEntity {

	private static final long serialVersionUID = 8935170527897537373L;
	private String name;

	@Column(name = "name", length = 100)
	@VProp(name = "name", length = 100, nullable = true, title = "【姓名】", type = ValidatingType.STRING)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Long getId() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getAccount()
	 */
	@Override
	public String getAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setAccount(java.lang.String)
	 */
	@Override
	public void setAccount(String account) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getPassword()
	 */
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String passwd) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getSex()
	 */
	@Override
	public Integer getSex() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setSex(java.lang.Integer)
	 */
	@Override
	public void setSex(Integer sex) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getPhone()
	 */
	@Override
	public String getPhone() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setPhone(java.lang.String)
	 */
	@Override
	public void setPhone(String phone) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getEmail()
	 */
	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getRoles()
	 */
	@Override
	public Set<? extends IRole> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setRoles(java.util.Set)
	 */
	@Override
	public void setRoles(Set<? extends IRole> roles) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getOrganizations()
	 */
	@Override
	public Set<? extends IOrganization> getOrganizations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setOrganizations(java.util.Set)
	 */
	@Override
	public void setOrganizations(Set<? extends IOrganization> organizations) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getAccountStatus()
	 */
	@Override
	public AccountStatus getAccountStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setAccountStatus(com.ineunet.knife.api.security.IUser.AccountStatus)
	 */
	@Override
	public void setAccountStatus(AccountStatus status) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ineunet.knife.api.security.IUser#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		
	}
	
}
