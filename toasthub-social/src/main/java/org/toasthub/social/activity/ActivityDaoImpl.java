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

package org.toasthub.social.activity;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.security.model.UserContext;
import org.toasthub.social.model.Activity;
import org.toasthub.social.model.UserRef;

@Repository("ActivityDao")
@Transactional("TransactionManagerData")
public class ActivityDaoImpl implements ActivityDao {

	@Autowired
	UserContext userContext;
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT count(*) FROM Activity AS a WHERE a.owner.id <> :uid";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid",userContext.getCurrentUser().getId());
		response.addParam("count", (Long) query.getSingleResult());
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		String HQLQuery = "FROM Activity a WHERE a.user.id = :uid ORDER BY a.created DESC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", userContext.getCurrentUser().getId());
		if ((Integer) request.getParam("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("items", (List<?>) query.getResultList());
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Activity WHERE id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id", new Long((Integer) request.getParam("id")));
		response.addParam("item", (Activity) query.getSingleResult());
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Activity activity = (Activity) request.getParam("activity");
		if (activity == null) {
			UserRef user = (UserRef) entityManagerDataSvc.getInstance().createQuery("FROM User AS u WHERE u.userRefId = :id")
					.setParameter("id",userContext.getCurrentUser().getId()).getSingleResult();
			activity = new Activity((String) request.getParam("message"),user);
		}
		entityManagerDataSvc.getInstance().merge(activity);
	}
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		Activity activity = (Activity) entityManagerDataSvc.getInstance().getReference(Activity.class, new Long((Integer) request.getParam("id")));
		entityManagerDataSvc.getInstance().remove(activity);
	}
}
