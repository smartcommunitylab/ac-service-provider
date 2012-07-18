/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.repository;

import java.util.Collection;
import java.util.List;

import eu.trentorise.smartcampus.ac.provider.model.AcObject;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;

/**
 * 
 * @author Viktor Pravdin
 */

public interface AcDao {

	public <T extends AcObject> void create(T acObj);

	public <T extends AcObject> void update(T acObj);

	public <T extends AcObject> boolean delete(T acObj);

	public User readUser(long id);

	public User readUser(String authToken);

	public List<User> readUsers(List<Attribute> attributes);

	public User readUserBySocialId(long id);

	Collection<Authority> readAuthorities();

	Authority readAuthorityByName(String name);

	Authority readAuthorityByUrl(String url);
}
