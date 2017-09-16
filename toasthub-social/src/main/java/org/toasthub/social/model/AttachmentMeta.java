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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "attachments_meta")
public class AttachmentMeta extends SocialBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title;
	private String fileName;
	private String filePath;
	private String comment;
	private Long size;
	private String contentType;
	private Attachment data;
	private AttachmentThumbnail thumbNail;
	//private Long thumbnailSize;   need to add
	//private String thumbnailName;
	private Directory directory;
	private UserRef userRef;
	
	// constructors
	public AttachmentMeta() {
		super();
	}
	public AttachmentMeta(String title,String fileName,String filePath, long size, String contentType ,Attachment data){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(new Date());
		this.setTitle(title);
		this.setFileName(fileName);
		this.setFilePath(filePath);
		this.setSize(size);
		this.setContentType(contentType);
		this.setData(data);
	}
	
	// getters/setters
	@JsonView({View.Member.class})
	@Column(name = "size", nullable = false)
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	@JsonView({View.Member.class})
	@Column(name = "content_type", nullable = false)
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@JsonView({View.Member.class})
	@Column(name = "title", nullable = false)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonView({View.Admin.class})
	@Column(name = "file_name", nullable = false)
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonView({View.Admin.class})
	@Column(name = "file_path")
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = Attachment.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "data_id")
	public Attachment getData() {
		return data;
	}
	public void setData(Attachment data) {
		this.data = data;
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = AttachmentThumbnail.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "thumbnail_id")
	public AttachmentThumbnail getThumbNail() {
		return thumbNail;
	}
	public void setThumbNail(AttachmentThumbnail thumbNail) {
		this.thumbNail = thumbNail;
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = Directory.class, cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_id")
	public Directory getDirectory() {
		return directory;
	}
	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = UserRef.class)
	@JoinColumn(name = "user_id")
	public UserRef getUserRef() {
		return userRef;
	}
	public void setUserRef(UserRef userRef) {
		this.userRef = userRef;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "comment")
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
