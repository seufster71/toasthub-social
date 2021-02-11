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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "social_group_discussions")
public class Discussion extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private String subject;
	private String messageShort;
	private String message;
	private UserRef owner;
	private Group group;
	private Integer rating;
	private List<DiscussionComment> comments;
	
	//Constructors
	public Discussion() {
		super();
	}
	// JPA Constructor for list
	public Discussion(Long id,String subject, String messageShort, UserRef owner){
		this.setId(id);
		this.setSubject(subject);
		this.setMessageShort(messageShort);
		this.setOwner(owner);
	}
	public Discussion(Long id, String messageShort, String message){
		this.setId(id);
		this.setMessageShort(messageShort);
		this.setMessage(message);
	}
	
	// Methods
	@JsonView({View.Member.class})
	@Column(name = "subject")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "message_short")
	public String getMessageShort() {
		return messageShort;
	}
	public void setMessageShort(String messageShort) {
		this.messageShort = messageShort;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonView({View.Member.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "owner_id")
	public UserRef getOwner() {
		return owner;
	}
	public void setOwner(UserRef owner) {
		this.owner = owner;
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
	@Column(name = "rating")
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL) @OrderBy("created desc")
	public List<DiscussionComment> getComments() {
		return comments;
	}
	public void setComments(List<DiscussionComment> comments) {
		this.comments = comments;
	}
	

}
