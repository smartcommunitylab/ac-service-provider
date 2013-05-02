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
package eu.trentorise.smartcampus.ac.provider.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineManager;
import eu.trentorise.smartcampus.ac.provider.model.App;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;

/**
 * Implementation of the service
 * 
 * @author Viktor Pravdin
 */
@Transactional
public class AcProviderServiceImpl implements TXAcProviderService {

	private static final Object AUTHORITY_ANONYMOUS = "anonymous";

	@Autowired
	private AcDao dao;
	/**
	 * Wrapper interface to perform a transaction
	 */
	@Autowired
	private CreationWrapper creationWrapper;

	@Autowired
	private SocialEngineManager socialManager;
	/**
	 * Creates a new user given its data. Creation is transactional
	 * 
	 * @param authToken
	 *            The authentication token for the user
	 * @param expTime
	 *            The expiration time
	 * @param attributes
	 *            The list of the attributes.
	 * @return the user object created
	 * @throws AcServiceExcetion
	 *             if some errors are generated during creation operations
	 */
	@Override
	public User createUser(String authToken, long expDate,
			List<Attribute> attributes) throws AcServiceException {
		User user = new User();
		user.setAuthToken(authToken);
		user.setExpTime(expDate);
		user.setAttributes(attributes);
		try {
			creationWrapper.create(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AcServiceException(e);
		}
		return user;
	}

	@Override
	public synchronized String createSessionToken(long userId, Long expTime) throws AcServiceException {
		return dao.createSessionToken(userId, expTime);
	}


	/**
	 * Deletes user binded to given authorization token
	 * 
	 * @param authToken
	 *            The authentication token of the user to delete
	 * @return true if operation gone right, false otherwise
	 * @throws AcServiceExcetion
	 *             if some errors are generated during delete operations
	 */

	@Override
	public boolean removeUser(String authToken) throws AcServiceException {
		User user = getUserByToken(authToken);
		if (user != null) {
			return dao.delete(user);
		}
		return false;
	}

	/**
	 * Returns the User binded to the given authentication token
	 * 
	 * @param authToken
	 *            authentication token
	 * @return user object
	 * @throws AcServiceException
	 */
	@Override
	public User getUserByToken(String authToken) throws AcServiceException {
		return dao.readUser(authToken);
	}

	/**
	 * Returns the subset of users that satisfied all the attributes of the
	 * given list
	 * 
	 * @param attributes
	 *            the list of attributes that users MUST contains
	 * @return the list of users that contains all the given attributes or an
	 *         empty list
	 * @throws AcServiceException
	 */
	@Override
	public List<User> getUsersByAttributes(List<Attribute> attributes)
			throws AcServiceException {
		return dao.readUsers(attributes);
	}

	/**
	 * Updates the user data with the given values. If a new value for an
	 * already existing value is not specified then the old value remains
	 * untouched.
	 * 
	 * @param userId
	 *            The ID of the user. Must not be null
	 * @param authToken
	 *            The new authentication token or null if the token shouldn't be
	 *            updated
	 * @param expTime
	 *            The new expiration time or null if it shouldn't be updated.
	 *            Must not be null if a new authentication token is present.
	 *            Ignored if the auth token is null.
	 * @param attributes
	 *            The map of the attributes. The existing attributes are
	 *            updated, the new ones are added, the old ones are kept intact.
	 * @throws IllegalArgumentException
	 *             if the user with the given ID doesn't exist or if the
	 *             expiration time is not specified for an authentication token
	 */
	@Override
	public void updateUser(long userId, String authToken, Long expTime,
			List<Attribute> attributes) throws AcServiceException {
		User user = dao.readUser(userId);
		if (user == null) {
			throw new IllegalArgumentException(
					"The user with that ID doesn't exist");
		}
		if (authToken != null) {
			if (expTime == null) {
				throw new IllegalArgumentException(
						"The expiration time is not specified");
			}
			user.setAuthToken(authToken);
		}
		if (expTime != null) {
			user.setExpTime(expTime);
		}

		user.getAttributes().clear();
		if (attributes != null) user.getAttributes().addAll(attributes);
		dao.update(user);
	}

	/**
	 * Returns if a user is yet valid
	 * 
	 * @param authToken
	 *            authentication token
	 * @return true if authentication token is not already expired, false
	 *         otherwise
	 * @throws AcServiceException
	 */

	@Override
	public boolean isValidUser(String authToken) throws AcServiceException {
		long time = System.currentTimeMillis();
		User user = getUserByToken(authToken);
		return user != null && ((user.getExpTime() - time) > 0);
	}

	/**
	 * Fetches the user attributes that match the given authority and the key.
	 * If neither is given then it will return all attributes of the user, if
	 * the authority is given then it will return only the attributes of that
	 * authority and if the key is also given then it will return only the
	 * attributes of that authority that have the given key.
	 * 
	 * @param authToken
	 *            The authentication token of the user
	 * @param authority
	 *            The authority name
	 * @param key
	 *            The key of the attribute
	 * @return The set of attributes that match the parameters
	 * @throws AcServiceException
	 */
	@Override
	public List<Attribute> getUserAttributes(String authToken,
			String authority, String key) throws AcServiceException {
		User user = getUserByToken(authToken);
		List<Attribute> attrs = new ArrayList<Attribute>();
		if (authority != null) {
			for (Attribute a : user.getAttributes()) {
				if (authority.equals(a.getAuthority().getName())) {
					if (key != null && !key.equals(a.getKey()))
						continue;
					attrs.add(a);
				}
			}
		} else {
			attrs.addAll(user.getAttributes());
		}
		return attrs;
	}

	/**
	 * Returns all the authorities present in the system
	 * 
	 * @return the collection of authorities of an empty list if there aren't
	 * @throws AcServiceException
	 */
	@Override
	public Collection<Authority> getAuthorities() throws AcServiceException {
		return dao.readAuthorities();
	}

	/**
	 * Returns an authority given its name
	 * 
	 * @param name
	 *            name of the authority
	 * @return the authority object if exists, null otherwise
	 * @throws AcServiceException
	 */
	@Override
	public Authority getAuthorityByName(String name) throws AcServiceException {
		return dao.readAuthorityByName(name);
	}

	/**
	 * Returns authority given its url
	 * 
	 * @param name
	 *            the url of authority
	 * @return authority object if exists, null otherwise
	 * @throws AcServiceException
	 */
	@Override
	public Authority getAuthorityByUrl(String name) throws AcServiceException {
		return dao.readAuthorityByUrl(name);
	}

	/**
	 * Creates an authority
	 * 
	 * @param auth
	 *            the authority to create
	 * @throws AcServiceException
	 */
	@Override
	public void createAuthority(Authority auth) throws AcServiceException {
		dao.create(auth);
	}

	/**
	 * Returns an unique token
	 * 
	 * @return the token generated
	 * @throws AcServiceException
	 */

	public String generateAuthToken() throws AcServiceException {
		return dao.generateAuthToken();
	}

	/**
	 * Updates data of an authority
	 * 
	 * @param auth
	 *            the new data of authority
	 * @throws AcServiceException
	 */
	@Override
	public void updateAuthority(Authority auth) throws AcServiceException {
		dao.update(auth);
	}

	/**
	 * Sets an attribute of a user. Attribute is appended to user attributes if
	 * it's not present, attribute value is updated otherwise
	 * 
	 * @param userId
	 *            id of the user owner of attribute
	 * @param attribute
	 *            attribute to set
	 * @throws AcServiceException
	 * @throws IllegalArgumentException
	 *             if user doesn't exist or attribute parameter is null
	 */
	@Override
	public void setAttribute(long userId, Attribute attribute)
			throws AcServiceException {
		if (attribute == null) {
			throw new IllegalArgumentException("Attribute is not specified");

		}

		User user = dao.readUser(userId);
		if (user == null) {
			throw new IllegalArgumentException(
					"The user with that ID doesn't exist");
		}

		int indexAttribute = user.getAttributes().indexOf(attribute);

		if (indexAttribute == -1) {
			user.getAttributes().add(attribute);
		} else {
			user.getAttributes().get(indexAttribute)
					.setValue(attribute.getValue());
		}
		dao.update(user);
	}

	/**
	 * Returns true if the user can access the specified resource. Currently, the resource should 
	 * correspond to the entityId as stored in the Social Engine entity space.
	 * 
	 * @return true if the read permission is granted for the resource
	 * @throws AcServiceException
	 */

	@Override
	public boolean canReadResource(String authToken, String resourceId) throws AcServiceException {
		User user = getUserByToken(authToken);
		if (user == null) {
			throw new AcServiceException("User matching the token "+authToken +" is not found.");
		}
		try {
			return socialManager.checkResourceAccess(user.getSocialId(), Long.parseLong(resourceId));
		} catch (NumberFormatException e) {
			throw new AcServiceException("Resource ID should be a number.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AcServiceException("Error reading the access rights: "+e.getMessage());
		}
	}

	/**
	 * Returns true if the user has no or only one 'anonymous' authority attributes.
	 */
	@Override
	public boolean isAnonymousUser(String authToken) throws AcServiceException {
		User user = getUserByToken(authToken);
		if (user == null) {
			throw new AcServiceException("User matching the token "+authToken +" is not found.");
		}
		if (user.getAttributes() != null) {
			for (Attribute a : user.getAttributes()) {
				if (!a.getAuthority().getName().equals(AUTHORITY_ANONYMOUS)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public App getAppByToken(String appToken) throws AcServiceException {
		return dao.readApp(appToken);
	}
	

	
}
