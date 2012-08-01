package eu.trentorise.smartcampus.ac.provider.repository.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.ac.provider.managers.SocialEngineManager;
import eu.trentorise.smartcampus.ac.provider.model.AcObject;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AttributeEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AuthorityEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.UserEntity;

@Transactional
@Repository("acPersistenceDao")
public class AcDaoPersistenceImpl implements AcDao {

	EntityManagerFactory emf;
	@PersistenceContext
	EntityManager em;

	@Autowired
	SocialEngineManager socialEngineManager;

	public AcDaoPersistenceImpl(String pu) {
		emf = Persistence.createEntityManagerFactory(pu);
		em = emf.createEntityManager();

	}

	public AcDaoPersistenceImpl() {

	}

	@Override
	public <T extends AcObject> long create(T acObj) {

		long id = 0;
		if (acObj != null) {
			if (acObj instanceof User) {
				if (readUser(((User) acObj).getAuthToken()) != null) {
					throw new IllegalArgumentException(
							"This authToken is already present");
				}
				if (((User) acObj).getSocialId() != 0
						&& readUserBySocialId(((User) acObj).getSocialId()) != null) {
					throw new IllegalArgumentException(
							"This socialId is already present");
				}

				// check authorities
				UserEntity toSave = PersistenceConverter.fromUser((User) acObj,
						true);
				try {
					checkAuthorities(toSave);
				} catch (IllegalArgumentException e) {
					throw e;
				}
				em.persist(toSave);
				id = toSave.getId();
			} else if (acObj instanceof Authority) {
				if (readAuthorityByName(((Authority) acObj).getName()) != null) {
					throw new IllegalArgumentException(
							"This name is already present");
				}
				if (readAuthorityByUrl(((Authority) acObj).getRedirectUrl()) != null) {
					throw new IllegalArgumentException(
							"This redirectUrl is already present");
				}
				AuthorityEntity toSave = PersistenceConverter.fromAuthority(
						(Authority) acObj, true);
				em.persist(toSave);
				id = toSave.getId();
			}
		}

		return id;
	}

	@Override
	public <T extends AcObject> void update(T acObj) {
		if (acObj != null) {
			if (acObj instanceof User) {
				UserEntity ue = em.find(UserEntity.class, acObj.getId());
				if (ue == null) {
					throw new IllegalArgumentException(
							"The object can't be updated because it doesn't exist");
				} else {
					User u = (User) acObj;
					User userPresent = readUser(u.getAuthToken());
					User userSocialIdPresent = readUserBySocialId(u
							.getSocialId());
					if (userPresent != null
							&& !userPresent.getId().equals(u.getId())) {
						throw new IllegalArgumentException(
								"The object can't be updated because authToken is already present");
					}
					if (userSocialIdPresent != null
							&& !userSocialIdPresent.getId().equals(u.getId())) {
						throw new IllegalArgumentException(
								"The object can't be updated because socialId is already present");
					}
					ue.setAuthToken(u.getAuthToken());
					ue.setExpTime(u.getExpDate());
					ue.setSocialId(u.getSocialId());
					try {
						ue = updateAttributes(ue, u);
					} catch (IllegalArgumentException e) {
						throw e;
					}
					em.merge(ue);

				}
			} else if (acObj instanceof Authority) {

				AuthorityEntity ae = em.find(AuthorityEntity.class,
						acObj.getId());
				if (ae == null) {
					throw new IllegalArgumentException(
							"The object can't be updated because it doesn't exist");
				} else {
					Authority a = (Authority) acObj;
					Authority authNamePresent = readAuthorityByName(a.getName());
					Authority authUrlPresent = readAuthorityByUrl(a
							.getRedirectUrl());

					if (authNamePresent != null
							&& !authNamePresent.getId().equals(a.getId())) {
						throw new IllegalArgumentException(
								"The object can't be updated because name is already present");
					}
					if (authUrlPresent != null
							&& !authUrlPresent.getId().equals(a.getId())) {
						throw new IllegalArgumentException(
								"The object can't be updated because redirectUrl is already present");
					}
					ae.setName(a.getName());
					ae.setRedirectUrl(a.getRedirectUrl());

					em.merge(ae);

				}

			}
		}

	}

