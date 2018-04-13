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

package org.toasthub.social.acquaintance;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.EmailInvite;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.social.model.Acquaintance;
import org.toasthub.social.model.Invite;
import org.toasthub.social.model.UserRef;

@Repository("AcquaintanceDao")
@Transactional("TransactionManagerData")
public class AcquaintanceDaoImpl implements AcquaintanceDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;

	@Override
	public void getAcquaintances(RestRequest request, RestResponse response) throws Exception{
		String HQLQuery = "SELECT a.acquaintance FROM Acquaintance as a WHERE a.user.id = :uid ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND (a.acquaintance.lastname like :searchValue OR a.acquaintance.firstname like :searchValue) ";
		}
		HQLQuery += "ORDER BY a.acquaintance.lastname ASC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", ((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		response.addParam("acquaintances", (List<?>) query.getResultList());
	}

	/*public void getMembers(RestRequest request, RestResponse response) {

		// check for pending invites and existing acquaintance
		List<Long> ids = new ArrayList<Long>();
		for(User member : members){
			ids.add(member.getId());
		}
		// Find pending request for this user sent by others
		String HQLQueryInvites = "SELECT i.receiver.id FROM Invite AS i WHERE i.status = 'PEND' AND i.sender.id = :uid AND i.receiver.id IN :ids";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQueryInvites).setParameter("ids",ids);
		List<Long> inviteReceiverIds = query.setParameter("ids",ids).setParameter("uid",login.getCurrentUser().getId()).getResultList();
		// Find pending request for this user sent by this user
		String HQLQueryInvites2 = "SELECT i.sender.id FROM Invite AS i WHERE i.status = 'PEND' AND i.receiver.id = :uid AND i.sender.id IN :ids";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQueryInvites2).setParameter("ids",ids);
		List<Long> inviteSenderIds = query.setParameter("ids",ids).setParameter("uid",login.getCurrentUser().getId()).getResultList();
		// Find all my connections
		String HQLQueryAcq = "SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.user.id = :uid AND a.acquaintance.id IN :ids ";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQueryAcq);
		List<Long> acquaintanceIds = query.setParameter("ids",ids).setParameter("uid",login.getCurrentUser().getId()).getResultList();
		
		for(User member : members){
			// check invites receivers
			for(Long id : inviteReceiverIds){
				if (member.getId() == id){
					member.setPendingInvite(true);
					break;
				}
			}
			// check invites sent
			for(Long id : inviteSenderIds){
				if (member.getId() == id){
					member.setPendingInvite(true);
					break;
				}
			}
			// check acquaintances
			for(Long id : acquaintanceIds){
				if (member.getId() == id){
					member.setConnected(true);
					break;
				}
			}
		}
		
		
		response.addParam("members", members);
	}*/

	@Override
	public void getAcquaintanceCount(RestRequest request, RestResponse response) throws Exception {
		Long count = 0l;
		String HQLQuery = "SELECT count(*) FROM Acquaintance as a WHERE a.user.id = :uid ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND (a.acquaintance.lastname like :searchValue OR a.acquaintance.firstname like :searchValue) ";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", ((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		count = (Long)	query.getSingleResult();
		response.addParam("acquaintanceCount", count);
	}
	
	/*public void getMemberCount(RestRequest request, RestResponse response) {
		Long count = 0l;
		String HQLQuery = "SELECT count(*) FROM AppUser AS u WHERE u.active = true AND u.archive = false AND u.locked = false AND u.id != :id ";
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND (u.lastname like :searchValue OR u.firstname like :searchValue) ";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", ((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		count = (Long)	query.setParameter("id",login.getCurrentUser().getId()).getSingleResult();
		response.addParam("memberCount", count);
	}*/
	
	@Override
	public void makeAcquaintanceInvite(RestRequest request, RestResponse response) throws Exception {
		// find sender
		UserRef sender = (UserRef) entityManagerDataSvc.getInstance().createQuery("FROM UserRef AS u WHERE u.sender.id = :id")
				.setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId()).getSingleResult();
		// find receiver
		UserRef receiver = null;
		String message = "I want to connect";
		if (request.containsParam("receiverAppUser")){
			receiver = (UserRef) request.getParam("receiverAppUser");
		} else {
			Map<String,String> json = (Map<String,String>) request.getParams().get("userInput");
			if (json != null){
				if (json.containsKey("SOCIAL_ACQUAINTANCE_INVITE_FORM_RECEIVER_ID")) {
					receiver = (UserRef) entityManagerDataSvc.getInstance().createQuery("FROM UserRef AS u WHERE u.receiver.id = :id")
							.setParameter("id", Long.parseLong(json.get("SOCIAL_ACQUAINTANCE_INVITE_FORM_RECEIVER_ID"))).getSingleResult();
				}
				if (json.containsKey("SOCIAL_ACQUAINTANCE_INVITE_FORM_MESSAGE")) {
					message = json.get("SOCIAL_ACQUAINTANCE_INVITE_FORM_MESSAGE");
				}
			}
		}
		// check for existing invite 
		Long count = (Long) entityManagerDataSvc.getInstance().createQuery("SELECT count(*) FROM Invite as i WHERE i.receiver = :receiver AND i.sender = :sender")
				.setParameter("receiver", receiver).setParameter("sender", sender).getSingleResult();
		if (count == 0){
			// if there is no invites
			Invite invite = (Invite) request.getParam("invite");
			invite.setSender(sender);
			invite.setReceiver(receiver);
			invite.setMessage(message);
			entityManagerDataSvc.getInstance().persist(invite);
		}
	}

	@Override
	public void makeEmailInvite(RestRequest request, RestResponse response) throws Exception {
		EmailInvite invite = (EmailInvite) request.getParam("emailInvite");
		
		UserRef sender = (UserRef) entityManagerDataSvc.getInstance().createQuery("FROM UserRef AS u WHERE u.sender.id = :id").setParameter("id",((UserRef) request.getParam(GlobalConstant.USERREF)).getId()).getSingleResult();
		Long count = (Long) entityManagerDataSvc.getInstance().createQuery("SELECT count(*) FROM EmailInvite as i WHERE i.receiverEmail = :receiverEmail AND i.sender = :sender")
				.setParameter("receiverEmail", invite.getReceiverEmail()).setParameter("sender", sender).getSingleResult();
		invite.setSenderRefId(sender.getId());
		if (count == 0){
			invite.setStatus(EmailInvite.PEND);
			entityManagerDataSvc.getInstance().persist(invite);
			invite.setMessage(invite.getMessage() + "Welcome! You have been invited to join Toasthub. Come join us http://"+TenantContext.getURLDomain()+":8090/login/login.html?inviteid="+invite.getId());
		} else {
			invite.setStatus(EmailInvite.DUPLICATE);
			throw new Exception("Invite already Exists!");
		}
		
	}
	
	@Override
	public void updateEmailInvite(RestRequest request, RestResponse response) throws Exception {
		EmailInvite invite = (EmailInvite) request.getParam("emailInvite");
		entityManagerDataSvc.getInstance().merge(invite);
	}
	
	@Override
	public void getInvitesReceived(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Invite as i WHERE i.receiver.id = :uid  ";
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			HQLQuery += "AND i.status = :status ";
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			HQLQuery += "AND (i.sender.lastname like :searchValue OR i.sender.firstname like :searchValue) ";
		}
		HQLQuery += "ORDER BY i.sender.lastname ASC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			query.setParameter("status", ((String)request.getParam("iStatus")));
		}
		if (request.getParam(GlobalConstant.SEARCHVALUE) != null && !((String)request.getParam(GlobalConstant.SEARCHVALUE)).isEmpty()){
			query.setParameter("searchValue", ((String)request.getParam(GlobalConstant.SEARCHVALUE))+"%");
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		response.addParam("invitesReceived", (List<?>) query.getResultList());
	}

	@Override
	public void getInvitesSent(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Invite as i WHERE i.sender.id = :uid  ";
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			HQLQuery += "AND i.status = :status ";
		}
		HQLQuery += "ORDER BY i.receiver.lastname ASC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			query.setParameter("status", ((String)request.getParam("iStatus")));
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		response.addParam("invitesSent", (List<?>) query.getResultList());
	}
	
	@Override
	public void getEvitesSent(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM EmailInvite as i WHERE i.sender.id = :uid  ";
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			HQLQuery += "AND i.status = :status ";
		}
		HQLQuery += "ORDER BY i.receiverEmail ASC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		
		if (request.getParam("iStatus") != null && !((String)request.getParam("iStatus")).isEmpty()){
			query.setParameter("status", ((String)request.getParam("iStatus")));
		}
		if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		response.addParam("evitesSent", (List<?>) query.getResultList());
	}

	@Override
	public void getInviteCount(RestRequest request, RestResponse response) throws Exception {
		//	String status
		Long count = (Long) entityManagerDataSvc.getInstance().createQuery("SELECT count(*) FROM Invite as i WHERE i.receiverRefId = :uid AND i.status = :status")
				.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId()).setParameter("status", (String) request.getParam("status")).getSingleResult();
		response.addParam("inviteCount", count);
	}

	@Override
	public void deleteInvite(RestRequest request, RestResponse response) throws Exception {
		//	Long inviteId
		entityManagerDataSvc.getInstance().createQuery("DELETE Invite as i WHERE i.id = :inviteid")
		.setParameter("inviteid", (Long) request.getParam("inviteId")).executeUpdate();
	}

	@Override
	public void acceptInvite(RestRequest request, RestResponse response) throws Exception {
		//	Long inviteId
		EntityManager em = entityManagerDataSvc.getInstance();
		Invite invite = (Invite) entityManagerDataSvc.getInstance().createQuery("FROM Invite as i WHERE i.id = :inviteid").
				setParameter("inviteid", Long.parseLong((String)request.getParam("inviteId"))).getSingleResult();
		invite.setStatus("ACPT");
		Acquaintance acquaintance = new Acquaintance(invite.getSender(),invite.getReceiver());
		em.persist(acquaintance);
		Acquaintance acquaintanceReverse = new Acquaintance(invite.getReceiver(),invite.getSender());
		em.persist(acquaintanceReverse);
		//int num = entityManagerDataSvc.getInstance().createQuery("UPDATE Invite as i set i.status = 'ACPT' WHERE i.id = :inviteid").setParameter("inviteid", inviteId).executeUpdate();
		em.merge(invite);
		//Activity activity1 = new Activity("You are now connected to "+invite.getSender().getFirstname()+" "+invite.getSender().getLastname(),invite.getReceiver());
		//Activity activity2 = new Activity("You are now connected to "+invite.getReceiver().getFirstname()+" "+invite.getReceiver().getLastname(),invite.getSender());
		//em.persist(activity1);
		//em.persist(activity2);
	}

	@Override
	public void denyInvite(RestRequest request, RestResponse response) throws Exception {
		//	Long inviteId
		int num = entityManagerDataSvc.getInstance().createQuery("UPDATE Invite as i set i.status = 'DENY' WHERE i.id = :inviteid")
				.setParameter("inviteid", Long.parseLong((String)request.getParam("inviteId"))).executeUpdate();
	}

	@Override
	public void deleteAcquaintance(RestRequest request, RestResponse response) throws Exception {
		//	Long memberId
		entityManagerDataSvc.getInstance().createQuery("DELETE Acquaintance as a WHERE a.user.id = :myid and a.acquaintance.id = :memberid")
			.setParameter("myid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId()).setParameter("memberid", (Long) request.getParam("memberId")).executeUpdate();
		entityManagerDataSvc.getInstance().createQuery("DELETE Acquaintance as a WHERE a.user.id = :memberid and a.acquaintance.id = :myid")
			.setParameter("myid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId()).setParameter("memberid", (Long) request.getParam("memberId")).executeUpdate();
	}

	@Override
	public void getMembersNotAquaintanceAlready(RestRequest request, RestResponse response) throws Exception {

	}
	
	@Override
	public void getAcquaintance(RestRequest request, RestResponse response) throws Exception {
		//	Long id
		UserRef appUser = (UserRef) entityManagerDataSvc.getInstance().createQuery("FROM UserRef WHERE id = :id")
				.setParameter("id", (Long) request.getParam("appUserId")).getSingleResult();
		response.addParam("appUser", appUser);
	}
}
