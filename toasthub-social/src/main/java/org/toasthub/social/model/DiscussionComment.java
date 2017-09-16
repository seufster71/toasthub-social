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
import javax.validation.constraints.Size;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "social_group_discussion_comments")
public class DiscussionComment extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private String messageShort;
	private String message;
	private UserRef owner;
	private Discussion discussion;
	private Integer rating;
	
	// Constructors
	
	public DiscussionComment(){
		super();
	}
	
	// JPA Constructor for list
	public DiscussionComment(Long id,String messageShort,UserRef owner){
		this.setId(id);
		this.setMessageShort(messageShort);
		this.setOwner(owner);
	}
	
	public DiscussionComment(Long id,String messageShort,String message){
		this.setId(id);
		this.setMessageShort(messageShort);
		this.setMessage(message);
	}
	
	// Methods
	@JsonView({View.Member.class})
	@Size(min = 1, max = 255)
	@Column(name = "message_short")
	public String getMessageShort() {
		return messageShort;
	}
	public void setMessageShort(String messageShort) {
		this.messageShort = messageShort;
	}
	
	@JsonView({View.Member.class})
	@Size(max = 4096)
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
	@ManyToOne(targetEntity = Discussion.class,fetch = FetchType.LAZY)
	@JoinColumn(name = "discussion_id")
	public Discussion getDiscussion() {
		return discussion;
	}
	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
	}

	@JsonView({View.Member.class})
	@Column(name = "rating")
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
