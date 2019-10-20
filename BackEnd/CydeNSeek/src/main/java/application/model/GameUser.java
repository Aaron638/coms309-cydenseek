package application.model;

import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class GameUser {
	
	@Column
	@ManyToOne
	@JoinColumn(name = "gameuser")
	private General general;
	
	@Column
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "game")
	private Game game;
	
	@Column
	private Boolean found;
	
	@Column
	private Boolean isHider;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	public Boolean getFound()
	{
		return found;
	}
	
	public void setFound(Boolean found)
	{
		this.found=found;
	}
	
	public Boolean getIsHider()
	{
		return isHider;
	}
	
	public void setIsHider(Boolean isHider)
	{
		this.isHider=isHider;
	}
	
	public Double getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude(Double latitude)
	{
		this.latitude=latitude;
	}
	
	public Double getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude(Double longitude)
	{
		this.longitude=longitude;
	}
}
