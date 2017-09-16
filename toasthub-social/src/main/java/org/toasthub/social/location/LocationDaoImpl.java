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

package org.toasthub.social.location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.AttachmentMeta;
import org.toasthub.social.model.Directory;
import org.toasthub.social.model.Location;
import org.toasthub.social.model.UserRef;
import org.toasthub.social.repository.BaseDaoImpl;


public class LocationDaoImpl extends BaseDaoImpl implements LocationDao {

	public LocationDaoImpl() {
		super();
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(*) FROM Location l WHERE l.ownerRefId = :id ";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		query.setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam("count", count);
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		String queryStr = "FROM Location l WHERE l.ownerRefId = :id ORDER BY l.created DESC ";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		query.setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		if ((Integer) request.getParam("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("items", (List<?>) query.getResultList());
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if ( (Boolean) request.getParam("currentLocation") ){
			Location location = (Location) entityManagerDataSvc.getInstance().createQuery("FROM Location l WHERE max(l.created)").getSingleResult();
			response.addParam("currentItem", location);
		} else {
			Location location = (Location) entityManagerDataSvc.getInstance().createQuery("FROM Location l WHERE l.id = :id")
				.setParameter("id",  new Long((Integer) request.getParam("id")) ).getSingleResult();
			if (location != null) {
				response.addParam("item", location);
			} else {
				utilSvc.addStatus(RestResponse.INFO, RestResponse.DOESNOTEXIST, "Location id does not exist", response);
			}
		}
	}
	
	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Location location = (Location) request.getParam("location");
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		location.setOwner(userRef);
		entityManagerDataSvc.getInstance().merge(location);
	}
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		Location location = (Location) entityManagerDataSvc.getInstance().find(Location.class, new Long((Integer) request.getParam("id")));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		if (location.getOwner().equals(userRef)){
			entityManagerDataSvc.getInstance().remove(location);
		}
	}

	@Override
	public void saveAttachment(RestRequest request, RestResponse response) throws Exception {
		AttachmentMeta attachment = (AttachmentMeta) request.getParam("attachment");
		Long id = new Long((Integer) request.getParam("fuLocationId"));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		attachment.setUserRef(userRef);
		Directory directory = null;
		try {
			directory = (Directory) entityManagerDataSvc.getInstance().createQuery("FROM Directory AS d WHERE d.name = :name").setParameter("name","FULOCATION").getSingleResult();
		} catch (Exception e) {
			// no directory found 
		}
		if (directory == null){
			Directory myDir = new Directory("FULOCATION",userRef);
			attachment.setDirectory(myDir);
		} else {
			attachment.setDirectory(directory);
		}
		// add 
		if (id.equals(0L) || id.equals(null)){
			//Draft
			Location fuLocation = new Location();
			fuLocation.setOwner(userRef);
			Set<AttachmentMeta> images = new HashSet<AttachmentMeta>();
			images.add(attachment);
			fuLocation.setImages(images);
			entityManagerDataSvc.getInstance().persist(fuLocation);
			response.addParam("fuLocationId", fuLocation.getId());
		} else {
			Location fuLocation = (Location) entityManagerDataSvc.getInstance().getReference(Location.class, id);
			fuLocation.getImages().add(attachment);
			entityManagerDataSvc.getInstance().merge(fuLocation);
			response.addParam("fuLocationId", id);
		}
		
	}

	@Override
	public void deleteAttachment(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void getAcquaintanceLocations(RestRequest request, RestResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		Long x = ((UserRef) request.getParam(GlobalConstant.USERREF)).getId();
		String queryStr = "SELECT new Location(l.id, l.ownerRefId, l.latitude, l.longitude, l.altitude, l.accuracy, l.provider, max(l.created)) FROM Location as l WHERE l.ownerRefId in (SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.userRefId = :uid)  GROUP BY l.ownerRefId ";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		if ((Integer) request.getParam("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("items", (List<?>) query.getResultList());
		
	}

}
