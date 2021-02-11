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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "social_groups")
public class Group extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String MYGROUPS = "MYGROUPS";
	public static final String JOINED = "JOINED";
	public static final String AVAILGROUPS = "AVAILGROUPS";
	private String name;
	private String description;
	private UserRef owner;
	private String access;
	private String statusColor;
	private String statusIcon;
	private Integer rating;
	private List<Discussion> discussions;
	private Set<UserRef> joiners;
	
	//Constructor
	public Group() {
		super();
	}
	public Group(String name, UserRef owner) {
		super();
		this.setName(name);
		this.setOwner(owner);
	}
	public Group(String name, UserRef owner, String statusColor, String statusIcon) {
		super();
		this.setName(name);
		this.setOwner(owner);
		this.setStatusColor(statusColor);
		this.setStatusIcon(statusIcon);
	}
	// JPA Constructor for list
	public Group(Long id, String name){
		this.setId(id);
		this.setName(name);
	}
	// Setter/Getter
	@JsonView({View.Member.class})
	@Column(name = "status_color")
	public String getStatusColor() {
		return statusColor;
	}
	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "status_icon")
	public String getStatusIcon() {
		return statusIcon;
	}
	public void setStatusIcon(String statusIcon) {
		this.statusIcon = statusIcon;
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
	
	@JsonView({View.Member.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "access")
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
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
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL) @OrderBy("created desc")
	public List<Discussion> getDiscussions() {
		return discussions;
	}
	public void setDiscussions(List<Discussion> discussions) {
		this.discussions = discussions;
	}
	
	@JsonIgnore
	@ManyToMany(targetEntity = UserRef.class)
	@JoinTable(name = "social_groups_user_ref",joinColumns = @JoinColumn(name = "social_group_id"),inverseJoinColumns = @JoinColumn(name = "user_ref_id"))
	public Set<UserRef> getJoiners() {
		return joiners;
	}
	public void setJoiners(Set<UserRef> joiners) {
		this.joiners = joiners;
	}
	

	
}
