/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.model.AcObject;
import eu.trentorise.smartcampus.ac.provider.model.App;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;

/**
 * 
 * @author Viktor Pravdin
 */

public interface AcDao {

	public <T extends AcObject> long create(T acObj);

	public <T extends AcObject> void update(T acObj);

	public <T extends AcObject> boolean delete(T acObj);

	public User readUser(long id);

	public User readUser(String authToken);

	public List<User> readUsers(List<Attribute> attributes);

	public User readUserBySocialId(long id);

	Collection<Authority> readAuthorities();

	Authority readAuthorityByName(String name);

	Authority readAuthorityByUrl(String url);
	
	public String createSessionToken(long userId, Long expTime);
	
	public String generateAuthToken();
	
	//App
	
	public App readApp(long id);

	public App readApp(String appToken);

	public List<App> readApps(List<Attribute> attributes);

	public App readAppBySocialId(long id);
	
	public String createSessionAppToken(long appId, Long expTime);
	
	public String generateAppToken();


}
