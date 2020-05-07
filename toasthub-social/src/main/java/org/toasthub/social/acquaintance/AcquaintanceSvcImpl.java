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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.EmailInvite;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.mail.MailSvc;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.security.model.UserContext;
import org.toasthub.security.userManager.UserManagerSvc;
import org.toasthub.social.model.Invite;

@Service("AcquaintanceSvc")
public class AcquaintanceSvcImpl implements AcquaintanceSvc, ServiceProcessor {

	@Autowired
	@Qualifier("AcquaintanceDao")
	AcquaintanceDao acquaintanceDao;
	
	@Autowired 
	UserManagerSvc userManagerSvc;
	
	@Autowired
	MailSvc mailSvc;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Autowired
	UserContext login;
	
	static Map<String,String> paramMap = new HashMap<String,String>();
	static {
		paramMap.put("receiverId", "Long");
		paramMap.put("memberId", "Long");
		paramMap.put("appUserId", "Long");
	}
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		utilSvc.preProcessParams(request, paramMap);
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "INIT": 
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			
			
			request.addParam("iStatus", Invite.PEND);
			this.getInviteCount(request, response);
			
			
			break;
		case "LIST":
			this.getAcquaintanceCount(request, response);
			if ((Long)response.getParam("acquaintanceCount") > 0) {
				this.getAcquaintances(request, response);
			}
			break;
		case "SHOW":
			
			this.getAcquaintance(request, response);
			break;
		case "DELETE":
			this.deleteAcquaintance(request, response);
			break;
		case "SAVE":
			
			break;
		case "MEMBER_LIST":
			if ("true".equals(request.getParam("includePending"))){
				this.getInvitesReceived(request, response);
			}
			//this.getMemberCount(request, response);
			//if ((Long)response.getParam("memberCount") > 0){
			//	this.getMembers(request, response);
			//}
			break;
		case "INVITE_SEND":
			request.addParam("sysPageFormName", "SOCIAL_ACQUAINTANCE_INVITE_FORM");
			prefCacheUtil.getPrefInfo(request,response);
			//"SOCIAL_ACQUAINTANCE_INVITE_FORM"
			Invite invite = new Invite();
			
			invite.setSubject("Aquaintance Invite Request");
			request.addParam("invite", invite);
			
	        this.makeAcquaintanceInvite(request, response); 
			break;
		case "INVITE_LIST":
			response.addParam("pageName", "ACQUAINTANCE_INVITE");
			request.addParam("iStatus", Invite.PEND);
			this.getInvitesReceived(request, response);
			this.getInvitesSent(request, response);
			this.getEvitesSent(request, response);
			break;
		case "INVITE_RECEIVED_LIST":
			this.getInvitesReceived(request, response);
			break;
		case "INVITE_SENT_LIST":
			this.getInvitesSent(request, response);
			this.getEvitesSent(request, response);
			break;
		case "INVITE_ACCEPT":
			this.acceptInvite(request, response);
			break;
		case "INVITE_DENY":
			this.denyInvite(request, response);
			break;
		case "INVITE_DELETE":
			this.deleteInvite(request, response);
			break;
		case "INVITE_EMAIL_SEND":
			request.addParam("sysPageFormName", "SOCIAL_ACQUAINTANCE_EMAIL_INVITE_FORM");
			prefCacheUtil.getPrefInfo(request,response);
			
			EmailInvite eInvite = new EmailInvite(request, response, "SOCIAL_ACQUAINTANCE_EMAIL_INVITE_FORM");

			eInvite.setSubject("Invitation to join Toasthub");
			request.addParam("emailInvite", eInvite);
			this.sendEmailInvite(request, response);
			break;	
		case "WATCH_LIST":
			response.addParam("pageName", "WATCH_LIST");
			// get watch list
			request.addParam("status", Invite.PEND);
			this.getInvitesReceived(request, response);
			this.getInvitesSent(request, response);
			
			this.getEvitesSent(request, response);
			break;
		case "WATCH_STATUS":
			
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	protected void getAcquaintances(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getAcquaintances(request, response);
			if (response.getParam("acquaintances") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Acquaintances", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	/*protected void getMembers(RestRequest request, RestResponse response) {
		try {
			//acquaintanceDao.getMembers(request, response);
			if (response.getParam("members") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Members", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, AppCachePageUtil.getAppText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}*/

	protected void getAcquaintanceCount(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getAcquaintanceCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	/*protected void getMemberCount(RestRequest request, RestResponse response) {
		//acquaintanceDao.getMemberCount(request, response);
	}*/
	
	protected void makeAcquaintanceInvite(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.makeAcquaintanceInvite(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Invite sent successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	protected void getInvitesReceived(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getInvitesReceived(request, response);
			if (response.getParam("invitesReceived") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Invites Received", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	protected void getInvitesSent(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getInvitesSent(request, response);
			if (response.getParam("invitesSent") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Invites Sent", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}
	
	protected void getEvitesSent(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getEvitesSent(request, response);
			if (response.getParam("evitesSent") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Email Invites Sent", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	protected void getInviteCount(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getInviteCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "ACQUAINTANCE_SERVICE", "ACQUAINTANCE_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	protected void acceptInvite(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.acceptInvite(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Accept Invite Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}

	protected void deleteInvite(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.deleteInvite(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Invite Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}

	protected void denyInvite(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.denyInvite(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Deny Invite Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}

	protected void deleteAcquaintance(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.deleteAcquaintance(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Acquaintance Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}
	
	protected void sendEmailInvite(RestRequest request, RestResponse response) {
		try {
			EmailInvite invite = (EmailInvite) request.getParam("emailInvite");
			// see if user already exists with this email
		//	User user = userManagerSvc.findUserByEmail(invite.getReceiverEmail());
			// do not allow them to send it to them selves
			//login.getAppUser()
			
		//	request.addParam("receiverUser", user);
		//	if (user != null){
		//		request.addParam("subject", "Aquaintance Invite");
		//		request.addParam("message", invite.getMessage());
		//		makeAcquaintanceInvite(request, response);
		//	} else {
		//		acquaintanceDao.makeEmailInvite(request, response);
		//		mailSvc.sendEmailInvite(request, response);
		//		acquaintanceDao.updateEmailInvite(request, response);
		//		utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Email invite sent Successful", response);
		//	}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to send email: " + e.getMessage(), response);
			EmailInvite invite = (EmailInvite) request.getParam("emailInvite");
			invite.setStatus(EmailInvite.FAIL);
			try {
				acquaintanceDao.updateEmailInvite(request, response);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	protected void getAcquaintance(RestRequest request, RestResponse response) {
		try {
			acquaintanceDao.getAcquaintance(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}
}
