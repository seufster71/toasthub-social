package org.toasthub.social.event;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface EventSvc {

	public void process(RestRequest request, RestResponse response);
}
