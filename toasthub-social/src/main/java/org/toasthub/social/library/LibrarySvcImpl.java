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

package org.toasthub.social.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.social.model.Directory;


@Service("LibrarySvc")
public class LibrarySvcImpl implements LibrarySvc, ServiceProcessor {

	@Autowired
	LibraryDaoImpl libraryDao;
	@Autowired
	UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		
		String action = (String) request.getParams().get("action");
		// get option from main
		request.addParam("sysPageFormName", "SOCIAL_LIBRARY");
		request.addParam("sysPageLabelName", "SOCIAL_LIBRARY");
		request.addParam("sysPageTextName", "SOCIAL_LIBRARY");
		
		switch (action) {
		case "INIT": 
			
			prefCacheUtil.getPrefInfo(request,response);
			
			response.addParam("pageName", "SOCIAL_LIBRARY");
			request.addParam("iType", Directory.MINE);
			request.addParam("parentId", null);
			getDirectoryCount(request, response);
			Long count = (Long) response.getParam("dirCount");
			if (count != null && count > 0){
				getDirectories(request, response);
			}
			response.addParam("type", "MINE");
			break;
		case "LIST":
			getDirectoryCount(request, response);
			Long dirCount = (Long) response.getParam("dirCount");
			if (dirCount != null && dirCount > 0){
				getDirectories(request, response);
			}
			if (request.getParam("parentId") != null){
				getFileCount(request, response);
				Long fileCount = (Long) response.getParam("fileCount");
				if (fileCount != null && fileCount > 0){
					getFiles(request, response);
				}
				response.addParam("directoryId", (Long) request.getParam("parentId"));
			}
			response.addParam("type", request.getParam("type"));
			break;
		case "DIRECTORY_SHOW":
			getDirectory(request, response);
			break;
		case "DIRECTORY_DELETE":
			deleteDirectory(request, response);
			break;
		case "DIRECTORY_SAVE":
			saveDirectory(request, response);
			break;
		case "ATTACHMENT_DELETE":
			//attachmentSvc.delete(request, response);
			break;
		case "ATTACHMENT_SHOW":
			//attachmentSvc.show(request, response);
			break;
		case "ATTACHMENT_SAVE":
			//attachmentSvc.save(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
		
	}
	
	///////////////////////////////////  Directory
	public void getDirectories(RestRequest request, RestResponse response){
		libraryDao.getDirectories(request, response);
	}
	
	public void getDirectory(RestRequest request, RestResponse response){
		libraryDao.getDirectory(request, response);
	}
	
	public void getDirectoryCount(RestRequest request, RestResponse response){
		libraryDao.getDirectoryCount(request, response);
	}
	
	public void saveDirectory(RestRequest request, RestResponse response){
		try {
			// marshallFactory("org.toastHub.social.model.Directory",request);
			libraryDao.saveDirectory(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Save successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Save Failed", response);
			e.printStackTrace();
		}
	}
	
	public void deleteDirectory(RestRequest request, RestResponse response){
		try {
			libraryDao.deleteDirectory(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Delete Failed", response);
			e.printStackTrace();
		}
	}
	///////////////////////////////////  File
	
	public void getFileCount(RestRequest request, RestResponse response){
		libraryDao.getFileCount(request, response);
	}
	
	public void getFiles(RestRequest request, RestResponse response){
		libraryDao.getFiles(request, response);
	}
	
	public void getFile(RestRequest request, RestResponse response){
		libraryDao.getDirectory(request, response);
	}
}
