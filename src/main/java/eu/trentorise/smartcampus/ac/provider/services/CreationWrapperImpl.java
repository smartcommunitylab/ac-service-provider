package eu.trentorise.smartcampus.ac.provider.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineException;
import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineManager;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;

/**
 * The class is the implementation of CreationWrapper interface. It permits to
 * create user in a transactional operation.
 * 
 * @author mirko perillo
 * 
 */
@Component
public class CreationWrapperImpl implements CreationWrapper {

	@Autowired
	private SocialEngineManager socialEngineManager;

	@Autowired
	private AcDao dao;

	/**
	 * Creates a user. Operation is transactional, user is persisted, user space
	 * is created in social engine and this is associated to user by the social
	 * id
	 * 
	 * @param user
	 *            the user to create
	 */

	@Transactional
	public void create(User user) {
		try {
			// creation of user in service and setting id result of creation
			user.setId(dao.create(user));
			user.setSocialId(socialEngineManager.createUser(user));
			dao.update(user);
		} catch (SocialEngineException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
