package application.model;

import java.util.UUID;

public class GameUser {

	private String username;

	private Boolean verified;

	private UUID gameSession;

	private String userSession;

	private Boolean found;

	private Boolean hider;

	private Double latitude;

	private Double longitude;

	public Boolean isFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	public Boolean isHider() {
		return hider;
	}

	public void setHider(Boolean isHider) {
		this.hider = isHider;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getUserSession() {
		return userSession;
	}

	public void setUserSession(String userSession) {
		this.userSession = userSession;
	}

	public UUID getGameSession() {
		return gameSession;
	}

	public void setGameSession(UUID gameSession) {
		this.gameSession = gameSession;
	}

	public Boolean isVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}