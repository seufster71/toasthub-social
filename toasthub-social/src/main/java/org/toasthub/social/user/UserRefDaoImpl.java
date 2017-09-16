package org.toasthub.social.user;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.Event;
import org.toasthub.social.model.UserRef;

@Repository("UserRefDao")
@Transactional("TransactionManagerData")
public class UserRefDaoImpl implements UserRefDao {

	@Autowired	
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM UserRef AS u WHERE u.active = :active ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND u.lastname like :searchValue ";
		}
		HQLQuery += "ORDER BY u.lastname ASC ";
		
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		
		if (request.containsParam("active")) {
			query.setParameter("active", request.getParam("active"));
		} else {
			query.setParameter("active", true);
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		List<UserRef> users = (List<UserRef>) query.getResultList();
		
		response.addParam("users", users);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Long count = 0l;
		String HQLQuery = "SELECT count(*) FROM UserRef AS u WHERE u.active = :active ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND u.lastname like :searchValue ";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		
		if (request.containsParam("active")) {
			query.setParameter("active", request.getParam("active"));
		} else {
			query.setParameter("active", true);
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		count = (Long)	query.getSingleResult();
		response.addParam("userCount", (Long) query.getSingleResult());
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM UserRef WHERE id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id", new Long((Integer) request.getParam("id")));
		response.addParam("user", (Event) query.getSingleResult());
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		UserRef userRef = (UserRef) request.getParam("userRef");
		UserRef u = entityManagerDataSvc.getInstance().merge(userRef);
		request.addParam("userRef", u);
		request.addParam("userRefId", u.getId());
		
	}

	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, new Long((Integer) request.getParam(GlobalConstant.ID)));
		entityManagerDataSvc.getInstance().remove(userRef);
	}

	
}
