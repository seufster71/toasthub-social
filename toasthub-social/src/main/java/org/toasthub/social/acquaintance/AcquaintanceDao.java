package org.toasthub.social.acquaintance;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface AcquaintanceDao {

	public void getAcquaintances(RestRequest request, RestResponse response) throws Exception;
	public void getAcquaintanceCount(RestRequest request, RestResponse response) throws Exception;
	public void makeAcquaintanceInvite(RestRequest request, RestResponse response) throws Exception;
	public void makeEmailInvite(RestRequest request, RestResponse response) throws Exception;
	public void updateEmailInvite(RestRequest request, RestResponse response) throws Exception;
	public void getInvitesReceived(RestRequest request, RestResponse response) throws Exception;
	public void getInvitesSent(RestRequest request, RestResponse response) throws Exception;
	public void getEvitesSent(RestRequest request, RestResponse response) throws Exception;
	public void getInviteCount(RestRequest request, RestResponse response) throws Exception;
	public void deleteInvite(RestRequest request, RestResponse response) throws Exception;
	public void acceptInvite(RestRequest request, RestResponse response) throws Exception;
	public void denyInvite(RestRequest request, RestResponse response) throws Exception;
	public void deleteAcquaintance(RestRequest request, RestResponse response) throws Exception;
	public void getMembersNotAquaintanceAlready(RestRequest request, RestResponse response) throws Exception;
	public void getAcquaintance(RestRequest request, RestResponse response) throws Exception;
	
}
