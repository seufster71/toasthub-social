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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppCachePageUtil;
import org.toasthub.social.model.Discussion;
import org.toasthub.social.model.Group;

@Service("GroupSvc")
public class GroupSvcImpl implements GroupSvc, ServiceProcessor {

	@Autowired
	GroupDao groupDao;
	@Autowired
	UtilSvc utilSvc;
	@Autowired
	AppCachePageUtil appCachePageUtil;
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get("action");
		// get option from main
		request.addParam("sysPageFormName", "SOCIAL_GROUP");
		request.addParam("sysPageLabelName", "SOCIAL_GROUP");
		request.addParam("sysPageTextName", "SOCIAL_GROUP");
		
		switch (action) {
		case "INIT":
			appCachePageUtil.getPageInfo(request,response);
			
			response.addParam("pageName", "SOCIAL_GROUP");
			request.addParam("tab", Group.MYGROUPS);
			getGroupCount(request, response);
			Long count = (Long) response.getParam("myGroupCount");
			if (count != null && count > 0){
				getGroups(request, response);
			}
			response.addParam("tab", "MYGROUPS");
			break;
		case "LIST":
			String tab = (String)request.getParams().get("tab");
			if (tab.equals("INVITES")){
				request.addParam("iType", "SENTINVITES");
				getInviteCount(request, response);
				Long sentInviteCount = (Long) response.getParam("sentInviteCount");
				if (sentInviteCount != null && sentInviteCount > 0){
					getGroupInvites(request, response);
				}
				
				request.addParam("iType", "RECEIVEINVITES");
				getInviteCount(request, response);
				Long receiveInviteCount = (Long) response.getParam("sentInviteCount");
				if (receiveInviteCount != null && receiveInviteCount > 0){
					getGroupInvites(request, response);
				}
			} else if (tab.equals("REQUESTS")) {
				request.addParam("iType", "SENTREQUESTS");
				getRequestCount(request, response);
				Long sentRequestCount = (Long) response.getParam("sentRequestCount");
				if (sentRequestCount != null && sentRequestCount > 0){
					getGroupRequests(request, response);
				}
				
				request.addParam("iType", "RECEIVEREQUESTS");
				getRequestCount(request, response);
				Long receiveRequestCount = (Long) response.getParam("receiveRequestCount");
				if (receiveRequestCount != null && receiveRequestCount > 0){
					getGroupRequests(request, response);
				}
			} else {
				getGroupCount(request, response);
				Long groupCount = (Long) response.getParam("groupCount");
				if (groupCount != null && groupCount > 0){
					getGroups(request, response);
				}
			}
			response.addParam("tab", tab);
			break;
		case "SHOW":
			getGroup(request, response);
			break;
		case "DELETE":
			deleteGroup(request, response);
			break;
		case "SAVE":
			saveGroup(request, response);
			break;
		case "JOIN":
			if (request.getParams().get("tab").equals("PUBO")){
				joinGroup(request, response);
			} else if (request.getParams().get("tab").equals("PUBR")){
				requestJoinGroup(request, response);
			} else if (request.getParams().get("tab").equals("PRII")){
				privateInviteGroup(request, response);
			} else {
				
			}
			break;
		case "LEAVE":
			leaveGroup(request, response);
			break;
		case "DISCUSSION_LIST":
			getDiscussionCount(request, response);
			Long discussionCount = (Long) response.getParam("count");
			if (discussionCount != null && discussionCount > 0){
				getDiscussions(request, response);
			}
			response.addParam("groupId", new Long((Integer) request.getParam("groupId")));
			response.addParam("tab", (String) request.getParams().get("tab"));
			break;
		case "DISCUSSION_SAVE":
			Discussion discussion = (Discussion) request.getParams().get("discussion");
			if (discussion != null && discussion.getMessage() != null && !discussion.getMessage().equals("")){
				String message = discussion.getMessage();
				discussion.setMessageShort(message.substring(0, 10));
				discussion.setMessage(message.substring(10));
				saveDiscussion(request, response);
			}
			break;
		case "DISCUSSION_SHOW":
			getDiscussion(request, response);
			break;
		case "DISCUSSION_DELETE":
			deleteDiscussion(request, response);
			break;
		case "DISCUSSION_MESSAGE":
			getDiscussionMessage(request, response);
			break;
		case "DISCUSSION_COMMENT_LIST":
			getCommentCount(request, response);
			Long commentCount = (Long) response.getParam("count");
			if ( commentCount != null && commentCount > 0){
				getDiscussionComments(request, response);
			}
			response.addParam("discussionId", (Long) request.getParams().get("discussionId"));
			break;
		case "DISCUSSION_COMMENT_SAVE":
			saveDiscussionComment(request, response);
			break;
		case "DISCUSSION_COMMENT_SHOW":
			//formSvc.getForm(request,response);
			getDiscussionComment(request, response);
			break;
		case "DISCUSSION_COMMENT_DELETE":
			deleteDiscussionComment(request, response);
			response.addParam("commentId", request.getParam("commentId"));
			break;
		case "DISCUSSION_COMMENT_MESSAGE":
			getCommentMessage(request, response);
			break;
		case "JOIN_CANCEL":
			if (request.getParams().get("tab").equals("PUBR")){
				cancelPublicRequestJoin(request, response);
			} else if (request.getParams().get("tab").equals("PRII")){
				cancelPrivateInviteJoin(request, response);
			}
			break;
		case "JOIN_ACCEPT":	
			if (request.getParams().get("tab").equals("PUBR")){
				acceptPublicRequestJoin(request, response);
			} else if (request.getParams().get("tab").equals("PRII")){
				acceptPrivateInviteJoin(request, response);
			}
			break;
		case "JOIN_REJECT":	
			if (request.getParams().get("tab").equals("PUBR")){
				rejectPublicRequestJoin(request, response);
			} else if (request.getParams().get("tab").equals("PRII")){
				rejectPrivateInviteJoin(request, response);
			}
			break;
		case "JOIN_DELETE":	
			if (request.getParams().get("tab").equals("PUBR")){
				deletePublicRequestJoin(request, response);
			} else if (request.getParams().get("tab").equals("PRII")){
				deletePrivateInviteJoin(request, response);
			}
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
		
		
	}
	
////////////////////////////////////////////// Groups ////////////////////////////////////////
	public void getGroupCount(RestRequest request, RestResponse response){
		try {
			groupDao.getGroupCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getInviteCount(RestRequest request, RestResponse response){
		try {
			groupDao.getInviteCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}

	public void getRequestCount(RestRequest request, RestResponse response){
		try {
			groupDao.getRequestCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getGroups(RestRequest request, RestResponse response) {
		try {
			groupDao.getGroups(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getLatestGroups(RestRequest request, RestResponse response){
		try {
			groupDao.getLatestGroups(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getGroup(RestRequest request, RestResponse response){
		try {
			groupDao.getGroup(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void saveGroup(RestRequest request, RestResponse response){
		try {
			groupDao.saveGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deleteGroup(RestRequest request, RestResponse response){
		try {
			groupDao.deleteGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Failed", response);
			e.printStackTrace();
		}
	}
	
	public void joinGroup(RestRequest request, RestResponse response){
		try {
			groupDao.joinGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Join Failed", response);
			e.printStackTrace();
		}
	}
	
	public void leaveGroup(RestRequest request, RestResponse response){
		try {
			groupDao.leaveGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Leave successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Leave Failed", response);
			e.printStackTrace();
		}
	}
	
	public void cancelPublicRequestJoin(RestRequest request, RestResponse response){
		try {
			groupDao.cancelPublicRequestJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Cancel Public Request Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Cancel Public Request Join Failed", response);
			e.printStackTrace();
		}
	}
	
	public void cancelPrivateInviteJoin(RestRequest request, RestResponse response){
		try {
			groupDao.cancelPrivateInviteJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Cancel Private Invite Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Cancel Private Invite Join Failed", response);
			e.printStackTrace();
		}
	}
	public void getGroupInvites(RestRequest request, RestResponse response) {
		try {
			groupDao.getGroupInvites(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getGroupRequests(RestRequest request, RestResponse response) {
		try {
			groupDao.getGroupRequests(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void privateInviteGroup(RestRequest request, RestResponse response){
		try {
			groupDao.privateInviteGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Private Invite successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Private Invite Failed", response);
			e.printStackTrace();
		}
	}
	
	public void acceptPrivateInviteJoin(RestRequest request, RestResponse response){
		try {
			groupDao.acceptPrivateInviteJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Accept Private Invite successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Accept Private Invite Failed", response);
			e.printStackTrace();
		}
	}
	
	public void rejectPrivateInviteJoin(RestRequest request, RestResponse response){
		try {
			groupDao.rejectPrivateInviteJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Reject Private Invite successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Reject Private Invite Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deletePrivateInviteJoin(RestRequest request, RestResponse response){
		try {
			groupDao.deletePrivateInviteJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Private Invite successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Private Invite Failed", response);
			e.printStackTrace();
		}
	}
	
	public void requestJoinGroup(RestRequest request, RestResponse response){
		try {
			groupDao.requestJoinGroup(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Request To Join Sent", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Request Join Failed", response);
			e.printStackTrace();
		}
	}
	
	public void acceptPublicRequestJoin(RestRequest request, RestResponse response){
		try {
			groupDao.acceptPublicRequestJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Accept Request to Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Accept Request to Join Failed", response);
			e.printStackTrace();
		}
	}
	
	public void rejectPublicRequestJoin(RestRequest request, RestResponse response){
		try {
			groupDao.rejectPublicRequestJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Reject Request to Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Reject Request to Join Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deletePublicRequestJoin(RestRequest request, RestResponse response){
		try {
			groupDao.deletePublicRequestJoin(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Request to Join successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Request to Join Failed", response);
			e.printStackTrace();
		}
	}
	//////////////////////////////////////////// Discussions ////////////////////////////////////////
	
	public void getDiscussionCount(RestRequest request, RestResponse response){
		try {
			groupDao.getDiscussionCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getDiscussions(RestRequest request, RestResponse response) {
		try {
			groupDao.getDiscussions(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getDiscussion(RestRequest request, RestResponse response){
		try {
			groupDao.getDiscussion(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getDiscussionMessage(RestRequest request, RestResponse response){
		try {
			groupDao.getDiscussionMessage(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveDiscussion(RestRequest request, RestResponse response){
		try {
			groupDao.saveDiscussion(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deleteDiscussion(RestRequest request, RestResponse response){
		try {
			groupDao.deleteDiscussion(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Failed", response);
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////// Comments ////////////////////////////////////////////
	
	public void getCommentCount(RestRequest request, RestResponse response){
		try {
			groupDao.getCommentCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getDiscussionComments(RestRequest request, RestResponse response) {
		try {
			groupDao.getDiscussionComments(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getDiscussionComment(RestRequest request, RestResponse response){
		try {
			groupDao.getDiscussionComment(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void getCommentMessage(RestRequest request, RestResponse response){
		try {
			groupDao.getCommentMessage(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void saveDiscussionComment(RestRequest request, RestResponse response){
		try {
			groupDao.saveDiscussionComment(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deleteDiscussionComment(RestRequest request, RestResponse response){
		try {
			groupDao.deleteDiscussionComment(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Failed", response);
			e.printStackTrace();
		}
	}
}
