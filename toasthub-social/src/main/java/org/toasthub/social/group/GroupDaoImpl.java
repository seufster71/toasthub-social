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

package org.toasthub.social.group;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.social.model.Discussion;
import org.toasthub.social.model.DiscussionComment;
import org.toasthub.social.model.Group;
import org.toasthub.social.model.GroupJoin;
import org.toasthub.social.model.GroupPrivateInvite;
import org.toasthub.social.model.GroupPublicRequest;
import org.toasthub.social.model.UserRef;

@Repository("GroupDao")
@Transactional("TransactionManagerData")
public class GroupDaoImpl implements GroupDao {

	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;

////////////////////////////////////// Groups ////////////////////////////////////	
	@Override
	public void getGroups(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		if (request.containsParam("tab") && request.getParams().get("tab").equals(Group.MYGROUPS)){
			String HQLQuery = "SELECT g FROM Group AS g WHERE g.owner.id = :uid ORDER BY g.name ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		} else if (request.containsParam("tab") && request.getParams().get("tab").equals(Group.JOINED)){
			String HQLQuery = "SELECT g FROM Group AS g LEFT OUTER JOIN g.joiners AS j WHERE j.id = :jid ORDER BY g.name ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("jid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		} else {
			String HQLQuery = "SELECT g FROM Group AS g WHERE g.owner.id <> :uid AND g.access IN(:access) AND g.id NOT IN(SELECT j.group.id FROM GroupJoin AS j WHERE j.user.id = :jid) AND g.id NOT IN(SELECT r.group.id FROM GroupPublicRequest AS r where r.sender.id = :uid) ORDER BY g.name ASC";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			query.setParameter("jid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			List<String> pubAccess = Arrays.asList("PUBO","PUBR");
			query.setParameter("access",pubAccess);
		}
		// groups I joined
		if ((Integer) request.getParam("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("groups", (List<?>) query.getResultList());
	}
	
	@Override
	public void getLatestGroups(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "FROM Group AS g WHERE g.created > :date AND g.access <> 'PRII' AND g.owner.id IN(SELECT a.acquaintance.id FROM Acquaintance as a WHERE a.user.id = :uid) ORDER BY g.created";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(d);
	    cal.add(Calendar.DAY_OF_MONTH, -1);
		query.setParameter("date", cal.getTime());
		query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		response.addParam("groups", (List<?>) query.getResultList());
	}

	@Override
	public void getGroupCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		if (request.containsParam("tab") && request.getParams().get("tab").equals(Group.MYGROUPS)){
			String HQLQuery = "SELECT count(*) FROM Group AS g WHERE g.owner.id = :uid";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("myGroupCount", (Long) query.getSingleResult());
		} else {
			String HQLQuery = "SELECT count(*) FROM Group AS g WHERE g.owner.id <> :uid";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("groupCount", (Long) query.getSingleResult());
		}
	}

	@Override
	public void getInviteCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = null;
		if (request.containsParam("iType") && request.getParams().get("iType").equals("SENTINVITES")){
			HQLQuery = "SELECT count(*) FROM GroupPrivateInvite AS i WHERE i.group.id IN(SELECT g.id FROM Group AS g WHERE g.owner.id = :uid)";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("sentInviteCount", (Long) query.getSingleResult());
		} else if (request.containsParam("iType") && request.getParams().get("iType").equals("RECEIVEINVITES")) {
			HQLQuery = "SELECT count(*) FROM GroupPrivateInvite AS i WHERE i.receiver.id = :uid";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("receiveInviteCount", (Long) query.getSingleResult());
		}
	}
	
	@Override
	public void getRequestCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		if (request.containsParam("iType") && request.getParams().get("iType").equals("SENTREQUESTS")){
			String HQLQuery = "SELECT count(*) FROM GroupPublicRequest AS r WHERE r.sender.id = :uid";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("sentRequestCount", (Long) query.getSingleResult());
		} else {
			String HQLQuery = "SELECT count(*) FROM GroupPublicRequest AS r WHERE r.group.id IN(SELECT g.id FROM Group AS g WHERE g.owner.id = :uid)";
			query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid",((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			response.addParam("receiveRequestCount", (Long) query.getSingleResult());
		}
	}
	
