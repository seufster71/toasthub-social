package org.toasthub.social.activity;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface ActivitySvc {

	public void process(RestRequest request, RestResponse response);
}
