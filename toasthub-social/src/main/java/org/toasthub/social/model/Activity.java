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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "activities")
public class Activity extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private String messageShort;
	private String message;
	private UserRef user;
	private String statusColor;
	private String statusIcon;
	//private Date eventTime;
	
	//Constructor
	public Activity() {
		super();
	}
	public Activity(String messageShort, UserRef user) {
		super();
		this.setMessageShort(messageShort);
		this.setUser(user);
	}
	public Activity(String messageShort, UserRef user, String statusColor, String statusIcon) {
		super();
		this.setMessageShort(messageShort);
		this.setUser(user);
		this.setStatusColor(statusColor);
		this.setStatusIcon(statusIcon);
	}
	// JPA Constructor for List
	public Activity(Long id, String messageShort, String statusColor){
		this.setId(id);
		this.setMessageShort(messageShort);
		this.setStatusColor(statusColor);
	}
	// Setter/Getter
	@JsonView({View.Public.class,View.Admin.class})
	@Size(max = 4096)
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonView({View.Public.class,View.Admin.class})
	@Column(name = "status_color")
	public String getStatusColor() {
		return statusColor;
	}
	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}
	
	@JsonView({View.Public.class,View.Admin.class})
	@Column(name = "status_icon")
	public String getStatusIcon() {
		return statusIcon;
	}
	public void setStatusIcon(String statusIcon) {
		this.statusIcon = statusIcon;
	}
	
	@JsonView({View.Public.class,View.Admin.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "user_id")
	public UserRef getUser() {
		return user;
	}
	public void setUser(UserRef user) {
		this.user = user;
	}
	
	@JsonView({View.Public.class,View.Admin.class})
	@Size(min = 1, max = 255)
	@Column(name = "message_short")
	public String getMessageShort() {
		return messageShort;
	}
	public void setMessageShort(String messageShort) {
		this.messageShort = messageShort;
	}

}
