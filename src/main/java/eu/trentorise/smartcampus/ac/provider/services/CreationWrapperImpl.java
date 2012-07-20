package eu.trentorise.smartcampus.ac.provider.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineException;
import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineManager;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;

@Component
public class CreationWrapperImpl implements CreationWrapper {

	@Autowired
	private SocialEngineManager socialEngineManager;

	@Autowired
	private AcDao dao;

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
