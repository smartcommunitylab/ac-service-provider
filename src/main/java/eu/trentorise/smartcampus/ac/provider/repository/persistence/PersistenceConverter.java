package eu.trentorise.smartcampus.ac.provider.repository.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AttributeEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AuthorityEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.UserEntity;

/**
 * The class performs the conversion from and to model classes and persistence
 * model classes
 * 
 * @author mirko perillo
 * 
 */
public class PersistenceConverter {

	public static UserEntity fromUser(User u, boolean create) {
		UserEntity userEntity = new UserEntity();
		try {
			if (!create) {
				userEntity.setId(u.getId());
			}
			userEntity.setAuthToken(u.getAuthToken());
			userEntity.setExpTime(u.getExpTime());
			userEntity.setSocialId(u.getSocialId());
			userEntity.setAttributeEntities(new HashSet<AttributeEntity>(
					fromAttribute(u.getAttributes(), create)));
		} catch (NullPointerException e) {
			userEntity = null;
		}

		return userEntity;
	}

	public static List<AttributeEntity> fromAttribute(List<Attribute> al,
			boolean create) {
		List<AttributeEntity> attrEntityList = new ArrayList<AttributeEntity>();
		try {
			for (Attribute temp : al) {
				attrEntityList.add(fromAttribute(temp, create));
			}
		} catch (NullPointerException e) {
			attrEntityList = null;
		}
		return attrEntityList;
	}

	public static AttributeEntity fromAttribute(Attribute a, boolean create) {
		AttributeEntity attrEntity = new AttributeEntity();
		try {
			attrEntity.setKey(a.getKey());
			attrEntity.setValue(a.getValue());
			attrEntity.setAuthority(fromAuthority(a.getAuthority(), create));
		} catch (NullPointerException e) {
			attrEntity = null;
		}
		return attrEntity;
	}

	public static AuthorityEntity fromAuthority(Authority a, boolean create) {
		AuthorityEntity authEntity = new AuthorityEntity();
		try {
			if (!create) {
				authEntity.setId(a.getId());
			}
			authEntity.setName(a.getName());
			authEntity.setRedirectUrl(a.getRedirectUrl());
		} catch (NullPointerException e) {
			authEntity = null;
		}
		return authEntity;
	}

	public static User toUser(UserEntity ue) {
		User u = new User();
		try {
			u.setId(ue.getId());
			u.setAuthToken(ue.getAuthToken());
			u.setExpTime(ue.getExpTime());
			u.setSocialId(ue.getSocialId());
			u.setAttributes(toAttribute(new ArrayList<AttributeEntity>(ue
					.getAttributeEntities())));
		} catch (NullPointerException e) {
			u = null;
		}
		return u;
	}

	public static List<User> toUser(List<UserEntity> uel) {
		List<User> list = new ArrayList<User>();
		try {
			for (UserEntity temp : uel) {
				list.add(toUser(temp));
			}
		} catch (NullPointerException e) {
			list = null;
		}
		return list;
	}

	public static Attribute toAttribute(AttributeEntity ae) {
		Attribute attr = new Attribute();
		try {
			attr.setKey(ae.getKey());
			attr.setValue(ae.getValue());
			attr.setAuthority(toAuthority(ae.getAuthority()));
		} catch (NullPointerException e) {
			attr = null;
		}
		return attr;
	}

	public static List<Attribute> toAttribute(List<AttributeEntity> ael) {
		List<Attribute> attrList = new ArrayList<Attribute>();
		try {
			for (AttributeEntity temp : ael) {
				attrList.add(toAttribute(temp));
			}
		} catch (NullPointerException e) {
			attrList = null;
		}

		return attrList;
	}

	public static List<Authority> toAuthority(List<AuthorityEntity> ael) {
		List<Authority> list = new ArrayList<Authority>();
		try {
			for (AuthorityEntity temp : ael) {
				list.add(toAuthority(temp));
			}
		} catch (NullPointerException e) {
			list = null;
		}
		return list;
	}

	public static Authority toAuthority(AuthorityEntity ae) {
		Authority a = new Authority();
		try {
			a.setId(ae.getId());
			a.setName(ae.getName());
			a.setRedirectUrl(ae.getRedirectUrl());
		} catch (NullPointerException e) {
			a = null;
		}

		return a;
	}

	public static boolean isSame(AttributeEntity ae, Attribute a) {
		try {
			return ae.getKey().equals(a.getKey())
					&& isSame(ae.getAuthority(), a.getAuthority());
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean isSame(AuthorityEntity ae, Authority a) {
		if ((ae.getName() == null) ? (a.getName() != null) : !ae.getName()
				.equals(a.getName())) {
			return false;
		}
		return true;
	}
}
