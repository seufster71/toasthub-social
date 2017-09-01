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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "acquaintances")
public class Acquaintance extends SocialBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	protected UserRef user;
	protected UserRef acquaintance;
	
	// Constructor
	public Acquaintance() {
		super();
	}
	public Acquaintance(UserRef user, UserRef acquaintance) {
		super();
		this.setUser(user);
		this.setAcquaintance(acquaintance);
	}
	//Setter/Getter
	@JsonView({View.Public.class,View.Admin.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "acquaintance_id")
	public UserRef getAcquaintance() {
		return acquaintance;
	}
	public void setAcquaintance(UserRef acquaintance) {
		this.acquaintance = acquaintance;
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

	
}
