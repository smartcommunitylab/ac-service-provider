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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import eu.trentorise.smartcampus.ac.provider.AcService;
import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.User;

/**
 * Implementation of the REST service for read-only AC operations.
 * 
 * @author Viktor Pravdin
 */
@Path("/")
public class RESTAcService implements AcService {

	@Autowired
	private AcProviderServiceImpl impl = null;
	@GET
    @Path("/users/me")
    @Produces({"application/xml","application/json"})
	public User getUserByToken(@HeaderParam("AUTH_TOKEN") String authToken) throws AcServiceException {
		return impl.getUserByToken(authToken);
	}
	@GET
    @Path("/users/me/validity")
    @Produces({"application/xml","application/json"})
	public boolean isValidUser(@HeaderParam("AUTH_TOKEN") String authToken) throws AcServiceException {
		return impl.isValidUser(authToken);
	}
	@GET
    @Path("/users/me/attributes")
    @Produces({"application/xml","application/json"})
	public List<Attribute> getUserAttributes(@HeaderParam("AUTH_TOKEN") String authToken,
			@QueryParam("authority") String authority, @QueryParam("key") String key) throws AcServiceException {
		return impl.getUserAttributes(authToken, authority, key);
	}
	@GET
    @Path("/resources/{id}/access")
    @Produces({"application/xml","application/json"})
	public boolean canReadResource(@HeaderParam("AUTH_TOKEN") String authToken, @PathParam("id") String resourceId) throws AcServiceException {
		return impl.canReadResource(authToken, resourceId);
	}

	@GET
    @Path("/users/{id}")
    @Produces({"application/xml","application/json"})
	public User getUserByToken(@HeaderParam("AUTH_TOKEN") String authToken, @PathParam("id") long userId) throws AcServiceException {
		return checkUser(authToken, userId);
	}

	private User checkUser(String authToken, long userId) throws AcServiceException {
		User us = getUserByToken(authToken);
		if (us == null) throw new WebApplicationException(Status.NOT_FOUND);
		if (!us.getId().equals(userId)) throw new WebApplicationException(Status.FORBIDDEN);
		return us;
	}
	@GET
    @Path("/users/{id}/validity")
    @Produces({"application/xml","application/json"})
	public boolean isValidUser(@HeaderParam("AUTH_TOKEN") String authToken, @PathParam("id") long userId) throws AcServiceException {
		return isValidUser(authToken);
	}
	@GET
    @Path("/users/{id}/attributes")
    @Produces({"application/xml","application/json"})
	public List<Attribute> getUserAttributes(@HeaderParam("AUTH_TOKEN") String authToken,
			@QueryParam("authority") String authority, @QueryParam("key") String key, @PathParam("id") long userId) throws AcServiceException {
		checkUser(authToken, userId);
		return getUserAttributes(authToken, authority, key);
	}
	@GET
    @Path("/users/{userId}/access/{id}")
    @Produces({"application/xml","application/json"})
	public boolean canReadResource(@HeaderParam("AUTH_TOKEN") String authToken, @PathParam("id") String resourceId, @PathParam("id") long userId) throws AcServiceException {
		checkUser(authToken, userId);
		return canReadResource(authToken, resourceId);
	}
}
