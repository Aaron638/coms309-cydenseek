package application.model;

import java.time.LocalTime;

public class GameBody {

	private String session;

	private Integer maxplayers;

	private LocalTime startTime;

	private Integer duration;

	private Integer mode;

	private Integer gperiod;

	private String creator;

	private Boolean hider;
	
	private Boolean found;

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

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Boolean getHider() {
		return hider;
	}

	public void setHider(Boolean hider) {
		this.hider = hider;
	}
	
	public Boolean getFound()
	{
		return found;
	}
	
	public void setFound(Boolean found)
	{
		this.found = found;
	}
}