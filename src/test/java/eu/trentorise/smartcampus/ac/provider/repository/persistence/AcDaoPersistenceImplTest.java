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

package eu.trentorise.smartcampus.ac.provider.repository.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.trentorise.smartcampus.ac.provider.AcProviderService;
import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AuthorityEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.UserEntity;
import eu.trentorise.smartcampus.ac.provider.services.AcProviderServiceImpl;

/**
 * Unit test for simple App.
 */

public class AcDaoPersistenceImplTest

{
	private static final String PERSISTENCE_UNIT_NAME = "persistence-unit";

	private static final String AUTH_NAME_PRESENT = "auth1";
	private static final String AUTH_NAME_NOT_PRESENT = "dummy_auth";

	private static final String AUTH_URL_PRESENT = "url_auth1";
	private static final String AUTH_URL_NOT_PRESENT = "dummy_auth";

	private static final String TOKEN_PRESENT = "token_1";
	private static final String TOKEN_NOT_PRESENT = "dummy_token";

	private static AcDao dao;
	private static AcProviderService serviceImpl;
	private static EntityManagerFactory emf;
	private static EntityManager em;

	@BeforeClass
	public static void setup() {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/config.xml");
		dao = ctx.getBean("acPersistenceDao", AcDao.class);
		serviceImpl = ctx.getBean(AcProviderServiceImpl.class);
	}

	@Before
	public void setupEnvironment() {
		loadData();
	}

	@After
	public void cleanEnvironment() {
		cleanDb();
	}

	@Test
	public void searchUserByToken() {
		User u = dao.readUser(TOKEN_PRESENT);
		Assert.assertNotNull(u);
		u = dao.readUser(TOKEN_NOT_PRESENT);
		Assert.assertNull(u);
	}

	@Test
	public void searchUserById() {
		User u = dao.readUser(2);
		Assert.assertNotNull(u);
		u = dao.readUser(1000);
		Assert.assertNull(u);
	}

	@Test
	public void searchUserBySocialId() {
		Assert.assertTrue(dao.readUserBySocialId((long) 1001) != null);
		Assert.assertTrue(dao.readUserBySocialId((long) 1) == null);
	}

	@Test
	public void searchUserByAttr() {
		Assert.assertTrue(!dao.readUsers(loadAttributes()).isEmpty());

		List<Attribute> attrs = new ArrayList<Attribute>();

		Authority auth = new Authority();
		auth.setName("FAKE_TrentoRise");
		auth.setRedirectUrl("http://www.trentorise.eu");
		Attribute a = new Attribute();
		a.setKey("projectName");
		a.setValue("SC");
		a.setAuthority(auth);
		attrs.add(a);

		Assert.assertTrue(dao.readUsers(attrs).isEmpty());

	}

	@Test
	public void searchAuthorities() {
		Assert.assertTrue(dao.readAuthorities().size() > 0);
	}

	@Test
	public void searchAuthorityByName() {
		Assert.assertNotNull(dao.readAuthorityByName(AUTH_NAME_PRESENT));
		Assert.assertNull(dao.readAuthorityByName(AUTH_NAME_NOT_PRESENT));

	}

	@Test
	public void searchAuthorityByUrl() {
		Assert.assertNotNull(dao.readAuthorityByUrl(AUTH_URL_PRESENT));
		Assert.assertNull(dao.readAuthorityByUrl(AUTH_URL_NOT_PRESENT));
	}

	public void testCreateUser() {

		User user = new User();
		user.setAuthToken("token_test");
		user.setExpTime((long) 3600);
		user.setSocialId((long) 1004);

		// test user definition

		Authority auth1 = new Authority();
		auth1.setName("auth1");
		auth1.setRedirectUrl("url_auth1");

		Attribute attr = new Attribute();
		attr.setKey("projectName");
		attr.setValue("SC");

		attr.setAuthority(auth1);

		List<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(attr);
		user.setAttributes(attrs);

		Assert.assertNull(dao.readUser("token_test"));
		dao.create(user);
		Assert.assertNotNull(dao.readUser("token_test"));

		user = new User();
		user.setAuthToken("token_test1");
		user.setExpTime((long) 3600);
		user.setSocialId((long) 1004);

		try {
			dao.create(user);
			Assert.fail("Eception not throw");
		} catch (IllegalArgumentException e) {

		}

	}

