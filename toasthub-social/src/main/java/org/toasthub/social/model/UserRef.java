package org.toasthub.social.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "user_ref")
public class UserRef extends SocialBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long userRefId;
	protected String firstname;
	protected String lastname;
	
	public UserRef(Long id, String firstname, String lastname){
		this.setFirstname(firstname);
		this.setLastname(lastname);
	}
	
	// Setters/Getters
	@JsonView({View.Admin.class,View.Member.class})
	@Column(name = "user_id")
	public Long getUserRefId() {
		return userRefId;
	}
	public void setUserRefId(Long userRefId) {
		this.userRefId = userRefId;
	}
	
	@JsonView({View.Admin.class,View.Member.class})
	@Column(name = "firstname")
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	@JsonView({View.Admin.class,View.Member.class})
	@Column(name = "lastname")
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
}
