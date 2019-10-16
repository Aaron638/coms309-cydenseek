package application.model;

import java.time.LocalTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GameUser {

	@Id
	
	@Column
	private Integer gameFK;
	
	@Column
	private Boolean found;
	
	@Column
	private Boolean isHider;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	public Integer getGameFK()
	{
		return gameFK;
	}
	
	public void setGameFK(Integer gameFK)
	{
		this.gameFK=gameFK;
	}
	
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