	public void testCreateAuthority() {
		Authority auth = new Authority();
		auth.setName("new_auth");
		auth.setRedirectUrl("new_url");

		Assert.assertNull(dao.readAuthorityByName("new_auth"));
		dao.create(auth);
		Assert.assertNotNull(dao.readAuthorityByName("new_auth"));

		auth = new Authority();
		auth.setName("new_auth1");
		auth.setRedirectUrl(AUTH_URL_PRESENT);
		try {
			dao.create(auth);
			Assert.fail("Exception not throw");
		} catch (IllegalArgumentException e) {
		}

		Assert.assertNull(dao.readAuthorityByName("new_auth1"));

		auth = new Authority();
		auth.setName(AUTH_NAME_PRESENT);
		auth.setRedirectUrl(AUTH_URL_NOT_PRESENT);
		try {
			dao.create(auth);
			Assert.fail("Exception not throw");
		} catch (IllegalArgumentException e) {
		}

		Assert.assertNull(dao.readAuthorityByName(AUTH_URL_NOT_PRESENT));

	}

	@Test
	public void updateAuthority() {
		Authority auth = new Authority();
		auth.setName("auth1");
		auth.setRedirectUrl("www.smartcampus.eu");
		auth.setId((long) 1);
		Assert.assertTrue(dao.readAuthorityByUrl("url_auth1") != null
				&& dao.readAuthorityByUrl("www.smartcampus.eu") == null);
		dao.update(auth);
		Assert.assertTrue(dao.readAuthorityByUrl("www.smartcampus.eu") != null
				&& dao.readAuthorityByUrl("url_auth1") == null);
	}

	@Test
	public void updateUserAttribute() {
		User u = dao.readUser("token_3");

		// check on attribute update
		List<Attribute> list = u.getAttributes();
		Assert.assertTrue(dao.readUsers(list).size() > 0);

		List<Attribute> cloneList = new ArrayList<Attribute>(list);

		// new attribute authority exists
		Authority newAuth = new Authority();
		newAuth.setName("auth1");
		newAuth.setRedirectUrl("url_auth1");

		Attribute newA = new Attribute();
		newA.setKey("key1");
		newA.setValue("valueA");
		newA.setAuthority(newAuth);

		cloneList.add(newA);

		cloneList.get(0).setValue("SMARTCAMPUS");

		Assert.assertTrue(dao.readUsers(cloneList).size() == 0);

		u.setAttributes(cloneList);
		dao.update(u);

		Assert.assertTrue(dao.readUsers(cloneList).size() > 0);

	}

	@Test
	public void updateUserAttributeNoAuth() {
		User u = dao.readUser("token_3");

		List<Attribute> list = u.getAttributes();
		List<Attribute> cloneList = new ArrayList<Attribute>(list);

		// new attribute authority not exist
		Authority newAuth = new Authority();
		newAuth.setName("newAuthority");
		newAuth.setRedirectUrl("url_authority");

		Attribute newA = new Attribute();
		newA.setKey("key1");
		newA.setValue("valueA");
		newA.setAuthority(newAuth);

		cloneList.add(newA);

		Assert.assertTrue(dao.readUsers(list).size() > 0);
		Assert.assertTrue(dao.readUsers(cloneList).size() == 0);
		Assert.assertTrue(dao.readAuthorityByName("newAuthority") == null);

		u.setAttributes(cloneList);
		try {
			dao.update(u);
			Assert.fail("IllegalArgumentException not throw");
		} catch (IllegalArgumentException e) {

		}
		Assert.assertTrue(dao.readAuthorityByName("newAuthority") == null);
		Assert.assertTrue(dao.readUsers(cloneList).size() == 0);

	}