	private UserEntity updateAttributes(UserEntity ue, User u)
			throws IllegalArgumentException {
		List<AttributeEntity> toAdd = new ArrayList<AttributeEntity>();
		for (Attribute a : u.getAttributes()) {
			boolean found = false;
			for (AttributeEntity ae : ue.getAttributeEntities()) {
				if (found = PersistenceConverter.isSame(ae, a)) {
					ae.setValue(a.getValue());
					break;
				}
			}
			// if it is a new attribute, it's persisted and added to
			// attribute list
			if (!found) {
				AttributeEntity newAe = new AttributeEntity();
				newAe.setKey(a.getKey());
				newAe.setValue(a.getValue());
				AuthorityEntity auth = findAuthorityByName(a.getAuthority()
						.getName());
				if (auth == null) {
					throw new IllegalArgumentException(
							"The object can't be updated because one or more attributes referenced to an nonexistent authority");
				} else {
					newAe.setAuthority(auth);
					//
					// em.persist(newAe);
					//
					toAdd.add(newAe);
				}
			}
		}
		ue.getAttributeEntities().addAll(toAdd);
		return ue;
	}

	private AuthorityEntity findAuthorityByName(String name) {
		Query query = em
				.createQuery("from AuthorityEntity a where a.name= :name");
		query.setParameter("name", name);
		try {
			return (AuthorityEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private UserEntity findUserById(long id) {
		Query query = em.createQuery("from UserEntity a where a.id= :id");
		query.setParameter("id", id);
		try {
			return (UserEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private void checkAuthorities(UserEntity ue)
			throws IllegalArgumentException {
		try {
			for (AttributeEntity ae : ue.getAttributeEntities()) {
				AuthorityEntity authEntity = null;
				if ((authEntity = findAuthorityByName(ae.getAuthority()
						.getName())) == null) {
					throw new IllegalArgumentException(
							"Attribute referenced to a nonexistence authority");
				} else {
					ae.setAuthority(authEntity);
				}
			}
		} catch (NullPointerException e) {
		}
	}

	@Override
	public <T extends AcObject> boolean delete(T acObj) {

		if (acObj != null) {
			Class objectType = null;
			if (acObj instanceof User) {
				objectType = UserEntity.class;
			} else if (acObj instanceof Authority) {
				objectType = AuthorityEntity.class;
			}

			try {
				// remove all attribute of user
				// the cascade on new added attribute in update user doesn t
				// work fine
				if (acObj instanceof User) {
					UserEntity ue = findUserById(acObj.getId());
					if (ue != null) {
						for (AttributeEntity attr : ue.getAttributeEntities()) {
							em.remove(attr);
						}
					}
				}
				em.remove(em.find(objectType, acObj.getId()));

			} catch (PersistenceException e) {
				throw new IllegalArgumentException(
						"the object referenced to other objects, cannot be removed");
			}
			return true;
		}
		return false;
	}

	@Override
	public User readUser(long id) {
		try {
			return PersistenceConverter.toUser((UserEntity) em.find(
					UserEntity.class, id));
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public User readUser(String authToken) {
		try {
			Query query = em
					.createQuery("from UserEntity u where u.authToken= :authToken");
			query.setParameter("authToken", authToken);
			try {
				return PersistenceConverter.toUser((UserEntity) query
						.getSingleResult());
			} catch (NoResultException e) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public List<User> readUsers(List<Attribute> attributes) {
		Query query = em.createQuery("from UserEntity u");
		List<User> result = PersistenceConverter.toUser(query.getResultList());
		List<User> filtered = new ArrayList<User>();
		for (User temp : result) {
			if (temp.getAttributes().containsAll(attributes)) {
				filtered.add(temp);
			}
		}
		return filtered;
	}

	@Override
	public Collection<Authority> readAuthorities() {
		Query query = em.createQuery("from AuthorityEntity a");
		return PersistenceConverter.toAuthority(query.getResultList());

	}

	@Override
	public Authority readAuthorityByName(String name) {
		try {
			Query query = em
					.createQuery("from AuthorityEntity a where a.name = :name");
			query.setParameter("name", name);
			try {
				return PersistenceConverter.toAuthority((AuthorityEntity) query
						.getSingleResult());
			} catch (NoResultException e) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public Authority readAuthorityByUrl(String url) {
		try {
			Query query = em
					.createQuery("from AuthorityEntity a where a.redirectUrl = :url");
			query.setParameter("url", url);
			try {
				return PersistenceConverter.toAuthority((AuthorityEntity) query
						.getSingleResult());
			} catch (NoResultException e) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public User readUserBySocialId(long socialId) {
		try {
			Query query = em
					.createQuery("from UserEntity u where u.socialId= :socialId");
			query.setParameter("socialId", socialId);
			try {
				return PersistenceConverter.toUser((UserEntity) query
						.getSingleResult());
			} catch (NoResultException e) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

}