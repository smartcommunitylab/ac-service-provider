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
@Table(name = "USER_AUTHENTICATION")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 1067996326671906278L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.REMOVE, CascadeType.MERGE })
	@JoinColumn(name = "USER_ID")
	private Set<AttributeEntity> attributeEntities;

	@Column(name = "AUTH_TOKEN")
	private String authToken;

	@Column(name = "TOKEN_EXP_TIME")
	private long expTime;

	@Column(name = "SOCIAL_ID")
	private Long socialId;

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

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public long getExpTime() {
		return expTime;
	}

	public void setExpTime(long expTime) {
		this.expTime = expTime;
	}

	@Override
	public String toString() {
		return "UserEntity{id=" + id + ", authToken=" + authToken
				+ ",socialId=" + socialId + "}";
	}

	public Long getSocialId() {
		return socialId;
	}

	public void setSocialId(Long socialId) {
		this.socialId = socialId;
	}

}
