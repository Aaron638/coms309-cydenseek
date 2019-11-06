package application.model;

public class GameUser {

	private String gameSession;

	private String userSession;

	private Boolean found;

	private Boolean hider;

	private Double latitude;

	private Double longitude;

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	public Boolean getHider() {
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

	public String getGameSession() {
		return gameSession;
	}

	public void setGameSession(String gameSession) {
		this.gameSession = gameSession;
	}
}