	@Override
	public void getGroup(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Group AS g WHERE g.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam("groupId")));
		response.addParam("group", (Group) query.getSingleResult());
	}
	
	//@Authorize
	@Override
	public void saveGroup(RestRequest request, RestResponse response) throws Exception {
		Group group = ((Group) request.getParam("group"));
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		group.setOwner(userRef);
		entityManagerDataSvc.getInstance().merge(group);
	}

	//@Authorize
	@Override
	public void deleteGroup(RestRequest request, RestResponse response) throws Exception {
		Group group = (Group) entityManagerDataSvc.getInstance().getReference(Group.class, new Long((Integer) request.getParam("groupId")));
		entityManagerDataSvc.getInstance().remove(group);
	}

	@Override
	public void joinGroup(RestRequest request, RestResponse response) throws Exception {
		Group group = (Group) entityManagerDataSvc.getInstance().getReference(Group.class, new Long((Integer) request.getParam("groupId")));
		GroupJoin join = new GroupJoin();
		join.setGroup(group);
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		join.setUser(userRef);
		entityManagerDataSvc.getInstance().persist(join);
	}
	
	//@Authorize
	@Override
	public void leaveGroup(RestRequest request, RestResponse response) throws Exception {
		Query query = entityManagerDataSvc.getInstance().createQuery("FROM GroupJoin as j WHERE j.group.id = :groupid AND j.user.id = :id");
		query.setParameter("groupid", new Long((Integer) request.getParam("groupId")));
		query.setParameter("id", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		GroupJoin join = (GroupJoin) query.getSingleResult();
		entityManagerDataSvc.getInstance().remove(join);
		
	}
	
	//@Authorize
	@Override
	public void cancelPublicRequestJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPublicRequest groupPublicRequest = (GroupPublicRequest) entityManagerDataSvc.getInstance().getReference(GroupPublicRequest.class, new Long((Integer) request.getParam("joinId")));
		entityManagerDataSvc.getInstance().remove(groupPublicRequest);
	}
	
	//@Authorize
	@Override
	public void cancelPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPrivateInvite invite = (GroupPrivateInvite) entityManagerDataSvc.getInstance().getReference(GroupPrivateInvite.class, new Long((Integer) request.getParam("joinId")));
		entityManagerDataSvc.getInstance().remove(invite);
	}
	
	@Override
	public void getGroupInvites(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = null;
		if (request.containsParam("iType") && request.getParam("iType").equals("SENTINVITES")){
			HQLQuery = "SELECT NEW GroupPrivateInvite(i.id,i.status,i.message,i.receiver,i.group.name) FROM GroupPrivateInvite AS i WHERE i.group.id IN (SELECT g.id FROM Group AS g WHERE g.owner.id = :uid)";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			
			if ((Integer) request.getParam("pageLimit") != 0){
				query.setFirstResult((Integer) request.getParam("pageStart"));
				query.setMaxResults((Integer) request.getParam("pageLimit"));
			}
			response.addParam("groupSentInvites", (List<?>) query.getResultList());
		} else if (request.containsParam("iType") && request.getParam("iType").equals("RECEIVEINVITES")){
			HQLQuery = "SELECT NEW GroupPrivateInvite(i.id,i.status,i.message,i.group.owner,i.group.name) FROM GroupPrivateInvite AS i WHERE i.receiver.id = :uid AND i.status = 'PEND'";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			
			if ((Integer) request.getParam("pageLimit") != 0){
				query.setFirstResult((Integer) request.getParam("pageStart"));
				query.setMaxResults((Integer) request.getParam("pageLimit"));
			}
			response.addParam("groupReceiveInvites", (List<?>) query.getResultList());
		}
	}
	
	@Override
	public void getGroupRequests(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = null;
		if (request.containsParam("iType") && request.getParam("iType").equals("SENTREQUESTS")){
			HQLQuery = "SELECT NEW GroupPublicRequest(r.id,r.status,r.message,r.sender,r.group.name) FROM GroupPublicRequest AS r WHERE r.sender.id = :uid";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			if ((Integer) request.getParam("pageLimit") != 0){
				query.setFirstResult((Integer) request.getParam("pageStart"));
				query.setMaxResults((Integer) request.getParam("pageLimit"));
			}
			response.addParam("groupSentRequests", (List<?>) query.getResultList());
		} else if (request.containsParam("iType") && request.getParam("iType").equals("SENTREQUESTS")){
			HQLQuery = "SELECT NEW GroupPublicRequest(r.id,r.status,r.message,r.sender,r.group.name) FROM GroupPublicRequest AS r WHERE r.group.id IN (SELECT g.id FROM Group AS g WHERE g.owner.id = :uid) ";
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("uid", ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
			if ((Integer) request.getParam("pageLimit") != 0){
				query.setFirstResult((Integer) request.getParam("pageStart"));
				query.setMaxResults((Integer) request.getParam("pageLimit"));
			}
			response.addParam("groupReceiveRequests", (List<?>) query.getResultList());
		}
	}
	
	@Override
	public void privateInviteGroup(RestRequest request, RestResponse response) throws Exception {
		Group group = (Group) entityManagerDataSvc.getInstance().getReference(Group.class, new Long((Integer) request.getParam("groupId")));
		GroupPrivateInvite groupPrivateInvite = (GroupPrivateInvite) request.getParams().get("groupPrivateInvite");
		groupPrivateInvite.setGroup(group);
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		groupPrivateInvite.setReceiver(userRef);
		groupPrivateInvite.setStatus(GroupPrivateInvite.PEND);
		entityManagerDataSvc.getInstance().persist(groupPrivateInvite);
	}
	
	@Override
	public void acceptPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPrivateInvite invite = entityManagerDataSvc.getInstance().find(GroupPrivateInvite.class, new Long((Integer) request.getParam("inviteId")));
		invite.setStatus(GroupPrivateInvite.APRV);
		request.addParam("groupId", invite.getGroup().getId());
		this.joinGroup(request, response);
		entityManagerDataSvc.getInstance().merge(invite);
	}
	
	@Override
	public void rejectPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception {
		int num = entityManagerDataSvc.getInstance().createQuery("UPDATE GroupPrivateInvite AS i SET i.status = 'REJT' WHERE i.id = :inviteid").setParameter("inviteid", new Long((Integer) request.getParam("inviteId"))).executeUpdate();
	
	}
	
	@Override
	public void deletePrivateInviteJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPrivateInvite invite = entityManagerDataSvc.getInstance().find(GroupPrivateInvite.class, new Long((Integer) request.getParam("inviteId")));
		entityManagerDataSvc.getInstance().remove(invite);
		
	}
	
	@Override
	public void requestJoinGroup(RestRequest request, RestResponse response) throws Exception {
		Group group = (Group) entityManagerDataSvc.getInstance().getReference(Group.class, new Long((Integer) request.getParam("groupId")));
		GroupPublicRequest groupRequest = (GroupPublicRequest) request.getParams().get("groupRequest");
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		groupRequest.setSender(userRef);
		groupRequest.setGroup(group);
		groupRequest.setStatus(GroupPublicRequest.PEND);
		entityManagerDataSvc.getInstance().persist(groupRequest);
		
	}
	
	@Override
	public void acceptPublicRequestJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPublicRequest groupPublicRequest = entityManagerDataSvc.getInstance().find(GroupPublicRequest.class, new Long((Integer) request.getParam("requestId")));
		groupPublicRequest.setStatus(GroupPublicRequest.APRV);
		GroupJoin join = new GroupJoin();
		join.setGroup(groupPublicRequest.getGroup());
		
		join.setUser(groupPublicRequest.getSender());
		entityManagerDataSvc.getInstance().persist(join);
		entityManagerDataSvc.getInstance().merge(groupPublicRequest);
	}
	
	@Override
	public void rejectPublicRequestJoin(RestRequest request, RestResponse response) throws Exception {
		int num = entityManagerDataSvc.getInstance().createQuery("UPDATE GroupPublicRequest AS r SET r.status = 'REJT' WHERE r.id = :requestid")
				.setParameter("requestid", new Long((Integer) request.getParam("requestId"))).executeUpdate();
		
	}
	
	@Override
	public void deletePublicRequestJoin(RestRequest request, RestResponse response) throws Exception {
		GroupPublicRequest groupPublicRequest = entityManagerDataSvc.getInstance().find(GroupPublicRequest.class, new Long((Integer) request.getParam("requestId")));
		entityManagerDataSvc.getInstance().remove(groupPublicRequest);
	}
