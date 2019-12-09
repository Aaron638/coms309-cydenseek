package application.model;

public class GameBody {

	private String session;

	private Integer maxplayers;

	private Integer duration;

	private Integer mode;

	private Integer gperiod;

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	
	public Integer getMaxplayers() {
		return maxplayers;
	}

	public void setMaxplayers(Integer maxplayers) {
		this.maxplayers = maxplayers;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Integer getGperiod() {
		return gperiod;
	}

	public void setGperiod(Integer gperiod) {
		this.gperiod = gperiod;
	}
}