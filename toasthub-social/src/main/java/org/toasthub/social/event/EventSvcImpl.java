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

package org.toasthub.social.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppCachePageUtil;
import org.toasthub.social.model.AttachmentMeta;
import org.toasthub.social.model.AttachmentThumbnail;
import org.toasthub.social.model.Event;

@Service("EventSvc")
public class EventSvcImpl implements EventSvc, ServiceProcessor {

	@Autowired 
	@Qualifier("EventDao")
	EventDao eventDao;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	AppCachePageUtil appCachePageUtil;
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParam("action");
		// get option from main
		request.addParam("sysPageFormName", "SOCIAL_EVENT");
		request.addParam("sysPageLabelName", "SOCIAL_EVENT");
		request.addParam("sysPageTextName", "SOCIAL_EVENT");
		Event event = null;
		Long count = 0l;
		switch (action) {
		case "INIT": 
			itemCount(request, response);
			count = (Long) response.getParam("count");
			if (count != null && count > 0){
				items(request, response);
			}
			break;
		case "LIST":
			itemCount(request, response);
			count = (Long) response.getParam("count");
			if (count != null && count > 0){
				items(request, response);
			}
			break;
		case "SHOW":
			item(request, response);
			break;
		case "SAVE":
			request.addParam("sysPageFormName", "SOCIAL_EVENT");
			appCachePageUtil.getPageInfo(request,response);
			
			event = new Event(request, response, "SOCIAL_EVENT");
			request.addParam("event", event);
			save(request, response);
			response.addParam("eventId", request.getParam("eventId"));
			break;
		case "DELETE":
			delete(request, response);
			break;
		case "SAVE_IMAGE": 
			request.addParam("sysPageFormName", "SOCIAL_EVENT");
			appCachePageUtil.getPageInfo(request,response);
			String fileName = "";
			String contentType = "txt";
			String comment = null;
		/*	Map<String, List<InputPart>> uploadForm = (Map<String, List<InputPart>>) request.getParam("uploadForm");
			if (!request.containsParam("eventId")){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONFAILED, "Missing Event Id", response);
				break;
			}
			// save attachments
			AttachmentMeta attachmentMeta = new AttachmentMeta();
			List<InputPart> inputFiles = uploadForm.get("uploaded_file");
			for (InputPart inputPart : inputFiles) {
				try {
					MultivaluedMap<String, String> header = inputPart.getHeaders();
					Map<String,String> attributes = utilSvc.getDispositionAttributes(header);
					if (attributes.containsKey("filename")){
						fileName = attributes.get("filename");
					}
					contentType = utilSvc.getContentType(header);
	 
					//convert the uploaded file to inputstream
					InputStream inputStream = inputPart.getBody(InputStream.class,null);
					
					if (attributes.containsKey("file-type") && "THUMBNAIL".equals(attributes.get("file-type"))){
						// create thumbnail
						attachmentMeta.setThumbNail( new AttachmentThumbnail( IOUtils.toByteArray(inputStream) ) );
					} else {
						// Full res attachment
						byte[] fullResBytes = IOUtils.toByteArray(inputStream);
						attachmentMeta.setFileName(fileName);
						attachmentMeta.setTitle(fileName);
						attachmentMeta.setContentType(contentType);
						attachmentMeta.setSize((long) fullResBytes.length);
						attachmentMeta.setData( new Attachment( fullResBytes  ) );
					}
					
					if (comment != null){
						attachmentMeta.setComment(comment);
					}	
					
					inputStream.close();
				} catch (IOException e) {
					utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "File upload failed",response);
					e.printStackTrace();
				}
			}
			request.addParam("attachmentMeta", attachmentMeta);
			this.saveAttachment(request, response);
			*/
			break;
		case "LOAD_IMAGE":
			this.getThumbNail(request, response);
			break;
		case "EVENT_CHECK":
			this.eventCheck(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	protected void itemCount(RestRequest request, RestResponse response) {
		try {
			eventDao.itemCount(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	protected void items(RestRequest request, RestResponse response) {
		try {
			eventDao.items(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	protected void item(RestRequest request, RestResponse response) {
		try {
			eventDao.item(request, response);
			//utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	//@Authorize
	protected void save(RestRequest request, RestResponse response){
		try {
			eventDao.save(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	//@Authorize
	protected void delete(RestRequest request, RestResponse response){
		try {
			eventDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Failed", response);
			e.printStackTrace();
		}
	}
	
	//@Authorize
	protected void saveAttachment(RestRequest request, RestResponse response){
		try {
			eventDao.saveAttachment(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void getThumbNail(RestRequest request, RestResponse response){
		try {
			eventDao.getThumbNail(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void eventCheck(RestRequest request, RestResponse response){
		try {
			eventDao.eventCheck(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
