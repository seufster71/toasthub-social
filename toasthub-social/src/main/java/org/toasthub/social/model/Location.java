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
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "locations")
public class Location extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String DRAFT = "DRAFT";
	public static final String ALL = "A";
	
	private UserRef owner;
	private String provider;
	private double latitude;
	private double longitude;
	private float accuracy;
	private double altitude;
	private String message;
	private Set<AttachmentMeta> videos;
	private Set<AttachmentMeta> images;
	private Set<AttachmentMeta> audios;
	private List<AttachmentMeta> imageList;
	
	// Constructor
	public Location() {
		super();
	}
	
	public Location(double latitude, double longitude, double altitude, float accuracy, String provider) {
		super();
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setAltitude(altitude);
		this.setAccuracy(accuracy);
		this.setProvider(provider);
	}
	// Constructor for ajax
	public Location(RestRequest request, RestResponse response, String formName) {
		//userInputHelper(request, response, formName);
	}
	
	// Constructor HQL query
	public Location(Long id, UserRef owner, double latitude, double longitude, double altitude, float accuracy, String provider, Date created){
		this.setId(id);
		this.setOwner(owner);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setAltitude(altitude);
		this.setAccuracy(accuracy);
		this.setProvider(provider);
		this.setCreated(created);
	}
	
	// Getters/Setters
	@JsonView({View.Member.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "user_id", nullable = false)
	public UserRef getOwner() {
		return owner;
	}
	public void setOwner(UserRef owner) {
		this.owner = owner;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "provider")
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "latitude")
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@JsonView({View.Member.class})
	@Column(name = "longitude")
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "accuracy")
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "altitude")
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@JsonView({View.Member.class})
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "location_attachment",joinColumns = @JoinColumn(name = "location_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	public Set<AttachmentMeta> getVideos() {
		return videos;
	}
	public void setVideos(Set<AttachmentMeta> videos) {
		this.videos = videos;
	}

	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "location_attachment",joinColumns = @JoinColumn(name = "location_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	public Set<AttachmentMeta> getImages() {
		return images;
	}
	public void setImages(Set<AttachmentMeta> images) {
		this.images = images;
	}

	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "location_attachment",joinColumns = @JoinColumn(name = "location_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	public Set<AttachmentMeta> getAudios() {
		return audios;
	}
	public void setAudios(Set<AttachmentMeta> audios) {
		this.audios = audios;
	}
	
	@JsonView({View.Member.class})
	@Transient
	public List<AttachmentMeta> getImageList() {
		return imageList;
	}
	public void setImageList(List<AttachmentMeta> imageList) {
		this.imageList = imageList;
	}

	
	
}