	@Test
	public void updateUser() {
		User u = dao.readUser("token_3");
		u.setAuthToken("new_token_3");
		long id = u.getId();
		Assert.assertTrue(dao.readUser("token_3") != null
				&& dao.readUser("new_token_3") == null);
		dao.update(u);
		Assert.assertTrue(dao.readUser("token_3") == null
				&& dao.readUser("new_token_3") != null
				&& id == dao.readUser("new_token_3").getId());
	}

	@Test
	public void removeUser() {
		User user = new User();
		user.setId((long) 1);

		Assert.assertNotNull(dao.readUser((long) 1));
		Assert.assertTrue(dao.delete(user));
		Assert.assertNull(dao.readUser((long) 1));
	}

	@Test
	public void sessionToken() throws AcServiceException, InterruptedException {
		User user = new User();
		user.setAuthToken(TOKEN_NOT_PRESENT);
		user.setExpTime(1000);
		user.setSocialId(System.currentTimeMillis());
		user.setAttributes(Collections.<Attribute> emptyList());
		dao.create(user);
		Assert.assertNotNull(dao.readUser((long) 1));
		
		String token = dao.createSessionToken(1L, System.currentTimeMillis()+1000);
		Assert.assertTrue(serviceImpl.isValidUser(token));
		User u = dao.readUser(token);
		Assert.assertEquals(u.getAuthToken(), token);
		Thread.sleep(1000);
		Assert.assertFalse(serviceImpl.isValidUser(token));
	}
	
	private void loadData() {
		loadAuthorities();
		loadUsers();
	}

	private void loadUsers() {
		User user = new User();
		user.setAuthToken(TOKEN_PRESENT);
		user.setExpTime(1000);
		user.setSocialId((long) 1001);
		user.setAttributes(Collections.<Attribute> emptyList());
		dao.create(user);
		System.out.println("Created " + user);

		user = new User();
		user.setAuthToken("token_2");
		user.setExpTime(1000);
		user.setSocialId((long) 1002);
		user.setAttributes(loadAttributes());
		dao.create(user);
		System.out.println("Created " + user);

		User u = new User();
		u.setAuthToken("token_3");
		u.setExpTime((long) 3600);
		u.setSocialId((long) 1003);
		u.setAttributes(loadAttributes());
		dao.create(u);
		System.out.println("Created " + u);
	}

	private static List<Attribute> loadAttributes() {
		List<Attribute> attrs = new ArrayList<Attribute>();

		Authority auth = new Authority();
		auth.setName("TrentoRise");
		auth.setRedirectUrl("http://www.trentorise.eu");
		Attribute a = new Attribute();
		a.setKey("projectName");
		a.setValue("SC");
		a.setAuthority(auth);

		attrs.add(a);

		return attrs;
	}

	private void loadAuthorities() {

		Authority auth = new Authority();
		auth.setName(AUTH_NAME_PRESENT);
		auth.setRedirectUrl(AUTH_URL_PRESENT);
		dao.create(auth);
		System.out.println("Created " + auth);
		auth = new Authority();
		auth.setName("a11");
		auth.setRedirectUrl("u11");
		dao.create(auth);
		System.out.println("Created " + auth);
		auth = new Authority();
		auth.setName("TrentoRise");
		auth.setRedirectUrl("http://www.trentorise.eu");
		dao.create(auth);
		System.out.println("Created " + auth);
	}

	@SuppressWarnings("unchecked")
	private static void cleanDb() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("from UserEntity");
		List<UserEntity> results = q.getResultList();

		for (UserEntity temp : results) {
			em.remove(temp);
		}

		q = em.createQuery("from AuthorityEntity");
		List<AuthorityEntity> res = q.getResultList();

		for (AuthorityEntity temp : res) {
			em.remove(temp);
		}
		em.getTransaction().commit();

		em.close();
		emf.close();
	}

}