//////////////////////////////////////////  Discussions //////////////////////////////////////
	
	@Override
	public void getDiscussionCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT count(*) FROM Discussion AS d WHERE d.group.id = :groupid";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("groupid",new Long((Integer) request.getParam("groupId")));
		response.addParam("discussionCount", (Long) query.getSingleResult());
	}
	
	@Override
	public void getDiscussions(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT NEW Discussion(d.id,d.subject,d.messageShort,d.owner) FROM Discussion AS d WHERE d.group.id = :groupid ORDER BY d.created DESC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("groupid", new Long((Integer) request.getParam("groupId")));
		if ((Integer) request.getParam("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("discussions", (List<?>) query.getResultList());
	}
	
	@Override
	public void getDiscussion(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM Discussion AS d WHERE d.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam("discussionId")));
		response.addParam("discussionComment", (Discussion) query.getSingleResult());
	}
	
	@Override
	public void getDiscussionMessage(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT NEW Discussion(d.id,d.messageShort,d.message) FROM Discussion AS d WHERE d.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam("discussionId")));
		response.addParam("discussionMessage", (Discussion) query.getSingleResult());
	}
	
	//@Authorize
	@Override
	public void saveDiscussion(RestRequest request, RestResponse response) throws Exception {
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		Discussion discussion = (Discussion) request.getParam("discussion");
		discussion.setOwner(userRef);
		Group group = (Group) entityManagerDataSvc.getInstance().getReference(Group.class, new Long((Integer) request.getParam("groupId")));
		discussion.setGroup(group);
		entityManagerDataSvc.getInstance().merge(discussion);
	}

	//@Authorize
	@Override
	public void deleteDiscussion(RestRequest request, RestResponse response) throws Exception {
		Discussion discussion = (Discussion) entityManagerDataSvc.getInstance().getReference(Discussion.class, new Long((Integer) request.getParam("discussionId")));
		entityManagerDataSvc.getInstance().remove(discussion);
	}
	
