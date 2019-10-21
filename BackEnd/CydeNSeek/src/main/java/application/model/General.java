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
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<User> userFK;
	
	@Column
	@OneToMany(mappedBy = "stats", cascade = CascadeType.ALL)
	private List<Stats> statsFK;
	
	@Column
	@OneToMany(mappedBy = "gameuser", cascade = CascadeType.ALL)
	private List<GameUser> gameuserFK;

	@Column
	private User user;

	@Column
	private Stats stats;

	@Column
	private GameUser gameUser;

	@Column
	private String session;
	
	public List<User> getUserFK()
	{
		return userFK;
	}
	
	public void setUserFK(List<User> userFK)
	{
		this.userFK = userFK;
	}
	
	public List<Stats> getStatsFK()
	{
		return statsFK;
	}
	
	public void setStatsFK(List<Stats> statsFK)
	{
		this.statsFK = statsFK;
	}
	
	public List<GameUser> getGameUserFK()
	{
		return gameuserFK;
	}
	
	public void setGameUserFK(List<GameUser> gameuserFK)
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
