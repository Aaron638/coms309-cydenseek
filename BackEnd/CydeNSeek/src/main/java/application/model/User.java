package application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String username;

	@Column
	private String password;

	@Column
	private String session;

	@Column
	private Integer gameId;

	@Column
	private String location;

	@Column
	private Integer gwhider;

	@Column
	private Integer gwseeker;

	@Column
	private Integer gphider;

	@Column
	private Integer gpseeker;

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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Integer getGwhider() {
		return gwhider;
	}

	public void setGwhider(Integer gwhider) {
		this.gwhider = gwhider;
	}

	public Integer getGwseeker() {
		return gwseeker;
	}

	public void setGwseeker(Integer gwseeker) {
		this.gwseeker = gwseeker;
	}

	public Integer getGphider() {
		return gphider;
	}

	public void setGphider(Integer gphider) {
		this.gphider = gphider;
	}

	public Integer getGpseeker() {
		return gpseeker;
	}

	public void setGpseeker(Integer gpseeker) {
		this.gpseeker = gpseeker;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}