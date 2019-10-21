package application.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity

public class General {

	@Column
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private User userFK;
	
	@Column
	@OneToOne(mappedBy = "stats", cascade = CascadeType.ALL)
	private Stats statsFK;
	
	@Column
	@OneToOne(mappedBy = "gameuser", cascade = CascadeType.ALL)
	private GameUser gameuserFK;

	@Column
	private User user;

	@Column
	private Stats stats;

	@Column
	private GameUser gameUser;

	@Column
	private String session;
	
	public User getUserFK()
	{
		return userFK;
	}
	
	public void setUserFK(User userFK)
	{
		this.userFK = userFK;
	}
	
	public Stats getStatsFK()
	{
		return statsFK;
	}
	
	public void setStatsFK(Stats statsFK)
	{
		this.statsFK = statsFK;
	}
	
	public GameUser getGameUserFK()
	{
		return gameuserFK;
	}
	
	public void setGameUserFK(GameUser gameuserFK)
	{
		this.gameuserFK = gameuserFK;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public GameUser getGameUser() {
		return gameUser;
	}

	public void setGameUser(GameUser gameUser) {
		this.gameUser = gameUser;
	}
}
