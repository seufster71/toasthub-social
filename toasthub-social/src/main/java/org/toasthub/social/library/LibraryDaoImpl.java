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

package org.toasthub.social.library;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.Directory;
import org.toasthub.social.model.UserRef;

@Repository("LibraryDao")
@Transactional("TransactionManagerData")
public class LibraryDaoImpl implements LibraryDao{

	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;

	public void getDirectories(RestRequest request, RestResponse response){
		Long parentId = null;
		if (request.containsParam("parentId") && request.getParam("parentId") != null){
			parentId =Long.valueOf((Integer) request.getParam("parentId"));
		}
		Query query = null;
		if (request.getParam("iType").equals(Directory.MINE)){
			String HQLQuery = "FROM Directory AS d WHERE ";
			if (parentId == null || parentId == 0){
				HQLQuery += "d.parent IS NULL ";
			} else {
				HQLQuery += "d.parent.id = :parent ";
			}
			HQLQuery += "AND d.owner.id = :id ORDER BY d.name ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			
			if ( parentId != null){
				query.setParameter("parent",parentId);
			}
			if ((Integer) request.getParam("pageLimit") != 0){
				query.setFirstResult((Integer) request.getParam("pageStart"));
				query.setMaxResults((Integer) request.getParam("pageLimit"));
			}
			
			query.setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		} else {
			
		}
		response.addParam("directories", (List<?>) query.getResultList());
	}
	
	public void getDirectory(RestRequest request, RestResponse response){
		String HQLQuery = "FROM Directory AS d WHERE d.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",Long.valueOf((Integer) request.getParam("directoryId")));
		response.addParam("group", (Directory) query.getSingleResult());
	}
	
	//@Authorize
	public void saveDirectory(RestRequest request, RestResponse response) throws Exception {
		Object d = request.getParam("directory");
		
		Directory directory = new Directory();
		Long parentId = Long.valueOf((Integer) request.getParam("parentId"));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		directory.setOwner(userRef);
		if (parentId != null){
			Directory parentDir = (Directory) entityManagerDataSvc.getInstance().getReference(Directory.class, parentId);
			directory.setParent(parentDir);
		}
		//entityManagerDataSvc.getInstance().merge(directory);
	}

	//@Authorize
	public void deleteDirectory(RestRequest request, RestResponse response) throws Exception {
		Directory directory = (Directory) entityManagerDataSvc.getInstance().getReference(Directory.class, Long.valueOf((Integer) request.getParam("directoryId")));
		entityManagerDataSvc.getInstance().remove(directory);
	}
	
	public void getDirectoryCount(RestRequest request, RestResponse response) {
		Long parentId = null;
		if (request.containsParam("parentId") && request.getParam("parentId") != null){
			parentId =Long.valueOf((Integer) request.getParam("parentId"));
		}
		Query query = null;
		if (request.containsParam("iType") && request.getParam("iType").equals(Directory.MINE)){
			String HQLQuery = "SELECT count(*) FROM Directory AS d WHERE ";
			if (parentId == null || parentId == 0){
				HQLQuery += "d.parent IS NULL ";
			} else {
				HQLQuery += "d.parent.id = :parent ";
			}
			HQLQuery += "AND d.owner.id = :uid";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			if (parentId != null){
				query.setParameter("parent",parentId);
			}
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		} else {
			String HQLQuery = "SELECT count(*) FROM Directory AS d WHERE d.owner.id <> :uid";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		}
		
		response.addParam("dirCount", (Long) query.getSingleResult());
	}
	
	//////////////////////////////////// File
	
	public void getFileCount(RestRequest request, RestResponse response) {
		Long parentId = Long.valueOf((Integer) request.getParam("parentId"));
		Query query = null;
		if (request.getParam("iType").equals(Directory.MINE) && parentId > 0){
			String HQLQuery = "SELECT count(*) FROM AttachmentMeta AS a WHERE a.directory.id = :parentid ";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("parentid",parentId);
		} else if (request.getParam("iType").equals(Directory.SHARED) && parentId > 0) {
			String HQLQuery = "SELECT count(*) FROM AttachmentMeta AS a WHERE a.directory.id = :parentid ";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("parentid",parentId);
			query.setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		} 
		response.addParam("fileCount", (Long) query.getSingleResult());
	}
	
	public void getFiles(RestRequest request, RestResponse response){
		Long parentId = Long.valueOf((Integer) request.getParam("parentId"));
		Query query = null;
		if (request.getParam("iType").equals(Directory.MINE) && parentId > 0){
			String HQLQuery = "FROM AttachmentMeta AS a WHERE a.directory.id = :parentid ORDER BY a.title ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("parentid",parentId);
		} else if (request.getParam("iType").equals(Directory.SHARED) && parentId > 0) {
			String HQLQuery = "FROM AttachmentMeta AS a WHERE a.directory.id = :parentid ORDER BY a.title ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("parentid",parentId);
		} 
		response.addParam("directories", (List<?>) query.getResultList());
	}
}
