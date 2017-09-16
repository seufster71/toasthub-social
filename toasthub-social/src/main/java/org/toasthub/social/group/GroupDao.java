package org.toasthub.social.group;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface GroupDao {

	public void getGroups(RestRequest request, RestResponse response) throws Exception;
	public void getLatestGroups(RestRequest request, RestResponse response) throws Exception;
	public void getGroupCount(RestRequest request, RestResponse response) throws Exception;
	public void getInviteCount(RestRequest request, RestResponse response) throws Exception;
	public void getRequestCount(RestRequest request, RestResponse response) throws Exception;
	public void getGroup(RestRequest request, RestResponse response) throws Exception;
	public void saveGroup(RestRequest request, RestResponse response) throws Exception;
	public void deleteGroup(RestRequest request, RestResponse response) throws Exception;
	public void joinGroup(RestRequest request, RestResponse response) throws Exception;
	public void leaveGroup(RestRequest request, RestResponse response) throws Exception;
	public void cancelPublicRequestJoin(RestRequest request, RestResponse response) throws Exception;
	public void cancelPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception;
	public void getGroupInvites(RestRequest request, RestResponse response) throws Exception;
	public void getGroupRequests(RestRequest request, RestResponse response) throws Exception;
	public void privateInviteGroup(RestRequest request, RestResponse response) throws Exception;
	public void acceptPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception;
	public void rejectPrivateInviteJoin(RestRequest request, RestResponse response) throws Exception;
	public void deletePrivateInviteJoin(RestRequest request, RestResponse response) throws Exception;
	public void requestJoinGroup(RestRequest request, RestResponse response) throws Exception;
	public void acceptPublicRequestJoin(RestRequest request, RestResponse response) throws Exception;
	public void rejectPublicRequestJoin(RestRequest request, RestResponse response) throws Exception;
	public void deletePublicRequestJoin(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussionCount(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussions(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussion(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussionMessage(RestRequest request, RestResponse response) throws Exception;
	public void saveDiscussion(RestRequest request, RestResponse response) throws Exception;
	public void deleteDiscussion(RestRequest request, RestResponse response) throws Exception;
	public void getCommentCount(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussionComments(RestRequest request, RestResponse response) throws Exception;
	public void getDiscussionComment(RestRequest request, RestResponse response) throws Exception;
	public void getCommentMessage(RestRequest request, RestResponse response) throws Exception;
	public void saveDiscussionComment(RestRequest request, RestResponse response) throws Exception;
	public void deleteDiscussionComment(RestRequest request, RestResponse response) throws Exception;
}
