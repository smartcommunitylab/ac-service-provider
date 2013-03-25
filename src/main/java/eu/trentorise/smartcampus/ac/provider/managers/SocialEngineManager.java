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

package eu.trentorise.smartcampus.ac.provider.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;
import it.unitn.disi.sweb.webapi.model.entity.Entity;
import it.unitn.disi.sweb.webapi.model.entity.EntityBase;
import it.unitn.disi.sweb.webapi.model.entity.EntityType;
import it.unitn.disi.sweb.webapi.model.smartcampus.ac.Operation;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.User;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class manages all the operations between ac-service-provider and the
 * social engine component
 * 
 * @author mirko perillo
 * 
 */
@Component
public class SocialEngineManager {

	@Value("${socialEngine.host}")
	private String socialEngineHost;
	@Value("${socialEngine.port}")
	private int socialEnginePort;

	private SCWebApiClient client = null;
	
	private SCWebApiClient getClient() throws SocialEngineException {
		if (client == null) {
			client = SCWebApiClient.getInstance(Locale.ENGLISH,
					socialEngineHost, socialEnginePort);
			if (!client.ping())
				throw new SocialEngineException("Social engine not available");
		}
		return client;
	}
	
	/**
	 * creates a new social user in the social engine component.
	 * 
	 * @param user
	 *            object user to create
	 * @return the id of social user created
	 * @throws SocialEngineException
	 *             if social engine threw an internal exception
	 */
	public long createUser(eu.trentorise.smartcampus.ac.provider.model.User user)
			throws SocialEngineException {
		long socialId = -1;
		EntityBase eb = null;
//		String userId = user.getId().toString();
		Long entityBaseId = null;
		Long entityId = null;
		try {
			eb = new EntityBase();
			eb.setLabel("SC_USER_" + System.currentTimeMillis());
			entityBaseId = getClient().create(eb);
			// Re-read to get the ID of the default KB
			eb = getClient().readEntityBase(entityBaseId);

			EntityType person = getClient()
					.readEntityType("person", eb.getKbLabel());
			Entity entity = new Entity();
			entity.setEntityBase(eb);
			entity.setEtype(person);
//			entity.setExternalId(userId);

			entityId = getClient().create(entity);

			User socialUser = new User();
			socialUser.setName("SC_" + System.currentTimeMillis());
			socialUser.setEntityBaseId(entityBaseId);
			socialUser.setPersonEntityId(entityId);
			socialId = getClient().create(socialUser);

		} catch (Exception e) {
			try {
				if (eb != null) {
					getClient().deleteEntityBase(eb.getId());
				}

				if (entityId != null) {
					getClient().deleteEntity(entityId);
				}
			} catch (WebApiException e1) {
				throw new SocialEngineException();
			}

			throw new SocialEngineException();
		}

		return socialId;
	}
	
	public boolean checkResourceAccess(long socialUserId, long entityId) throws WebApiException, SocialEngineException {
		return getClient().readPermission(socialUserId, entityId, Operation.READ);
	}
	
	public static void main(String[] args) throws SocialEngineException {
		SocialEngineManager mgr = new SocialEngineManager();
		mgr.client = SCWebApiClient.getInstance(Locale.ENGLISH, "sweb.smartcampuslab.it", 8080);
		eu.trentorise.smartcampus.ac.provider.model.User u = new eu.trentorise.smartcampus.ac.provider.model.User();
		u.setId(100L);
		mgr.createUser(u);
		
	}
}
