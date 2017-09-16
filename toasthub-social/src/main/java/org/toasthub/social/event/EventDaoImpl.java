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

package org.toasthub.social.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.AttachmentMeta;
import org.toasthub.social.model.Directory;
import org.toasthub.social.model.Event;
import org.toasthub.social.model.UserRef;
import org.toasthub.social.repository.BaseDaoImpl;

@Repository("EventDao")
@Transactional("TransactionManagerData")
public class EventDaoImpl extends BaseDaoImpl implements EventDao {

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Event WHERE id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter(GlobalConstant.ID, new Long((Integer) request.getParam(GlobalConstant.ID)));
		response.addParam("item", (Event) query.getSingleResult());
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Long count = 0l;
		String HQLQuery = "SELECT count(*) FROM Event AS e WHERE e.active = :active AND (e.owner.id = :uid ";
		HQLQuery += "OR e.owner.id in (SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.user.id = :uid)) ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND e.message like :searchValue ";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam("userRef")).getId());
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter(GlobalConstant.ACTIVE, request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter(GlobalConstant.ACTIVE, true);
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		count = (Long)	query.getSingleResult();
		response.addParam("count", (Long) query.getSingleResult());
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Event AS e WHERE e.active = :active AND (e.owner.id = :uid ";
		HQLQuery += "OR e.owner.id in (SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.user.id = :uid)) ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND e.message like :searchValue ";
		}
		HQLQuery += "ORDER BY e.created DESC ";
		
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam("userRef")).getId());
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter(GlobalConstant.ACTIVE, request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter(GlobalConstant.ACTIVE, true);
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		List<Event> events = (List<Event>) query.getResultList();
		if (request.containsParam("includeImages") && "LISTID".equals(request.getParam("includeImages"))) {
			for (Event e : events){
				Query queryImage = entityManagerDataSvc.getInstance().createQuery("SELECT e.attachment.id FROM EventAttachment as e WHERE e.event.id = :id");
				queryImage.setParameter("id", e.getId());
				e.setImageIdList((List<Long>) queryImage.getResultList());
			}
		}
		
		response.addParam("events", events);
	}
	
	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Event event = (Event) request.getParam("event");
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam("userRef")).getId());
		event.setOwner(userRef);
		Event e = entityManagerDataSvc.getInstance().merge(event);
		request.addParam("event", e);
		request.addParam("eventId", e.getId());
	}

	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		Event event = (Event) entityManagerDataSvc.getInstance().getReference(Event.class, new Long((Integer) request.getParam("id")));
		entityManagerDataSvc.getInstance().remove(event);
	}


	@Override
	public void saveAttachment(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta attachment = (AttachmentMeta) request.getParam("attachmentMeta");
		Long id = Long.parseLong((String) request.getParam("eventId"));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam("userRef")).getId());
		attachment.setUserRef(userRef);
		Directory directory = null;
		try {
			directory = (Directory) entityManagerDataSvc.getInstance().createQuery("FROM Directory AS d WHERE d.name = :name").setParameter("name","EVENT").getSingleResult();
		} catch (Exception e) {
			// no directory found 
		}
		if (directory == null){
			Directory myDir = new Directory("EVENT",userRef);
			attachment.setDirectory(myDir);
		} else {
			attachment.setDirectory(directory);
		}
		// add 
		Event event = (Event) entityManagerDataSvc.getInstance().getReference(Event.class, id);
		if (event.getImages() == null){
			event.setImages(new HashSet<AttachmentMeta>());
		}
		event.getImages().add(attachment);
		entityManagerDataSvc.getInstance().merge(event);
	}

	@Override
	public void deleteAttachment(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void getThumbNail(RestRequest request, RestResponse response) throws Exception {	
		String HQLQuery = "FROM AttachmentMeta as a inner join fetch a.thumbNail WHERE a.id = :id";
		
		if (request.containsParam("imageIdList")) {
			List<AttachmentMeta> thumbNails = new ArrayList<AttachmentMeta>();
			List<String> imageIdList = (List<String>) request.getParam("imageIdList");
			for(String id : imageIdList){
				Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
				query.setParameter("id", Long.parseLong((String)request.getParam("imageId")));
				thumbNails.add((AttachmentMeta) query.getSingleResult());
			}
			response.addParam("attachments", thumbNails);
		} else {
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("id", Long.parseLong((String)request.getParam("imageId")));
			response.addParam("attachment", (AttachmentMeta) query.getSingleResult());
		}
	}

	@Override
	public void eventCheck(RestRequest request, RestResponse response) throws Exception {	
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(d);
	    cal.add(Calendar.MINUTE, -240);
		
		String HQLQuery = "SELECT e.id FROM Event AS e WHERE e.active = :active AND ";
		HQLQuery += "e.owner.id in (SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.user.id = :uid) AND e.created > :fromdate ";
		HQLQuery += "ORDER BY e.created DESC ";
		
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam("userRef")).getId());
		query.setParameter("fromdate", cal.getTime());
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter(GlobalConstant.ACTIVE, request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter(GlobalConstant.ACTIVE, true);
		}
		
		List<Long> events = (List<Long>) query.getResultList();
		
		
		response.addParam("events", events);
	}
}
