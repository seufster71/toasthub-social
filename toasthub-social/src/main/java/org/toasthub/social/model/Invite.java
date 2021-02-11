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
import javax.xml.bind.annotation.XmlRootElement;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.SendMailIntf;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@XmlRootElement
@Table(name = "invites")
public class Invite extends SocialBaseEntity implements SendMailIntf, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PEND = "PEND";
	public static final String DENY = "DENY";
	public static final String ACCEPT = "ACPT";
	
	protected UserRef sender;
	protected UserRef receiver;
	protected String subject;
	protected String message;
	protected String status;
	
	// constructors
	public Invite() {
		super();
	}
	
	public Invite(UserRef sender,UserRef receiver,String subject,String message){
		super();
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setStatus(PEND);
		this.setSubject(subject);
		this.setMessage(message);
	}
	
	// setter/getters
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "sender_id")
	public UserRef getSender() {
		return sender;
	}
	public void setSender(UserRef sender) {
		this.sender = sender;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "receiver_id")
	public UserRef getReceiver() {
		return receiver;
	}
	public void setReceiver(UserRef receiver) {
		this.receiver = receiver;
	}
	
	@Column(name = "subject")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
