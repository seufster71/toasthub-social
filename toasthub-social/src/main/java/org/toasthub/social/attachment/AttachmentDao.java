package org.toasthub.social.attachment;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface AttachmentDao {

	public void getAttachmentMeta(RestRequest request, RestResponse response) throws Exception;
	public void getAttachment(RestRequest request, RestResponse response) throws Exception;
	public void getThumbNail(RestRequest request, RestResponse response) throws Exception;
	public void delete(RestRequest request, RestResponse response) throws Exception;
	public void save(RestRequest request, RestResponse response) throws Exception;
}