/////////////////////////////////////////  Discussion Comments ///////////////////////////////////	
	
	@Override
	public void getCommentCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT count(*) FROM DiscussionComment AS c WHERE c.discussion.id = :discussionid";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("discussionid",new Long((Integer) request.getParam("discussionId")));
		response.addParam("commentCount", (Long) query.getSingleResult());
	}
	
	@Override
	public void getDiscussionComments(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT NEW DiscussionComment(c.id,c.messageShort,c.owner) FROM DiscussionComment AS c WHERE c.discussion.id = :discussionid ORDER BY c.created DESC";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("discussionid", new Long((Integer) request.getParam("discussionId")));
		if ((Integer) request.getParams().get("pageLimit") != 0){
			query.setFirstResult((Integer) request.getParam("pageStart"));
			query.setMaxResults((Integer) request.getParam("pageLimit"));
		}
		response.addParam("discussionComments", (List<?>) query.getResultList());
	}
	
	@Override
	public void getDiscussionComment(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "FROM DiscussionComment AS c WHERE c.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam("commentId")));
		response.addParam("discussionComments", (DiscussionComment) query.getSingleResult());
	}
	
	@Override
	public void getCommentMessage(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT NEW DiscussionComment(c.id,c.messageShort,c.message) FROM DiscussionComment AS c WHERE c.id = :id";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam("commentId")));
		response.addParam("commentMessage", (DiscussionComment) query.getSingleResult());
	}
	
	//@Authorize
	@Override
	public void saveDiscussionComment(RestRequest request, RestResponse response) throws Exception {
		UserRef userRef = (UserRef) entityManagerDataSvc.getInstance().getReference(UserRef.class, ((UserRef) request.getParam(GlobalConstant.USERREF)).getId());
		DiscussionComment comment = (DiscussionComment) request.getParam("comment");
		comment.setOwner(userRef);
		Discussion discussion = (Discussion) entityManagerDataSvc.getInstance().getReference(Discussion.class, new Long((Integer) request.getParam("discussionId")));
		comment.setDiscussion(discussion);
		entityManagerDataSvc.getInstance().merge(comment);
	}

	//@Authorize
	@Override
	public void deleteDiscussionComment(RestRequest request, RestResponse response) throws Exception {
		int deletedEntities = entityManagerDataSvc.getInstance().createQuery("DELETE DiscussionComment as c WHERE c.id = :id")
				.setParameter("id", new Long((Integer) request.getParam("commentId"))).executeUpdate();
		if (deletedEntities == 0){
			throw new Exception("Nothing deleted verify your id");
		}
	}
}
