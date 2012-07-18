/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.repository.persistence.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ATTRIBUTE")
public class AttributeEntity implements Serializable {

	private static final long serialVersionUID = 5306076968727353508L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "AUTH_ID")
	private AuthorityEntity authorityEntity;

	@Column(name = "NAME")
	private String key;

	@Column(name = "VALUE")
	private String value;

	public AuthorityEntity getAuthority() {
		return authorityEntity;
	}

	public void setAuthority(AuthorityEntity authorityEntity) {
		this.authorityEntity = authorityEntity;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AttributeEntity other = (AttributeEntity) obj;
		if (this.authorityEntity != other.authorityEntity
				&& (this.authorityEntity == null || !this.authorityEntity
						.equals(other.authorityEntity))) {
			return false;
		}
		if ((this.key == null) ? (other.key != null) : !this.key
				.equals(other.key)) {
			return false;
		}
		if ((this.value == null) ? (other.value != null) : !this.value
				.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83
				* hash
				+ (this.authorityEntity != null ? this.authorityEntity
						.hashCode() : 0);
		hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
		hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Attribute{authority=" + authorityEntity + ", key=" + key
				+ ", value=" + value + '}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
