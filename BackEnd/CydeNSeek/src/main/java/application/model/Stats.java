package application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	private Integer generalId;

	@Column
	private Integer gphider;

	@Column
	private Integer gpseeker;

	@Column
	private Integer gwhider;

	@Column
	private Integer gwseeker;

	@Column
	private Double totdistance;

	@Column
	private Integer tottime;

	public Integer getGPHider() {
		return gphider;
	}

	public void setGPHider(Integer gphider) {
		this.gphider = gphider;
	}

	public Integer getGPSeeker() {
		return gpseeker;
	}

	public void setGPSeeker(Integer gpseeker) {
		this.gpseeker = gpseeker;
	}

	public Integer getGWHider() {
		return gwhider;
	}

	public void setGWHider(Integer gwhider) {
		this.gwhider = gwhider;
	}

	public Integer getGWSeeker() {
		return gwseeker;
	}

	public void setGWSeeker(Integer gwseeker) {
		this.gwseeker = gwseeker;
	}

	public Double getTotDistance() {
		return totdistance;
	}

	public void setTotDistance(Double totdistance) {
		this.totdistance = totdistance;
	}

	public Integer getTotTime() {
		return tottime;
	}

	public void setTotTime(Integer tottime) {
		this.tottime = tottime;
	}

	public Integer getGeneralId() {
		return generalId;
	}

	public void setGeneralId(Integer generalId) {
		this.generalId = generalId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}