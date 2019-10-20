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
	
	public General(List<User> userFK, List<Stats> statsFK, List<GameUser> gameuserFK)
	{
		this.userFK=userFK;
		this.statsFK=statsFK;
		this.gameuserFK=gameuserFK;
	}
	
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
}
