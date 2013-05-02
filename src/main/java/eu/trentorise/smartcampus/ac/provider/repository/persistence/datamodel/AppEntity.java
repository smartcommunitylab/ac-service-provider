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

package eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "APP_AUTHENTICATION")
public class AppEntity implements Serializable {

	private static final long serialVersionUID = 1067996326671906278L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.REMOVE, CascadeType.MERGE })
	@JoinColumn(name = "USER_ID", nullable=false)
	private Set<AttributeEntity> attributeEntities;

	@Column(name = "APP_TOKEN")
	private String appToken;

	@Column(name = "TOKEN_EXP_TIME")
	private long expTime;

	@Column(name = "SOCIAL_ID")
	private Long socialId;

	@Column(name = "SESSION_APP_TOKEN")
	private String sessionAppToken;

	@Column(name = "SESSION_TOKEN_EXP_TIME")
	private long sessionExpTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<AttributeEntity> getAttributeEntities() {
		return attributeEntities;
	}

	public void setAttributeEntities(Set<AttributeEntity> attributeEntities) {
		this.attributeEntities = attributeEntities;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String authToken) {
		this.appToken = authToken;
	}

	public long getExpTime() {
		return expTime;
	}

	public void setExpTime(long expTime) {
		this.expTime = expTime;
	}

	/**
	 * @return the sessionAuthToken
	 */
	public String getSessionAppToken() {
		return sessionAppToken;
	}

	/**
	 * @param sessionAuthToken the sessionAuthToken to set
	 */
	public void setSessionAppToken(String sessionAuthToken) {
		this.sessionAppToken = sessionAuthToken;
	}

	/**
	 * @return the sessionExpTime
	 */
	public long getSessionExpTime() {
		return sessionExpTime;
	}

	/**
	 * @param sessionExpTime the sessionExpTime to set
	 */
	public void setSessionExpTime(long sessionExpTime) {
		this.sessionExpTime = sessionExpTime;
	}

	@Override
	public String toString() {
		return "UserEntity{id=" + id + ", appToken=" + appToken
				+ ",socialId=" + socialId + "}";
	}

	public Long getSocialId() {
		return socialId;
	}

	public void setSocialId(Long socialId) {
		this.socialId = socialId;
	}

}
