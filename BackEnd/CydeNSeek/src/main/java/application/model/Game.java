package application.model;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Game {

	@Id
	@Column
	private String session;

	@Column
	private Integer maxplayers;

	@Column
	private LocalTime startTime;

	@Column
	private final Integer duration = 10;

	@Column
	private final Integer gperiod = 5;

	@Column
	private String creator;

	public int getDuration() {
		return duration;
	}

	/*public void setDuration(int duration) {
		this.duration = duration;
	}*/

	public int getGperiod() {
		return gperiod;
	}

	/*public void setGperiod(int gperiod) {
		this.gperiod = gperiod;
	}*/

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public Integer getMaxplayers() {
		return maxplayers;
	}

	public void setMaxplayers(Integer maxplayers) {
		this.maxplayers = maxplayers;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
