package application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class User {

	@Column
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user", nullable = false)
	private General general;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	private String username;

	@Column
	private String password;

	@Column
	private byte[] salt;

	@Column
	private Boolean developer;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getDeveloper() {
		return developer;
	}

	public void setDeveloper(Boolean developer) {
		this.developer = developer;
	}

	public General getGeneral() {
		return general;
	}

	public void setGeneral(General general) {
		this.general = general;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
}