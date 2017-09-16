package org.toasthub.social.activity;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface ActivityDao {

	public void itemCount(RestRequest request, RestResponse response) throws Exception;
	public void items(RestRequest request, RestResponse response) throws Exception;
	public void item(RestRequest request, RestResponse response) throws Exception;
	public void save(RestRequest request, RestResponse response) throws Exception;
	public void delete(RestRequest request, RestResponse response) throws Exception;
}
