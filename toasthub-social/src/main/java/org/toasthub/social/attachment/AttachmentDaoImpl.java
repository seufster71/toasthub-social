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

package org.toasthub.social.attachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.AttachmentMeta;
import org.toasthub.social.model.Directory;
import org.toasthub.social.model.UserRef;

@Repository("AttachmentDao")
@Transactional("TransactionManagerData")
public class AttachmentDaoImpl implements AttachmentDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	
	public static final String ATTACHMENTMETA = "attachmentMeta";
	
	@Override
	public void getAttachmentMeta(RestRequest request, RestResponse response) throws Exception { 
		AttachmentMeta attachmentMeta = (AttachmentMeta) entityManagerDataSvc.getInstance()
				.createQuery("SELECT NEW AttachmentMeta(a.id,a.title,a.fileName,a.filePath,a.size,a.contentType) FROM AttachmentMeta a WHERE a.id = :id")
				.setParameter(GlobalConstant.ID,(Long) request.getParam("metaId")).getSingleResult();
		response.addParam(ATTACHMENTMETA, attachmentMeta);
	}
	
	@Override
	public void getAttachment(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta attachmentMeta = (AttachmentMeta) entityManagerDataSvc.getInstance()
				.createQuery("FROM AttachmentMeta a INNER JOIN FETCH a.data WHERE a.id = :id")
				.setParameter(GlobalConstant.ID,(Long) request.getParam("metaId")).getSingleResult();
		response.addParam(ATTACHMENTMETA, attachmentMeta);
	}
	
	@Override
	public void getThumbNail(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta attachmentMeta = (AttachmentMeta) entityManagerDataSvc.getInstance()
				.createQuery("FROM AttachmentMeta a LEFT OUTER JOIN FETCH a.thumbNail WHERE a.id = :id")
				.setParameter(GlobalConstant.ID,(Long) request.getParam("metaId")).getSingleResult();
		response.addParam(ATTACHMENTMETA, attachmentMeta);
	}
	
	//@Authorize
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta meta = (AttachmentMeta) entityManagerDataSvc.getInstance().find(AttachmentMeta.class, (Long) request.getParam("metaId"));
		if (meta.getUserRef().equals(((UserRef) request.getParam(GlobalConstant.USERREF)).getId())){
			entityManagerDataSvc.getInstance().remove(meta);
		}
	}
	
	//@Authorize
	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta attachment = (AttachmentMeta) request.getParam("attachmentMeta");
		Long id = new Long((Integer) request.getParam("id"));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		attachment.setUserRef(userRef);
		Directory directory = null;
		try {
			directory = (Directory) entityManagerDataSvc.getInstance().createQuery("FROM Directory AS d WHERE d.name = :name")
					.setParameter("name",(String)request.getParam("directoryName")).getSingleResult();
		} catch (Exception e) {
			// no directory found 
		}
		if (directory == null){
			Directory myDir = new Directory((String)request.getParam("directoryName"),userRef);
			attachment.setDirectory(myDir);
		} else {
			attachment.setDirectory(directory);
		}
		// add to Bottle
	/*	if (id.equals(0L) || id.equals(null)){
			//Draft
			Bottle bottle = new Bottle();
			bottle.setDraft(true);
			bottle.setOwner(user);
			Set<AttachmentMeta> images = new HashSet<AttachmentMeta>();
			images.add(attachment);
			bottle.setImages(images);
			entityManagerDataSvc.getInstance().persist(bottle);
			response.addParam("bottleId", bottle.getId());
		} else {
			Bottle bottle = (Bottle) entityManagerDataSvc.getInstance().getReference(Bottle.class, id);
			bottle.getImages().add(attachment);
			entityManagerDataSvc.getInstance().merge(bottle);
			response.addParam("bottleId", id);
		}*/
	}
	
}
