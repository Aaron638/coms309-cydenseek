package application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity

public class General {

	@Id
	@Column
	private Integer userFK;
	
	@Column
	private Integer statsFK;
	
	@Column
	private Integer gameuserFK;
	
	public General(Integer userFK, Integer statsFK, Integer gameuserFK)
	{
		this.userFK=userFK;
		this.statsFK=statsFK;
		this.gameuserFK=gameuserFK;
	}
	
	public int getUserFK()
	{
		return userFK;
	}
	
	public void setUserFK(int userFK)
	{
		this.userFK = userFK;
	}
	
	public int getStatsFK()
	{
		return statsFK;
	}
	
	public void setStatsFK(int statsFK)
	{
		this.statsFK = statsFK;
	}
	
	public int getGameUserFK()
	{
		return gameuserFK;
	}
	
	public void setGameUserFK(int gameuserFK)
	{
		this.gameuserFK = gameuserFK;
	}
}
