package application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Stats {

	@Column
	@OneToOne
	@JoinColumn(name = "stats")
	private General general;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	private Integer gphider;

	@Column
	private Integer gpseeker;

	@Column
	private Integer gwhider;

	@Column
	private Integer gwseeker;

	@Column
	private Integer totdistance;

	@Column
	private Integer tottime;

	public General getGeneral() {
		return general;
	}

	public void setGeneral(General general) {
		this.general = general;
	}

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

	public Integer getTotDistance() {
		return totdistance;
	}

	public void setTotDistance(Integer totdistance) {
		this.totdistance = totdistance;
	}

	public Integer getTotTime() {
		return tottime;
	}

	public void setTotTime(Integer tottime) {
		this.tottime = tottime;
	}
}