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

package org.toasthub.social.directory;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.AttachmentMeta;
import org.toasthub.social.model.Directory;
import org.toasthub.social.model.UserRef;
import org.toasthub.social.repository.BaseDaoImpl;

@Repository("DirectoryDao")
@Transactional("TransactionManagerData")
public class DirectoryDaoImpl extends BaseDaoImpl implements DirectoryDao {

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Directory directory = (Directory) request.getParam(Directory.DIRECTORY);
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		directory.setOwner(userRef);
		Directory d = entityManagerDataSvc.getInstance().merge(directory);
		request.addParam(Directory.DIRECTORY, d);
		request.addParam(Directory.ID, d.getId());
	}

	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		Directory directory = (Directory) entityManagerDataSvc.getInstance().getReference(Directory.class, request.getParamLong("id"));
		entityManagerDataSvc.getInstance().remove(directory);
	}

	public void getThumbNail(RestRequest request, RestResponse response) throws Exception {	
		String HQLQuery = "FROM AttachmentMeta as a inner join fetch a.thumbNail WHERE a.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id", Long.parseLong((String)request.getParam("imageId")));
		response.addParam("attachment", (AttachmentMeta) query.getSingleResult());
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveAttachment(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAttachment(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}



}
