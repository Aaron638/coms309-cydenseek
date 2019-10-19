package application.model;

import java.time.LocalTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private Integer hiders;

	@Column
	private Integer seekers;

	@Column
	private Integer maxplayers;

	@Column
	private LocalTime startTime;

	@Column
	private Integer duration;

	@Column
	private Integer gperiod;

	@Column
	private String creator;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getGperiod() {
		return gperiod;
	}

	public void setGperiod(int gperiod) {
		this.gperiod = gperiod;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public Integer getMaxplayers() {
		return maxplayers;dsfoiafsal;hf
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

	public Integer getHiders() {
		return hiders;
	}

	public void setHiders(Integer hiders) {
		this.hiders = hiders;
	}

	public Integer getSeekers() {
		return seekers;
	}

	public void setSeekers(Integer seekers) {
		this.seekers = seekers;
	}
}