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
package eu.trentorise.smartcampus.ac.provider.services;

import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.AcProviderService;
import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.model.App;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;

/**
 * Implementation of the SOAP service
 * 
 * @author Viktor Pravdin
 */
@WebService(endpointInterface = "eu.trentorise.smartcampus.ac.provider.AcProviderService")
@Service
public class WSAcProviderService implements AcProviderService {

	@Autowired
	private TXAcProviderService impl = null;
	
	public User createUser(String authToken, long expDate,
			List<Attribute> attributes) throws AcServiceException {
		return impl.createUser(authToken, expDate, attributes);
	}

	@Override
	public String createSessionToken(long userId, Long expTime) throws AcServiceException {
		return impl.createSessionToken(userId, expTime);
	}

	public boolean removeUser(String authToken) throws AcServiceException {
		return impl.removeUser(authToken);
	}
	public User getUserByToken(String authToken) throws AcServiceException {
		return impl.getUserByToken(authToken);
	}
	public List<User> getUsersByAttributes(List<Attribute> attributes)
			throws AcServiceException {
		return impl.getUsersByAttributes(attributes);
	}
	public void updateUser(long userId, String authToken, Long expTime,
			List<Attribute> attributes) throws AcServiceException {
		impl.updateUser(userId, authToken, expTime, attributes);
	}
	public boolean isValidUser(String authToken) throws AcServiceException {
		return impl.isValidUser(authToken);
	}
	public boolean isAnonymousUser(String authToken) throws AcServiceException {
		return impl.isAnonymousUser(authToken);
	}
	public List<Attribute> getUserAttributes(String authToken,
			String authority, String key) throws AcServiceException {
		return impl.getUserAttributes(authToken, authority, key);
	}
	public Collection<Authority> getAuthorities() throws AcServiceException {
		return impl.getAuthorities();
	}
	public Authority getAuthorityByName(String name) throws AcServiceException {
		return impl.getAuthorityByName(name);
	}
	public Authority getAuthorityByUrl(String name) throws AcServiceException {
		return impl.getAuthorityByUrl(name);
	}
	public void createAuthority(Authority auth) throws AcServiceException {
		impl.createAuthority(auth);
	}
	public String generateAuthToken() throws AcServiceException {
		return impl.generateAuthToken();
	}
	public void updateAuthority(Authority auth) throws AcServiceException {
		impl.updateAuthority(auth);
	}
	public void setAttribute(long userId, Attribute attribute)
			throws AcServiceException {
		impl.setAttribute(userId, attribute);
	}
	public boolean canReadResource(String authToken, String resourceId) throws AcServiceException {
		return impl.canReadResource(authToken, resourceId);
	}
	public App getAppByToken(String appToken) throws AcServiceException {
		return impl.getAppByToken(appToken);
	}
}
