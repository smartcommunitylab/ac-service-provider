/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.ac.provider.model.AcObject;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;

/**
 * 
 * @author Viktor Pravdin
 */
@Repository("acDaoMemory")
public class AcDaoMemoryImpl implements AcDao {

	final Map<Class<?>, AtomicLong> ids = new HashMap<Class<?>, AtomicLong>();
	final Map<Class<?>, Map<Long, ? extends AcObject>> cache = new ConcurrentHashMap<Class<?>, Map<Long, ? extends AcObject>>();

	@Override
	public <T extends AcObject> void create(T acObj) {
		acObj.setId(getNextId(acObj.getClass()));
		Map<Long, T> map;
		synchronized (cache) {
			map = (Map<Long, T>) cache.get(acObj.getClass());
			if (map == null) {
				map = new ConcurrentHashMap();
				cache.put(acObj.getClass(), map);
			}
		}
		map.put(acObj.getId(), acObj);
		if (acObj instanceof User) {
			User user = (User) acObj;
			for (Attribute a : user.getAttributes()) {
				create(a.getAuthority());
			}
		}
	}

	@Override
	public <T extends AcObject> void update(T acObj) {
		Map<Long, T> map = (Map<Long, T>) cache.get(acObj.getClass());
		if (map == null || map.get(acObj.getId()) == null) {
			throw new IllegalArgumentException(
					"The object can't be updated because it doesn't exist");
		}
		map.put(acObj.getId(), acObj);
		if (acObj instanceof User) {
			User user = (User) acObj;
			for (Attribute a : user.getAttributes()) {
				create(a.getAuthority());
			}
		}
	}

	@Override
	public <T extends AcObject> boolean delete(T acObj) {
		boolean result = false;
		Map<Long, T> map = (Map<Long, T>) cache.get(acObj.getClass());
		if (map != null) {
			result = map.remove(acObj.getId()) != null;
		}
		return result;
	}

	@Override
	public User readUser(long id) {
		User user = null;
		Map<Long, User> map = (Map<Long, User>) cache.get(User.class);
		if (map != null) {
			user = map.get(id);
		}
		return user;
	}

	private Long getNextId(Class<?> cls) {
		AtomicLong id;
		synchronized (ids) {
			id = ids.get(cls);
			if (id == null) {
				id = new AtomicLong();
				ids.put(cls, id);
			}
		}
		return id.getAndIncrement();
	}

	@Override
	public User readUser(String authToken) {
		User result = null;
		Map<Long, User> map = (Map<Long, User>) cache.get(User.class);
		if (map != null) {
			for (User user : map.values()) {
				if (authToken.equals(user.getAuthToken())) {
					result = user;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<User> readUsers(List<Attribute> attributes) {
		List<User> list = new ArrayList<User>();
		Map<Long, User> map = (Map<Long, User>) cache.get(User.class);
		if (map != null) {
			for (User user : map.values()) {
				List<Attribute> userAttrs = user.getAttributes();
				if (userAttrs.containsAll(attributes)) {
					list.add(user);
				}
			}
		}
		return list;
	}

	@Override
	public Collection<Authority> readAuthorities() {
		if (cache.get(Authority.class) != null) {
			return (Collection<Authority>) cache.get(Authority.class).values();
		}
		return Collections.emptyList();
	}

	@Override
	public Authority readAuthorityByName(String name) {
		for (Authority auth : readAuthorities()) {
			if (auth.getName().equals(name)) {
				return auth;
			}
		}
		return null;
	}

	public Authority readAuthorityByUrl(String url) {
		for (Authority auth : readAuthorities()) {
			if (auth.getRedirectUrl().equals(url)) {
				return auth;
			}
		}
		return null;
	}

	@Override
	public User readUserBySocialId(long id) {
		throw new IllegalArgumentException("Method not implemented");
	}
}
