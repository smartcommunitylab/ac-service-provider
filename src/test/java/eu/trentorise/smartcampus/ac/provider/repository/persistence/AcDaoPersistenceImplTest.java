package eu.trentorise.smartcampus.ac.provider.repository.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.repository.AcDao;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.AuthorityEntity;
import eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel.UserEntity;

/**
 * Unit test for simple App.
 */

public class AcDaoPersistenceImplTest extends TestCase

{
	private static final String PERSISTENCE_UNIT_NAME = "persistence-unit-mysql";

	private static final String AUTH_NAME_PRESENT = "auth1";
	private static final String AUTH_NAME_NOT_PRESENT = "dummy_auth";

	private static final String AUTH_URL_PRESENT = "url_auth1";
	private static final String AUTH_URL_NOT_PRESENT = "dummy_auth";

	private static final String TOKEN_PRESENT = "token_1";
	private static final String TOKEN_NOT_PRESENT = "dummy_token";

	private AcDao dao;
	EntityManagerFactory emf;
	EntityManager em;

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AcDaoPersistenceImplTest(String testName) {
		super(testName);
		dao = new AcDaoPersistenceImpl(PERSISTENCE_UNIT_NAME);

		loadData();

	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AcDaoPersistenceImplTest.class);
	}

	public void testSearchUserByToken() {

		User u = dao.readUser(TOKEN_PRESENT);

		assertNotNull(u);

		u = dao.readUser(TOKEN_NOT_PRESENT);
		assertNull(u);
	}

	public void testSearchUserById() {
		User u = dao.readUser(2);

		assertNotNull(u);

		u = dao.readUser(1000);
		assertNull(u);
	}

	public void testSearchUserBySocialId() {
		assertTrue(dao.readUserBySocialId((long) 1001) != null);
		assertTrue(dao.readUserBySocialId((long) 1) == null);
	}

	public void testSearchUserByAttr() {
		assertTrue(!dao.readUsers(loadAttributes()).isEmpty());

		List<Attribute> attrs = new ArrayList<Attribute>();

		Authority auth = new Authority();
		auth.setName("FAKE_TrentoRise");
		auth.setRedirectUrl("http://www.trentorise.eu");
		Attribute a = new Attribute();
		a.setKey("projectName");
		a.setValue("SC");
		a.setAuthority(auth);
		attrs.add(a);

		assertTrue(dao.readUsers(attrs).isEmpty());

	}

	public void testSearchAuthorities() {
		assertTrue(dao.readAuthorities().size() > 0);
	}

	public void testSearchAuthorityByName() {
		assertNotNull(dao.readAuthorityByName(AUTH_NAME_PRESENT));
		assertNull(dao.readAuthorityByName(AUTH_NAME_NOT_PRESENT));

	}

	public void testSearchAuthorityByUrl() {
		assertNotNull(dao.readAuthorityByUrl(AUTH_URL_PRESENT));
		assertNull(dao.readAuthorityByUrl(AUTH_URL_NOT_PRESENT));
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

		assertNull(dao.readUser("token_test"));
		dao.create(user);
		assertNotNull(dao.readUser("token_test"));

		user = new User();
		user.setAuthToken("token_test1");
		user.setExpTime((long) 3600);
		user.setSocialId((long) 1004);

		try {
			dao.create(user);
			fail("Eception not throw");
		} catch (IllegalArgumentException e) {

		}

	}

	public void testCreateAuthority() {
		Authority auth = new Authority();
		auth.setName("new_auth");
		auth.setRedirectUrl("new_url");

		assertNull(dao.readAuthorityByName("new_auth"));
		dao.create(auth);
		assertNotNull(dao.readAuthorityByName("new_auth"));

		auth = new Authority();
		auth.setName("new_auth1");
		auth.setRedirectUrl(AUTH_URL_PRESENT);
		try {
			dao.create(auth);
			fail("Exception not throw");
		} catch (IllegalArgumentException e) {
		}

		assertNull(dao.readAuthorityByName("new_auth1"));

		auth = new Authority();
		auth.setName(AUTH_NAME_PRESENT);
		auth.setRedirectUrl(AUTH_URL_NOT_PRESENT);
		try {
			dao.create(auth);
			fail("Exception not throw");
		} catch (IllegalArgumentException e) {
		}

		assertNull(dao.readAuthorityByName(AUTH_URL_NOT_PRESENT));

	}

	public void testUpdateAuthority() {
		Authority auth = new Authority();
		auth.setName("auth1");
		auth.setRedirectUrl("www.smartcampus.eu");
		auth.setId((long) 1);
		assertTrue(dao.readAuthorityByUrl("url_auth1") != null
				&& dao.readAuthorityByUrl("www.smartcampus.eu") == null);
		dao.update(auth);
		assertTrue(dao.readAuthorityByUrl("www.smartcampus.eu") != null
				&& dao.readAuthorityByUrl("url_auth1") == null);
	}

	public void testUpdateUserAttribute() {
		User u = dao.readUser("token_3");

		// check on attribute update
		List<Attribute> list = u.getAttributes();
		assertTrue(dao.readUsers(list).size() > 0);

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

		assertTrue(dao.readUsers(cloneList).size() == 0);

		u.setAttributes(cloneList);
		dao.update(u);

		assertTrue(dao.readUsers(cloneList).size() > 0);

	}

	public void testUpdateUserAttributeNoAuth() {
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

		assertTrue(dao.readUsers(list).size() > 0);
		assertTrue(dao.readUsers(cloneList).size() == 0);
		assertTrue(dao.readAuthorityByName("newAuthority") == null);

		u.setAttributes(cloneList);
		try {
			dao.update(u);
			fail("IllegalArgumentException not throw");
		} catch (IllegalArgumentException e) {

		}
		assertTrue(dao.readAuthorityByName("newAuthority") == null);
		assertTrue(dao.readUsers(cloneList).size() == 0);

	}

	public void testUpdateUser() {
		User u = dao.readUser("token_3");
		u.setAuthToken("new_token_3");
		long id = u.getId();
		assertTrue(dao.readUser("token_3") != null
				&& dao.readUser("new_token_3") == null);
		dao.update(u);
		assertTrue(dao.readUser("token_3") == null
				&& dao.readUser("new_token_3") != null
				&& id == dao.readUser("new_token_3").getId());
	}

	public void testRemoveUser() {
		User user = new User();
		user.setId((long) 3);

		assertNotNull(dao.readUser((long) 3));
		assertTrue(dao.delete(user));
		assertNull(dao.readUser((long) 3));

	}

	public void testRemoveAuthority() {
		Authority auth = new Authority();
		auth.setId((long) 1);
		try {
			dao.delete(auth);
			fail("Exception not throw");
		} catch (IllegalArgumentException e) {

		}
		// auth = new Authority();
		// auth.setId((long) 3);
		//
		// assertNotNull(dao.readAuthorityByName("a12"));
		// assertTrue(dao.delete(auth));
		// assertNull(dao.readAuthorityByName("a12"));

		// auth = new Authority();
		// auth.setName("other_authority");
		// auth.setRedirectUrl("other_url");
		// dao.create(auth);
		// dao.delete(dao.readAuthorityByUrl("other_url"));
	}

	private void loadData() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
		loadAuthorities();
		loadUsers();
	}

	private void loadUsers() {
		em.getTransaction().begin();
		UserEntity user = new UserEntity();
		user.setAuthToken(TOKEN_PRESENT);
		user.setExpTime(1000);
		user.setSocialId((long) 1001);
		em.persist(user);
		System.out.println("Created " + user);

		user = new UserEntity();
		user.setAuthToken("token_2");
		user.setExpTime(1000);
		user.setSocialId((long) 1002);
		em.persist(user);
		System.out.println("Created " + user);
		em.getTransaction().commit();

		User u = new User();
		u.setAuthToken("token_3");
		u.setExpTime((long) 3600);
		u.setSocialId((long) 1003);
		u.setAttributes(loadAttributes());
		dao.create(u);
		System.out.println("Created " + u);
	}

	private List<Attribute> loadAttributes() {
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
		em.getTransaction().begin();

		AuthorityEntity auth = new AuthorityEntity();
		auth.setName(AUTH_NAME_PRESENT);
		auth.setRedirectUrl(AUTH_URL_PRESENT);
		em.persist(auth);
		System.out.println("Created " + auth);
		auth = new AuthorityEntity();
		auth.setName("a11");
		auth.setRedirectUrl("u11");
		em.persist(auth);
		System.out.println("Created " + auth);
		auth = new AuthorityEntity();
		auth.setName("a12");
		auth.setRedirectUrl("u12");
		em.persist(auth);
		System.out.println("Created " + auth);
		em.getTransaction().commit();

		Authority auth1 = new Authority();
		auth1.setName("TrentoRise");
		auth1.setRedirectUrl("http://www.trentorise.eu");
		dao.create(auth1);
		System.out.println("Created " + auth1);

	}

}
