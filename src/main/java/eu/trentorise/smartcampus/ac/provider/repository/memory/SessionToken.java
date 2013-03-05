/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.ac.provider.repository.memory;

/**
 * Session token descriptor with token and expTime
 * @author raman
 *
 */
public class SessionToken {

	public static final long SESSION_TOKEN_DURATION = 1000*60*60*4;
	private String token;
	private long expTime;

	public SessionToken() {
		super();
	}

	/**
	 * @param token
	 * @param expTime
	 */
	public SessionToken(String token, long expTime) {
		super();
		this.token = token;
		this.expTime = expTime;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the expTime
	 */
	public long getExpTime() {
		return expTime;
	}
	/**
	 * @param expTime the expTime to set
	 */
	public void setExpTime(long expTime) {
		this.expTime = expTime;
	}
	
	
}
