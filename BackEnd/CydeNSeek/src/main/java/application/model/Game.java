package application.model;

import java.time.LocalTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
<<<<<<< BackEnd/CydeNSeek/src/main/java/application/model/Game.java
=======
import javax.persistence.GeneratedValue;
>>>>>>> BackEnd/CydeNSeek/src/main/java/application/model/Game.java
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Game {

	@Id

	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column
	private UUID session;

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


	public UUID getSession() {
		return session;
	}

	public void setSession(UUID session) {
		this.session = session;
	}
}
