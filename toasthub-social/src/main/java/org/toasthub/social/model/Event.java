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
import java.math.BigDecimal;
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
@Table(name = "events")
public class Event extends SocialBaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	private UserRef owner;
	private String statusColor;
	private String statusIcon;
    private double latitude = 0;
    private double longitude = 0;
    private float accuracy = 0;
    private double altitude = 0;
    private String provider;
    private String s1; 
    private String s2;
    private String s3;
    private String s4;
    private String s5;
    private boolean b1;
    private boolean b2;
    private boolean b3;
    private int i1;
    private int i2;
    private int i3;
    private double d1;
    private double d2;
    private double d3;
    private Long l1;
    private Long l2;
    private Long l3;
    private float f1;
    private float f2;
    private float f3;
    private BigDecimal bd1;
    private BigDecimal bd2;
    private BigDecimal bd3;
    private String[] searchColumns = {"message","code"};
    
    private List<Long> imageIdList;
    
	private Set<AttachmentMeta> videos;
	private Set<AttachmentMeta> images;
	private Set<AttachmentMeta> audios;
	private List<AttachmentMeta> imageList;
	
	// Constructor
	public Event() {
		super();
	}
	public Event(String message, UserRef user) {
		super();
		this.setMessage(message);
		this.setOwner(user);
	}
	public Event(String message, UserRef user, String statusColor, String statusIcon) {
		super();
		this.setMessage(message);
		this.setOwner(user);
		this.setStatusColor(statusColor);
		this.setStatusIcon(statusIcon);
	}
	// Constructor for ajax
	public Event(RestRequest request, RestResponse response, String formName) {
		//userInputHelper(request, response, formName);
	}
	// JPA Constructor for list
	public Event(Long id, String message, String statusColor, String statusIcon){
		this.setId(id);
		this.setMessage(message);
		this.setStatusColor(statusColor);
		this.setStatusIcon(statusIcon);
	}
	// Setter/Getter
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "status_color")
	public String getStatusColor() {
		return statusColor;
	}
	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "status_icon")
	public String getStatusIcon() {
		return statusIcon;
	}
	public void setStatusIcon(String statusIcon) {
		this.statusIcon = statusIcon;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "owner_id")
	public UserRef getOwner() {
		return owner;
	}
	public void setOwner(UserRef owner) {
		this.owner = owner;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "latitude")
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "longitude")
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "accuracy")
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "altitude")
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
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
	@Column(name = "s1")
	public String getS1() {
		return s1;
	}
	public void setS1(String s1) {
		this.s1 = s1;
	}

	@JsonView({View.Member.class})
	@Column(name = "s2")
	public String getS2() {
		return s2;
	}
	public void setS2(String s2) {
		this.s2 = s2;
	}

	@JsonView({View.Member.class})
	@Column(name = "s3")
	public String getS3() {
		return s3;
	}
	public void setS3(String s3) {
		this.s3 = s3;
	}

	@JsonView({View.Member.class})
	@Column(name = "s4")
	public String getS4() {
		return s4;
	}
	public void setS4(String s4) {
		this.s4 = s4;
	}

	@JsonView({View.Member.class})
	@Column(name = "s5")
	public String getS5() {
		return s5;
	}
	public void setS5(String s5) {
		this.s5 = s5;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "b1")
	public boolean isB1() {
		return b1;
	}
	public void setB1(boolean b1) {
		this.b1 = b1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "b2")
	public boolean isB2() {
		return b2;
	}
	public void setB2(boolean b2) {
		this.b2 = b2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "b3")
	public boolean isB3() {
		return b3;
	}
	public void setB3(boolean b3) {
		this.b3 = b3;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "i1")
	public int getI1() {
		return i1;
	}
	public void setI1(int i1) {
		this.i1 = i1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "i2")
	public int getI2() {
		return i2;
	}
	public void setI2(int i2) {
		this.i2 = i2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "i3")
	public int getI3() {
		return i3;
	}
	public void setI3(int i3) {
		this.i3 = i3;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "d1")
	public double getD1() {
		return d1;
	}
	public void setD1(double d1) {
		this.d1 = d1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "d2")
	public double getD2() {
		return d2;
	}
	public void setD2(double d2) {
		this.d2 = d2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "d3")
	public double getD3() {
		return d3;
	}
	public void setD3(double d3) {
		this.d3 = d3;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "l1")
	public Long getL1() {
		return l1;
	}
	public void setL1(Long l1) {
		this.l1 = l1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "l2")
	public Long getL2() {
		return l2;
	}
	public void setL2(Long l2) {
		this.l2 = l2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "l3")
	public Long getL3() {
		return l3;
	}
	public void setL3(Long l3) {
		this.l3 = l3;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "f1")
	public float getF1() {
		return f1;
	}
	public void setF1(float f1) {
		this.f1 = f1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "f2")
	public float getF2() {
		return f2;
	}
	public void setF2(float f2) {
		this.f2 = f2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "f3")
	public float getF3() {
		return f3;
	}
	public void setF3(float f3) {
		this.f3 = f3;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "bd1")
	public BigDecimal getBd1() {
		return bd1;
	}
	public void setBd1(BigDecimal bd1) {
		this.bd1 = bd1;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "bd2")
	public BigDecimal getBd2() {
		return bd2;
	}
	public void setBd2(BigDecimal bd2) {
		this.bd2 = bd2;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "bd3")
	public BigDecimal getBd3() {
		return bd3;
	}
	public void setBd3(BigDecimal bd3) {
		this.bd3 = bd3;
	}
	
	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "event_attachment",joinColumns = @JoinColumn(name = "event_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	public Set<AttachmentMeta> getVideos() {
		return videos;
	}

	public void setVideos(Set<AttachmentMeta> videos) {
		this.videos = videos;
	}

	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "event_attachment",joinColumns = @JoinColumn(name = "event_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	public Set<AttachmentMeta> getImages() {
		return images;
	}

	public void setImages(Set<AttachmentMeta> images) {
		this.images = images;
	}

	@JsonIgnore
	@OneToMany(targetEntity = AttachmentMeta.class, cascade = CascadeType.ALL)
	@JoinTable(name = "event_attachment",joinColumns = @JoinColumn(name = "event_id"),inverseJoinColumns = @JoinColumn(name = "attachment_id"))
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
	
	@JsonView({View.Member.class})
	@Transient
	public List<Long> getImageIdList() {
		return imageIdList;
	}
	public void setImageIdList(List<Long> imageIdList) {
		this.imageIdList = imageIdList;
	}
	
	
}
