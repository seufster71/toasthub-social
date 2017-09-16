/*
 * Copyright (C) 2016 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.toasthub.social.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "social_group_public_request")
public class GroupPublicRequest extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String APRV = "APRV";
	public static final String REJT = "REJT";
	public static final String PEND = "PEND";
	private String status;
	private String message;
	private UserRef sender;
	private Group group;
	private String groupName;
	
	//Constructor
	public GroupPublicRequest() {
		super();
	}
	public GroupPublicRequest(Group group, UserRef sender, String message) {
		super();
		this.setStatus(GroupPublicRequest.PEND);
		this.setGroup(group);
		this.setSender(sender);
		this.setMessage(message);
	}
	
	// JPA Constructor for list
	public GroupPublicRequest(Long id, String message){
		this.setId(id);
		this.setMessage(message);
	}
	public GroupPublicRequest(Long id, String status, String message, UserRef sender, String groupName){
		this.setId(id);
		this.setStatus(status);
		this.setMessage(message);
		this.setSender(sender);
		this.setGroupName(groupName);
	}
	// Setter/Getter
	
	@JsonView({View.Member.class})
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonView({View.Member.class})
	@Size(min = 1, max = 2048)
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonView({View.Member.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "sender_id")
	public UserRef getSender() {
		return sender;
	}
	public void setSender(UserRef sender) {
		this.sender = sender;
	}

	@JsonIgnore
	@ManyToOne(targetEntity = Group.class,fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	
	@JsonView({View.Member.class})
	@Transient
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	

}
